/**
 * Copyright 2020 interactive instruments GmbH
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.collect.ImmutableMap;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder.ValueClassification;
import de.interactive_instruments.xtraserver.config.api.MappingValueClassification;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;


/** @author zahnen */
public class MappingTransformerNil extends AbstractMappingTransformer {

  private final Namespaces namespaces;

  MappingTransformerNil(XtraServerMapping xtraServerMapping, final ApplicationSchema applicationSchema) {
    super(xtraServerMapping);
    this.namespaces = applicationSchema.getNamespaces();
  }

  @Override
  protected XtraServerMappingBuilder transformXtraServerMapping(
      Context context, List<FeatureTypeMapping> transformedFeatureTypeMappings) {
    return new XtraServerMappingBuilder()
        .copyOf(context.xtraServerMapping)
        .featureTypeMappings(transformedFeatureTypeMappings);
  }

  @Override
  protected MappingTableBuilder transformMappingTable(
      Context context,
      List<MappingTable> transformedMappingTables,
      List<MappingJoin> transformedMappingJoins,
      List<MappingValue> transformedMappingValues) {
    final MappingTable mappingTable = context.mappingTable;

    MappingTableBuilder mappingTableBuilder =
        super.transformMappingTable(
            context, transformedMappingTables, transformedMappingJoins, transformedMappingValues);

      List<MappingValue> nilValues =
          mappingTable.getValues()
              .stream()
              .filter(this::isNil)
              .collect(Collectors.toList());

    List<List<QName>> nilPaths =
       nilValues.stream()
            .map(value -> value.getQualifiedTargetPath().subList(0, value.getQualifiedTargetPath().size()-1))
            .collect(Collectors.toList());

    Map<List<QName>, List<MappingValue>> nonNilValues =
        nilPaths.stream()
        .map(nilPath -> new SimpleImmutableEntry<>(nilPath, mappingTable.getValues().stream()
            .filter(value -> !value.isConstant() && !value.isNil())
            .filter(value -> pathStartsWith(value.getQualifiedTargetPath(), nilPath))
            .collect(Collectors.toList())))
        .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

    List<MappingValue> cleanedValues =
        mappingTable.getValues().stream()
            .map(
                value -> {
                  Optional<List<QName>> nilPath =
                      pathStartsWithOne(value.getQualifiedTargetPath(), nilPaths);

                  if (nilPath.isPresent()) {
                    if (!nonNilValues.containsKey(nilPath.get())
                        || nonNilValues.get(nilPath.get()).isEmpty()) {
                      return isNil(nilPath.get(), value) || isNilReason(nilPath.get(), value)
                          ? value
                          : null;
                    }

                    if (isNil(nilPath.get(), value)) {
                      return null;
                    } else if (isNilReason(nilPath.get(), value)) {
                      ValueClassification nilMapping = new MappingValueBuilder()
                          .nil();

                      if (nonNilValues.get(nilPath.get()).get(0).isClassification()) {
                        nilMapping.keyValue("! " + String.join(" ", ((MappingValueClassification)nonNilValues.get(nilPath.get()).get(0)).getKeys()), value.getValue());
                      } else {
                        nilMapping.keyValue("NULL", value.getValue());
                      }

                      nilMapping
                          .value(nonNilValues.get(nilPath.get()).get(0).getValue())
                          .targetPath(namespaces.getPrefixedPath(nilPath.get()))
                          .qualifiedTargetPath(nilPath.get())
                          .description(value.getDescription());

                      return nilMapping.build();
                    }
                  }

                  return value;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    mappingTableBuilder
        .clearValues()
        .values(cleanedValues);

    return mappingTableBuilder;
  }

  private Optional<List<QName>> pathStartsWithOne(List<QName> path, List<List<QName>> prefixes) {
    return prefixes.stream().filter(prefix -> pathStartsWith(path, prefix)).findFirst();
  }

  private boolean pathStartsWith(List<QName> path, List<QName> prefix) {
    return (path.size() > prefix.size() && Objects.equals(path.subList(0, prefix.size()), prefix)) || Objects.equals(path, prefix);
  }

  private boolean isNil(MappingValue value) {
    return value.isConstant() && Objects.equals(value.getQualifiedTargetPath()
        .get(value.getQualifiedTargetPath().size() - 1), new QName("http://www.w3.org/2001/XMLSchema-instance", "@nil")) && Objects
        .equals(value.getValue(), "true");
  }

  private boolean isNil(List<QName> path, MappingValue value) {
    return pathStartsWith(value.getQualifiedTargetPath(), path)
        && value.getQualifiedTargetPath().size() == path.size() + 1
        && isNil(value);
  }

  private boolean isNilReason(List<QName> path, MappingValue value) {
    return pathStartsWith(value.getQualifiedTargetPath(), path)
        && value.getQualifiedTargetPath().size() == path.size() + 1
        && value.isConstant() && Objects.equals(value.getQualifiedTargetPath()
        .get(value.getQualifiedTargetPath().size() - 1), new QName("@nilReason"));
  }
}

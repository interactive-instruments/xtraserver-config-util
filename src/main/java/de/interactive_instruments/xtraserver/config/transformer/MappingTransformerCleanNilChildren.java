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

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import de.interactive_instruments.xtraserver.config.transformer.SchemaInfo.OptionalProperty;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;


/** @author zahnen */
public class MappingTransformerCleanNilChildren extends AbstractMappingTransformer {

  MappingTransformerCleanNilChildren(XtraServerMapping xtraServerMapping) {
    super(xtraServerMapping);
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
              .filter(value -> value.isConstant() && Objects.equals(value.getQualifiedTargetPath()
                  .get(value.getQualifiedTargetPath().size() - 1), new QName("http://www.w3.org/2001/XMLSchema-instance", "@nil")) && Objects
                  .equals(value.getValue(), "true"))
              .collect(Collectors.toList());

    List<List<QName>> nilPaths =
       nilValues.stream()
            .map(value -> value.getQualifiedTargetPath().subList(0, value.getQualifiedTargetPath().size()-1))
            .collect(Collectors.toList());

    List<MappingValue> cleanedValues = mappingTable.getValues()
        .stream()
        .filter(value -> !pathStartsWithOneOf(value.getQualifiedTargetPath(), nilPaths))
        .collect(Collectors.toList());

    mappingTableBuilder
        .clearValues()
        .values(cleanedValues);

    return mappingTableBuilder;
  }

  private boolean pathStartsWithOneOf(List<QName> path, List<List<QName>> prefixes) {
    return prefixes.stream().anyMatch(prefix -> pathStartsWith(path, prefix) && !pathIsAttributeOf(path, prefix));
  }

  private boolean pathStartsWith(List<QName> path, List<QName> prefix) {
    return (path.size() > prefix.size() && Objects.equals(path.subList(0, prefix.size()), prefix)) || Objects.equals(path, prefix);
  }

  private boolean pathIsAttributeOf(List<QName> path, List<QName> prefix) {
    return path.size() == prefix.size() + 1 && path.get(path.size()-1).getLocalPart().startsWith("@");
  }
}

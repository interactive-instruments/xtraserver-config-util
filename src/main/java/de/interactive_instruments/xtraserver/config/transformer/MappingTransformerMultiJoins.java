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

import com.google.common.collect.ImmutableList;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;

/** @author zahnen */
public class MappingTransformerMultiJoins extends AbstractMappingTransformer {

  public static final String HINT_MULTI_JOIN = "MULTI_JOIN";

  MappingTransformerMultiJoins(XtraServerMapping xtraServerMapping) {
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
  protected FeatureTypeMappingBuilder transformFeatureTypeMapping(
      Context context, List<MappingTable> transformedMappingTables) {

    List<MappingTable> transformedMappingTables2 =
        transformedMappingTables.stream()
            .flatMap(t -> traverseMappingTable(context).apply(t).stream())
            .collect(Collectors.toList());

    return super.transformFeatureTypeMapping(context, transformedMappingTables2);
  }

  protected List<MappingTableBuilder> applyMultiJoins(
      Context context,
      List<MappingTable> transformedMappingTables,
      List<MappingJoin> transformedMappingJoins,
      List<MappingValue> transformedMappingValues) {
    final FeatureTypeMapping featureTypeMapping = context.featureTypeMapping;
    final MappingTable mappingTable = context.mappingTable;

    MappingTableBuilder mappingTableBuilder =
        super.transformMappingTable(
            context, transformedMappingTables, transformedMappingJoins, transformedMappingValues);

    boolean isMultiJoin = mappingTable.getTransformationHints().containsKey(HINT_MULTI_JOIN);

    if (isMultiJoin) {
      Map<QName, List<MappingValue>> collect =
          mappingTable.getValues().stream()
              .collect(
                  Collectors.groupingBy(
                      value -> value.getQualifiedTargetPath().get(0),
                      LinkedHashMap::new,
                      Collectors.toList()));

      // TODO: support cases where multiple property is not first in path
      // normally first multiple target path element is detected in plugin
      // maybe we can store additional paths in transformationHints
      List<MappingTableBuilder> collect1 =
          collect.entrySet().stream()
              .map(
                  entry ->
                      new MappingTableBuilder()
                          .copyOf(mappingTableBuilder.build())
                          .qualifiedTargetPath(ImmutableList.of(entry.getKey()))
                          // TODO
                          //.targetPath(entry.getKey().toString().replace("{", "").replace("}", ":"))
                          .clearValues()
                          .values(entry.getValue()))
              .collect(Collectors.toList());

      return collect1;
    }

    return ImmutableList.of(mappingTableBuilder);
  }

  private Function<MappingTable, List<MappingTable>> traverseMappingTable(final Context context) {
    return mappingTable -> {
      // recurse
      final List<MappingTable> transformedJoiningTables =
          mappingTable.getJoiningTables().stream()
              .flatMap(t -> traverseMappingTable(context).apply(t).stream())
              .collect(Collectors.toList());

      context.mappingTable = mappingTable;

      return applyMultiJoins(
              context,
              transformedJoiningTables,
              mappingTable.getJoinPaths().asList(),
              mappingTable.getValues().asList())
          .stream()
          .map(MappingTableBuilder::build)
          .collect(Collectors.toList());
    };
  }
}

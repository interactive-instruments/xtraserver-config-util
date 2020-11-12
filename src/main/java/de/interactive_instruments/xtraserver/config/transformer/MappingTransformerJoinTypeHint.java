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
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import de.interactive_instruments.xtraserver.config.transformer.SchemaInfo.OptionalProperty;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: Schema stage, Collection of multiple AddHintTransformers
/** @author zahnen */
public class MappingTransformerJoinTypeHint extends AbstractMappingTransformer {

  public static final String HINT_JOIN_TYPE = "JOIN_TYPE";

  private final SchemaInfo.OptionalProperty schemaInfo;

  MappingTransformerJoinTypeHint(XtraServerMapping xtraServerMapping, OptionalProperty schemaInfo) {
    super(xtraServerMapping);
    this.schemaInfo = schemaInfo;
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
    final FeatureTypeMapping featureTypeMapping = context.featureTypeMapping;
    final MappingTable mappingTable = context.mappingTable;

    MappingTableBuilder mappingTableBuilder =
        super.transformMappingTable(
            context, transformedMappingTables, transformedMappingJoins, transformedMappingValues);

    if (mappingTable.isMerged()) {
      boolean allPropertiesOptional =
          mappingTable.getValues().stream()
              .allMatch(
                  value ->
                      schemaInfo.isOptional(
                          featureTypeMapping.getQualifiedName(), value.getQualifiedTargetPath()));

      if (allPropertiesOptional) {
        // TODO: set at MappingJoin
        mappingTableBuilder
            .clearJoinPaths()
            .joinPaths(
                transformedMappingJoins.stream()
                    .map(
                        join ->
                            new MappingJoinBuilder()
                                .copyOf(join)
                                .transformationHint(HINT_JOIN_TYPE, "LEFT")
                                .build())
                    .collect(Collectors.toList()));
        // mappingTableBuilder.transformationHint(HINT_JOIN_TYPE, "LEFT");
      }
    }

    return mappingTableBuilder;
  }
}

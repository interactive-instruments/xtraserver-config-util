/**
 * Copyright 2020 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.collect.Lists;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.Hints;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
public class MappingTransformerMergeTables extends AbstractMappingTransformer {

    private final List<VirtualTable> virtualTables;
    private final Map<String, VirtualTable.Builder> currentVirtualTables;

    MappingTransformerMergeTables(XtraServerMapping xtraServerMapping) {
        super(xtraServerMapping);
        this.virtualTables = new ArrayList<>();
        this.currentVirtualTables = new LinkedHashMap<>();

        xtraServerMapping.getVirtualTables().forEach(virtualTable -> currentVirtualTables.put("$" + virtualTable.getName() + "$", new VirtualTable.Builder().from2(virtualTable)));
    }

    @Override
    protected XtraServerMappingBuilder transformXtraServerMapping(Context context, List<FeatureTypeMapping> transformedFeatureTypeMappings) {
        return new XtraServerMappingBuilder()
                .copyOf(context.xtraServerMapping)
                .clearVirtualTables()
                .virtualTables(currentVirtualTables.values()
                                                   .stream()
                                                   .map(VirtualTable.Builder::build)
                                                   .collect(Collectors.toList()))
                .featureTypeMappings(transformedFeatureTypeMappings);
    }

    @Override
    protected MappingTableBuilder transformMappingTable(Context context, List<MappingTable> transformedMappingTables, List<MappingJoin> transformedMappingJoins, List<MappingValue> transformedMappingValues) {
        final FeatureTypeMapping featureTypeMapping = context.featureTypeMapping;
        final MappingTable mappingTable = context.mappingTable;

        List<MappingTable> transformedMappingTables2 = transformedMappingTables.stream()
                                                                               .filter(joinedTable -> !joinedTable.isMerged() && !(currentVirtualTables.containsKey(joinedTable.getName()) && mappingTable.getPredicate() != null && !mappingTable.getPredicate().isEmpty()))
                                                                               .collect(Collectors.toList());

        MappingTableBuilder mappingTableBuilder = new MappingTableBuilder()
                .shallowCopyOf(mappingTable)
                .values(transformedMappingValues);

        final VirtualTable.Builder[] currentVirtualTable = {null};
        final String[] currentVirtualName = {null};

    // TODO: if currentVirtualTable and mergedVirtualTable, merge into one
    transformedMappingTables.stream()
        // .filter(MappingTable::isMerged)
        .filter(
            mappingTable1 ->
                mappingTable1.isMerged()
                    || (currentVirtualTables.containsKey(mappingTable1.getName())
                        && mappingTable.getPredicate() != null
                        && !mappingTable.getPredicate().isEmpty()))
        .forEach(
            mergedTable -> {
              Optional<VirtualTable.Builder> mergedVirtualTable =
                  Optional.ofNullable(currentVirtualTables.remove(mergedTable.getName()));

              String[] lastVirtualName = {null};

              if (mergedVirtualTable.isPresent()) {
                currentVirtualTable[0] = new VirtualTable.Builder();
                currentVirtualTable[0].aliases(mergedVirtualTable.get().aliases());
                currentVirtualTable[0].originalTable(mappingTable);
                currentVirtualTable[0].from(mergedVirtualTable.get().build());

                currentVirtualName[0] =
                    mergedTable
                        .getName()
                        .replaceAll("\\$", "")
                        .replace("vrt_", "vrt_" + mappingTable.getName() + "_");
                currentVirtualTable[0].name(currentVirtualName[0]);

              } else {
                if (currentVirtualTable[0] == null) {
                  if (currentVirtualTables.containsKey(mappingTable.getName())) {
                    currentVirtualTable[0] = currentVirtualTables.get(mappingTable.getName());
                    currentVirtualName[0] = mappingTable.getName().replaceAll("\\$", "");
                  } else {
                    currentVirtualTable[0] = VirtualTable.builder();
                    currentVirtualTable[0].originalTable(mappingTable);
                    currentVirtualName[0] = "vrt_" + mappingTable.getName();
                  }
                }

                if (!currentVirtualName[0].contains(mergedTable.getName())
                    || currentVirtualName[0].contains(mergedTable.getName() + "__")) {
                  lastVirtualName[0] = "$" + currentVirtualName[0] + "$";
                  currentVirtualName[0] += "_" + mergedTable.getName();
                  if (virtualTableExists(currentVirtualName[0])) {
                    int i = 2;
                    while (virtualTableExists(currentVirtualName[0] + "_" + i)) {
                      i++;
                    }
                    currentVirtualName[0] = currentVirtualName[0] + "_" + i;
                  }
                }

                currentVirtualTable[0].name(currentVirtualName[0]);
                //currentVirtualTable[0].originalTable(mergedTable);

                if (currentVirtualTables.containsKey(mappingTable.getName())) {
                  currentVirtualTable[0].originalTable(new MappingTableBuilder().copyOf(mergedTable)
                      .clearJoinPaths()
                      .joinPaths(mergedTable.getJoinPaths()
                      .stream()
                      .map(jp -> new MappingJoinBuilder().shallowCopyOf(jp)
                          .joinConditions(jp.getJoinConditions()
                              .stream()
                              .map(jc -> jc.getSourceTable()
                                  .equals(mappingTable.getName()) ? new MappingJoinBuilder.ConditionBuilder()
                                  .copyOf(jc)
                                  .sourceTable(mappingTable.getName().replaceAll("\\$", "").replaceAll("vrt_", ""))
                                  .build() : jc)
                              .collect(Collectors.toList()))
                          .build())
                      .collect(Collectors.toList())).build());
                } else {
                  currentVirtualTable[0].originalTable(mergedTable);
                }
              }

              String newName = "$" + currentVirtualName[0] + "$";
              mappingTableBuilder.name(newName);
              mappingTableBuilder.predicate(null);

                // add all values from table and nested merged tables (don't we iterate over nested
              // merged tables anyhow???)
              mappingTableBuilder.values(
                  mergedTable
                      .getAllValuesStream(MappingTable::isMerged)
                      .collect(Collectors.toList()));

              List<MappingTable> oldJoiningTables =
                  Lists.newArrayList(mappingTableBuilder.buildDraft().getJoiningTables());
              mappingTableBuilder.clearJoiningTables();

              mappingTableBuilder.joiningTables(
                  mergedTable.getJoiningTables().stream()
                      .map(
                          jt ->
                              new MappingTableBuilder()
                                  .shallowCopyOf(jt)
                                  .joiningTables(jt.getJoiningTables())
                                  .values(jt.getValues())
                                  .joinPaths(
                                      jt.getJoinPaths().stream()
                                          .map(
                                              jp ->
                                                  new MappingJoinBuilder()
                                                      .shallowCopyOf(jp)
                                                      .joinConditions(
                                                          jp.getJoinConditions().stream()
                                                              .map(
                                                                  jc ->
                                                                      jc.getSourceTable()
                                                                              .equals(
                                                                                  mergedTable
                                                                                      .getName())
                                                                          ? new MappingJoinBuilder
                                                                                  .ConditionBuilder()
                                                                              .copyOf(jc)
                                                                              .sourceTable(newName)
                                                                              .sourceField(currentVirtualTable[0].aliases().getWithAlias(jc.getSourceTable(), jc.getSourceField(), ""))
                                                                              .build()
                                                                          : jc)
                                                              .collect(Collectors.toList()))
                                                      .build())
                                          .collect(Collectors.toList()))
                                  .build())
                      .collect(Collectors.toList()));

              mappingTableBuilder.joiningTables(
                  oldJoiningTables.stream()
                      .map(
                          jt ->
                              new MappingTableBuilder()
                                  .shallowCopyOf(jt)
                                  .joiningTables(jt.getJoiningTables())
                                  .values(jt.getValues())
                                  .joinPaths(
                                      jt.getJoinPaths().stream()
                                          .map(
                                              jp ->
                                                  new MappingJoinBuilder()
                                                      .shallowCopyOf(jp)
                                                      .joinConditions(
                                                          jp.getJoinConditions().stream()
                                                              .map(
                                                                  jc ->
                                                                      jc.getSourceTable()
                                                                              .equals(
                                                                                  lastVirtualName[
                                                                                      0])
                                                                          ? new MappingJoinBuilder
                                                                                  .ConditionBuilder()
                                                                              .copyOf(jc)
                                                                              .sourceTable(newName)
                                                                              .sourceField(currentVirtualTable[0].aliases().getWithAlias(jc.getSourceTable(), jc.getSourceField(), ""))
                                                                              .build()
                                                                          : jc)
                                                              .collect(Collectors.toList()))
                                                      .build())
                                          .collect(Collectors.toList()))
                                  .build())
                      .collect(Collectors.toList()));

              this.currentVirtualTables.put(
                  newName, currentVirtualTable[0]);
            });
        mappingTableBuilder.joinPaths(transformedMappingJoins.stream()
                                                             .map(jp -> new MappingJoinBuilder().shallowCopyOf(jp)
                                                                                                .joinConditions(jp.getJoinConditions()
                                                                                                                  .stream()
                                                                                                                  .map(jc -> jc.getTargetTable()
                                                                                                                               .equals(mappingTable.getName()) ? new MappingJoinBuilder.ConditionBuilder()
                                                                                                                          .copyOf(jc)
                                                                                                                          .targetTable(mappingTableBuilder.buildDraft()
                                                                                                                                                          .getName())
                                                                                                                          .build() : jc)
                                                                                                                  .collect(Collectors.toList()))
                                                                                                .build())
                                                             .collect(Collectors.toList()));

        if (Objects.nonNull(currentVirtualName[0])) {
            return mappingTableBuilder
                    .primaryKey(currentVirtualTable[0].applyAliasIfNecessary(mappingTable.getName(), new MappingValueBuilder().column().value(mappingTable.getPrimaryKey()).targetPath("FOO").build()).getValue())
                    .joiningTables(transformedMappingTables2
                            .stream()
                            .map(jt -> new MappingTableBuilder().shallowCopyOf(jt)
                                                                .joiningTables(jt.getJoiningTables())
                                                                .values(jt.getValues())
                                                                .joinPaths(jt.getJoinPaths()
                                                                             .stream()
                                                                             .map(jp -> new MappingJoinBuilder().shallowCopyOf(jp)
                                                                                                                .joinConditions(jp.getJoinConditions()
                                                                                                                                  .stream()
                                                                                                                                  .map(jc -> jc.getSourceTable()
                                                                                                                                               .equals(mappingTable.getName()) ? new MappingJoinBuilder.ConditionBuilder()
                                                                                                                                          .copyOf(jc)
                                                                                                                                          .sourceTable("$" + currentVirtualName[0] + "$")
                                                                                                                                          .sourceField(currentVirtualTable[0].aliases().getWithAlias(jc.getSourceTable(), jc.getSourceField(), ""))
                                                                                                                                          .build() : jc)
                                                                                                                                  .collect(Collectors.toList()))
                                                                                                                .build())
                                                                             .collect(Collectors.toList()))
                                                                .build())
                            .collect(Collectors.toList()));
        }

        return mappingTableBuilder
                .joiningTables(transformedMappingTables2);

    }

    private boolean virtualTableExists(String name) {
        return currentVirtualTables.values()
                                   .stream()
                                   .map(VirtualTable.Builder::build)
                                   .anyMatch(virtualTable -> virtualTable.getName()
                                                                         .equals(name));
    }
}

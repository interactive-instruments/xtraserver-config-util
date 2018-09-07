/**
 * Copyright 2018 interactive instruments GmbH
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

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.VirtualTableBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
public class MappingTransformerMergeTables extends AbstractMappingTransformer {

    private final List<VirtualTable> virtualTables;

    MappingTransformerMergeTables(XtraServerMapping xtraServerMapping) {
        super(xtraServerMapping);
        this.virtualTables = new ArrayList<>();
    }

    @Override
    protected XtraServerMappingBuilder transformXtraServerMapping(Context context, List<FeatureTypeMapping> transformedFeatureTypeMappings) {
        return new XtraServerMappingBuilder()
                //.shallowCopyOf(xtraServerMapping)
                .copyOf(context.xtraServerMapping)
                .virtualTables(virtualTables)
                .featureTypeMappings(transformedFeatureTypeMappings);
    }

    @Override
    protected MappingTableBuilder transformMappingTable(Context context, List<MappingTable> transformedMappingTables, List<MappingJoin> transformedMappingJoins, List<MappingValue> transformedMappingValues) {
        final FeatureTypeMapping featureTypeMapping = context.featureTypeMapping;
        final MappingTable mappingTable = context.mappingTable;

        List<MappingTable> transformedMappingTables2 = transformedMappingTables.stream()
                                                                               .filter(joinedTable -> !joinedTable.isMerged())
                                                                               .collect(Collectors.toList());

        MappingTableBuilder mappingTableBuilder = new MappingTableBuilder()
                .shallowCopyOf(mappingTable)
                .values(transformedMappingValues)
                .joinPaths(transformedMappingJoins);

        Map<String, VirtualTableBuilder> currentVirtualTables = new LinkedHashMap<>();

        transformedMappingTables.stream()
                                .filter(MappingTable::isMerged)
                                .forEach(mergedTable -> {
                                    String primaryName = mappingTable.getName();
                                    String virtualName = mappingTable.getName();

                                    boolean virtualExists = currentVirtualTables.containsKey(primaryName);

                                    VirtualTableBuilder virtualTable = currentVirtualTables.get(primaryName);
                                    if (!virtualExists) {
                                        virtualTable = new VirtualTableBuilder();
                                        virtualTable.originalTable(mappingTable);
                                        currentVirtualTables.put(primaryName, virtualTable);
                                    }

                                    if (!virtualExists) {
                                        virtualName = "vrt_" + virtualName;
                                    }

                                    if (!virtualName.contains(mergedTable.getName()) || virtualName.contains(mergedTable.getName() + "__")) {
                                        virtualName += "_" + mergedTable.getName();
                                        if (virtualTableExists(virtualName)) {
                                            int i = 2;
                                            while (virtualTableExists(virtualName + "_" + i)) {
                                                i++;
                                            }
                                            virtualName = virtualName + "_" + i;
                                        }
                                        mappingTableBuilder.name("$" + virtualName + "$");
                                    }

                                    virtualTable.name(virtualName);
                                    virtualTable.originalTable(mergedTable);

                                    mappingTableBuilder.predicate(null);
                                    mappingTableBuilder.values(mergedTable.getAllValuesStream()
                                                                          .collect(Collectors.toList()));
                                });

        currentVirtualTables.values().forEach(virtualTableBuilder -> virtualTables.add(virtualTableBuilder.build()));

        return mappingTableBuilder
                .joiningTables(transformedMappingTables2);
    }

    private boolean virtualTableExists(String name) {
        return virtualTables.stream().anyMatch(virtualTable -> virtualTable.getName().equals(name));
    }
}

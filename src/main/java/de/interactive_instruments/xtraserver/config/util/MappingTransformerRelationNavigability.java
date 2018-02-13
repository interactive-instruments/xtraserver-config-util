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
package de.interactive_instruments.xtraserver.config.util;

import com.google.common.collect.ImmutableMap;
import de.interactive_instruments.xtraserver.config.util.api.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zahnen
 */
public class MappingTransformerRelationNavigability implements MappingTransformer {

    private final XtraServerMapping xtraServerMapping;
    private final Map<String, List<String>> replaceAssociationTarget;

    public MappingTransformerRelationNavigability(XtraServerMapping xtraServerMapping) {
        this.xtraServerMapping = xtraServerMapping;

        this.replaceAssociationTarget = new ImmutableMap.Builder<String, List<String>>()
                //.put("adv:AA_REO/adv:istAbgeleitetAus", Lists.newArrayList("adv:AP_PTO"))
                //.put("adv:AA_REO/adv:hatDirektUnten", Lists.newArrayList("adv:AP_PTO"))
                //.put("adv:AA_REO/adv:istAbgeleitetAus", Lists.newArrayList("adv:AX_Hafenbecken"))
                //.put("adv:AA_Objekt/adv:istTeilVon", Lists.newArrayList("adv:AX_Verwaltungsgemeinschaft", "adv:AX_Grenzpunkt"))
                .build();
    }

    @Override
    public XtraServerMapping transform() {
        final List<FeatureTypeMapping> transformedFeatureTypeMappings = xtraServerMapping.getFeatureTypeMappings().stream()
                .map(ensureRelNavForFeatureType())
                .collect(Collectors.toList());

        return new XtraServerMappingBuilder()
                .featureTypeMappings(transformedFeatureTypeMappings)
                .build();
    }

    @Override
    public FeatureTypeMapping transform(FeatureTypeMapping featureTypeMapping) {
        return null;
    }

    private Function<FeatureTypeMapping, FeatureTypeMapping> ensureRelNavForFeatureType() {
        return featureTypeMapping -> {

            final List<MappingTable> transformedTables = featureTypeMapping.getPrimaryTables().stream()
                    .map(ensureRelNavForTable(featureTypeMapping.getName()))
                    .collect(Collectors.toList());

            return new FeatureTypeMappingBuilder()
                    .shallowCopyOf(featureTypeMapping)
                    .primaryTables(transformedTables)
                    .build();
        };
    }

    private Function<MappingTable, MappingTable> ensureRelNavForTable(final String featureTypeName) {
        return mappingTable -> {

            // recurse
            final List<MappingTable> transformedJoiningTables = mappingTable.getJoiningTables().stream()
                    .map(ensureRelNavForTable(featureTypeName))
                    .collect(Collectors.toList());

            final List<MappingTable> missingRelNavs = mappingTable.getValues().stream()
                    .filter(MappingValue::isReference)
                    .map(MappingValueReference.class::cast)
                    .map(createMissingRelNavs(mappingTable, featureTypeName))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            return new MappingTableBuilder()
                    .shallowCopyOf(mappingTable)
                    //.clearJoiningTables()
                    .values(mappingTable.getValues())
                    .joinPaths(mappingTable.getJoinPaths())
                    .joiningTables(transformedJoiningTables)
                    .joiningTables(missingRelNavs)
                    .build();
        };
    }

    private Function<MappingValueReference, List<MappingTable>> createMissingRelNavs(MappingTable mappingTable, final String featureTypeName) {
        return refValue -> {

            // TODO: can be multiple joins, join target should always match table target
            final Optional<MappingJoin> refJoin = mappingTable.getJoinPaths().stream()
                    .filter(mappingJoin -> mappingJoin.getTargetPath().equals(refValue.getReferencedTarget()))
                    .findFirst();

            final Stream<Optional<FeatureTypeMapping>> refMappingStream;
            if (replaceAssociationTarget.containsKey(featureTypeName + "/" + refValue.getReferencedTarget())) {
                refMappingStream = replaceAssociationTarget.get(featureTypeName + "/" + refValue.getReferencedTarget()).stream()
                        .map(xtraServerMapping::getFeatureTypeMapping);
            } else {
                refMappingStream = Stream.of(xtraServerMapping.getFeatureTypeMapping(refValue.getReferencedFeatureType()));
            }

            return refMappingStream
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    // join is not connected to FeatureType
                    .filter(refMapping -> !refJoin.isPresent() || !refMapping.getPrimaryTableNames().contains(refJoin.get().getTargetTable()))
                    .flatMap(refMapping -> createMissingRelNav(mappingTable.getName(), refValue, refMapping, !refJoin.isPresent()).stream())
                    .collect(Collectors.toList());
        };
    }

    private List<MappingTable> createMissingRelNav(final String sourceTable, final MappingValueReference refValue, final FeatureTypeMapping refMapping, final boolean isOneToOneRel) {

        final List<MappingTable> missingRelNavs = new ArrayList<>();

        final String sourceField = refValue.getValueColumn().orElse(refValue.getValue());

        // special case reference without join, add not-null predicate for optimization
        if (isOneToOneRel) {
            MappingTable mappingTable = new MappingTableBuilder()
                    .name(sourceTable)
                    .targetPath(refValue.getReferencedTarget())
                    .predicate("$T$." + sourceField + " IS NOT NULL")
                    .description("navrel")
                    .build();

            missingRelNavs.add(mappingTable);
        }

        // find id mappings for referenced object, use as join targets
        //Map<MappingTable, MappingValue> refMappingIds = refMapping.getTableValuesForPath("@gml:id");
        ImmutableMap.Builder<MappingTable, MappingValue> refMappingIds = new ImmutableMap.Builder<>();

        xtraServerMapping.getFeatureTypeMappingInheritanceChain(refMapping.getName()).stream()
                .flatMap(featureTypeMapping -> featureTypeMapping.getTableValuesForPath("@gml:id").entrySet().stream())
                .filter(mappingTableMappingValueEntry -> refMapping.getPrimaryTableNames().contains(mappingTableMappingValueEntry.getKey().getName()))
                .forEach(refMappingIds::put);


        refMappingIds.build().forEach((targetTable, refMappingId) -> {
            String targetField = refMappingId.getValueColumn().orElse(refMappingId.getValue());

            // TODO: add description: connection to ... for rel nav
            MappingJoin mappingJoin = new MappingJoinBuilder()
                    .targetPath(refValue.getReferencedTarget() + "/" + refValue.getReferencedFeatureType())
                    .joinCondition(new MappingJoinBuilder.ConditionBuilder().sourceTable(sourceTable).sourceField(sourceField).targetTable(targetTable.getName()).targetField(targetField).build())
                    .build();

            MappingTable mappingTable = new MappingTableBuilder()
                    .shallowCopyOf(targetTable)
                    .targetPath(mappingJoin.getTargetPath())
                    .joinPath(mappingJoin)
                    .build();


            missingRelNavs.add(mappingTable);
        });

        return missingRelNavs;

    }
}

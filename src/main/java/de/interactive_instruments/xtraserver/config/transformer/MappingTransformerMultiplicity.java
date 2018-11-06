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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import de.interactive_instruments.xtraserver.config.api.*;

import javax.xml.namespace.QName;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author zahnen
 */
public class MappingTransformerMultiplicity extends AbstractMappingTransformer {

    final ApplicationSchema applicationSchema;

    MappingTransformerMultiplicity(XtraServerMapping xtraServerMapping, final ApplicationSchema applicationSchema) {
        super(xtraServerMapping);
        this.applicationSchema = applicationSchema;
    }

    @Override
    protected MappingTableBuilder transformMappingTable(Context context, List<MappingTable> transformedMappingTables, List<MappingJoin> transformedMappingJoins, List<MappingValue> transformedMappingValues) {
        final FeatureTypeMapping featureTypeMapping = context.featureTypeMapping;
        final MappingTable mappingTable = context.mappingTable;

        List<MappingValue> transformedMappingValues2 = transformedMappingValues.stream()
                                                                               .sorted(Comparator.comparing(MappingValue::getTargetPath))
                                                                               .collect(new ForEachSelectIdCollector(featureTypeMapping.getQualifiedName(), mappingTable, transformedMappingTables));

        return new MappingTableBuilder()
                .shallowCopyOf(mappingTable)
                .values(transformedMappingValues2)
                .joiningTables(transformedMappingTables)
                .joinPaths(transformedMappingJoins);
    }

    private class ForEachSelectIdCollector implements Collector<MappingValue, List<MappingValue>, List<MappingValue>> {

        private final QName currentFeatureType;
        private final MappingTable currentTable;
        private final List<MappingTable> transformedMappingTables;
        private final Map<List<QName>, List<Integer>> selectIds;

        public ForEachSelectIdCollector(final QName currentFeatureType, final MappingTable currentTable, final List<MappingTable> transformedMappingTables) {
            this.currentFeatureType = currentFeatureType;
            this.currentTable = currentTable;
            this.transformedMappingTables = transformedMappingTables;
            this.selectIds = new LinkedHashMap<>();
        }

        @Override
        public Supplier<List<MappingValue>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<MappingValue>, MappingValue> accumulator() {
            return (values, mappingValue) -> {
                final Optional<MappingValue> first = values.stream()
                                                           .filter(value -> isMappingsForSameMultiplePathWithDifferentValues(value, mappingValue))
                                                           .sorted((mappingValue1, mappingValue2) -> mappingValue2.getSelectId() - mappingValue1.getSelectId())
                                                           .findFirst();

                if (first.isPresent()) {
                    MappingValue firstValue = first.get();
                    if (Objects.isNull(firstValue.getSelectId())) { // no select id
                        int i = values.indexOf(firstValue);
                        firstValue = new MappingValueBuilder()
                                .copyOf(firstValue)
                                .selectId(1) // set select id
                                .build();
                        values.set(i, firstValue); // set select id

                        selectIds.put(firstValue.getQualifiedTargetPath(), Lists.newArrayList(1));
                    }

                    int newId = firstValue.getSelectId() + 1;

                    final MappingValue mappingValue2 = new MappingValueBuilder()
                            .copyOf(mappingValue)
                            .selectId(newId) // set select id
                            .build();
                    values.add(mappingValue2);

                    selectIds.get(mappingValue.getQualifiedTargetPath())
                             .add(newId);
                } else {
                    values.add(mappingValue);
                }
            };
        }

        private boolean isMappingsForSameMultiplePathWithDifferentValues(MappingValue value1, MappingValue value2) {
            return value1.getTargetPath()
                         .equals(value2.getTargetPath())
                    && !value1.equals(value2)
                    && !applicationSchema.getLastMultiplePropertyPath(currentFeatureType, value2.getQualifiedTargetPath())
                                         .isEmpty();
        }

        @Override
        public BinaryOperator<List<MappingValue>> combiner() {
            return (left, right) -> {
                left.addAll(right);
                return left;
            };
        }

        @Override
        public Function<List<MappingValue>, List<MappingValue>> finisher() {
            return mappingValues -> {

                //TODO: only one multiplicity on path, merge spelling to geographicalName
                //TODO: copy all other values on same path
                final Map<List<QName>, List<Integer>> mergedSelectIds = mergeSelectIdsIfSharedPath(selectIds);

                // add for_each_select_id mapping
                mergedSelectIds.forEach((lastMultiplePropertyPath, value) -> {
                    transformedMappingTables.add(new MappingTableBuilder()
                            .name(currentTable.getName())
                            //TODO
                            .primaryKey(currentTable.getPrimaryKey())
                            .qualifiedTargetPath(lastMultiplePropertyPath)
                            .selectIds(Joiner.on(",")
                                             .join(value))
                            .description("multiplicity - for_each_select_id for " + applicationSchema.getNamespaces()
                                                                                                     .getPrefixedPath(lastMultiplePropertyPath))
                            .build()
                    );
                });


                return mappingValues;
            };
        }

        /**
         * merge for_each_select_ids if they share a subpath, e.g.: hy-n:geographicalName and hy-n:geographicalName/gn:GeographicalName/gn:spelling -> hy-n:geographicalName
         * @param selectIds
         * @return
         */
        private Map<List<QName>, List<Integer>> mergeSelectIdsIfSharedPath(final Map<List<QName>, List<Integer>> selectIds) {
            final Map<List<QName>, List<Integer>> mergedSelectIds = new HashMap<>();
            final List<List<QName>> ignoreSelectIds = new ArrayList<>();

            selectIds.forEach((key, value) -> {
                final List<QName> lastMultiplePropertyPath = applicationSchema.getLastMultiplePropertyPath(currentFeatureType, key);

                if (ignoreSelectIds.contains(lastMultiplePropertyPath)) return;

                final boolean[] merged = {false};

                List<Integer> mergedValue = mergedSelectIds.getOrDefault(lastMultiplePropertyPath, value).size() > value.size() ? mergedSelectIds.getOrDefault(lastMultiplePropertyPath, value) : value;

                selectIds.forEach((key2, value2) -> {
                    final List<QName> lastMultiplePropertyPath2 = applicationSchema.getLastMultiplePropertyPath(currentFeatureType, key2);

                    if (!Objects.equals(key, key2) && lastMultiplePropertyPath2.size() >= lastMultiplePropertyPath.size() && lastMultiplePropertyPath2.subList(0, lastMultiplePropertyPath.size())
                                                                                                                                                      .equals(lastMultiplePropertyPath)) {
                        ignoreSelectIds.add(lastMultiplePropertyPath2);
                        mergedSelectIds.put(lastMultiplePropertyPath, value2.size() > mergedValue.size() ? value2 : mergedValue);

                        merged[0] = true;
                    }
                });

                if (!merged[0]) {
                    mergedSelectIds.put(lastMultiplePropertyPath, mergedValue);
                }
            });

            return mergedSelectIds;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Characteristics.UNORDERED);
        }
    }
}

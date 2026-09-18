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
import de.interactive_instruments.xtraserver.config.api.Hints;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** @author zahnen */
public class MappingTransformerCloneColumns extends AbstractMappingTransformer {

  private final VirtualTablesHelper virtualTables;
  // seeded from the input, then grown per wrapped table: without this two feature types that clone
  // columns on the same physical table both get vrt_<table> and the later one replaces the earlier
  private final Set<String> usedVirtualNames;

  MappingTransformerCloneColumns(XtraServerMapping xtraServerMapping) {
    super(xtraServerMapping);
    virtualTables = new VirtualTablesHelper();
    usedVirtualNames =
        xtraServerMapping.getVirtualTables().stream()
            .map(VirtualTable::getName)
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  protected XtraServerMappingBuilder transformXtraServerMapping(
      Context context, List<FeatureTypeMapping> transformedFeatureTypeMappings) {
    return new XtraServerMappingBuilder()
        .copyOf(context.xtraServerMapping)
        .virtualTables(
            virtualTables.getVirtualTables().values().stream()
                .map(VirtualTable.Builder::build)
                .collect(Collectors.toList()))
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

    List<MappingValue> transformedMappingValuesWithClones =
        transformedMappingValues.stream().collect(new CloneCollector());

    MappingTableBuilder mappingTableBuilder =
        super.transformMappingTable(
            context,
            transformedMappingTables,
            transformedMappingJoins,
            transformedMappingValuesWithClones);

    boolean hasClone =
        transformedMappingValuesWithClones.stream()
            .anyMatch(mappingValue -> mappingValue.getTransformationHints().containsKey(Hints.CLONE));

    if (hasClone) {
      final MappingTable tableWithClones = mappingTableBuilder.build();

      return virtualTables
          .from(
              tableWithClones,
              uniqueVirtualName(String.format("vrt_%s", tableWithClones.getName())))
          .getCurrentTable();
    }

    return mappingTableBuilder;
  }

  private String uniqueVirtualName(String candidate) {
    if (usedVirtualNames.add(candidate)) {
      return candidate;
    }

    int i = 2;
    while (!usedVirtualNames.add(candidate + "_" + i)) {
      i++;
    }

    return candidate + "_" + i;
  }

  private class CloneCollector extends AbstractMappingValueCollector {

    @Override
    protected void accumulate(List<MappingValue> values, MappingValue currentValue) {
      final Optional<MappingValue> optionalCloneableValue =
          values.stream()
              .filter(
                  value ->
                      Objects.equals(value.getValue(), currentValue.getValue())
                          && Objects.equals(value.getTargetPath(), currentValue.getTargetPath()))
              .reduce((first, second) -> second); // findLast

      if (optionalCloneableValue.isPresent()) {
        MappingValue cloneableValue = optionalCloneableValue.get();
        if (!cloneableValue.getTransformationHints().containsKey(Hints.CLONE)) { // no select id
          int i = values.indexOf(cloneableValue);
          cloneableValue =
              new MappingValueBuilder()
                  .copyOf(cloneableValue)
                  .transformationHint(Hints.CLONE, "1") // set select id
                  .build();
          values.set(i, cloneableValue); // set select id
        }

        final MappingValue clonedValue =
            new MappingValueBuilder()
                .copyOf(currentValue)
                .transformationHint(
                    Hints.CLONE,
                    String.valueOf(
                        Integer.parseInt(cloneableValue.getTransformationHints().get(Hints.CLONE))
                            + 1)) // set select id
                .build();
        values.add(clonedValue);
      } else {
        values.add(currentValue);
      }
    }
  }
}

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

import com.google.common.base.Strings;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * XtraServer does not support a feature type having several primary tables that share a physical
 * table name and differ only in their predicate. {@code JaxbWriter} writes a predicated primary
 * table as {@code <Table table_name="t[predicate]"/>}, so such a group emits several rows whose
 * base table name is identical.
 *
 * <p>This transformer detects those groups and turns every member into its own {@link VirtualTable},
 * so the main mapping file references distinct names ({@code $vrt_t_1$}, {@code $vrt_t_2$}) and each
 * predicate moves into that virtual table's SQL WHERE clause.
 *
 * <p>Besides the writer, this also removes the input for three name-keyed lookups that silently
 * collapse the variants today: {@code MappingTransformerFanOutInheritance} drops all but the first
 * same-named table, {@code FeatureTypeMapping.getTable(String)} resolves by name only, and the
 * generated {@code _xsv_tmp_} name repeats the table name.
 *
 * @author zahnen
 */
public class MappingTransformerPredicateVariants extends AbstractMappingTransformer {

  private final VirtualTablesHelper virtualTables;
  private final Set<String> reservedNames;

  MappingTransformerPredicateVariants(final XtraServerMapping xtraServerMapping) {
    super(xtraServerMapping);
    this.virtualTables = new VirtualTablesHelper();
    // seeded from the input so we never collide with a virtual table that already exists, e.g. one
    // MappingTransformerMergeTables or MappingTransformerCloneColumns just created
    this.reservedNames =
        xtraServerMapping.getVirtualTables().stream()
            .map(VirtualTable::getName)
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  protected XtraServerMappingBuilder transformXtraServerMapping(
      final Context context, final List<FeatureTypeMapping> transformedFeatureTypeMappings) {
    // copyOf carries the incoming virtual tables and virtualTables(..) appends to them
    return new XtraServerMappingBuilder()
        .copyOf(context.xtraServerMapping)
        .virtualTables(
            virtualTables.getVirtualTables().values().stream()
                .map(VirtualTable.Builder::build)
                .collect(Collectors.toList()))
        .featureTypeMappings(transformedFeatureTypeMappings);
  }

  @Override
  protected FeatureTypeMappingBuilder transformFeatureTypeMapping(
      final Context context, final List<MappingTable> transformedMappingTables) {
    return new FeatureTypeMappingBuilder()
        .shallowCopyOf(context.featureTypeMapping)
        .primaryTables(splitPredicateVariants(context, transformedMappingTables));
  }

  private List<MappingTable> splitPredicateVariants(
      final Context context, final List<MappingTable> primaryTables) {
    final Map<String, List<MappingTable>> byName =
        primaryTables.stream()
            .collect(
                Collectors.groupingBy(
                    MappingTable::getName, LinkedHashMap::new, Collectors.toList()));

    // no name occurs twice, nothing to do
    if (byName.size() == primaryTables.size()) {
      return primaryTables;
    }

    // iterate the original list so the order of the emitted <Table> rows is preserved
    final List<MappingTable> transformedPrimaryTables = new ArrayList<>(primaryTables.size());

    for (final MappingTable primaryTable : primaryTables) {
      if (needsSplit(context, byName.get(primaryTable.getName()))) {
        transformedPrimaryTables.add(
            virtualTables
                .from(primaryTable, nextVirtualName(primaryTable.getName()))
                .getCurrentTable()
                .build());
      } else {
        transformedPrimaryTables.add(primaryTable);
      }
    }

    return transformedPrimaryTables;
  }

  private boolean needsSplit(final Context context, final List<MappingTable> group) {
    if (group.size() < 2) {
      return false;
    }

    final String name = group.get(0).getName();

    // nothing is ambiguous without a predicate
    if (group.stream().noneMatch(table -> !Strings.isNullOrEmpty(table.getPredicate()))) {
      return false;
    }

    // already a virtual table reference, so already distinct by construction
    if (isVirtualTableReference(name)) {
      return false;
    }

    if (!group.stream().allMatch(MappingTable::isPrimary)) {
      warn(context, name, "the group is not made up of primary tables only");
      return false;
    }

    // VirtualTable.Builder.originalTable only harvests join key columns from joined children, so a
    // merged child would end up joining against a column that is not in the SELECT
    if (group.stream().anyMatch(MappingTransformerPredicateVariants::hasMergedJoiningTable)) {
      warn(context, name, "a member still has a merged joining table");
      return false;
    }

    return true;
  }

  private String nextVirtualName(final String tableName) {
    int i = 1;
    String virtualName;

    do {
      virtualName = String.format("vrt_%s_%d", tableName, i++);
    } while (!reservedNames.add(virtualName));

    return virtualName;
  }

  private static boolean hasMergedJoiningTable(final MappingTable mappingTable) {
    return mappingTable.getJoiningTables().stream().anyMatch(MappingTable::isMerged);
  }

  private static boolean isVirtualTableReference(final String tableName) {
    return tableName.startsWith("$") && tableName.endsWith("$");
  }

  private static void warn(final Context context, final String tableName, final String reason) {
    System.out.println(
        "WARNING: feature type "
            + context.featureTypeMapping.getName()
            + " has several primary tables named "
            + tableName
            + " that differ only in their predicate, but they were not split into virtual tables "
            + "because "
            + reason
            + ". The generated mapping will contain duplicate table names.");
  }
}

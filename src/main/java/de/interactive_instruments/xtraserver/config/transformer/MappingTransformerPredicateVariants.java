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
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * XtraServer resolves tables by name when it builds the joins for a feature type, so a main table
 * that {@code JaxbWriter} writes as {@code <Table table_name="t[predicate]"/>} is ambiguous with
 * every other use of {@code t} - a second variant of {@code t} in the mapping is not needed for one
 * filter to leak into another query.
 *
 * <p>This transformer therefore gives every main table that carries a predicate a name of its own,
 * by turning it into a {@link VirtualTable} whose SQL WHERE clause holds the predicate. Tables that
 * share a name with such a table follow, even when they carry no predicate themselves: leaving one
 * of them under the bare name would put that name back beside the filtered variants.
 *
 * <p>Joined tables get the same treatment, for a sharper reason: {@code JaxbWriter} writes the
 * predicate into the {@code <Table>} row but never into the {@code <Join>} row's join path, so the
 * two rows describing one joined table disagree on its name and XtraServer resolves the join by the
 * bare one.
 *
 * <p>Moving the predicates out also removes the input for three name-keyed lookups that silently
 * collapse same-named variants: {@code MappingTransformerFanOutInheritance} drops all but the first
 * same-named table, {@code FeatureTypeMapping.getTable(String)} resolves by name only, and the
 * generated {@code _xsv_tmp_} name repeats the table name.
 *
 * @author zahnen
 */
public class MappingTransformerPredicateVariants extends AbstractMappingTransformer {

  private final VirtualTablesHelper virtualTables;
  private final Map<String, VirtualTable> virtualTablesByName;
  private final Set<String> reservedNames;

  MappingTransformerPredicateVariants(final XtraServerMapping xtraServerMapping) {
    super(xtraServerMapping);
    this.virtualTables = new VirtualTablesHelper();
    this.virtualTablesByName =
        xtraServerMapping.getVirtualTables().stream()
            .collect(
                Collectors.toMap(
                    VirtualTable::getName, Function.identity(), (a, b) -> b, LinkedHashMap::new));
    // seeded from the input so we never collide with a virtual table that already exists, e.g. one
    // MappingTransformerMergeTables or MappingTransformerCloneColumns just created
    this.reservedNames = new LinkedHashSet<>(virtualTablesByName.keySet());
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

  @Override
  protected MappingTableBuilder transformMappingTable(
      final Context context,
      final List<MappingTable> transformedMappingTables,
      final List<MappingJoin> transformedMappingJoins,
      final List<MappingValue> transformedMappingValues) {
    final MappingTableBuilder rebuilt =
        super.transformMappingTable(
            context, transformedMappingTables, transformedMappingJoins, transformedMappingValues);

    warnAmbiguousPredicateRows(context, transformedMappingTables);

    if (!needsVirtualTable(context, context.mappingTable)) {
      return rebuilt;
    }

    // the traversal rewrote the children on the way up, so the virtual table has to be built from
    // the rebuilt node rather than from the one the context still points at
    final MappingTable joinedTable = rebuilt.build();

    return isVirtualTableReference(joinedTable.getName())
        ? clonedVirtualTable(context, joinedTable)
        : virtualTables
            .fromJoined(joinedTable, nextVirtualName(joinedTable.getName()))
            .getCurrentTable();
  }

  private boolean needsVirtualTable(final Context context, final MappingTable mappingTable) {
    if (!mappingTable.isJoined() || !hasPredicate(mappingTable)) {
      return false;
    }

    if (isConstantPredicate(mappingTable.getPredicate())) {
      return false;
    }

    // only the end of a join path moves onto the virtual table, so a path that reaches a table of
    // the same name earlier on cannot be retargeted without breaking the chain
    if (mappingTable.getJoinPaths().stream().anyMatch(passesThrough(mappingTable.getName()))) {
      warnJoined(
          context,
          mappingTable.getName(),
          "its join path reaches a table of the same name more than once");
      return false;
    }

    return true;
  }

  private static Predicate<MappingJoin> passesThrough(final String tableName) {
    return join ->
        join.getJoinConditions().subList(0, join.getJoinConditions().size() - 1).stream()
            .anyMatch(
                condition ->
                    tableName.equals(condition.getSourceTable())
                        || tableName.equals(condition.getTargetTable()));
  }

  /**
   * Two filters on the same table under the same target path are the one shape XtraServer cannot
   * tell apart. Different target paths scope each filter to their own property, so those are left
   * alone - see the predicate row discussion in the change that introduced this warning.
   */
  private static void warnAmbiguousPredicateRows(
      final Context context, final List<MappingTable> children) {
    children.stream()
        .filter(child -> child.isPredicate() && child.getJoinPaths().isEmpty())
        .collect(
            Collectors.groupingBy(
                child -> child.getName() + " " + child.getTargetPath(),
                LinkedHashMap::new,
                Collectors.mapping(MappingTable::getPredicate, Collectors.toSet())))
        .forEach(
            (nameAndTarget, predicates) -> {
              if (predicates.size() > 1) {
                System.out.println(
                    "WARNING: feature type "
                        + context.featureTypeMapping.getName()
                        + " filters "
                        + nameAndTarget
                        + " "
                        + predicates.size()
                        + " different ways under one target path, which XtraServer cannot tell"
                        + " apart.");
              }
            });
  }

  private List<MappingTable> splitPredicateVariants(
      final Context context, final List<MappingTable> primaryTables) {
    final Map<String, List<MappingTable>> byName =
        primaryTables.stream()
            .collect(
                Collectors.groupingBy(
                    MappingTable::getName, LinkedHashMap::new, Collectors.toList()));

    final Set<String> filteredNames =
        byName.entrySet().stream()
            .filter(entry -> needsVirtualTables(context, entry.getKey(), entry.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toCollection(LinkedHashSet::new));

    // map the original list so the order of the emitted <Table> rows is preserved
    return primaryTables.stream()
        .map(
            primaryTable ->
                filteredNames.contains(primaryTable.getName())
                    ? asVirtualTable(context, primaryTable)
                    : primaryTable)
        .collect(Collectors.toList());
  }

  private boolean needsVirtualTables(
      final Context context, final String name, final List<MappingTable> group) {
    if (group.stream().noneMatch(MappingTransformerPredicateVariants::hasPredicate)) {
      return false;
    }

    // a root with a target path is bound to the feature instance table by name alone, so renaming
    // the instance tables of the group would leave it addressing a table that no longer exists
    if (!group.stream().allMatch(MappingTable::isPrimary)) {
      warn(context, name, "the group is not made up of primary tables only");
      return false;
    }

    return true;
  }

  private MappingTable asVirtualTable(final Context context, final MappingTable primaryTable) {
    if (isVirtualTableReference(primaryTable.getName())) {
      // the definition behind the reference already has a name of its own, so only a predicate
      // still sitting on the reference has to be moved
      return hasPredicate(primaryTable)
          ? withClonedVirtualTable(context, primaryTable)
          : primaryTable;
    }

    return virtualTables
        .from(primaryTable, nextVirtualName(primaryTable.getName()))
        .getCurrentTable()
        .build();
  }

  private MappingTable withClonedVirtualTable(
      final Context context, final MappingTable primaryTable) {
    return clonedVirtualTable(context, primaryTable).build();
  }

  private MappingTableBuilder clonedVirtualTable(
      final Context context, final MappingTable mappingTable) {
    final String referencedName = mappingTable.getName().replaceAll("\\$", "");
    final VirtualTable referenced = virtualTablesByName.get(referencedName);

    if (referenced == null) {
      warn(context, mappingTable.getName(), "there is no virtual table named " + referencedName);
      return new MappingTableBuilder().copyOf(mappingTable);
    }

    return virtualTables
        .cloneOf(mappingTable, referenced, nextVariantName(referencedName))
        .getCurrentTable();
  }

  private String nextVirtualName(final String tableName) {
    return nextVariantName(String.format("vrt_%s", tableName));
  }

  private String nextVariantName(final String baseName) {
    int i = 1;
    String virtualName;

    do {
      virtualName = String.format("%s_%d", baseName, i++);
    } while (!reservedNames.add(virtualName));

    return virtualName;
  }

  /**
   * {@code MappingTransformerRelationNavigability} wraps the value a reference is mapped from in
   * "(..) IS NOT NULL", and that value is a constant whenever the reference is. Such a predicate
   * filters nothing, so a virtual table for it would only add a query. Matching that one generator's
   * shape rather than testing for a column reference keeps hand written filters like
   * {@code zus IS NULL}, which name their column bare, out of it.
   */
  private static final Pattern CONSTANT_PREDICATE =
      Pattern.compile(
          "\\(\\s*(?:'[^']*'|\\{\\$[^}]*\\}|[+-]?[0-9]+(?:\\.[0-9]+)?)\\s*\\)\\s+IS\\s+(?:NOT\\s+)?NULL",
          Pattern.CASE_INSENSITIVE);

  private static boolean isConstantPredicate(final String predicate) {
    return CONSTANT_PREDICATE.matcher(predicate.trim()).matches();
  }

  private static boolean hasPredicate(final MappingTable mappingTable) {
    return !Strings.isNullOrEmpty(mappingTable.getPredicate());
  }

  private static boolean isVirtualTableReference(final String tableName) {
    return tableName.startsWith("$") && tableName.endsWith("$");
  }

  private static void warnJoined(
      final Context context, final String tableName, final String reason) {
    System.out.println(
        "WARNING: feature type "
            + context.featureTypeMapping.getName()
            + " has a filtered joined table "
            + tableName
            + " that was not turned into a virtual table because "
            + reason
            + ". Its filter stays in the table name, where the join path cannot see it.");
  }

  private static void warn(final Context context, final String tableName, final String reason) {
    System.out.println(
        "WARNING: feature type "
            + context.featureTypeMapping.getName()
            + " has a filtered main table "
            + tableName
            + " that was not turned into a virtual table because "
            + reason
            + ". Its filter stays in the table name, where XtraServer may apply it to another"
            + " query over the same table.");
  }
}

package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.collect.ImmutableSet;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.VirtualTable.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VirtualTablesHelper {

  private final Map<String, Builder> virtualTables;

  private String currentVirtualName;
  private Builder currentVirtualTable;
  private String currentName;
  private MappingTableBuilder currentTable;

  public VirtualTablesHelper() {
    this.virtualTables = new LinkedHashMap<>();
  }

  public Map<String, Builder> getVirtualTables() {
    return virtualTables;
  }

  public MappingTableBuilder getCurrentTable() {
    return currentTable;
  }

  public VirtualTablesHelper from(MappingTable mappingTable) {
    return from(mappingTable, String.format("vrt_%s", mappingTable.getName()));
  }

  /**
   * Same as {@link #from(MappingTable)}, but with an explicit virtual table name. Needed when
   * several tables share a base name and would otherwise all become {@code vrt_<name>} - the
   * name-keyed maps here and in {@code XtraServerMappingBuilder} would silently drop all but one.
   */
  public VirtualTablesHelper from(MappingTable mappingTable, String virtualTableName) {
    this.currentVirtualName = virtualTableName;
    this.currentVirtualTable = VirtualTable.builder();
    this.currentName = mappingTable.getName();
    this.currentTable = new MappingTableBuilder().shallowCopyOf(mappingTable);

    currentVirtualTable.wrappedTable(mappingTable);
    currentVirtualTable.name(currentVirtualName);
    currentVirtualTable.primaryTable(currentName);
    virtualTables.put(currentVirtualName, currentVirtualTable);

    currentTable.name(String.format("$%s$", currentVirtualName));
    currentTable.predicate(null);
    currentTable.primaryKey(currentVirtualTable.applyAliasIfNecessary(currentName, new MappingValueBuilder().column().value(mappingTable.getPrimaryKey()).targetPath("FOO").build()).getValue());

    return this.values(mappingTable.getValues())
        .joinPaths(mappingTable.getJoinPaths())
        .joiningTables(mappingTable.getJoiningTables());
  }

  /**
   * Same as {@link #from(MappingTable, String)} for a joined table. Its join path reaches it from
   * the outside and stays there; only the far end of that join moves onto the virtual table, under
   * the column name the query exposes.
   */
  public VirtualTablesHelper fromJoined(MappingTable mappingTable, String virtualTableName) {
    this.currentVirtualName = virtualTableName;
    this.currentVirtualTable = VirtualTable.builder();
    this.currentName = mappingTable.getName();
    this.currentTable = new MappingTableBuilder().shallowCopyOf(mappingTable);

    // registers the join key in the alias map, which incomingJoinPaths reads back below
    currentVirtualTable.wrappedJoinedTable(mappingTable);
    currentVirtualTable.name(currentVirtualName);
    currentVirtualTable.primaryTable(currentName);
    virtualTables.put(currentVirtualName, currentVirtualTable);

    currentTable.name(String.format("$%s$", currentVirtualName));
    currentTable.predicate(null);
    currentTable.primaryKey(currentVirtualTable.applyAliasIfNecessary(currentName, new MappingValueBuilder().column().value(mappingTable.getPrimaryKey()).targetPath("FOO").build()).getValue());

    return this.values(mappingTable.getValues())
        .incomingJoinPaths(mappingTable.getJoinPaths())
        .joiningTables(mappingTable.getJoiningTables());
  }

  /**
   * The join now ends on the virtual table, so besides the table its last condition also has to
   * name the column under which that table exposes the join key. Only the last condition is
   * retargeted - the hops before it never touch the wrapped table.
   */
  private VirtualTablesHelper incomingJoinPaths(ImmutableSet<MappingJoin> joinPaths) {

    currentTable.joinPaths(
        joinPaths.stream()
            .map(
                jp ->
                    new MappingJoinBuilder()
                        .shallowCopyOf(jp)
                        .joinConditions(retargetedToVirtualTable(jp.getJoinConditions()))
                        .build())
            .collect(Collectors.toList()));

    return this;
  }

  private List<MappingJoin.Condition> retargetedToVirtualTable(
      final List<MappingJoin.Condition> joinConditions) {
    final List<MappingJoin.Condition> retargeted = new ArrayList<>(joinConditions);
    final int last = retargeted.size() - 1;
    final MappingJoin.Condition end = retargeted.get(last);

    retargeted.set(
        last,
        new MappingJoinBuilder.ConditionBuilder()
            .copyOf(end)
            .targetTable(String.format("$%s$", currentVirtualName))
            .targetField(
                currentVirtualTable
                    .aliases()
                    .getWithAlias(end.getTargetTable(), end.getTargetField(), null))
            .build());

    return retargeted;
  }

  /**
   * Same as {@link #from(MappingTable, String)} for a table that already references a virtual
   * table. Building a new definition around the reference would nest the query
   * ({@code SELECT ... FROM $vrt_x$}), and appending the predicate to the referenced definition
   * would change it for every other mapping using it, so the definition is copied first.
   */
  public VirtualTablesHelper cloneOf(
      MappingTable mappingTable, VirtualTable referenced, String virtualTableName) {
    this.currentVirtualName = virtualTableName;
    this.currentVirtualTable = VirtualTable.builder().from2(referenced).name(virtualTableName);
    this.currentName = mappingTable.getName();
    this.currentTable = new MappingTableBuilder().shallowCopyOf(mappingTable);

    currentVirtualTable.addWhereClause(referenced.resolvePredicate(mappingTable.getPredicate()));
    virtualTables.put(currentVirtualName, currentVirtualTable);

    currentTable.name(String.format("$%s$", currentVirtualName));
    currentTable.predicate(null);
    // the values already address the columns of the copied definition, so the alias pass of
    // from(..) would suffix an already suffixed column a second time
    currentTable.values(mappingTable.getValues());

    return this.joinPaths(mappingTable.getJoinPaths())
        .joiningTables(mappingTable.getJoiningTables());
  }

  public VirtualTablesHelper values(Collection<MappingValue> values) {

    currentTable.values(
        values.stream()
            .map(value -> currentVirtualTable.applyAliasIfNecessary(currentName, value))
            .collect(Collectors.toList()));

    return this;
  }

  private VirtualTablesHelper joinPaths(ImmutableSet<MappingJoin> joinPaths) {

    currentTable.joinPaths(
        joinPaths.stream()
            .map(
                jp ->
                    new MappingJoinBuilder()
                        .shallowCopyOf(jp)
                        .joinConditions(
                            jp.getJoinConditions().stream()
                                .map(
                                    jc ->
                                        jc.getTargetTable().equals(currentName)
                                            ? new MappingJoinBuilder.ConditionBuilder()
                                                .copyOf(jc)
                                                .targetTable(
                                                    String.format("$%s$", currentVirtualName))
                                                .build()
                                            : jc)
                                .collect(Collectors.toList()))
                        .build())
            .collect(Collectors.toList()));

    return this;
  }

  private VirtualTablesHelper joiningTables(ImmutableSet<MappingTable> joiningTables) {

    currentTable.joiningTables(
        joiningTables.stream()
            .map(
                jt ->
                    new MappingTableBuilder()
                        .shallowCopyOf(jt)
                        // a child without join paths (predicate / for_each_select_id table) that
                        // shares the parent's name would otherwise be written out under the base
                        // name again, re-introducing the row we just replaced
                        .name(
                            jt.getName().equals(currentName)
                                ? String.format("$%s$", currentVirtualName)
                                : jt.getName())
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
                                                            jc.getSourceTable().equals(currentName)
                                                                ? new MappingJoinBuilder
                                                                        .ConditionBuilder()
                                                                    .copyOf(jc)
                                                                    .sourceTable(
                                                                        String.format(
                                                                            "$%s$",
                                                                            currentVirtualName))
                                                                    .build()
                                                                : jc)
                                                    .collect(Collectors.toList()))
                                            .build())
                                .collect(Collectors.toList()))
                        .build())
            .collect(Collectors.toList()));

    return this;
  }
}

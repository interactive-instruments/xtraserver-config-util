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
import java.util.Collection;
import java.util.LinkedHashMap;
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

    currentVirtualTable.originalTable(mappingTable);
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

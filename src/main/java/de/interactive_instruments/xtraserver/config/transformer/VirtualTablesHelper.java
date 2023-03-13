package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.collect.ImmutableSet;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
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
    this.currentVirtualName = String.format("vrt_%s", mappingTable.getName());
    this.currentVirtualTable = VirtualTable.builder();
    this.currentName = mappingTable.getName();
    this.currentTable = new MappingTableBuilder().shallowCopyOf(mappingTable);

    currentVirtualTable.originalTable(mappingTable);
    currentVirtualTable.name(currentVirtualName);
    //NOTE: is derived from joinPaths now
    //currentVirtualTable.primaryTable(currentName);
    virtualTables.put(currentVirtualName, currentVirtualTable);

    currentTable.name(String.format("$%s$", currentVirtualName));
    currentTable.predicate(null);

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

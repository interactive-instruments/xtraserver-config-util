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
package de.interactive_instruments.xtraserver.config.api;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import org.immutables.value.Value;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zahnen
 */
@Value.Immutable
public abstract class VirtualTable {

    public abstract String getName();

    @Value.Derived
    public String getQuery() {
        String query = null;

        if (!getJoinPaths().isEmpty()) {
            final String primaryTable = getJoinPaths().iterator()
                                                      .next()
                                                      .getSourceTable();
            if (getJoinPaths().stream()
                              .anyMatch(mappingJoin -> !mappingJoin.getSourceTable()
                                                                   .equals(primaryTable))) {
                System.out.println("WARNING: joins for VirtualTable " + getName() + " have differing source tables");
            }
            Set<String> columns = new ImmutableSet.Builder<String>().add(getPrimaryKeyColumns().iterator().next())
                                                                    .addAll(getColumns())
                                                                    .build();

            query = "SELECT ";
            query += Joiner.on(",")
                           .join(columns) + " FROM " + primaryTable + " ";
            query += getJoinPaths().stream()
                                   .flatMap(mappingJoin -> mappingJoin.getJoinConditions()
                                                                      .stream())
                                   .map(condition -> "INNER JOIN " + condition.getTargetTable() + " ON " + condition.getTargetTable() + "." + condition.getTargetField() + " = " + condition.getSourceTable() + "." + condition.getSourceField() + " ")
                                   .distinct()
                                   .collect(Collectors.joining());
            if (getWhereClause().isPresent()) {
                query += "WHERE " + getWhereClause().get();
            }
        }

        return query;
    }

    protected abstract Set<MappingJoin> getJoinPaths();

    protected abstract Set<String> getColumns();

    protected abstract Set<String> getPrimaryKeyColumns();

    protected abstract Optional<String> getWhereClause();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ImmutableVirtualTable.Builder {
        private boolean noTables = true;

        public Builder originalTable(final MappingTable mappingTable) {
            this.addAllJoinPaths(mappingTable.getJoinPaths());

            if (noTables) {
                this.noTables = false;

                this.addPrimaryKeyColumns(mappingTable.getName() + "." + mappingTable.getPrimaryKey());

                if (mappingTable.getPredicate() != null) {
                    this.whereClause(mappingTable.getPredicate()
                                                 .replaceAll("\\$T\\$", mappingTable.getName()));
                    //whereClause = mappingTable.getName() + "." + mappingTable.getPredicate().replaceAll("( or | and )", "$1" + mappingTable.getName() + ".");
                }
            }

            mappingTable.getValues()
                        .stream()
                        .flatMap(mappingValue -> mappingValue.getValueColumns()
                                                             .stream())
                        .map(column -> mappingTable.getName() + "." + column)
                        .forEach(this::addColumns);

            mappingTable.getJoiningTables().stream()
                        .filter(MappingTable::isJoined)
                        .flatMap(mappingTable1 -> mappingTable1.getJoinPaths().stream())
                        .map(mappingJoin -> mappingJoin.getJoinConditions().stream().findFirst())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(condition -> condition.getSourceTable()  + "." + condition.getSourceField())
                        .forEach(this::addColumns);
            
            return this;
        }
    }
}

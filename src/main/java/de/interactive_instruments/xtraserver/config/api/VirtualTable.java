/**
 * Copyright 2020 interactive instruments GmbH
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

import static de.interactive_instruments.xtraserver.config.transformer.MappingTransformerJoinTypeHint.HINT_JOIN_TYPE;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.interactive_instruments.xtraserver.config.transformer.MappingValueAliases;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.immutables.value.Value.Default;

/**
 * @author zahnen
 */
@Value.Immutable
public abstract class VirtualTable {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ImmutableVirtualTable.Builder {
        private boolean noTables = true;
        private MappingValueAliases mappingValueAliases = new MappingValueAliases();

        public MappingValue applyAliasIfNecessary(String table, MappingValue value) {
            return mappingValueAliases.applyAliasIfNecessary(table, value);
        }

        public Builder aliases(MappingValueAliases mappingValueAliases) {
            this.mappingValueAliases = mappingValueAliases;
            return this;
        }

        public MappingValueAliases aliases() {
            return mappingValueAliases;
        }

        public final VirtualTable.Builder from2(VirtualTable instance) {
            this.noTables = false;
            return from(instance);
        }

        public Builder originalTable(final MappingTable mappingTable) {
            this.addAllJoinPaths(mappingTable.getJoinPaths());

            if (noTables) {
                this.noTables = false;

                this.primaryTable(mappingTable.getName());
                this.addPrimaryKeyColumns(mappingValueAliases.getWithAsAlias(mappingTable.getName(), mappingTable.getPrimaryKey(),null));

                if (mappingTable.getPredicate() != null) {
                    this.whereClause(mappingTable.getPredicate()
                                                 .replaceAll("\\$T\\$", mappingTable.getName()));
                    //whereClause = mappingTable.getName() + "." + mappingTable.getPredicate().replaceAll("( or | and )", "$1" + mappingTable.getName() + ".");
                }
            }

            // workaround for XtraServer issue, booleans in virtual tables are not returned as 't' or 'f'
            mappingTable.getValues()
                        .stream()
                        .filter(this::isBooleanClassification)
                        .flatMap(mappingValue -> mappingValue.getValueColumns()
                                                 .stream().map(column -> "CASE WHEN " + mappingTable.getName() + "." + column + " THEN 't' ELSE 'f' END AS " + column))
                        .forEach(this::addColumns);

            mappingTable.getValues()
                        .stream()
                        .filter(mappingValue -> !isBooleanClassification(mappingValue))
                        .flatMap(mappingValue -> mappingValue.getValueColumns()
                                                          .stream()
                                                          .map(column -> mappingValueAliases.getWithAsAlias(mappingTable.getName(), column, mappingValue.getTransformationHints().get(Hints.CLONE))))
                        .forEach(this::addColumns);

            /*mappingTable.getJoinPaths().stream()
                        .map(mappingJoin -> mappingJoin.getJoinConditions().stream().findFirst())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(condition -> condition.getTargetTable()  + "." + condition.getTargetField())
                        .forEach(this::addColumns);*/

            mappingTable.getJoiningTables().stream()
                        .filter(MappingTable::isJoined)
                        .flatMap(mappingTable1 -> mappingTable1.getJoinPaths().stream())
                        .map(mappingJoin -> mappingJoin.getJoinConditions().stream().findFirst())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        //.flatMap(condition -> Stream.of(condition.getSourceTable()  + "." + condition.getSourceField(), condition.getTargetTable()  + "." + condition.getTargetField()))
                        .map(condition -> mappingValueAliases.getWithAsAlias(condition.getSourceTable(), condition.getSourceField(),null))
                        .forEach(this::addColumns);

            return this;
        }

        private boolean isBooleanClassification(MappingValue mappingValue) {
            if (mappingValue.isClassification()) {
                List<String> keys = ((MappingValueClassification) mappingValue).getKeys();
                if (keys.size() == 2 && keys.contains("t") && keys.contains("f")) {
                    return true;
                }
            }
            return false;
        }
    }


    public abstract String getName();

    protected abstract Set<MappingJoin> getJoinPaths();

    protected abstract Set<String> getColumns();

    protected abstract Set<String> getPrimaryKeyColumns();

    protected abstract Optional<String> getWhereClause();

    @Value.Default
    protected String getPrimaryTable() {
        return getJoinPaths().iterator()
            .next()
            .getSourceTable();
    }

    @Value.Derived
    public String getQuery() {
        String query = null;

            final String primaryTable = getJoinPaths().isEmpty()
                    ? getPrimaryTable()
                    : getJoinPaths().iterator()
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
            //TODO: transformationHints for MappingJoin, JOIN_TYPE=LEFT
            query += getJoinPaths().stream()
                                   .flatMap(mappingJoin -> mappingJoin.getJoinConditions()
                                                                      .stream()
                                       .map(condition -> String
                                           .format("%s JOIN %s ON %s.%s = %s.%s ",
                                               mappingJoin.getTransformationHints().getOrDefault(HINT_JOIN_TYPE, "INNER"),
                                               condition.getTargetTable(),
                                               condition.getTargetTable(),
                                               condition.getTargetField(),
                                               condition.getSourceTable(),
                                               condition.getSourceField()))
                                   )
                                   .distinct()
                                   .collect(Collectors.joining());
            if (getWhereClause().isPresent()) {
                query += "WHERE " + getWhereClause().get();
            }

        return query;
    }
}

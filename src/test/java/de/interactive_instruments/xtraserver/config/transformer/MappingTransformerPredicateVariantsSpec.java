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
package de.interactive_instruments.xtraserver.config.transformer;

import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.Assertions.assertThat;

import com.greghaskins.spectrum.Spectrum;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.runner.RunWith;

/** Spectrum spec for MappingTransformerPredicateVariants */
@RunWith(Spectrum.class)
public class MappingTransformerPredicateVariantsSpec {

  {
    describe(
        "MappingTransformerPredicateVariants",
        () -> {
          context(
              "two primary tables with the same name and different predicates",
              () -> {
                XtraServerMapping given =
                    mapping(
                        primaryTable("o61001", "$T$.fkt = '1000'"),
                        primaryTable("o61001", "$T$.fkt = '2000'"));

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should replace both primary tables with distinct virtual table references",
                    () -> {
                      assertThat(primaryTableNames(transformed))
                          .containsExactly("$vrt_o61001_1$", "$vrt_o61001_2$");
                    });

                it(
                    "should create one virtual table per variant",
                    () -> {
                      assertThat(virtualTableNames(transformed))
                          .containsExactly("vrt_o61001_1", "vrt_o61001_2");
                    });

                it(
                    "should move each predicate into its own virtual table query, with $T$ resolved",
                    () -> {
                      assertThat(virtualTableQueries(transformed))
                          .containsExactly(
                              "SELECT o61001.id,o61001.objid FROM o61001 WHERE (o61001.fkt = '1000')",
                              "SELECT o61001.id,o61001.objid FROM o61001 WHERE (o61001.fkt = '2000')");
                    });

                it(
                    "should clear the predicate on both primary tables",
                    () -> {
                      assertThat(primaryTables(transformed))
                          .allSatisfy(table -> assertThat(table.getPredicate()).isNull());
                    });

                it(
                    "should keep both tables primary so they are still written as feature instance"
                        + " tables",
                    () -> {
                      assertThat(primaryTables(transformed))
                          .allSatisfy(table -> assertThat(table.isPrimary()).isTrue());
                    });

                it(
                    "should keep the primary key",
                    () -> {
                      assertThat(primaryTables(transformed))
                          .allSatisfy(table -> assertThat(table.getPrimaryKey()).isEqualTo("id"));
                    });
              });

          context(
              "two primary tables with the same name where only one has a predicate",
              () -> {
                XtraServerMapping given =
                    mapping(primaryTable("o61001", null), primaryTable("o61001", "fkt = '1000'"));

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should virtualise both, so that no two table names collide",
                    () -> {
                      assertThat(primaryTableNames(transformed))
                          .containsExactly("$vrt_o61001_1$", "$vrt_o61001_2$");
                    });

                it(
                    "should generate a query without a WHERE clause for the variant without a"
                        + " predicate",
                    () -> {
                      assertThat(virtualTableQueries(transformed).get(0)).doesNotContain("WHERE");
                      assertThat(virtualTableQueries(transformed).get(0))
                          .startsWith("SELECT o61001.id,o61001.objid FROM o61001");
                    });

                it(
                    "should generate a WHERE clause for the variant with a predicate",
                    () -> {
                      assertThat(virtualTableQueries(transformed).get(1))
                          .endsWith("WHERE (fkt = '1000')");
                    });
              });

          context(
              "a colliding primary table with a joined child",
              () -> {
                MappingJoin join =
                    new MappingJoinBuilder()
                        .targetPath("ft:child")
                        .joinCondition(
                            new MappingJoinBuilder.ConditionBuilder()
                                .sourceTable("o61001")
                                .sourceField("objid")
                                .targetTable("o02341")
                                .targetField("rid")
                                .build())
                        .build();

                MappingTable child =
                    new MappingTableBuilder()
                        .name("o02341")
                        .primaryKey("id")
                        .targetPath("ft:child")
                        .joinPath(join)
                        .value(value("position", "ft:child/ft:position"))
                        .build();

                XtraServerMapping given =
                    mapping(
                        new MappingTableBuilder()
                            .shallowCopyOf(primaryTable("o61001", "fkt = '1000'"))
                            .value(value("objid", "ft:objid"))
                            .joiningTable(child)
                            .build(),
                        primaryTable("o61001", "fkt = '2000'"));

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should retarget the child's join to the virtual table",
                    () -> {
                      MappingTable transformedChild =
                          primaryTables(transformed).get(0).getJoiningTables().asList().get(0);

                      assertThat(transformedChild.getJoinPaths().asList().get(0).getSourceTable())
                          .isEqualTo("$vrt_o61001_1$");
                    });

                it(
                    "should select the child's join source column in the virtual table",
                    () -> {
                      assertThat(virtualTableQueries(transformed).get(0))
                          .contains("o61001.objid")
                          .startsWith("SELECT o61001.id,");
                    });
              });

          context(
              "an input mapping that already contains a virtual table with a generated name",
              () -> {
                XtraServerMapping given =
                    new XtraServerMappingBuilder()
                        .copyOf(
                            mapping(
                                primaryTable("o61001", "fkt = '1000'"),
                                primaryTable("o61001", "fkt = '2000'")))
                        .virtualTable(
                            VirtualTable.builder()
                                .name("vrt_o61001_1")
                                .primaryTable("somewhere_else")
                                .addPrimaryKeyColumns("somewhere_else.id")
                                .build())
                        .build();

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should skip the reserved name instead of overwriting it",
                    () -> {
                      assertThat(primaryTableNames(transformed))
                          .containsExactly("$vrt_o61001_2$", "$vrt_o61001_3$");
                    });

                it(
                    "should keep the pre-existing virtual table",
                    () -> {
                      assertThat(virtualTableNames(transformed))
                          .containsExactlyInAnyOrder(
                              "vrt_o61001_1", "vrt_o61001_2", "vrt_o61001_3");
                    });
              });

          context(
              "two feature types colliding on the same base table name",
              () -> {
                XtraServerMapping given =
                    new XtraServerMappingBuilder()
                        .featureTypeMapping(
                            featureTypeMapping(
                                "ft:A",
                                primaryTable("o61001", "fkt = '1000'"),
                                primaryTable("o61001", "fkt = '2000'")))
                        .featureTypeMapping(
                            featureTypeMapping(
                                "ft:B",
                                primaryTable("o61001", "fkt = '3000'"),
                                primaryTable("o61001", "fkt = '4000'")))
                        .build();

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should allocate globally unique names so no query is silently overwritten",
                    () -> {
                      assertThat(virtualTableNames(transformed))
                          .containsExactly(
                              "vrt_o61001_1", "vrt_o61001_2", "vrt_o61001_3", "vrt_o61001_4");
                      assertThat(virtualTableQueries(transformed)).doesNotHaveDuplicates();
                    });
              });

          context(
              "a single primary table with a predicate",
              () -> {
                XtraServerMapping given = mapping(primaryTable("o61001", "fkt = '1000'"));

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should leave it untouched",
                    () -> {
                      assertThat(transformed).isEqualTo(given);
                      assertThat(transformed.getVirtualTables()).isEmpty();
                    });
              });

          context(
              "two primary tables with different names",
              () -> {
                XtraServerMapping given =
                    mapping(
                        primaryTable("city", "fkt = '1000'"), primaryTable("river", "fkt = '2000'"));

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should leave them untouched",
                    () -> {
                      assertThat(transformed).isEqualTo(given);
                      assertThat(transformed.getVirtualTables()).isEmpty();
                    });
              });

          context(
              "two primary tables with the same name and no predicate at all",
              () -> {
                XtraServerMapping given =
                    mapping(
                        new MappingTableBuilder()
                            .name("o61001")
                            .primaryKey("id")
                            .value(value("objid", "ft:objid"))
                            .build(),
                        new MappingTableBuilder()
                            .name("o61001")
                            .primaryKey("id")
                            .value(value("name", "ft:name"))
                            .build());

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should leave them untouched, since no predicate makes them ambiguous",
                    () -> {
                      assertThat(transformed).isEqualTo(given);
                      assertThat(transformed.getVirtualTables()).isEmpty();
                    });
              });

          context(
              "a colliding primary table that still has a merged joining table",
              () -> {
                MappingJoin join =
                    new MappingJoinBuilder()
                        .targetPath("NONE")
                        .joinCondition(
                            new MappingJoinBuilder.ConditionBuilder()
                                .sourceTable("o61001")
                                .sourceField("id")
                                .targetTable("o02341")
                                .targetField("id")
                                .build())
                        .build();

                MappingTable mergedChild =
                    new MappingTableBuilder()
                        .name("o02341")
                        .primaryKey("id")
                        .joinPath(join)
                        .value(value("position", "ft:position"))
                        .build();

                XtraServerMapping given =
                    mapping(
                        new MappingTableBuilder()
                            .shallowCopyOf(primaryTable("o61001", "fkt = '1000'"))
                            .value(value("objid", "ft:objid"))
                            .joiningTable(mergedChild)
                            .build(),
                        primaryTable("o61001", "fkt = '2000'"));

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should be left untouched, because the merged child's join column would not be"
                        + " selected",
                    () -> {
                      assertThat(transformed).isEqualTo(given);
                      assertThat(transformed.getVirtualTables()).isEmpty();
                    });
              });

          context(
              "primary tables that are already virtual table references",
              () -> {
                XtraServerMapping given =
                    mapping(
                        primaryTable("$vrt_o61001$", "fkt = '1000'"),
                        primaryTable("$vrt_o61001$", "fkt = '2000'"));

                XtraServerMapping transformed =
                    new MappingTransformerPredicateVariants(given).transform();

                it(
                    "should be left untouched rather than wrapped again",
                    () -> {
                      assertThat(transformed).isEqualTo(given);
                      assertThat(transformed.getVirtualTables()).isEmpty();
                    });
              });

          context(
              "applying the transformer twice",
              () -> {
                XtraServerMapping given =
                    mapping(
                        primaryTable("o61001", "fkt = '1000'"),
                        primaryTable("o61001", "fkt = '2000'"));

                XtraServerMapping once = new MappingTransformerPredicateVariants(given).transform();
                XtraServerMapping twice = new MappingTransformerPredicateVariants(once).transform();

                it(
                    "should be idempotent",
                    () -> {
                      assertThat(twice).isEqualTo(once);
                    });
              });
        });
  }

  private static MappingValue value(final String column, final String targetPath) {
    return new MappingValueBuilder().column().value(column).targetPath(targetPath).build();
  }

  private static MappingTable primaryTable(final String name, final String predicate) {
    return new MappingTableBuilder()
        .name(name)
        .primaryKey("id")
        .predicate(predicate)
        .value(value("objid", "ft:objid"))
        .build();
  }

  private static FeatureTypeMapping featureTypeMapping(
      final String name, final MappingTable... primaryTables) {
    FeatureTypeMappingBuilder builder = new FeatureTypeMappingBuilder().name(name);
    for (final MappingTable primaryTable : primaryTables) {
      builder.primaryTable(primaryTable);
    }
    return builder.build();
  }

  private static XtraServerMapping mapping(final MappingTable... primaryTables) {
    return new XtraServerMappingBuilder()
        .featureTypeMapping(featureTypeMapping("ft:Feature", primaryTables))
        .build();
  }

  private static List<MappingTable> primaryTables(final XtraServerMapping xtraServerMapping) {
    return xtraServerMapping.getFeatureTypeMappings().get(0).getPrimaryTables();
  }

  private static List<String> primaryTableNames(final XtraServerMapping xtraServerMapping) {
    return primaryTables(xtraServerMapping).stream()
        .map(MappingTable::getName)
        .collect(Collectors.toList());
  }

  private static List<String> virtualTableNames(final XtraServerMapping xtraServerMapping) {
    return xtraServerMapping.getVirtualTables().stream()
        .map(VirtualTable::getName)
        .collect(Collectors.toList());
  }

  private static List<String> virtualTableQueries(final XtraServerMapping xtraServerMapping) {
    return xtraServerMapping.getVirtualTables().stream()
        .map(VirtualTable::getQuery)
        .collect(Collectors.toList());
  }
}

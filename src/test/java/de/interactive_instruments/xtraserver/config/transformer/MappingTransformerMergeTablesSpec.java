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

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.greghaskins.spectrum.Spectrum;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;

import static org.assertj.core.api.Assertions.*;

/**
 * @author zahnen
 */
@RunWith(Spectrum.class)
public class MappingTransformerMergeTablesSpec {

    {//TODO test name changes for tables

        describe("MappingTransformerMergeTables", () -> {

            context("joined table with target path", () -> {

                it("it should do nothing", () -> {
                    XtraServerMapping given = createXtraServerMapping(false, false, true);
                    XtraServerMapping transformed = applyTransformation(given);
                    assertThat(transformed.getVirtualTables()).hasSize(1);
                });

            });

            context("joined table without target path", () -> {

                it("it should create a virtual table and a merged table mapping", () -> {
                    XtraServerMapping given = createXtraServerMapping(false, false, false);
                    XtraServerMapping transformed = applyTransformation(given);
                    assertThat(transformed.getVirtualTables()).hasSize(1);
                    MappingTable mergedTable = transformed.getFeatureTypeMappings().get(0).getPrimaryTables().get(0);
                    assertThat(mergedTable.getValues()).anyMatch(v -> "ci:location".equals(v.getTargetPath()));
                });

            });

            context("no condition context", () -> {

                context("no fields from primary table", () -> {

                    XtraServerMapping given = createXtraServerMapping(false, false, false);

                    XtraServerMapping transformed = applyTransformation(given);

                    it("virtual table should select from primary table", () -> {

                        assertThat(transformed.getVirtualTables()).hasSize(1);

                        String actual = transformed.getVirtualTables()
                                                   .get(0)
                                                   .getQuery();
                        String expected = "SELECT gn_boeschungkliff_pto.id AS id_2,o61001.objid,o02341.position FROM gn_boeschungkliff_pto INNER JOIN o61001 ON o61001.id = gn_boeschungkliff_pto.id INNER JOIN o02341__p0000103000 ON o02341__p0000103000.p0000103000 = o61001.objid INNER JOIN o02341 ON o02341.id = o02341__p0000103000.rid ";

                        assertThat(actual).isEqualTo(expected);

                    });

                    it("merged table should contain value from joined table", () -> {

                        MappingTable joinedTable = given.getFeatureTypeMappings()
                                                        .get(0)
                                                        .getPrimaryTables()
                                                        .get(0)
                                                        .getJoiningTables()
                                                        .iterator()
                                                        .next();

                        MappingTable mergedTable = transformed.getFeatureTypeMappings()
                                                              .get(0)
                                                              .getPrimaryTables()
                                                              .get(0);

                        MappingValue expected = joinedTable.getValues()
                                                           .iterator()
                                                           .next();

                        assertThat(mergedTable.getValues()).anyMatch(actual -> actual.getTargetPath()
                                                                                     .equals(expected.getTargetPath()) && actual.getValue()
                                                                                                                                .equals(expected.getValue()) /*&& actual.toString()
                                                                                                                                                                      .equals(expected.toString())*/);

                    });

                });

                context("uses fields from primary table", () -> {

                    XtraServerMapping given = createXtraServerMapping(true, false, false);

                    XtraServerMapping transformed = applyTransformation(given);

                    it("virtual table should contain field from primary table", () -> {

                        assertThat(transformed.getVirtualTables()).hasSize(1);

                        String actual = transformed.getVirtualTables()
                                                   .get(0)
                                                   .getQuery();
                        String expected = "SELECT gn_boeschungkliff_pto.id AS id_2,gn_boeschungkliff_pto.country,o61001.objid,o02341.position FROM gn_boeschungkliff_pto INNER JOIN o61001 ON o61001.id = gn_boeschungkliff_pto.id INNER JOIN o02341__p0000103000 ON o02341__p0000103000.p0000103000 = o61001.objid INNER JOIN o02341 ON o02341.id = o02341__p0000103000.rid ";

                        assertThat(actual).isEqualTo(expected);

                    });

                    it("merged table should contain value from primary table", () -> {

                        MappingTable primaryTable = given.getFeatureTypeMappings()
                                                         .get(0)
                                                         .getPrimaryTables()
                                                         .get(0);

                        MappingTable mergedTable = transformed.getFeatureTypeMappings()
                                                              .get(0)
                                                              .getPrimaryTables()
                                                              .get(0);

                        MappingValue expected = primaryTable.getValues()
                                                           .iterator()
                                                           .next();

                        assertThat(mergedTable.getValues()).anyMatch(actual -> actual.getTargetPath()
                                                                                     .equals(expected.getTargetPath()) && actual.getValue()
                                                                                                                                .equals(expected.getValue()) && actual.toString()
                                                                                                                                                                      .equals(expected.toString()));

                    });

                });

                context("has nested join with target path", () -> {

                    XtraServerMapping given = createXtraServerMapping(false, false, true);

                    XtraServerMapping transformed = applyTransformation(given);

                    it("it should not add joined table but should add join condition source field", () -> {

                        assertThat(transformed.getVirtualTables()).hasSize(1);

                        String actual = transformed.getVirtualTables()
                                                   .get(0)
                                                   .getQuery();
                        String expected = "SELECT gn_boeschungkliff_pto.id AS id_1,o61001.objid,o02341__p0000103000.rid FROM gn_boeschungkliff_pto INNER JOIN o61001 ON o61001.id = gn_boeschungkliff_pto.id INNER JOIN o02341__p0000103000 ON o02341__p0000103000.p0000103000 = o61001.objid ";

                        assertThat(actual).isEqualTo(expected);

                    });

                    it("merged table should not contain value from joined table", () -> {

                        MappingTable mergedTable = transformed.getFeatureTypeMappings()
                                                              .get(0)
                                                              .getPrimaryTables()
                                                              .get(0);

                        MappingTable joinedTable = mergedTable.getJoiningTables()
                                                              .iterator()
                                                              .next();

                        MappingValue expected = joinedTable.getValues()
                                                            .iterator()
                                                            .next();

                        assertThat(mergedTable.getValues()).noneMatch(actual -> actual.getTargetPath()
                                                                                     .equals(expected.getTargetPath()) && actual.getValue()
                                                                                                                                .equals(expected.getValue()) && actual.toString()
                                                                                                                                                                      .equals(expected.toString()));

                    });

                });

                context("has one merged join and one other join with nested join with target path", () -> {

                    XtraServerMapping given2 = MappingExamples.EXAMPLE_1;

                    XtraServerMapping transformed2 = applyTransformation(given2);

                    it("merged table should not contain value from joined table", () -> {

                        XtraServerMapping given3 = given2;
                        XtraServerMapping transformed3 = transformed2;

                       //assertThat(given2).isEqualTo(transformed2);

                    });

                });

            });

            context("condition context on primary table", () -> {

                context("no fields from primary table", () -> {

                    it("it should select from primary table and add where clause", () -> {

                        XtraServerMapping given = createXtraServerMapping(false, true, false);

                        XtraServerMapping transformed = applyTransformation(given);

                        assertThat(transformed.getVirtualTables()).hasSize(1);

                        String actual = transformed.getVirtualTables()
                                                   .get(0)
                                                   .getQuery();
                        String expected = "SELECT gn_boeschungkliff_pto.id AS id_2,o61001.objid,o02341.position FROM gn_boeschungkliff_pto INNER JOIN o61001 ON o61001.id = gn_boeschungkliff_pto.id INNER JOIN o02341__p0000103000 ON o02341__p0000103000.p0000103000 = o61001.objid INNER JOIN o02341 ON o02341.id = o02341__p0000103000.rid WHERE (NOT (gn_boeschungkliff_pto.fkt = '8300'))";

                        assertThat(actual).isEqualTo(expected);

                    });

                });

                context("uses fields from primary table", () -> {

                    it("it should create correct virtual table query with where clause", () -> {

                        XtraServerMapping given = createXtraServerMapping(true, true, false);

                        XtraServerMapping transformed = applyTransformation(given);

                        assertThat(transformed.getVirtualTables()).hasSize(1);

                        String actual = transformed.getVirtualTables()
                                                   .get(0)
                                                   .getQuery();
                        String expected = "SELECT gn_boeschungkliff_pto.id AS id_2,gn_boeschungkliff_pto.country,o61001.objid,o02341.position FROM gn_boeschungkliff_pto INNER JOIN o61001 ON o61001.id = gn_boeschungkliff_pto.id INNER JOIN o02341__p0000103000 ON o02341__p0000103000.p0000103000 = o61001.objid INNER JOIN o02341 ON o02341.id = o02341__p0000103000.rid WHERE (NOT (gn_boeschungkliff_pto.fkt = '8300'))";

                        assertThat(actual).isEqualTo(expected);

                    });

                });

                context("has nested join with target path", () -> {

                    it("it should write nothing", () -> {
                        XtraServerMapping given = createXtraServerMapping(false, true, true);
                        XtraServerMapping transformed = applyTransformation(given);
                        assertThat(transformed.getVirtualTables()).hasSize(1);
                        assertThat(transformed.getVirtualTables().get(0).getQuery()).contains("WHERE");
                    });

                });

                context("does not have any nested join with target path", () -> {

                    it("it should write nothing", () -> {
                        XtraServerMapping given = createXtraServerMapping(false, true, false);
                        XtraServerMapping transformed = applyTransformation(given);
                        assertThat(transformed.getVirtualTables()).hasSize(1);
                        assertThat(transformed.getVirtualTables().get(0).getQuery()).contains("WHERE");
                    });

                });

            });

            context("two feature types merging the same child of the same physical table", () -> {

                // Reproduces the customer's LN_Freizeitanlage / LN_Sportanlage case: both feature
                // types merge o51006__spo into o51006, but only one of them maps the same column
                // twice, so MappingTransformerCloneColumns wraps that child into a virtual table
                // first. The two MergeTables branches then derive the same name for both.
                XtraServerMapping given = createTwoFeatureTypesSharingATable();

                it("should give each feature type its own virtual table", () -> {
                    XtraServerMapping transformed = applyTransformationWithClones(given);

                    assertThat(transformed.getFeatureTypeMappings()
                                          .stream()
                                          .map(ft -> ft.getPrimaryTables()
                                                       .get(0)
                                                       .getName())
                                          .collect(Collectors.toList())).doesNotHaveDuplicates();
                });

                it("should not overwrite a virtual table that another feature type still references", () -> {
                    XtraServerMapping transformed = applyTransformationWithClones(given);

                    List<String> referenced = transformed.getFeatureTypeMappings()
                                                         .stream()
                                                         .map(ft -> ft.getPrimaryTables()
                                                                      .get(0)
                                                                      .getName()
                                                                      .replaceAll("\\$", ""))
                                                         .collect(Collectors.toList());

                    assertThat(transformed.getVirtualTables()
                                          .stream()
                                          .map(VirtualTable::getName)
                                          .collect(Collectors.toList())).containsAll(referenced);
                });

                it("should keep each feature type's own predicate", () -> {
                    XtraServerMapping transformed = applyTransformationWithClones(given);

                    assertThat(transformed.getVirtualTables()
                                          .stream()
                                          .map(VirtualTable::getQuery)
                                          .filter(query -> query.contains("spo"))
                                          .collect(Collectors.toList()))
                            .anySatisfy(query -> assertThat(query).contains("'1120'"))
                            .anySatisfy(query -> assertThat(query).contains("'1070'"));
                });

            });

        });

    }

    private XtraServerMapping applyTransformationWithClones(XtraServerMapping xtraServerMapping) throws URISyntaxException {
        URI uri = Resources.getResource("flatten/Cities.xsd")
                           .toURI();

        return XtraServerMappingTransformer.forMapping(xtraServerMapping)
                                           .applySchemaInfo(uri)
                                           .cloneColumns()
                                           .virtualTables()
                                           .transform();
    }

    /**
     * Two feature types over o51006, each merging o51006__spo with its own predicate. ci:City maps
     * the spo column once, ci:River maps it twice onto the same target path, which is what makes
     * MappingTransformerCloneColumns wrap its child into a virtual table beforehand.
     */
    private XtraServerMapping createTwoFeatureTypesSharingATable() {
        return new XtraServerMappingBuilder().featureTypeMapping(featureTypeSharingATable("ci:City", "'1120'", false))
                                             .featureTypeMapping(featureTypeSharingATable("ci:River", "'1070'", true))
                                             .build();
    }

    private FeatureTypeMapping featureTypeSharingATable(String name, String code, boolean mapsTheColumnTwice) {
        // two classifications over the same column and target path: same shape as the customer's
        // two ln:sportart mappings on spo, which is what makes MappingTransformerCloneColumns alias
        // the column and wrap the table into a virtual table of its own
        List<MappingValue> values = mapsTheColumnTwice
                ? ImmutableList.of(new MappingValueBuilder().classification()
                                                            .keyValue("1070", "1080")
                                                            .value("spo")
                                                            .targetPath("ci:name")
                                                            .build(),
                                   new MappingValueBuilder().classification()
                                                            .keyValue("1070", "1070")
                                                            .value("spo")
                                                            .targetPath("ci:name")
                                                            .build())
                : ImmutableList.of(new MappingValueBuilder().column()
                                                            .value("spo")
                                                            .targetPath("ci:name")
                                                            .build());

        MappingTable mergedChild = new MappingTableBuilder().name("o51006__spo")
                                                            .primaryKey("id")
                                                            .predicate("$T$.spo = " + code)
                                                            .values(values)
                                                            .joinPath(new MappingJoinBuilder().joinCondition(new MappingJoinBuilder.ConditionBuilder().sourceTable("o51006")
                                                                                                                                                      .sourceField("id")
                                                                                                                                                      .targetTable("o51006__spo")
                                                                                                                                                      .targetField("rid")
                                                                                                                                                      .build())
                                                                                              .targetPath("TODO")
                                                                                              .build())
                                                            .build();

        MappingTable primaryTable = new MappingTableBuilder().name("o51006")
                                                             .primaryKey("id")
                                                             .value(new MappingValueBuilder().column()
                                                                                             .value("objid")
                                                                                             .targetPath("@gml:id")
                                                                                             .build())
                                                             .joiningTable(mergedChild)
                                                             .build();

        return new FeatureTypeMappingBuilder().name(name)
                                              .primaryTable(primaryTable)
                                              .build();
    }

    private XtraServerMapping applyTransformation(XtraServerMapping xtraServerMapping) throws URISyntaxException {
        URI uri = Resources.getResource("flatten/Cities.xsd")
                           .toURI();

        return XtraServerMappingTransformer.forMapping(xtraServerMapping)
                                           .applySchemaInfo(uri)
                                           .virtualTables()
                                           .transform();
    }

    private XtraServerMapping createXtraServerMapping(boolean primaryTableHasField, boolean primaryTableHasPredicate, boolean lastJoinIsMultiple) {
        MappingTable o02341 = new MappingTableBuilder().name("o02341")
                                                       .primaryKey("id")
                                                       .targetPath(lastJoinIsMultiple ? "ci:location" : null)
                                                       .value(new MappingValueBuilder().geometry()
                                                                                       .value("position")
                                                                                       .targetPath("ci:location")
                                                                                       .build())
                                                       .joinPath(new MappingJoinBuilder().joinCondition(new MappingJoinBuilder.ConditionBuilder().sourceTable("o02341__p0000103000")
                                                                                                                                                 .sourceField("rid")
                                                                                                                                                 .targetTable("o02341")
                                                                                                                                                 .targetField("id")
                                                                                                                                                 .build())
                                                                                         .targetPath("TODO")
                                                                                         .build())
                                                       .build();

        MappingTable o02341__p0000103000 = new MappingTableBuilder().name("o02341__p0000103000")
                                                                    .primaryKey("id")
                                                                    .joiningTable(o02341)
                                                                    .joinPath(new MappingJoinBuilder().joinCondition(new MappingJoinBuilder.ConditionBuilder().sourceTable("o61001")
                                                                                                                                                              .sourceField("objid")
                                                                                                                                                              .targetTable("o02341__p0000103000")
                                                                                                                                                              .targetField("p0000103000")
                                                                                                                                                              .build())
                                                                                                      .targetPath("TODO")
                                                                                                      .build())
                                                                    .build();

        MappingTable o61001 = new MappingTableBuilder().name("o61001")
                                                       .primaryKey("id")
                                                       .value(new MappingValueBuilder().column()
                                                                                       .value("objid")
                                                                                       .targetPath("@gml:id")
                                                                                       .build())
                                                       .joiningTable(o02341__p0000103000)
                                                       .joinPath(new MappingJoinBuilder().joinCondition(new MappingJoinBuilder.ConditionBuilder().sourceTable("gn_boeschungkliff_pto")
                                                                                                                                                 .sourceField("id")
                                                                                                                                                 .targetTable("o61001")
                                                                                                                                                 .targetField("id")
                                                                                                                                                 .build())
                                                                                         .targetPath("TODO")
                                                                                         .build())
                                                       .build();

        List<MappingValue> primaryValues = primaryTableHasField
                ? ImmutableList.of(new MappingValueBuilder().column()
                                                            .value("country")
                                                            .targetPath("ci:country")
                                                            .build())
                : ImmutableList.of();

        MappingTable primaryTable = new MappingTableBuilder().name("gn_boeschungkliff_pto")
                                                             .primaryKey("id")
                                                             .predicate(primaryTableHasPredicate ? "NOT ($T$.fkt = '8300')" : null)
                                                             .values(primaryValues)
                                                             .joiningTable(o61001)
                                                             .build();

        FeatureTypeMapping namedPlace = new FeatureTypeMappingBuilder().name("ci:City")
                                                                       .primaryTable(primaryTable)
                                                                       .build();

        return new XtraServerMappingBuilder().featureTypeMapping(namedPlace)
                                             .build();
    }
}
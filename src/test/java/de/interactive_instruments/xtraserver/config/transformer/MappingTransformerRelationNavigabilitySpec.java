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

import com.greghaskins.spectrum.Spectrum;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spectrum spec for MappingTransformerRelationNavigability.
 *
 * Covers the working one-to-one reference nav case and exposes the known bug at line 108
 * of createMissingRelNavs where the join filter uses mappingTable.getTargetPath() instead
 * of mappingJoin.getTargetPath(), causing it to never match on primary tables.
 */
@RunWith(Spectrum.class)
public class MappingTransformerRelationNavigabilitySpec {

    {
        describe("MappingTransformerRelationNavigability", () -> {

            context("feature type with a reference value and no existing join (one-to-one)", () -> {

                // plot mapping: primary table with @gml:id value
                MappingTable plotTable = new MappingTableBuilder()
                        .name("plot_tbl")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("gml_id").targetPath("@gml:id").build())
                        .build();
                FeatureTypeMapping plot = new FeatureTypeMappingBuilder()
                        .name("test:Plot")
                        .qualifiedName(new QName("test", "Plot"))
                        .primaryTable(plotTable)
                        .build();

                // reference value: building_tbl.fk_col -> test:Plot via xlink:href
                MappingValue refValue = new MappingValueBuilder()
                        .reference()
                        .referencedFeatureType("test:Plot")
                        .value("fk_col")
                        .targetPath("test:relatedPlot/@xlink:href")
                        .build();

                // building mapping: primary table with the reference value, no existing join
                MappingTable buildingTable = new MappingTableBuilder()
                        .name("building_tbl")
                        .primaryKey("id")
                        .value(refValue)
                        .build();
                FeatureTypeMapping building = new FeatureTypeMappingBuilder()
                        .name("test:Building")
                        .qualifiedName(new QName("test", "Building"))
                        .primaryTable(buildingTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(building)
                        .featureTypeMapping(plot)
                        .build();

                XtraServerMapping result = new MappingTransformerRelationNavigability(given).transform();

                it("should add IS NOT NULL joining table and a join to referenced type primary table", () -> {
                    List<MappingTable> joiningTables = result
                            .getFeatureTypeMapping("test:Building").get()
                            .getPrimaryTables().get(0)
                            .getJoiningTables().asList();

                    assertThat(joiningTables).hasSize(2);
                    assertThat(joiningTables.get(0).getPredicate()).contains("IS NOT NULL");
                    assertThat(joiningTables.get(1).getTargetPath()).isEqualTo("test:relatedPlot/test:Plot");
                });

            });

            context("feature type with a reference value and an existing join to referenced type " +
                    "(known bug: line 108 filter never matches on primary tables, IS NOT NULL table always added)", () -> {

                // plot mapping: primary table with @gml:id value
                MappingTable plotTable = new MappingTableBuilder()
                        .name("plot_tbl")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("gml_id").targetPath("@gml:id").build())
                        .build();
                FeatureTypeMapping plot = new FeatureTypeMappingBuilder()
                        .name("test:Plot")
                        .qualifiedName(new QName("test", "Plot"))
                        .primaryTable(plotTable)
                        .build();

                // reference value: building_tbl.fk_col -> test:Plot via xlink:href
                MappingValue refValue = new MappingValueBuilder()
                        .reference()
                        .referencedFeatureType("test:Plot")
                        .value("fk_col")
                        .targetPath("test:relatedPlot/@xlink:href")
                        .build();

                // existing joining table: building_tbl -> plot_tbl for targetPath "test:relatedPlot"
                // this join already connects building to plot, so ideally no IS NOT NULL table should be added
                MappingTable existingPlotJoin = new MappingTableBuilder()
                        .name("plot_tbl")
                        .primaryKey("id")
                        .targetPath("test:relatedPlot")
                        .joinPath(new MappingJoinBuilder()
                                .targetPath("test:relatedPlot")
                                .joinCondition(new MappingJoinBuilder.ConditionBuilder()
                                        .sourceTable("building_tbl")
                                        .sourceField("fk_col")
                                        .targetTable("plot_tbl")
                                        .targetField("gml_id")
                                        .build())
                                .build())
                        .build();

                // building mapping: primary table with reference and the existing join
                MappingTable buildingTableWithJoin = new MappingTableBuilder()
                        .name("building_tbl")
                        .primaryKey("id")
                        .value(refValue)
                        .joiningTable(existingPlotJoin)
                        .build();
                FeatureTypeMapping buildingWithJoin = new FeatureTypeMappingBuilder()
                        .name("test:Building")
                        .qualifiedName(new QName("test", "Building"))
                        .primaryTable(buildingTableWithJoin)
                        .build();

                XtraServerMapping givenWithJoin = new XtraServerMappingBuilder()
                        .featureTypeMapping(buildingWithJoin)
                        .featureTypeMapping(plot)
                        .build();

                XtraServerMapping resultWithJoin = new MappingTransformerRelationNavigability(givenWithJoin).transform();

                it("should add IS NOT NULL table even though join already exists (current broken behavior)", () -> {
                    List<MappingTable> joiningTablesWithJoin = resultWithJoin
                            .getFeatureTypeMapping("test:Building").get()
                            .getPrimaryTables().get(0)
                            .getJoiningTables().asList();

                    // current broken behavior: IS NOT NULL table is still added because line 108's filter
                    // uses mappingTable.getTargetPath() (always "" for primary tables) instead of
                    // mappingJoin.getTargetPath(), so refJoin is always Optional.empty() and isOneToOneRel
                    // is always true — producing 3 joining tables: 1 existing + 1 IS NOT NULL + 1 join-to-plot
                    assertThat(joiningTablesWithJoin).hasSize(3);
                    assertThat(joiningTablesWithJoin).anyMatch(
                            t -> t.getPredicate() != null && t.getPredicate().contains("IS NOT NULL"));
                });

            });

        });
    }
}

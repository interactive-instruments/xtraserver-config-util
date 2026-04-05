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

import com.google.common.io.Resources;
import com.greghaskins.spectrum.Spectrum;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.Hints;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import org.junit.runner.RunWith;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Spectrum spec for MappingTransformerChoice.
 */
@RunWith(Spectrum.class)
public class MappingTransformerChoiceSpec {

    {
        describe("MappingTransformerChoice", () -> {

            context("two column values sharing the same non-multiple target path", () -> {

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("city_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("col_a").targetPath("ci:country").build())
                        .value(new MappingValueBuilder().column().value("col_b").targetPath("ci:country").build())
                        .build();

                FeatureTypeMapping cityMapping = new FeatureTypeMappingBuilder()
                        .name("ci:City")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(cityMapping)
                        .build();

                XtraServerMapping transformed = applyTransformation(given);

                it("should set IS NOT NULL predicate on first value", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();

                    assertThat(values).hasSize(2);
                    assertThat(values.get(0).getPredicate()).isEqualTo("$T$.col_a IS NOT NULL");
                });

                it("should set IS NULL AND IS NOT NULL predicate on second value", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();

                    assertThat(values.get(1).getPredicate()).isEqualTo("$T$.col_a IS NULL AND $T$.col_b IS NOT NULL");
                });

            });

            context("three column values sharing the same non-multiple target path", () -> {

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("city_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("col_a").targetPath("ci:country").build())
                        .value(new MappingValueBuilder().column().value("col_b").targetPath("ci:country").build())
                        .value(new MappingValueBuilder().column().value("col_c").targetPath("ci:country").build())
                        .build();

                FeatureTypeMapping cityMapping = new FeatureTypeMappingBuilder()
                        .name("ci:City")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(cityMapping)
                        .build();

                XtraServerMapping transformed = applyTransformation(given);

                it("should set IS NOT NULL predicate on first value", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();

                    assertThat(values).hasSize(3);
                    assertThat(values.get(0).getPredicate()).isEqualTo("$T$.col_a IS NOT NULL");
                });

                it("should set IS NULL AND IS NOT NULL predicate on second value", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();

                    assertThat(values.get(1).getPredicate()).isEqualTo("$T$.col_a IS NULL AND $T$.col_b IS NOT NULL");
                });

                it("should set negated-AND combined predicate on third value", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();

                    assertThat(values.get(2).getPredicate())
                            .contains("col_b IS NULL")
                            .contains("col_c IS NOT NULL");
                });

            });

            context("two column values with CHOICE hint on second value", () -> {

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("city_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("col_a").targetPath("ci:district").build())
                        .value(new MappingValueBuilder().column().value("col_b").targetPath("ci:district")
                                .transformationHint(Hints.CHOICE, "true").build())
                        .build();

                FeatureTypeMapping cityMapping = new FeatureTypeMappingBuilder()
                        .name("ci:City")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(cityMapping)
                        .build();

                XtraServerMapping transformed = applyTransformation(given);

                it("should apply IS NOT NULL predicate to first value via CHOICE hint", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();

                    assertThat(values).hasSize(2);
                    assertThat(values.get(0).getPredicate()).isEqualTo("$T$.col_a IS NOT NULL");
                });

                it("should apply IS NULL AND IS NOT NULL predicate to second value via CHOICE hint", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();

                    assertThat(values.get(1).getPredicate()).isEqualTo("$T$.col_a IS NULL AND $T$.col_b IS NOT NULL");
                });

            });

        });
    }

    private XtraServerMapping applyTransformation(XtraServerMapping mapping) throws URISyntaxException {
        URI uri = Resources.getResource("flatten/Cities.xsd").toURI();
        return XtraServerMappingTransformer.forMapping(mapping)
                .applySchemaInfo(uri)
                .applyChoicePredicates()
                .transform();
    }
}

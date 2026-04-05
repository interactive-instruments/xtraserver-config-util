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
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import org.junit.runner.RunWith;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Spectrum spec for MappingTransformerNil.
 */
@RunWith(Spectrum.class)
public class MappingTransformerNilSpec {

    {
        describe("MappingTransformerNil", () -> {

            context("nil value with nilReason and a non-nil sibling column for ci:function", () -> {

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("city_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("fkt_col").targetPath("ci:function").build())
                        .value(new MappingValueBuilder().constant().value("true").targetPath("ci:function/@xsi:nil").build())
                        .value(new MappingValueBuilder().constant().value("unknown").targetPath("ci:function/@nilReason").build())
                        .build();

                FeatureTypeMapping cityMapping = new FeatureTypeMappingBuilder()
                        .name("ci:City")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(cityMapping)
                        .build();

                XtraServerMapping transformed = applyTransformation(given);

                it("should remove the xsi:nil constant value", () -> {
                    Collection<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues();

                    assertThat(values).noneMatch(v -> v.getTargetPath() != null && v.getTargetPath().contains("@xsi:nil"));
                });

                it("should remove the nilReason constant value", () -> {
                    Collection<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues();

                    assertThat(values).noneMatch(v -> v.getTargetPath() != null && v.getTargetPath().contains("@nilReason"));
                });

                it("should produce exactly one NIL-type mapping value", () -> {
                    Collection<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues();

                    assertThat(values).anyMatch(MappingValue::isNil);
                    assertThat(values).filteredOn(MappingValue::isNil).hasSize(1);
                });

                it("should set the NIL-type value's target path to ci:function", () -> {
                    Collection<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues();

                    assertThat(values)
                            .filteredOn(MappingValue::isNil)
                            .extracting(MappingValue::getTargetPath)
                            .containsExactly("ci:function");
                });

            });

            context("nil value with nilReason and no non-nil sibling column for ci:function", () -> {

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("city_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().constant().value("true").targetPath("ci:function/@xsi:nil").build())
                        .value(new MappingValueBuilder().constant().value("unknown").targetPath("ci:function/@nilReason").build())
                        .build();

                FeatureTypeMapping cityMapping = new FeatureTypeMappingBuilder()
                        .name("ci:City")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(cityMapping)
                        .build();

                XtraServerMapping transformed = applyTransformation(given);

                it("should preserve both constant values unchanged", () -> {
                    Collection<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues();

                    assertThat(values).hasSize(2);
                    assertThat(values).allMatch(MappingValue::isConstant);
                });

                it("should not produce any NIL-type value", () -> {
                    Collection<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues();

                    assertThat(values).noneMatch(MappingValue::isNil);
                });

            });

        });
    }

    private XtraServerMapping applyTransformation(XtraServerMapping mapping) throws URISyntaxException {
        URI uri = Resources.getResource("flatten/Cities.xsd").toURI();
        return XtraServerMappingTransformer.forMapping(mapping)
                .applySchemaInfo(uri)
                .cleanNilChildren()
                .transform();
    }
}

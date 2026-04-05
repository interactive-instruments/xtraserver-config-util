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
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Spectrum spec for MappingTransformerSchemaInfo.
 */
@RunWith(Spectrum.class)
public class MappingTransformerSchemaInfoSpec {

    {
        describe("MappingTransformerSchemaInfo", () -> {

            context("ci:City mapping with prefixed name only", () -> {

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("city_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("country_col").targetPath("ci:country").build())
                        .build();

                FeatureTypeMapping cityMapping = new FeatureTypeMappingBuilder()
                        .name("ci:City")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(cityMapping)
                        .build();

                XtraServerMapping transformed = applyTransformation(given);

                it("should retain the ci:City feature type in the result", () -> {
                    assertThat(transformed.getFeatureTypeMapping("ci:City")).isPresent();
                });

                it("should resolve qualified name namespace URI to Cities namespace", () -> {
                    FeatureTypeMapping result = transformed.getFeatureTypeMapping("ci:City").get();
                    assertThat(result.getQualifiedName().getNamespaceURI())
                            .isEqualTo("http://www.interactive-instruments.de/namespaces/demo/cities/4.0/cities");
                });

                it("should resolve qualified name local part to City", () -> {
                    FeatureTypeMapping result = transformed.getFeatureTypeMapping("ci:City").get();
                    assertThat(result.getQualifiedName().getLocalPart()).isEqualTo("City");
                });

                it("should populate superTypeName as ci:NamedPlace from substitutionGroup", () -> {
                    FeatureTypeMapping result = transformed.getFeatureTypeMapping("ci:City").get();
                    assertThat(result.getSuperTypeName()).isPresent();
                    assertThat(result.getSuperTypeName().get()).isEqualTo("ci:NamedPlace");
                });

            });

            context("ci:City mapping with location column value", () -> {

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("city_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("location_col").targetPath("ci:location").build())
                        .build();

                FeatureTypeMapping cityMapping = new FeatureTypeMappingBuilder()
                        .name("ci:City")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(cityMapping)
                        .build();

                XtraServerMapping transformed = applyTransformation(given);

                it("should reclassify the location COLUMN value as GEOMETRY type", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMapping("ci:City").get()
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();
                    assertThat(values).hasSize(1);
                    assertThat(values.get(0).isGeometry()).isTrue();
                });

                it("should set qualifiedTargetPath on the location value with local part location", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMapping("ci:City").get()
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();
                    assertThat(values.get(0).getQualifiedTargetPath()).hasSize(1);
                    assertThat(values.get(0).getQualifiedTargetPath().get(0).getLocalPart()).isEqualTo("location");
                });

            });

            context("mapping with feature type name not present in schema", () -> {

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("unknown_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("some_col").targetPath("ci:foo").build())
                        .build();

                FeatureTypeMapping unknownMapping = new FeatureTypeMappingBuilder()
                        .name("ci:UnknownType")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(unknownMapping)
                        .build();

                it("should filter out the unknown feature type, resulting in an empty mapping error", () -> {
                    // The transformer filters ci:UnknownType (not present in schema), leaving zero
                    // feature type mappings. XtraServerMappingBuilder.validate() then rejects an
                    // empty result with IllegalStateException — which proves the filtering occurred.
                    assertThatThrownBy(() -> applyTransformation(given))
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessageContaining("no feature type mappings");
                });

            });

        });
    }

    private XtraServerMapping applyTransformation(XtraServerMapping mapping) throws URISyntaxException {
        URI uri = Resources.getResource("flatten/Cities.xsd").toURI();
        return XtraServerMappingTransformer.forMapping(mapping)
                .applySchemaInfo(uri)
                .transform();
    }
}

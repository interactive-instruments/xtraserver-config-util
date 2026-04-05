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
import de.interactive_instruments.xtraserver.config.api.Hints;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Spectrum spec for MappingTransformerCloneColumns
 */
@RunWith(Spectrum.class)
public class MappingTransformerCloneColumnsSpec {

    {
        describe("MappingTransformerCloneColumns", () -> {

            context("two column values with the same column and same targetPath", () -> {

                // Two values with identical column+targetPath but different descriptions so they
                // are distinct objects in the ImmutableSet — this is the scenario CloneColumns handles.
                MappingValue value1 = new MappingValueBuilder()
                        .column()
                        .value("status")
                        .targetPath("ft:status")
                        .description("original")
                        .build();

                MappingValue value2 = new MappingValueBuilder()
                        .column()
                        .value("status")
                        .targetPath("ft:status")
                        .description("duplicate")
                        .build();

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("feature_table")
                        .primaryKey("id")
                        .value(value1)
                        .value(value2)
                        .build();

                FeatureTypeMapping featureTypeMapping = new FeatureTypeMappingBuilder()
                        .name("ft:Feature")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(featureTypeMapping)
                        .build();

                XtraServerMapping transformed = new MappingTransformerCloneColumns(given).transform();

                it("should generate one VirtualTable", () -> {
                    assertThat(transformed.getVirtualTables()).hasSize(1);
                });

                it("should assign CLONE hint '1' to the original value", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .stream()
                            .filter(v -> "ft:status".equals(v.getTargetPath()))
                            .collect(Collectors.toList());

                    assertThat(values).anyMatch(v -> "1".equals(v.getTransformationHints().get(Hints.CLONE)));
                });

                it("should assign CLONE hint '2' to the cloned value", () -> {
                    List<MappingValue> values = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .stream()
                            .filter(v -> "ft:status".equals(v.getTargetPath()))
                            .collect(Collectors.toList());

                    assertThat(values).anyMatch(v -> "2".equals(v.getTransformationHints().get(Hints.CLONE)));
                });

            });

            context("all column values have distinct column+targetPath pairs", () -> {

                MappingValue valueA = new MappingValueBuilder()
                        .column()
                        .value("col_a")
                        .targetPath("ft:propA")
                        .build();

                MappingValue valueB = new MappingValueBuilder()
                        .column()
                        .value("col_b")
                        .targetPath("ft:propB")
                        .build();

                MappingTable primaryTable = new MappingTableBuilder()
                        .name("feature_table")
                        .primaryKey("id")
                        .value(valueA)
                        .value(valueB)
                        .build();

                FeatureTypeMapping featureTypeMapping = new FeatureTypeMappingBuilder()
                        .name("ft:Feature")
                        .primaryTable(primaryTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(featureTypeMapping)
                        .build();

                XtraServerMapping transformed = new MappingTransformerCloneColumns(given).transform();

                it("should produce no VirtualTable", () -> {
                    assertThat(transformed.getVirtualTables()).isEmpty();
                });

                it("should produce no CLONE hints", () -> {
                    boolean anyCloneHint = transformed.getFeatureTypeMappings()
                            .get(0)
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .stream()
                            .anyMatch(v -> v.getTransformationHints().containsKey(Hints.CLONE));

                    assertThat(anyCloneHint).isFalse();
                });

            });

        });
    }
}

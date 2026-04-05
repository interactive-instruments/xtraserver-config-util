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
 * Spectrum spec for MappingTransformerFlattenInheritance.
 */
@RunWith(Spectrum.class)
public class MappingTransformerFlattenInheritanceSpec {

    {
        describe("MappingTransformerFlattenInheritance", () -> {

            context("parent type with one value and child type with different value on shared primary table", () -> {

                // given
                MappingTable parentTable = new MappingTableBuilder()
                        .name("shared_tbl")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("pcol").targetPath("test:parentProp").build())
                        .build();

                FeatureTypeMapping parent = new FeatureTypeMappingBuilder()
                        .name("test:Parent")
                        .qualifiedName(new QName("test", "Parent"))
                        .primaryTable(parentTable)
                        .build();

                MappingTable childTable = new MappingTableBuilder()
                        .name("shared_tbl")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("ccol").targetPath("test:childProp").build())
                        .build();

                FeatureTypeMapping child = new FeatureTypeMappingBuilder()
                        .name("test:Child")
                        .qualifiedName(new QName("test", "Child"))
                        .superTypeName("test:Parent")
                        .primaryTable(childTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(parent)
                        .featureTypeMapping(child)
                        .build();

                XtraServerMapping result = new MappingTransformerFlattenInheritance(given).transform();

                it("should remove the parent type from the output (has children)", () -> {
                    assertThat(result.getFeatureTypeNames()).doesNotContain("test:Parent");
                });

                it("should keep the child type in the output (leaf — no children)", () -> {
                    assertThat(result.getFeatureTypeNames()).contains("test:Child");
                });

                it("should merge parent table values into child primary table", () -> {
                    List<MappingValue> values = result.getFeatureTypeMapping("test:Child")
                            .get()
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();

                    assertThat(values).extracting(MappingValue::getValue).contains("pcol", "ccol");
                });

            });

            context("standalone type with no parent and no children", () -> {

                // given
                MappingTable standaloneTable = new MappingTableBuilder()
                        .name("standalone_tbl")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column().value("scol").targetPath("test:standaloneProp").build())
                        .build();

                FeatureTypeMapping standalone = new FeatureTypeMappingBuilder()
                        .name("test:Standalone")
                        .qualifiedName(new QName("test", "Standalone"))
                        .primaryTable(standaloneTable)
                        .build();

                XtraServerMapping given = new XtraServerMappingBuilder()
                        .featureTypeMapping(standalone)
                        .build();

                XtraServerMapping result = new MappingTransformerFlattenInheritance(given).transform();

                it("should preserve the standalone type in output (leaf — no children)", () -> {
                    assertThat(result.getFeatureTypeNames()).contains("test:Standalone");
                });

                it("should retain the standalone type's own values unchanged", () -> {
                    List<MappingValue> values = result.getFeatureTypeMapping("test:Standalone")
                            .get()
                            .getPrimaryTables()
                            .get(0)
                            .getValues()
                            .asList();

                    assertThat(values).extracting(MappingValue::getValue).containsExactly("scol");
                });

            });

        });
    }
}

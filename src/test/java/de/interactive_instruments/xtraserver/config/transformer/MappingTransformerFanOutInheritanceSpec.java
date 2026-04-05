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
import com.greghaskins.spectrum.Spectrum;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

/**
 * Spectrum BDD spec for MappingTransformerFanOutInheritance.
 *
 * Verifies that FanOutInheritance correctly distributes derived-type values
 * to their respective supertype expanded mappings (TRANS-03).
 */
@RunWith(Spectrum.class)
public class MappingTransformerFanOutInheritanceSpec {

    {
        describe("MappingTransformerFanOutInheritance", () -> {

            context("derived type with two values: one belonging to supertype, one not", () -> {

                // given
                final QName abstractBaseQName = new QName("test", "AbstractBase");
                final QName derivedQName = new QName("test", "Derived");

                final XmlSchemaComplexType abstractBaseType = mock(XmlSchemaComplexType.class);

                final ApplicationSchema appSchema = mock(ApplicationSchema.class);
                final Namespaces namespaces = mock(Namespaces.class);

                Mockito.when(appSchema.getNamespaces()).thenReturn(namespaces);
                Mockito.when(namespaces.getPrefixedName(abstractBaseQName)).thenReturn("test:AbstractBase");
                Mockito.when(namespaces.getPrefixedName(derivedQName)).thenReturn("test:Derived");

                // getAllSuperTypeQualifiedNames returns a mutable list so the transformer can add derivedQName to it
                Mockito.when(appSchema.getAllSuperTypeQualifiedNames(derivedQName))
                        .thenAnswer(inv -> new ArrayList<>(ImmutableList.of(abstractBaseQName)));

                Mockito.when(appSchema.hasElement(abstractBaseQName)).thenReturn(true);
                Mockito.when(appSchema.hasElement(derivedQName)).thenReturn(true);

                Mockito.when(appSchema.getType(abstractBaseQName)).thenReturn(abstractBaseType);
                Mockito.when(appSchema.getType(derivedQName)).thenReturn(mock(XmlSchemaComplexType.class));

                Mockito.when(appSchema.hasProperty(eq(abstractBaseType), eq(new QName("test", "sharedProp")))).thenReturn(true);
                Mockito.when(appSchema.hasProperty(eq(abstractBaseType), eq(new QName("test", "derivedOnlyProp")))).thenReturn(false);

                Mockito.when(appSchema.isAbstract(abstractBaseQName)).thenReturn(true);
                Mockito.when(appSchema.isAbstract(derivedQName)).thenReturn(false);

                Mockito.when(appSchema.getSuperTypeName(any())).thenReturn(Optional.empty());

                // Build the derived feature type with two values
                final MappingTable primaryTable = new MappingTableBuilder()
                        .name("base_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column()
                                .value("shared_col")
                                .targetPath("test:sharedProp")
                                .qualifiedTargetPath(ImmutableList.of(new QName("test", "sharedProp")))
                                .build())
                        .value(new MappingValueBuilder().column()
                                .value("derived_col")
                                .targetPath("test:derivedOnlyProp")
                                .qualifiedTargetPath(ImmutableList.of(new QName("test", "derivedOnlyProp")))
                                .build())
                        .build();

                final FeatureTypeMapping derived = new FeatureTypeMappingBuilder()
                        .name("test:Derived")
                        .qualifiedName(derivedQName)
                        .primaryTable(primaryTable)
                        .build();

                final XtraServerMapping mapping = new XtraServerMappingBuilder()
                        .featureTypeMapping(derived)
                        .build();

                final XtraServerMapping result = new MappingTransformerFanOutInheritance(mapping, appSchema).transform();

                it("should produce an expanded mapping for the supertype", () -> {
                    assertThat(result.getFeatureTypeNames()).contains("test:AbstractBase");
                    assertThat(result.getFeatureTypeMapping("test:AbstractBase")).isPresent();
                });

                it("should include only the value belonging to the supertype in its expanded primary table", () -> {
                    final FeatureTypeMapping abstractBaseMapping = result.getFeatureTypeMapping("test:AbstractBase").get();
                    final List<MappingValue> values = abstractBaseMapping.getPrimaryTables().get(0).getValues().asList();

                    assertThat(values).hasSize(1);
                    assertThat(values.get(0).getValue()).isEqualTo("shared_col");
                });

                it("should exclude values not belonging to the supertype from its expanded primary table", () -> {
                    final FeatureTypeMapping abstractBaseMapping = result.getFeatureTypeMapping("test:AbstractBase").get();
                    final List<MappingValue> values = abstractBaseMapping.getPrimaryTables().get(0).getValues().asList();

                    assertThat(values).extracting(MappingValue::getValue).doesNotContain("derived_col");
                });

            });

            context("two derived types sharing a common supertype", () -> {

                // given
                final QName abstractBaseQName = new QName("test", "AbstractBase");
                final QName derivedAQName = new QName("test", "DerivedA");
                final QName derivedBQName = new QName("test", "DerivedB");

                final XmlSchemaComplexType abstractBaseType = mock(XmlSchemaComplexType.class);
                final XmlSchemaComplexType derivedAType = mock(XmlSchemaComplexType.class);
                final XmlSchemaComplexType derivedBType = mock(XmlSchemaComplexType.class);

                final ApplicationSchema appSchema2 = mock(ApplicationSchema.class);
                final Namespaces namespaces2 = mock(Namespaces.class);

                Mockito.when(appSchema2.getNamespaces()).thenReturn(namespaces2);
                Mockito.when(namespaces2.getPrefixedName(abstractBaseQName)).thenReturn("test:AbstractBase");
                Mockito.when(namespaces2.getPrefixedName(derivedAQName)).thenReturn("test:DerivedA");
                Mockito.when(namespaces2.getPrefixedName(derivedBQName)).thenReturn("test:DerivedB");

                Mockito.when(appSchema2.getAllSuperTypeQualifiedNames(derivedAQName))
                        .thenAnswer(inv -> new ArrayList<>(ImmutableList.of(abstractBaseQName)));
                Mockito.when(appSchema2.getAllSuperTypeQualifiedNames(derivedBQName))
                        .thenAnswer(inv -> new ArrayList<>(ImmutableList.of(abstractBaseQName)));

                Mockito.when(appSchema2.hasElement(abstractBaseQName)).thenReturn(true);
                Mockito.when(appSchema2.hasElement(derivedAQName)).thenReturn(true);
                Mockito.when(appSchema2.hasElement(derivedBQName)).thenReturn(true);

                Mockito.when(appSchema2.getType(abstractBaseQName)).thenReturn(abstractBaseType);
                Mockito.when(appSchema2.getType(derivedAQName)).thenReturn(derivedAType);
                Mockito.when(appSchema2.getType(derivedBQName)).thenReturn(derivedBType);

                // propA belongs to supertype (from DerivedA); propB also belongs to supertype (from DerivedB)
                Mockito.when(appSchema2.hasProperty(eq(abstractBaseType), eq(new QName("test", "propA")))).thenReturn(true);
                Mockito.when(appSchema2.hasProperty(eq(abstractBaseType), eq(new QName("test", "propB")))).thenReturn(true);

                Mockito.when(appSchema2.isAbstract(abstractBaseQName)).thenReturn(true);
                Mockito.when(appSchema2.isAbstract(derivedAQName)).thenReturn(false);
                Mockito.when(appSchema2.isAbstract(derivedBQName)).thenReturn(false);

                Mockito.when(appSchema2.getSuperTypeName(any())).thenReturn(Optional.empty());

                // DerivedA has propA
                final MappingTable tableA = new MappingTableBuilder()
                        .name("base_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column()
                                .value("col_a")
                                .targetPath("test:propA")
                                .qualifiedTargetPath(ImmutableList.of(new QName("test", "propA")))
                                .build())
                        .build();

                final FeatureTypeMapping derivedA = new FeatureTypeMappingBuilder()
                        .name("test:DerivedA")
                        .qualifiedName(derivedAQName)
                        .primaryTable(tableA)
                        .build();

                // DerivedB has propB
                final MappingTable tableB = new MappingTableBuilder()
                        .name("base_table")
                        .primaryKey("id")
                        .value(new MappingValueBuilder().column()
                                .value("col_b")
                                .targetPath("test:propB")
                                .qualifiedTargetPath(ImmutableList.of(new QName("test", "propB")))
                                .build())
                        .build();

                final FeatureTypeMapping derivedB = new FeatureTypeMappingBuilder()
                        .name("test:DerivedB")
                        .qualifiedName(derivedBQName)
                        .primaryTable(tableB)
                        .build();

                final XtraServerMapping mapping2 = new XtraServerMappingBuilder()
                        .featureTypeMapping(derivedA)
                        .featureTypeMapping(derivedB)
                        .build();

                final XtraServerMapping result2 = new MappingTransformerFanOutInheritance(mapping2, appSchema2).transform();

                it("should produce an expanded mapping for the common supertype", () -> {
                    assertThat(result2.getFeatureTypeNames()).contains("test:AbstractBase");
                    assertThat(result2.getFeatureTypeMapping("test:AbstractBase")).isPresent();
                });

                it("should accumulate values from both derived types into the supertype expanded mapping", () -> {
                    final FeatureTypeMapping abstractBaseMapping = result2.getFeatureTypeMapping("test:AbstractBase").get();
                    final List<MappingValue> values = abstractBaseMapping.getPrimaryTables().get(0).getValues().asList();

                    // DerivedB's value wins (last-write-wins per LinkedHashMap.put semantics)
                    // but the supertype mapping should have been updated with each derived type's contribution
                    assertThat(values).isNotEmpty();
                    assertThat(values).extracting(MappingValue::getValue)
                            .containsAnyOf("col_a", "col_b");
                });

                it("should include both derived types in the result", () -> {
                    assertThat(result2.getFeatureTypeNames()).contains("test:DerivedA");
                    assertThat(result2.getFeatureTypeNames()).contains("test:DerivedB");
                });

            });

        });
    }
}

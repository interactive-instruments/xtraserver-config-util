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
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.runner.RunWith;

/**
 * Pins the position of MappingTransformerPredicateVariants in the transformer chain: after
 * flattenInheritance, before fanOutInheritance.
 *
 * <p>MappingTransformerFanOutInheritance copies a feature type's primary tables up into every super
 * type mapping, so a same-name group is not just written twice for the feature type itself - the
 * duplicate name is replicated into every fanned out mapping as well. Renaming before that stage is
 * what keeps all of those distinct.
 */
@RunWith(Spectrum.class)
public class MappingTransformerPredicateVariantsChainSpec {

  {
    describe(
        "MappingTransformerPredicateVariants in the transformer chain",
        () -> {
          context(
              "a feature type with two primary tables differing only in their predicate,"
                  + " fanned out to its super types",
              () -> {
                it(
                    "should keep both variants in the ci:City mapping, under distinct names",
                    () -> {
                      XtraServerMapping transformed =
                          transform(givenCityMapping(), true);

                      assertThat(primaryTableNames(transformed, "ci:City"))
                          .containsExactly("$vrt_city_table_1$", "$vrt_city_table_2$");
                    });

                it(
                    "should keep both variants in the fanned out super type mappings",
                    () -> {
                      XtraServerMapping transformed =
                          transform(givenCityMapping(), true);

                      assertThat(primaryTableNames(transformed, "ci:NamedGeoObject"))
                          .containsExactly("$vrt_city_table_1$", "$vrt_city_table_2$");
                    });

                it(
                    "should leave no duplicate primary table name in any feature type mapping,"
                        + " which is what the generated _xsv_tmp_ name and every name based"
                        + " lookup depend on",
                    () -> {
                      XtraServerMapping transformed =
                          transform(givenCityMapping(), true);

                      assertThat(transformed.getFeatureTypeMappings())
                          .allSatisfy(
                              featureTypeMapping ->
                                  assertThat(featureTypeMapping.getPrimaryTableNames())
                                      .doesNotHaveDuplicates());
                    });

                it(
                    "should, without the fix, propagate the duplicate table name into every fanned"
                        + " out super type mapping - the regression this guards against",
                    () -> {
                      XtraServerMapping withoutFix = transform(givenCityMapping(), false);

                      assertThat(withoutFix.getFeatureTypeMappings())
                          .isNotEmpty()
                          .allSatisfy(
                              featureTypeMapping ->
                                  assertThat(featureTypeMapping.getPrimaryTableNames())
                                      .containsExactly("city_table", "city_table"));
                    });
              });
        });
  }

  private static XtraServerMapping transform(
      final XtraServerMapping mapping, final boolean virtualTables) throws Exception {
    final URI uri = Resources.getResource("flatten/Cities.xsd").toURI();

    XtraServerMappingTransformer.Transform transform =
        XtraServerMappingTransformer.forMapping(mapping).applySchemaInfo(uri);

    if (virtualTables) {
      transform = transform.virtualTables();
    }

    return transform.fanOutInheritance().transform();
  }

  private static XtraServerMapping givenCityMapping() {
    return new XtraServerMappingBuilder()
        .featureTypeMapping(
            new FeatureTypeMappingBuilder()
                .name("ci:City")
                .primaryTable(primaryTable("$T$.fkt = '1000'"))
                .primaryTable(primaryTable("$T$.fkt = '2000'"))
                .build())
        .build();
  }

  private static MappingTable primaryTable(final String predicate) {
    return new MappingTableBuilder()
        .name("city_table")
        .primaryKey("id")
        .predicate(predicate)
        .value(value("gml_id", "@gml:id"))
        .value(value("city_name", "ci:name"))
        .build();
  }

  private static MappingValue value(final String column, final String targetPath) {
    return new MappingValueBuilder().column().value(column).targetPath(targetPath).build();
  }

  private static List<String> primaryTableNames(
      final XtraServerMapping xtraServerMapping, final String featureTypeName) {
    return xtraServerMapping.getFeatureTypeMappings().stream()
        .filter(featureTypeMapping -> featureTypeName.equals(featureTypeMapping.getName()))
        .findFirst()
        .map(FeatureTypeMapping::getPrimaryTableNames)
        .orElseThrow(() -> new AssertionError("no mapping for " + featureTypeName));
  }
}

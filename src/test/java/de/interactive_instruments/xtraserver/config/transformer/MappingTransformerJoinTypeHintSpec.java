/**
 * Copyright 2020 interactive instruments GmbH
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.xtraserver.config.transformer;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.assertj.core.api.Assertions.*;

import com.google.common.io.Resources;
import com.greghaskins.spectrum.Spectrum;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.runner.RunWith;

/** Spectrum spec for MappingTransformerJoinTypeHint */
@RunWith(Spectrum.class)
public class MappingTransformerJoinTypeHintSpec {

  {
    describe(
        "MappingTransformerJoinTypeHint",
        () -> {
          context(
              "merged table where all values are schema-optional (ci:name)",
              () -> {
                MappingValue nameValue =
                    new MappingValueBuilder()
                        .column()
                        .value("name_col")
                        .targetPath("ci:name")
                        .build();

                XtraServerMapping given = createCityMapping(nameValue);

                XtraServerMapping transformed = applyTransformation(given);

                it(
                    "should annotate the join with JOIN_TYPE=LEFT",
                    () -> {
                      MappingJoin join =
                          transformed
                              .getFeatureTypeMappings()
                              .get(0)
                              .getPrimaryTables()
                              .get(0)
                              .getJoiningTables()
                              .iterator()
                              .next()
                              .getJoinPaths()
                              .iterator()
                              .next();

                      assertThat(join.getTransformationHints())
                          .containsEntry(MappingTransformerJoinTypeHint.HINT_JOIN_TYPE, "LEFT");
                    });
              });

          context(
              "merged table where at least one value is schema-required (ci:country)",
              () -> {
                MappingValue countryValue =
                    new MappingValueBuilder()
                        .column()
                        .value("country_col")
                        .targetPath("ci:country")
                        .build();

                XtraServerMapping given = createCityMapping(countryValue);

                XtraServerMapping transformed = applyTransformation(given);

                it(
                    "should NOT annotate the join with JOIN_TYPE",
                    () -> {
                      MappingJoin join =
                          transformed
                              .getFeatureTypeMappings()
                              .get(0)
                              .getPrimaryTables()
                              .get(0)
                              .getJoiningTables()
                              .iterator()
                              .next()
                              .getJoinPaths()
                              .iterator()
                              .next();

                      assertThat(join.getTransformationHints())
                          .doesNotContainKey(MappingTransformerJoinTypeHint.HINT_JOIN_TYPE);
                    });
              });

          context(
              "merged table without any values but with a predicate that ends with ' IS NULL'",
              () -> {
                XtraServerMapping given = createCityMapping(List.of(), "foo IS NULL");

                XtraServerMapping transformed = applyTransformation(given);

                it(
                    "should remove the join and add the predicate to the main table as a NOT EXISTS subquery",
                    () -> {
                      MappingTable table =
                          transformed.getFeatureTypeMappings().get(0).getPrimaryTables().get(0);

                      assertThat(table.getJoiningTables()).isNullOrEmpty();
                      assertThat(table.getPredicate()).isNotBlank();
                      assertThat(table.getPredicate())
                          .isEqualToIgnoringCase(
                              "(NOT EXISTS (SELECT 1 FROM details_table WHERE details_table.city_id = $T$.id LIMIT 1))");
                    });
              });
        });
  }

  private XtraServerMapping applyTransformation(XtraServerMapping mapping)
      throws URISyntaxException {
    // Use the flatten/Cities.xsd which has local imports that resolve without network access
    URI uri = Resources.getResource("flatten/Cities.xsd").toURI();
    return XtraServerMappingTransformer.forMapping(mapping)
        .applySchemaInfo(uri)
        .joinTypes()
        .transform();
  }

  /**
   * Builds a ci:City mapping with one primary table and one merged joining table. The merged table
   * contains no targetPath (null) and one joinPath — satisfying isMerged().
   */
  private XtraServerMapping createCityMapping(
      List<MappingValue> mergedTableValue, String predicate) {
    // A merged table: the MappingTable has no targetPath (default null) and has a joinPath.
    // The joinPath itself still requires a non-null targetPath per builder validation.
    MappingTable mergedTable =
        new MappingTableBuilder()
            .name("details_table")
            .primaryKey("id")
            .values(mergedTableValue)
            .predicate(predicate)
            .joinPath(
                new MappingJoinBuilder()
                    .joinCondition(
                        new MappingJoinBuilder.ConditionBuilder()
                            .sourceTable("city_table")
                            .sourceField("id")
                            .targetTable("details_table")
                            .targetField("city_id")
                            .build())
                    .targetPath("TODO")
                    .build())
            .build();

    MappingTable primaryTable =
        new MappingTableBuilder()
            .name("city_table")
            .primaryKey("id")
            .joiningTable(mergedTable)
            .build();

    FeatureTypeMapping cityMapping =
        new FeatureTypeMappingBuilder().name("ci:City").primaryTable(primaryTable).build();

    return new XtraServerMappingBuilder().featureTypeMapping(cityMapping).build();
  }

  private XtraServerMapping createCityMapping(MappingValue mergedTableValue) {
    return createCityMapping(List.of(mergedTableValue), null);
  }
}

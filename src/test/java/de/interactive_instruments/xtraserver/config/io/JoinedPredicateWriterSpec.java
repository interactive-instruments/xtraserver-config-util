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
package de.interactive_instruments.xtraserver.config.io;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.Resources;
import com.greghaskins.spectrum.Spectrum;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import de.interactive_instruments.xtraserver.config.transformer.XtraServerMappingTransformer;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.runner.RunWith;

/**
 * The reported symptom, asserted on the written file rather than on the model: JaxbWriter puts a
 * joined table's predicate into its {@code <Table>} row but never into the {@code <Join>} row's
 * join path, so the two rows describing one table disagree on its name and XtraServer resolves the
 * join by the bare one.
 *
 * <p>Modelled on the ALKIS tn_transportnetwork case, where one joined table name carries four
 * different filters in a single mapping file.
 */
@RunWith(Spectrum.class)
public class JoinedPredicateWriterSpec {

  private static final Pattern TABLE_NAME = Pattern.compile("table_name=\"([^\"]*)\"");
  private static final Pattern JOIN_PATH = Pattern.compile("join_path=\"([^\"]*)\"");

  {
    describe(
        "the written mapping for a feature type joining one table with several filters",
        () -> {
          final AtomicReference<XtraServerMapping> mapping = new AtomicReference<>();
          final AtomicReference<String> xml = new AtomicReference<>();

          beforeAll(
              () -> {
                final URI uri = Resources.getResource("flatten/Cities.xsd").toURI();

                mapping.set(
                    XtraServerMappingTransformer.forMapping(givenMapping())
                        .applySchemaInfo(uri)
                        .virtualTables()
                        .transform());

                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                XtraServerMappingFile.write().mapping(mapping.get()).toStream(outputStream);
                xml.set(new String(outputStream.toByteArray(), StandardCharsets.UTF_8));
              });

          it(
              "should write no table name carrying a predicate in brackets",
              () -> {
                assertThat(tableNames(xml.get()))
                    .isNotEmpty()
                    .allSatisfy(tableName -> assertThat(tableName).doesNotContain("["));
              });

          it(
              "should name, in every join path, a table that the mapping also writes a row for -"
                  + " the agreement the predicate in brackets used to break",
              () -> {
                assertThat(joinTargets(xml.get()))
                    .isNotEmpty()
                    .allSatisfy(
                        joinTarget -> assertThat(tableNames(xml.get())).contains(joinTarget));
              });

          it(
              "should give each filter its own virtual table",
              () -> {
                assertThat(mapping.get().getVirtualTables()).hasSize(3);
                assertThat(
                        mapping.get().getVirtualTables().stream()
                            .map(virtualTable -> virtualTable.getQuery())
                            .distinct()
                            .count())
                    .isEqualTo(3L);
              });

          it(
              "should keep every generated query flat, since the parent joins from outside",
              () -> {
                assertThat(mapping.get().getVirtualTables())
                    .allSatisfy(
                        virtualTable ->
                            assertThat(virtualTable.getQuery()).doesNotContain("JOIN"));
              });
        });
  }

  private static XtraServerMapping givenMapping() {
    return new XtraServerMappingBuilder()
        .featureTypeMapping(
            new FeatureTypeMappingBuilder()
                .name("ci:City")
                .primaryTable(
                    new MappingTableBuilder()
                        .name("city_table")
                        .primaryKey("id")
                        .value(value("gml_id", "@gml:id"))
                        .joiningTable(network("AirTransportNetwork"))
                        .joiningTable(network("CableTransportNetwork"))
                        .joiningTable(network("RailwayTransportNetwork"))
                        .build())
                .build())
        .build();
  }

  private static MappingTable network(final String network) {
    return new MappingTableBuilder()
        .name("tn_transportnetwork")
        .primaryKey("id")
        .targetPath("ci:name")
        .predicate(String.format("$T$.network = '%s'", network))
        .joinPath(
            new MappingJoinBuilder()
                .targetPath("ci:name")
                .joinCondition(
                    new MappingJoinBuilder.ConditionBuilder()
                        .sourceTable("city_table")
                        .sourceField("id")
                        .targetTable("tn_transportnetwork")
                        .targetField("rid")
                        .build())
                .build())
        .value(value("network", "ci:name"))
        .build();
  }

  private static MappingValue value(final String column, final String targetPath) {
    return new MappingValueBuilder().column().value(column).targetPath(targetPath).build();
  }

  private static List<String> tableNames(final String xml) {
    return allMatches(TABLE_NAME, xml);
  }

  /** The leftmost segment of a join path is the table the join reaches. */
  private static List<String> joinTargets(final String xml) {
    final List<String> joinTargets = new ArrayList<>();

    for (final String joinPath : allMatches(JOIN_PATH, xml)) {
      joinTargets.add(joinPath.split("/")[0]);
    }

    return joinTargets;
  }

  private static List<String> allMatches(final Pattern pattern, final String xml) {
    final Matcher matcher = pattern.matcher(xml);
    final List<String> matches = new ArrayList<>();

    while (matcher.find()) {
      matches.add(matcher.group(1));
    }

    return matches;
  }
}

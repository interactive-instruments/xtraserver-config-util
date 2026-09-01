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
package de.interactive_instruments.xtraserver.config.io;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.Resources;
import com.greghaskins.spectrum.Spectrum;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
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
import java.util.stream.Collectors;
import org.junit.runner.RunWith;

/**
 * The defect this guards against is only visible in the written file: two primary tables that
 * differ only in their predicate produce two {@code <Table>} rows whose base table name is
 * identical. This checks the written mapping against the virtual tables that back it.
 */
@RunWith(Spectrum.class)
public class PredicateVariantsWriterSpec {

  private static final Pattern TABLE_NAME = Pattern.compile("table_name=\"([^\"]*)\"");

  {
    describe(
        "the written mapping for a feature type with predicate-only primary table variants",
        () -> {
          final AtomicReference<XtraServerMapping> mapping = new AtomicReference<>();
          final AtomicReference<String> xml = new AtomicReference<>();

          beforeAll(
              () -> {
                final URI uri = Resources.getResource("flatten/Cities.xsd").toURI();

                mapping.set(
                    XtraServerMappingTransformer.forMapping(givenCityMapping())
                        .applySchemaInfo(uri)
                        .virtualTables()
                        .fanOutInheritance()
                        .transform());

                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                XtraServerMappingFile.write().mapping(mapping.get()).toStream(outputStream);
                xml.set(new String(outputStream.toByteArray(), StandardCharsets.UTF_8));
              });

          it(
              "should not write any table name carrying a predicate in brackets",
              () -> {
                assertThat(tableNames(xml.get()))
                    .isNotEmpty()
                    .allSatisfy(tableName -> assertThat(tableName).doesNotContain("["));
              });

          it(
              "should reference both variants under their virtual table names",
              () -> {
                assertThat(tableNames(xml.get()))
                    .contains("$vrt_city_table_1$", "$vrt_city_table_2$");
              });

          it(
              "should never write the bare base table name again",
              () -> {
                assertThat(tableNames(xml.get())).doesNotContain("city_table");
              });

          it(
              "should back every $...$ reference in the written mapping with a virtual table",
              () -> {
                final List<String> virtualTableNames =
                    mapping.get().getVirtualTables().stream()
                        .map(VirtualTable::getName)
                        .collect(Collectors.toList());

                final List<String> referenced =
                    tableNames(xml.get()).stream()
                        .filter(name -> name.startsWith("$") && name.endsWith("$"))
                        .map(name -> name.substring(1, name.length() - 1))
                        .distinct()
                        .collect(Collectors.toList());

                assertThat(referenced).isNotEmpty();
                assertThat(virtualTableNames).containsAll(referenced);
              });

          it(
              "should give each virtual table the predicate of its own variant",
              () -> {
                assertThat(
                        mapping.get().getVirtualTables().stream()
                            .map(VirtualTable::getQuery)
                            .collect(Collectors.toList()))
                    .containsExactly(
                        "SELECT city_table.id,city_table.gml_id,city_table.city_name FROM"
                            + " city_table WHERE (city_table.fkt = '1000')",
                        "SELECT city_table.id,city_table.gml_id,city_table.city_name FROM"
                            + " city_table WHERE (city_table.fkt = '2000')");
              });
        });
  }

  private static List<String> tableNames(final String xml) {
    final Matcher matcher = TABLE_NAME.matcher(xml);
    final List<String> tableNames = new ArrayList<>();

    while (matcher.find()) {
      tableNames.add(matcher.group(1));
    }

    return tableNames;
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
}

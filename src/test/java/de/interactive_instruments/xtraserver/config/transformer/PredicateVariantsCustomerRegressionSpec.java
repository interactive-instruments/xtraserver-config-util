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

import com.google.common.collect.ImmutableList;
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
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.runner.RunWith;

/**
 * End to end regression for the two defects a customer hit with the generated VirtualTables, both
 * reduced from their ALKIS o51006 / o51006__spo mapping:
 *
 * <ul>
 *   <li>two feature types over the same physical table derived the same virtual table name, so the
 *       later definition replaced the earlier one and one feature type silently got the other's
 *       filter and column aliases
 *   <li>a joined child's filter was folded onto the main table verbatim, which resolved its columns
 *       against the main table and dropped the relation - {@code o51006.spo = '1115'}
 * </ul>
 */
@RunWith(Spectrum.class)
public class PredicateVariantsCustomerRegressionSpec {

  {
    describe(
        "the reduced o51006 mapping",
        () -> {
          context(
              "two feature types over o51006, one of them with several predicate variants",
              () -> {
                XtraServerMapping transformed = transform(givenMapping());

                it(
                    "should not reference a column of the joined table on the main table",
                    () -> {
                      assertThat(queries(transformed))
                          .allSatisfy(query -> assertThat(query).doesNotContain("o51006.spo"));
                    });

                it(
                    "should express the filter of a child that contributes no value as a"
                        + " correlated subquery over that child",
                    () -> {
                      assertThat(queries(transformed))
                          .anySatisfy(
                              query ->
                                  assertThat(query)
                                      .contains(
                                          "EXISTS (SELECT 1 FROM o51006__spo WHERE"
                                              + " o51006__spo.rid = o51006.id AND"
                                              + " (o51006__spo.spo = '1115') LIMIT 1)"));
                    });

                it(
                    "should give every primary table of every feature type a distinct name",
                    () -> {
                      assertThat(transformed.getFeatureTypeMappings())
                          .allSatisfy(
                              featureTypeMapping ->
                                  assertThat(featureTypeMapping.getPrimaryTableNames())
                                      .doesNotHaveDuplicates());
                    });

                it(
                    "should keep a virtual table for every reference, none overwritten",
                    () -> {
                      assertThat(virtualTableNames(transformed))
                          .containsAll(referencedVirtualTableNames(transformed));
                    });

                it(
                    "should keep both filters on the shared table, not just the last one written",
                    () -> {
                      assertThat(queries(transformed))
                          .anySatisfy(query -> assertThat(query).contains("'1120'"))
                          .anySatisfy(query -> assertThat(query).contains("'1070'"));
                    });

                it(
                    "should select every column that the mappings read back",
                    () -> {
                      assertThat(transformed.getFeatureTypeMappings())
                          .allSatisfy(
                              featureTypeMapping ->
                                  featureTypeMapping
                                      .getPrimaryTables()
                                      .forEach(
                                          table ->
                                              assertThat(queryOf(transformed, table.getName()))
                                                  .satisfies(
                                                      query ->
                                                          table
                                                              .getValues()
                                                              .forEach(
                                                                  value ->
                                                                      assertThat(query)
                                                                          .contains(
                                                                              value.getValue())))));
                    });
              });
        });
  }

  private static XtraServerMapping transform(final XtraServerMapping mapping) throws Exception {
    final URI uri = Resources.getResource("flatten/Cities.xsd").toURI();

    return XtraServerMappingTransformer.forMapping(mapping)
        .applySchemaInfo(uri)
        .cloneColumns()
        .joinTypes()
        .virtualTables()
        .transform();
  }

  /**
   * ci:River stands in for LN_Freizeitanlage - one mapping, its own filter on the joined table.
   * ci:City stands in for LN_Sportanlage - four variants over the same table: no filter, a filter
   * on a child that is read back twice, a filter on a child that is read back not at all, and a
   * filter on a column of the main table.
   */
  private static XtraServerMapping givenMapping() {
    return new XtraServerMappingBuilder()
        .featureTypeMapping(
            new FeatureTypeMappingBuilder()
                .name("ci:River")
                .primaryTable(withJoinedChild(classifications("1120"), "$T$.spo = '1120'"))
                .build())
        .featureTypeMapping(
            new FeatureTypeMappingBuilder()
                .name("ci:City")
                .primaryTable(plain(null))
                .primaryTable(withJoinedChild(classifications("1070", "1080"), "$T$.spo = '1070'"))
                .primaryTable(withJoinedChild(ImmutableList.of(), "$T$.spo = '1115'"))
                .primaryTable(plain("$T$.bwf IN ('1480','1650')"))
                .build())
        .build();
  }

  private static MappingTable plain(final String predicate) {
    return new MappingTableBuilder()
        .name("o51006")
        .primaryKey("id")
        .predicate(predicate)
        .value(column("objid", "@gml:id"))
        .build();
  }

  private static MappingTable withJoinedChild(
      final List<MappingValue> childValues, final String childPredicate) {
    final MappingJoin join =
        new MappingJoinBuilder()
            .joinCondition(
                new MappingJoinBuilder.ConditionBuilder()
                    .sourceTable("o51006")
                    .sourceField("id")
                    .targetTable("o51006__spo")
                    .targetField("rid")
                    .build())
            .targetPath("TODO")
            .build();

    final MappingTable child =
        new MappingTableBuilder()
            .name("o51006__spo")
            .primaryKey("id")
            .predicate(childPredicate)
            .values(childValues)
            .joinPath(join)
            .build();

    return new MappingTableBuilder()
        .shallowCopyOf(plain(null))
        .value(column("objid", "@gml:id"))
        .joiningTable(child)
        .build();
  }

  private static List<MappingValue> classifications(final String... targetCodes) {
    return java.util.Arrays.stream(targetCodes)
        .map(
            code ->
                new MappingValueBuilder()
                    .classification()
                    .keyValue("1070", code)
                    .value("spo")
                    .targetPath("ci:name")
                    .build())
        .collect(Collectors.toList());
  }

  private static MappingValue column(final String value, final String targetPath) {
    return new MappingValueBuilder().column().value(value).targetPath(targetPath).build();
  }

  private static List<String> queries(final XtraServerMapping xtraServerMapping) {
    return xtraServerMapping.getVirtualTables().stream()
        .map(VirtualTable::getQuery)
        .collect(Collectors.toList());
  }

  private static String queryOf(final XtraServerMapping xtraServerMapping, final String tableName) {
    return xtraServerMapping.getVirtualTables().stream()
        .filter(virtualTable -> tableName.equals("$" + virtualTable.getName() + "$"))
        .map(VirtualTable::getQuery)
        .findFirst()
        .orElse("");
  }

  private static List<String> virtualTableNames(final XtraServerMapping xtraServerMapping) {
    return xtraServerMapping.getVirtualTables().stream()
        .map(VirtualTable::getName)
        .collect(Collectors.toList());
  }

  private static List<String> referencedVirtualTableNames(
      final XtraServerMapping xtraServerMapping) {
    return xtraServerMapping.getFeatureTypeMappings().stream()
        .map(FeatureTypeMapping::getPrimaryTableNames)
        .flatMap(List::stream)
        .filter(name -> name.startsWith("$") && name.endsWith("$"))
        .map(name -> name.replaceAll("\\$", ""))
        .collect(Collectors.toList());
  }
}

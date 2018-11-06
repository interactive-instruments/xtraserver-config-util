package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

/**
 * @author zahnen
 */
@RunWith(Spectrum.class)
public class MappingTransformerMultiplicitySpec {

    {

        describe("MappingTransformerMultiplicity", () -> {

            context("one mapping for path with single multiple property", () -> {

                //given
                final XtraServerMapping address = createAddressSingleValue();

                final List<QName> computedProperty = toQualified("ad:locator/ad:AddressLocator/ad:designator");

                final ApplicationSchema applicationSchema = createApplicationSchema(computedProperty);

                it("it should do nothing", () -> {

                    //when
                    final XtraServerMapping actual = new MappingTransformerMultiplicity(address, applicationSchema).transform();

                    //then
                    assertThat(actual).isEqualTo(address);

                });

            });

            context("two different mappings for same path with no multiple property", () -> {

                //given
                final XtraServerMapping address = createAddress(false);

                final List<QName> computedProperty = ImmutableList.of();

                final ApplicationSchema applicationSchema = createApplicationSchema(computedProperty);

                it("it should do nothing", () -> {

                    //when
                    final XtraServerMapping actual = new MappingTransformerMultiplicity(address, applicationSchema).transform();

                    //then
                    assertThat(actual).isEqualTo(address);

                });

            });

            context("two different value mappings for the same path with a multiple property", () -> {

                //given
                final XtraServerMapping address = createAddress(false);

                final List<QName> expectedProperty = toQualified("ad:locator/ad:AddressLocator/ad:designator");

                final ApplicationSchema applicationSchema = createApplicationSchema(expectedProperty);

                it("it should add select_ids=1,2 to the value mappings and append a for_each_select_id mapping for the computed multiple property path", () -> {

                    //when
                    final XtraServerMapping actual = new MappingTransformerMultiplicity(address, applicationSchema).transform();

                    final MappingTable actualPrimaryTable = actual.getFeatureTypeMappings()
                                                                  .get(0)
                                                                  .getPrimaryTables()
                                                                  .get(0);

                    final List<MappingValue> actualValues = actualPrimaryTable
                            .getValues()
                            .asList();

                    final MappingTable forEachSelectId = actualPrimaryTable
                            .getJoiningTables()
                            .asList()
                            .get(0);

                    //then
                    assertThat(actualValues.get(0).getSelectId()).isEqualTo(1);
                    assertThat(actualValues.get(1).getSelectId()).isEqualTo(2);

                    assertThat(forEachSelectId.getName()).isEqualTo(actualPrimaryTable.getName());

                    assertThat(forEachSelectId.getQualifiedTargetPath()).isEqualTo(expectedProperty);

                    assertThat(forEachSelectId.getSelectIds()).isEqualTo("1,2");

                });

            });

            context("two different value mappings for a path that contains a multiple property " +
                    "and two different value mappings for a different path that contains the same multiple property", () -> {

                //given
                final XtraServerMapping address = createAddress(true);

                final List<QName> expectedMultiplePropertyPath = toQualified("ad:locator/ad:AddressLocator/ad:designator");

                final ApplicationSchema applicationSchema = createApplicationSchema(expectedMultiplePropertyPath);

                it("it should add select_ids=1,2 to the value mappings and append a for_each_select_id mapping for the computed multiple property path", () -> {

                    //when
                    final XtraServerMapping actual = new MappingTransformerMultiplicity(address, applicationSchema).transform();

                    final MappingTable actualPrimaryTable = actual.getFeatureTypeMappings()
                                                                  .get(0)
                                                                  .getPrimaryTables()
                                                                  .get(0);

                    final List<MappingValue> actualValues = actualPrimaryTable
                            .getValues()
                            .asList();

                    final MappingTable forEachSelectId = actualPrimaryTable
                            .getJoiningTables()
                            .asList()
                            .get(0);

                    //then
                    assertThat(actualValues.get(0).getSelectId()).isEqualTo(1);
                    assertThat(actualValues.get(1).getSelectId()).isEqualTo(2);
                    assertThat(actualValues.get(2).getSelectId()).isEqualTo(1);
                    assertThat(actualValues.get(3).getSelectId()).isEqualTo(2);

                    assertThat(forEachSelectId.getName()).isEqualTo(actualPrimaryTable.getName());

                    assertThat(forEachSelectId.getQualifiedTargetPath()).isEqualTo(expectedMultiplePropertyPath);

                    assertThat(forEachSelectId.getSelectIds()).isEqualTo("1,2");

                });

            });

            context("two different value mappings for a path that contains a multiple property " +
                    "and two different value mappings for a different path that contains the same multiple property plus another one", () -> {

                //given
                final XtraServerMapping waterCourseLink = createWaterCourseLink();

                final Map<List<QName>, List<QName>> computedMultiplePropertyPaths = ImmutableMap.of(
                        toQualified("hy-n:geographicalName/gn:GeographicalName/gn:nameStatus/@xlink:href"), toQualified("hy-n:geographicalName"),
                        toQualified("hy-n:geographicalName/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text"), toQualified("hy-n:geographicalName/gn:GeographicalName/gn:spelling")
                );

                final List<QName> expectedMultiplePropertyPath = toQualified("hy-n:geographicalName");

                final ApplicationSchema applicationSchema = createApplicationSchema(computedMultiplePropertyPaths);

                it("it should add select_ids=1,2 to the value mappings and append exactly one for_each_select_id mapping for the shared multiple property path", () -> {

                    //when
                    final XtraServerMapping actual = new MappingTransformerMultiplicity(waterCourseLink, applicationSchema).transform();

                    final MappingTable actualPrimaryTable = actual.getFeatureTypeMappings()
                                                                  .get(0)
                                                                  .getPrimaryTables()
                                                                  .get(0);

                    final List<MappingValue> actualValues = actualPrimaryTable
                            .getValues()
                            .asList();

                    final MappingTable forEachSelectId = actualPrimaryTable
                            .getJoiningTables()
                            .asList()
                            .get(0);

                    //then
                    assertThat(actualValues.get(0).getSelectId()).isEqualTo(1);
                    assertThat(actualValues.get(1).getSelectId()).isEqualTo(2);
                    assertThat(actualValues.get(2).getSelectId()).isEqualTo(1);
                    assertThat(actualValues.get(3).getSelectId()).isEqualTo(2);

                    assertThat(actualPrimaryTable.getJoiningTables()).hasSize(1);

                    assertThat(forEachSelectId.getName()).isEqualTo(actualPrimaryTable.getName());

                    assertThat(forEachSelectId.getQualifiedTargetPath()).isEqualTo(expectedMultiplePropertyPath);

                    assertThat(forEachSelectId.getSelectIds()).isEqualTo("1,2");

                });

            });

        });

    }

    private ApplicationSchema createApplicationSchema(final List<QName> expectedProperty) {
        final ApplicationSchema applicationSchema = createApplicationSchema();
        Mockito.when(applicationSchema.getLastMultiplePropertyPath(ArgumentMatchers.any(), ArgumentMatchers.any()))
               .thenReturn(expectedProperty);

        return applicationSchema;
    }

    private ApplicationSchema createApplicationSchema(final Map<List<QName>, List<QName>> expectedProperties) {
        final ApplicationSchema applicationSchema = createApplicationSchema();
        expectedProperties.forEach((key, value) -> Mockito.when(applicationSchema.getLastMultiplePropertyPath(ArgumentMatchers.any(), eq(key)))
                                                      .thenReturn(value));

        return applicationSchema;
    }

    private ApplicationSchema createApplicationSchema() {
        final ApplicationSchema applicationSchema = mock(ApplicationSchema.class);
        Mockito.when(applicationSchema.getNamespaces())
               .thenReturn(mock(Namespaces.class));

        return applicationSchema;
    }

    private XtraServerMapping createAddressSingleValue() {

        final MappingTable primaryTable = new MappingTableBuilder().name("o12006")
                                                                   .primaryKey("id")
                                                                   .value(new MappingValueBuilder().column()
                                                                                             .value("adz")
                                                                                             .targetPath("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:designator")
                                                                                             .qualifiedTargetPath(toQualified("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:designator"))
                                                                                             .build())
                                                                   .build();

        final FeatureTypeMapping address = new FeatureTypeMappingBuilder().name("ad:Address")
                                                                          .qualifiedName(new QName("ad", "Address"))
                                                                          .primaryTable(primaryTable)
                                                                          .build();

        return new XtraServerMappingBuilder().featureTypeMapping(address)
                                             .build();
    }

    private XtraServerMapping createAddress(final boolean addTypes) {

        final List<MappingValue> designators = ImmutableList.of(
                new MappingValueBuilder().column()
                                         .value("adz")
                                         .targetPath("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:designator")
                                         .qualifiedTargetPath(toQualified("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:designator"))
                                         .build(),
                new MappingValueBuilder().column()
                                         .value("hnr")
                                         .targetPath("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:designator")
                                         .qualifiedTargetPath(toQualified("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:designator"))
                                         .build()
        );

        final List<MappingValue> types = ImmutableList.of(
                new MappingValueBuilder().constant()
                                         .value("http://inspire.ec.europa.eu/codelist/LocatorDesignatorTypeValue/addressNumber")
                                         .targetPath("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:type/@xlink:href")
                                         .qualifiedTargetPath(toQualified("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:type/@xlink:href"))
                                         .build(),
                new MappingValueBuilder().constant()
                                         .value("http://inspire.ec.europa.eu/codelist/LocatorDesignatorTypeValue/addressNumberExtension")
                                         .targetPath("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:type/@xlink:href")
                                         .qualifiedTargetPath(toQualified("ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator/ad:type/@xlink:href"))
                                         .build()
        );

        final List<MappingValue> values = addTypes ? new ImmutableList.Builder<MappingValue>().addAll(designators)
                                                                                              .addAll(types)
                                                                                              .build() : designators;


        final MappingTable primaryTable = new MappingTableBuilder().name("o12006")
                                                                   .primaryKey("id")
                                                                   .values(values)
                                                                   .build();

        final FeatureTypeMapping address = new FeatureTypeMappingBuilder().name("ad:Address")
                                                                          .qualifiedName(new QName("ad", "Address"))
                                                                          .primaryTable(primaryTable)
                                                                          .build();

        return new XtraServerMappingBuilder().featureTypeMapping(address)
                                             .build();
    }

    private XtraServerMapping createWaterCourseLink() {

        final List<MappingValue> values = ImmutableList.of(
                new MappingValueBuilder().constant()
                                         .value("http://inspire.ec.europa.eu/codelist/NameStatusValue/official")
                                         .targetPath("hy-n:geographicalName/gn:GeographicalName/gn:nameStatus/@xlink:href")
                                         .qualifiedTargetPath(toQualified("hy-n:geographicalName/gn:GeographicalName/gn:nameStatus/@xlink:href"))
                                         .build(),
                new MappingValueBuilder().constant()
                                         .value("http://inspire.ec.europa.eu/codelist/NameStatusValue/other")
                                         .targetPath("hy-n:geographicalName/gn:GeographicalName/gn:nameStatus/@xlink:href")
                                         .qualifiedTargetPath(toQualified("hy-n:geographicalName/gn:GeographicalName/gn:nameStatus/@xlink:href"))
                                         .build(),
                new MappingValueBuilder().column()
                                         .value("nam")
                                         .targetPath("hy-n:geographicalName/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text")
                                         .qualifiedTargetPath(toQualified("hy-n:geographicalName/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text"))
                                         .build(),
                new MappingValueBuilder().column()
                                         .value("znm")
                                         .targetPath("hy-n:geographicalName/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text")
                                         .qualifiedTargetPath(toQualified("hy-n:geographicalName/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text"))
                                         .build()
        );

        final MappingTable primaryTable = new MappingTableBuilder().name("$vrt_o44004_o44004__p0000103000_o44002$")
                                                                   .primaryKey("id")
                                                                   .values(values)
                                                                   .build();

        final FeatureTypeMapping address = new FeatureTypeMappingBuilder().name("hy-n:WatercourseLink")
                                                                          .qualifiedName(new QName("hy-n", "WatercourseLink"))
                                                                          .primaryTable(primaryTable)
                                                                          .build();

        return new XtraServerMappingBuilder().featureTypeMapping(address)
                                             .build();
    }

    private static List<QName> toQualified(final String path) {
        return Splitter.on('/')
                       .splitToList(path)
                       .stream()
                       .map(element -> {
                           List<String> qn = Splitter.on(':')
                                                     .splitToList(element);
                           return new QName(qn.get(0), qn.get(1));
                       })
                       .collect(Collectors.toList());
    }
}
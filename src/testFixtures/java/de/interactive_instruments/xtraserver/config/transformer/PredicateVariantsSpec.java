package de.interactive_instruments.xtraserver.config.transformer;

import static de.interactive_instruments.xtraserver.config.api.Dsl.mappingOf;
import static de.interactive_instruments.xtraserver.config.api.Dsl.predicate;
import static de.interactive_instruments.xtraserver.config.api.Dsl.table;
import static de.interactive_instruments.xtraserver.config.api.Dsl.value;

import de.interactive_instruments.xtraserver.config.api.Spec;
import de.interactive_instruments.xtraserver.config.api.UseCase;
import de.interactive_instruments.xtraserver.config.api.VirtualTable;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;

public class PredicateVariantsSpec {

  public static Spec get() {
    return Spec.builder()
        .title("PredicateVariants")
        .description(
            "XtraServer does not support a feature type with several primary tables that share a"
                + " table name and differ only in their predicate. Each of them becomes its own"
                + " virtual table, so they appear as distinct tables in the mapping file.")
        .transform(mapping -> new MappingTransformerPredicateVariants(mapping).transform())
        .useCase(
            UseCase.builder()
                .title("sameMainTableDifferentPredicates")
                .description("")
                .given("given", given())
                .expected("expected", expected())
                .build())
        .useCase(
            UseCase.builder()
                .title("sameMainTableDifferentPredicatesVirtualTables")
                .description("")
                .virtualTables()
                .given("given", given())
                .expected("expected", expected())
                .build())
        .build();
  }

  private static XtraServerMapping given() {
    return mappingOf(
        predicate("$T$.fkt = '1000'", table("o61001", value("objid", "ft:objid"))),
        predicate("$T$.fkt = '2000'", table("o61001", value("objid", "ft:objid"))));
  }

  private static XtraServerMapping expected() {
    return new XtraServerMappingBuilder()
        .copyOf(
            mappingOf(
                table("$vrt_o61001_1$", value("objid", "ft:objid")),
                table("$vrt_o61001_2$", value("objid", "ft:objid"))))
        .virtualTable(virtualTable("vrt_o61001_1", "o61001.fkt = '1000'"))
        .virtualTable(virtualTable("vrt_o61001_2", "o61001.fkt = '2000'"))
        .build();
  }

  private static VirtualTable virtualTable(final String name, final String whereClause) {
    return VirtualTable.builder()
        .name(name)
        .primaryTable("o61001")
        .addPrimaryKeyColumns("o61001.id")
        .addColumns("o61001.objid")
        .addWhereClause(whereClause)
        .build();
  }
}

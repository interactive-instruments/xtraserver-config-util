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
            "XtraServer resolves tables by name when it builds the joins for a feature type, so a"
                + " main table written as t[predicate] is ambiguous with every other use of t."
                + " Every main table that carries a predicate therefore becomes its own virtual"
                + " table, and tables sharing its name follow.")
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
        .useCase(
            UseCase.builder()
                .title("singleMainTableWithPredicate")
                .description("")
                .given("given", givenSingle())
                .expected("expected", expectedSingle())
                .build())
        .useCase(
            UseCase.builder()
                .title("singleMainTableWithPredicateVirtualTables")
                .description("")
                .virtualTables()
                .given("given", givenSingle())
                .expected("expected", expectedSingle())
                .build())
        .useCase(
            UseCase.builder()
                .title("joinedTableWithPredicate")
                .description("")
                .given("given", givenJoined())
                .expected("expected", expectedJoined())
                .build())
        .useCase(
            UseCase.builder()
                .title("joinedTableWithPredicateVirtualTables")
                .description("")
                .virtualTables()
                .given("given", givenJoined())
                .expected("expected", expectedJoined())
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

  private static XtraServerMapping givenSingle() {
    return mappingOf(
        predicate("$T$.fkt = '1000'", table("o61001", value("objid", "ft:objid"))));
  }

  private static XtraServerMapping expectedSingle() {
    return new XtraServerMappingBuilder()
        .copyOf(mappingOf(table("$vrt_o61001_1$", value("objid", "ft:objid"))))
        .virtualTable(virtualTable("vrt_o61001_1", "o61001.fkt = '1000'"))
        .build();
  }

  private static XtraServerMapping givenJoined() {
    return mappingOf(
        table(
            "o61001",
            predicate(
                "$T$.zus IS NULL",
                table("o02341", "ft:child", value("position", "ft:child/ft:position")))));
  }

  private static XtraServerMapping expectedJoined() {
    return new XtraServerMappingBuilder()
        .copyOf(
            mappingOf(
                table(
                    "o61001",
                    table(
                        "$vrt_o02341_1$",
                        "ft:child",
                        value("position", "ft:child/ft:position")))))
        .virtualTable(
            VirtualTable.builder()
                .name("vrt_o02341_1")
                .primaryTable("o02341")
                .addPrimaryKeyColumns("o02341.id")
                .addColumns("o02341.id")
                .addColumns("o02341.position")
                .addWhereClause("o02341.zus IS NULL")
                .build())
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

package de.interactive_instruments.xtraserver.config.transformer;

import static de.interactive_instruments.xtraserver.config.api.Dsl.hint;
import static de.interactive_instruments.xtraserver.config.api.Dsl.mapping;
import static de.interactive_instruments.xtraserver.config.api.Dsl.table;
import static de.interactive_instruments.xtraserver.config.api.Dsl.value;
import static de.interactive_instruments.xtraserver.config.transformer.MappingTransformerJoinTypeHint.HINT_JOIN_TYPE;

import de.interactive_instruments.xtraserver.config.api.Spec;
import de.interactive_instruments.xtraserver.config.api.UseCase;

public class MergeLeftJoinSpec {

  public Spec other(String bla) {
    return get();
  }

  public static Spec get() {
    return Spec.builder()
        .title("MergeLeftJoin")
        .description("bla bla")
        .transform(
            mapping ->
                new MappingTransformerMergeTables(
                        new MappingTransformerJoinTypeHint(mapping, (ft, prop) -> true).transform())
                    .transform())
        .useCase(
            UseCase.builder()
                .title("optionalColumnsDifferentPaths")
                .description("")
                .virtualTables()
                .given(
                    "given",
                    mapping(
                        table(
                            "o31001",
                            table(
                                "o31001_bja",
                                value(
                                    "bja",
                                    "bu-base:dateOfConstruction/bu-base:DateOfEvent/bu-base:end"),
                                value(
                                    "bja",
                                    "bu-base:dateOfRenovation/bu-base:DateOfEvent/bu-base:end")))))
                .expected(
                    "expected",
                    new MappingTransformerMergeTables(
                            mapping(
                                table(
                                    "o31001",
                                    hint(
                                        HINT_JOIN_TYPE,
                                        "LEFT",
                                        table(
                                            "o31001_bja",
                                            value(
                                                "bja",
                                                "bu-base:dateOfConstruction/bu-base:DateOfEvent/bu-base:end"),
                                            value(
                                                "bja",
                                                "bu-base:dateOfRenovation/bu-base:DateOfEvent/bu-base:end"))))))
                        .transform())
                .build())
        .build();
  }
}

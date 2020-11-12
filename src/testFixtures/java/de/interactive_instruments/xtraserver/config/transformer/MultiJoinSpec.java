package de.interactive_instruments.xtraserver.config.transformer;

import static de.interactive_instruments.xtraserver.config.api.Dsl.hint;
import static de.interactive_instruments.xtraserver.config.api.Dsl.mapping;
import static de.interactive_instruments.xtraserver.config.api.Dsl.table;
import static de.interactive_instruments.xtraserver.config.api.Dsl.value;
import static de.interactive_instruments.xtraserver.config.transformer.MappingTransformerMultiJoins.HINT_MULTI_JOIN;

import de.interactive_instruments.xtraserver.config.api.Spec;
import de.interactive_instruments.xtraserver.config.api.UseCase;

public class MultiJoinSpec {

  public Spec other(String bla) {
    return get();
  }

  public static Spec get() {
    return Spec.builder()
        .title("MultiJoin")
        .description("bla bla")
        .transform(mapping -> new MappingTransformerMultiJoins(mapping).transform())
        .useCase(
            UseCase.builder()
                .title("sameColumnDifferentPaths")
                .description(
                    "The same column in a joined table is mapped to multiple paths with different roots")
                .given(
                    "given",
                    mapping(
                        table(
                            "o51001",
                            hint(
                                HINT_MULTI_JOIN,
                                table(
                                    "o51001_bwf",
                                    value("bwf", "bu-base:buildingNature/@xlink:href"),
                                    value(
                                        "bwf",
                                        "bu-base:currentUse/bu-base:CurrentUse/bu-base:currentUse/@xlink:href"))))))
                .expected(
                    "expected",
                    mapping(
                        table(
                            "o51001",
                            table(
                                "o51001_bwf",
                                "bu-base:buildingNature",
                                value("bwf", "bu-base:buildingNature/@xlink:href")),
                            table(
                                "o51001_bwf",
                                "bu-base:currentUse",
                                value(
                                    "bwf",
                                    "bu-base:currentUse/bu-base:CurrentUse/bu-base:currentUse/@xlink:href")))))
                .build())
        .build();
  }

}

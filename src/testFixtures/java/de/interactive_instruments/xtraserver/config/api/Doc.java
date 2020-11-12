package de.interactive_instruments.xtraserver.config.api;

import java.util.List;
import java.util.stream.Collectors;

public class Doc {

  public static String from(Spec spec) {
    return String.format(
        "# %s\n\n%s\n\n%s\n\n",
        spec.title(),
        spec.description(),
        spec.useCases().stream().map(Doc::from).collect(Collectors.joining("\n")));
  }

  public static String from(UseCase useCase) {
    return String.format(
        "## %s\n\n%s\n\n### Before\n\n %s\n\n```xml\n%s```\n\n### After\n\n %s\n\n```xml\n%s```\n\n",
        useCase.title(),
        useCase.description(),
        useCase.givenDescription(),
        useCase.isVirtualTables() ? from(useCase.given().getVirtualTables()) : from(useCase.given()),
        useCase.expectedDescription(),
        useCase.isVirtualTables() ? from(useCase.expected().getVirtualTables()) : from(useCase.expected()));
  }

  public static String from(XtraServerMapping xtraServerMapping) {
    return xtraServerMapping.getFeatureTypeMappings().stream()
        .map(Doc::from)
        .collect(Collectors.joining("\n"));
  }

  public static String from(List<VirtualTable> virtualTables) {
    return virtualTables.stream()
        .map(Doc::from)
        .collect(Collectors.joining("\n"));
  }

  public static String from(FeatureTypeMapping featureTypeMapping) {
    return featureTypeMapping.getPrimaryTables().stream()
        .map(Doc::from)
        .collect(Collectors.joining("\n"));
  }

  public static String from(VirtualTable virtualTable) {
    return String.format(
        "<VirtualTable name=\"%s\" query=\"%s\"/>",
        virtualTable.getName(), virtualTable.getQuery());
  }

  public static String from(MappingTable mappingTable) {
    String join =
        mappingTable.getJoinPaths().isEmpty()
            ? ""
            : String.format(
                "<Join target=\"%s\" axis=\"parent\" join_path=\"%s/ref(%s:%s)::%s\"/>\n",
                //TODO: targetPath not set in transformer
                mappingTable.getQualifiedTargetPath(),
                mappingTable.getJoinPaths().asList().get(0).getSourceTable(),
                mappingTable
                    .getJoinPaths()
                    .asList()
                    .get(0)
                    .getJoinConditions()
                    .get(0)
                    .getSourceField(),
                mappingTable.getJoinPaths().asList().get(0).getTargetTable(),
                mappingTable
                    .getJoinPaths()
                    .asList()
                    .get(0)
                    .getJoinConditions()
                    .get(0)
                    .getTargetField());

    String values =
        mappingTable.getValues().isEmpty()
            ? ""
            : mappingTable.getValues().stream()
                .map(value -> from(mappingTable.getName(), value))
                .collect(Collectors.joining("\n", "", "\n"));

    String joining =
        mappingTable.getJoiningTables().isEmpty()
            ? ""
            : mappingTable.getJoiningTables().stream()
                .map(Doc::from)
                .collect(Collectors.joining("\n", values.isEmpty() ? "" : "\n", ""));

    return String.format("%s%s%s", join, values, joining);
  }

  public static String from(String table, MappingValue mappingValue) {
    return String.format(
        "<Table table_name=\"%s\" target=\"%s\" value=\"%s\"/>",
        table, mappingValue.getTargetPath(), mappingValue.getValue());
  }
}

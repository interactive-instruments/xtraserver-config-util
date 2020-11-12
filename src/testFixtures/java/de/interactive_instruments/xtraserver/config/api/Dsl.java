package de.interactive_instruments.xtraserver.config.api;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;

public class Dsl {

  public static MappingTable hint(String transformationHint, MappingTable table) {
    return new MappingTableBuilder()
        .copyOf(table)
        .transformationHint(transformationHint, "true")
        .build();
  }

  public static MappingTable hint(String transformationHint, String hintValue, MappingTable table) {
    return new MappingTableBuilder()
        .copyOf(table)
        .transformationHint(transformationHint, hintValue)
        .build();
  }

  public static MappingJoin join(String source, String target, Map<String,String> transformationHints) {
    return new MappingJoinBuilder()
        .joinCondition(
            new MappingJoinBuilder.ConditionBuilder()
                .sourceTable(source)
                .sourceField(PRIMARY_KEY)
                .targetTable(target)
                .targetField(PRIMARY_KEY)
                .build())
        .targetPath("NONE")
        .transformationHints(transformationHints)
        .build();
  }

  public static MappingValue value(String column, String path) {
    return new MappingValueBuilder()
        .column()
        .value(column)
        .targetPath(path)
        .qualifiedTargetPath(toQualifiedPath(path))
        .build();
  }

  public static MappingTable table(String name, MappingTable... joiningTables) {
    return new MappingTableBuilder()
        .name(name)
        .primaryKey(PRIMARY_KEY)
        .joiningTables(
            Arrays.stream(joiningTables)
                .map(
                    table ->
                        new MappingTableBuilder()
                            .copyOf(table)
                            .clearJoinPaths()
                            .joinPath(join(name, table.getName(), table.getTransformationHints()))
                            .build())
                .collect(Collectors.toList()))
        .build();
  }

  public static MappingTable table(String name, String path, MappingValue... values) {
    return new MappingTableBuilder()
        .name(name)
        .primaryKey(PRIMARY_KEY)
        .targetPath(path)
        .qualifiedTargetPath(toQualifiedPath(path))
        .joinPath(join("TODO", name, ImmutableMap.of()))
        .values(Arrays.asList(values))
        .build();
  }

  public static MappingTable table(String name, MappingValue... values) {
    return new MappingTableBuilder()
        .name(name)
        .primaryKey(PRIMARY_KEY)
        .values(Arrays.asList(values))
        .build();
  }

  public static FeatureTypeMapping createFeatureTypeMapping(
      String name, MappingTable primaryTable) {
    return new FeatureTypeMappingBuilder()
        .name(name)
        .qualifiedName(toQualifiedName(name))
        .primaryTable(primaryTable)
        .build();
  }

  public static FeatureTypeMapping createFeatureTypeMapping(MappingTable primaryTable) {
    return createFeatureTypeMapping("test:FeatureType", primaryTable);
  }

  public static XtraServerMapping mapping(FeatureTypeMapping featureTypeMapping) {
    return new XtraServerMappingBuilder().featureTypeMapping(featureTypeMapping).build();
  }

  public static XtraServerMapping mapping(MappingTable primaryTable) {
    return mapping(createFeatureTypeMapping(primaryTable));
  }

  public static XtraServerMapping mapping(String name, MappingTable primaryTable) {
    return mapping(createFeatureTypeMapping(name, primaryTable));
  }

  public static List<QName> toQualifiedPath(final String path) {
    return SPLITTER_SLASH.splitToList(path).stream()
        .map(Dsl::toQualifiedName)
        .collect(Collectors.toList());
  }

  public static QName toQualifiedName(final String element) {
    List<String> qn = SPLITTER_COLON.splitToList(element);
    return new QName(qn.get(0), qn.get(1));
  }

  private static final Splitter SPLITTER_COLON = Splitter.on(':');
  private static final Splitter SPLITTER_SLASH = Splitter.on('/');
  private static final String PRIMARY_KEY = "id";
}

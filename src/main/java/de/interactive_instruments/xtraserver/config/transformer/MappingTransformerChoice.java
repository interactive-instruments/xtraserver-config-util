package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import javax.xml.namespace.QName;

public class MappingTransformerChoice extends AbstractMappingTransformer {

  final ApplicationSchema applicationSchema;

  MappingTransformerChoice(
      XtraServerMapping xtraServerMapping, final ApplicationSchema applicationSchema) {
    super(xtraServerMapping);
    this.applicationSchema = applicationSchema;
  }

  @Override
  protected MappingTableBuilder transformMappingTable(
      Context context,
      List<MappingTable> transformedMappingTables,
      List<MappingJoin> transformedMappingJoins,
      List<MappingValue> transformedMappingValues) {
    final FeatureTypeMapping featureTypeMapping = context.featureTypeMapping;
    final MappingTable mappingTable = context.mappingTable;

    List<MappingValue> transformedMappingValues2 =
        transformedMappingValues.stream()
            .sorted(Comparator.comparing(MappingValue::getTargetPath))
            .collect(
                new ChoiceCollector(featureTypeMapping.getQualifiedName()));

    return new MappingTableBuilder()
        .shallowCopyOf(mappingTable)
        .values(transformedMappingValues2)
        .joiningTables(transformedMappingTables)
        .joinPaths(transformedMappingJoins);
  }

  private class ChoiceCollector extends AbstractMappingValueCollector {

    private final QName currentFeatureType;

    public ChoiceCollector(final QName currentFeatureType) {
      this.currentFeatureType = currentFeatureType;
    }

    @Override
    protected void accumulate(List<MappingValue> values, MappingValue currentValue) {
      final Optional<MappingValue> optionalPreviousChoiceValue =
          values.stream()
              .filter(
                  value -> haveSameNonMultipleTargetPathWithDifferentValues(value, currentValue))
              .reduce((first, second) -> second); // findLast

      if (optionalPreviousChoiceValue.isPresent()) {
        MappingValue previousChoiceValue = optionalPreviousChoiceValue.get();
        if (Objects.isNull(previousChoiceValue.getPredicate())) { // no select id
          int i = values.indexOf(previousChoiceValue);
          previousChoiceValue =
              new MappingValueBuilder()
                  .copyOf(previousChoiceValue)
                  .predicate(createPredicate(previousChoiceValue)) // set select id
                  .build();
          values.set(i, previousChoiceValue); // set select id
        }

        final MappingValue mappingValue2 =
            new MappingValueBuilder()
                .copyOf(currentValue)
                .predicate(createPredicate(previousChoiceValue.getPredicate(),
                    currentValue)) // set select id
                .build();
        values.add(mappingValue2);
      } else {
        values.add(currentValue);
      }
    }

    private String createPredicate(MappingValue choiceValue) {
      return String.format("$T$.%s IS NOT NULL", choiceValue.getValue());
    }

    private String createPredicate(String previousPredicate, MappingValue choiceValue) {
      String negatedPreviousPredicate = previousPredicate.replaceAll("NOT NULL", "NULL");

      if (choiceValue.isConstant()) {
        return negatedPreviousPredicate;
      }

      return String.format(
          "%s AND $T$.%s IS NOT NULL", negatedPreviousPredicate, choiceValue.getValue());
    }

    private boolean haveSameNonMultipleTargetPathWithDifferentValues(
        MappingValue value1, MappingValue value2) {
      return Objects.equals(value1.getTargetPath(), value2.getTargetPath())
          && !Objects.equals(value1, value2)
          && (value1.isColumn()
              || value1.isConstant()
              || (value1.isClassification() && !value1.isNil()))
          && (value2.isColumn()
              || value2.isConstant()
              || (value2.isClassification() && !value2.isNil()))
          && (value2.getTransformationHints().containsKey("CHOICE")
              || applicationSchema
                  .getLastMultiplePropertyPath(currentFeatureType, value2.getQualifiedTargetPath())
                  .isEmpty());
    }
  }
}

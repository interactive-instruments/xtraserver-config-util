package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.Hints;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
            .sorted((mappingValue, mappingValue2) -> {
              if (Objects.equals(mappingValue.getTargetPath(), mappingValue2.getTargetPath())) {
                return Integer.compare(getPriority(mappingValue), getPriority(mappingValue2)) * -1;
              }
              return mappingValue.getTargetPath().compareTo(mappingValue2.getTargetPath());
            })
            .collect(
                new ChoiceCollector(featureTypeMapping.getQualifiedName()));

    return new MappingTableBuilder()
        .shallowCopyOf(mappingTable)
        .values(transformedMappingValues2)
        .joiningTables(transformedMappingTables)
        .joinPaths(transformedMappingJoins);
  }

  private int getPriority(MappingValue mappingValue) {
    return Integer.parseInt(mappingValue.getTransformationHints().getOrDefault(Hints.PRIORITY, "0"));
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
                .predicate(createPredicate(previousChoiceValue, currentValue)) // set select id
                .build();
        values.add(mappingValue2);
      } else {
        values.add(currentValue);
      }
    }

    private String createPredicate(MappingValue choiceValue) {
      if (choiceValue.isConstant()) {
        if (choiceValue.getTransformationHints().containsKey(Hints.BOUND)) {
          return String.format("$T$.%s IS NOT NULL", choiceValue.getTransformationHints().get(Hints.BOUND));
        }
        return null;
      }
      return String.format("$T$.%s IS NOT NULL", choiceValue.getValue());
    }

    private String createPredicate(MappingValue previousChoiceValue, MappingValue choiceValue) {
      String previousPredicate = previousChoiceValue.getPredicate();

      // if previous is constant without predicate, it will be used anyway
      if (Objects.isNull(previousPredicate)) {
        return null;
      }

      String negatedPreviousPredicate = previousPredicate.replaceAll("NOT NULL", "NULL");

      String predicate = createPredicate(choiceValue);

      if (Objects.isNull(predicate)) {
        //don't use negated predicate from constant for constant
        if (previousChoiceValue.isConstant()) {
          return null;
        }
        return negatedPreviousPredicate;
      }

      return String.format(
          "%s AND %s", negatedPreviousPredicate, predicate);
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
          && (value2.getTransformationHints().containsKey(Hints.CHOICE)
              || applicationSchema
                  .getLastMultiplePropertyPath(currentFeatureType, value2.getQualifiedTargetPath())
                  .isEmpty());
    }
  }
}

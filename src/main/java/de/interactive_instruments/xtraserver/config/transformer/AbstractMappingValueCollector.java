package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

public abstract class AbstractMappingValueCollector implements
    Collector<MappingValue, List<MappingValue>, List<MappingValue>> {

  protected abstract void accumulate(List<MappingValue> values, MappingValue currentValue);

  public Supplier<List<MappingValue>> supplier() {
    return ArrayList::new;
  }

  public BiConsumer<List<MappingValue>, MappingValue> accumulator() {
    return this::accumulate;
  }

  public BinaryOperator<List<MappingValue>> combiner() {
    return (left, right) -> {
      left.addAll(right);
      return left;
    };
  }

  public Function<List<MappingValue>, List<MappingValue>> finisher() {
    return mappingValues -> mappingValues;
  }

  public Set<Characteristics> characteristics() {
    return EnumSet.of(Characteristics.UNORDERED);
  }
}

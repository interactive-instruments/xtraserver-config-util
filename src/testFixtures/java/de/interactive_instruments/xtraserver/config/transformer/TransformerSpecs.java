package de.interactive_instruments.xtraserver.config.transformer;

import com.google.common.collect.ImmutableList;
import de.interactive_instruments.xtraserver.config.api.Spec;
import java.util.List;

public class TransformerSpecs {

  public static List<Spec> get() {
    return ImmutableList.of(MultiJoinSpec.get(), MergeLeftJoinSpec.get());
  }

}

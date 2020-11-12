package de.interactive_instruments.xtraserver.config.transformer;

import java.util.List;
import javax.xml.namespace.QName;

public interface SchemaInfo {

  interface OptionalProperty {
    boolean isOptional(final QName qualifiedTypeName, final List<QName> propertyPath);
  }

  interface MultipleProperty {
    boolean isMultiple(final QName qualifiedTypeName, final List<QName> propertyPath);
  }
}

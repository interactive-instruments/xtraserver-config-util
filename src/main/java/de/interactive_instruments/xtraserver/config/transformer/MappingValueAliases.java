package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MappingValueAliases {

    private final Map<String,Integer> nameCounter;

    public MappingValueAliases() {
        this.nameCounter = new LinkedHashMap<>();
    }


    public MappingValue applyAliasIfNecessary(MappingValue value) {

        if (value.isConstant()) {
            return value;
        }

        MappingValue valueWithAlias = value;

        for (String name: value.getValueColumns()) {
            String nameWithAlias = getWithAlias(name);

            if (!Objects.equals(name, nameWithAlias)) {
                value = new MappingValueBuilder().copyOf(value).value(value.getValue().replaceAll(name, nameWithAlias)).build();
            }
        }

        return valueWithAlias;
    }

    public String getWithAlias(String name) {

        if (nameCounter.containsKey(name)) {
            int count = nameCounter.get(name) + 1;
            nameCounter.put(name, count);

            return String.format("%s_%s", name, count);
        } else  {
            nameCounter.put(name, 0);
        }

        return name;
    }

    public String getWithAsAlias(String name) {
        String nameWithAlias = getWithAlias(name);

        if (!Objects.equals(name, nameWithAlias)) {
            return String.format("%s AS %s", name, nameWithAlias);
        }

        return name;
    }
}

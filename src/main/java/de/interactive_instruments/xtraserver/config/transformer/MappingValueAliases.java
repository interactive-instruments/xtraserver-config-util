package de.interactive_instruments.xtraserver.config.transformer;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MappingValueAliases {

    private final Map<String, List<String>> nameCounter;

    public MappingValueAliases() {
        this.nameCounter = new LinkedHashMap<>();
    }


    public MappingValue applyAliasIfNecessary(String table, MappingValue value) {

        if (value.isConstant()) {
            return value;
        }

        MappingValue valueWithAlias = value;

        for (String column: new LinkedHashSet<>(value.getValueColumns())) {
            String columnWithAlias = getWithAlias(table, column);

            if (!Objects.equals(column, columnWithAlias)) {
                valueWithAlias = new MappingValueBuilder().copyOf(valueWithAlias).value(valueWithAlias.getValue().replaceAll(column, columnWithAlias)).build();
            }
        }

        return valueWithAlias;
    }

    public String getWithAlias(String table, String column) {

        if (nameCounter.containsKey(column)) {
            if (!nameCounter.get(column).contains(table)) {
                nameCounter.get(column).add(table);
            }

            int count = nameCounter.get(column).indexOf(table);

            if (count > 0) {
                return String.format("%s_%s", column, count);
            }
        } else  {
            nameCounter.put(column, new ArrayList<>());
            nameCounter.get(column).add(table);
        }

        return column;
    }

    public String getWithAsAlias(String table, String column) {
        String name = String.format("%s.%s", table, column);
        String columnAlias = getWithAlias(table, column);

        if (!Objects.equals(column, columnAlias)) {
            return String.format("%s AS %s", name, columnAlias);
        }

        return name;
    }
}

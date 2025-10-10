package com.mycompany.sample.frontend.components.tables.tableUtil;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.mycompany.sample.backend.enums.ColumnType;

public final class ColumnMeta<T> {

    private final String title;
    private final ColumnType type;
    private final Function<T, ?> getter;
    private final BiConsumer<T, ?> setter;
    private final List<String> validator;

    public <V> ColumnMeta(String title, ColumnType type, Function<T, V> getter,
            BiConsumer<T, V> setter, List<String> validator) {
        this.title = Objects.requireNonNull(title);
        this.type = Objects.requireNonNull(type);
        this.getter = Objects.requireNonNull(getter);
        this.setter = setter;
        this.validator = validator != null ? List.copyOf(validator) : List.of();
    }

    public String getTitle() {
        return title;
    }

    public ColumnType getType() {
        return type;
    }

    public Function<T, ?> getGetter() {
        return getter;
    }

    public BiConsumer<T, ?> getSetter() {
        return setter;
    }

    public List<String> getValidator() {
        return validator;
    }

}

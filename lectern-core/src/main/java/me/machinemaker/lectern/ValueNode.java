package me.machinemaker.lectern;

import io.leangen.geantyref.TypeToken;
import me.machinemaker.lectern.validations.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public final class ValueNode<T> extends Node {

    private final TypeToken<T> type;
    private final List<ValueValidator<T>> validators;
    private final T defaultValue;
    private T value;

    ValueNode(@NotNull String key, @NotNull TypeToken<T> type, @Nullable T defaultValue) {
        this(key, Collections.emptyList(), type, defaultValue);
    }

    ValueNode(@NotNull String key, @Nullable String description, @NotNull TypeToken<T> type, @Nullable T defaultValue) {
        this(key, description, Collections.emptyList(), type, defaultValue);
    }

    ValueNode(@NotNull String key, @NotNull List<@NotNull ValueValidator<T>> validators, @NotNull TypeToken<T> type, @Nullable T defaultValue) {
        this(key, null, validators, type, defaultValue);
    }

    ValueNode(@NotNull String key, @Nullable String description, @NotNull List<@NotNull ValueValidator<T>> validators, @NotNull TypeToken<T> type, @Nullable T defaultValue) {
        super(key, description);
        this.type = type;
        this.validators = List.copyOf(validators);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public TypeToken<T> type() {
        return this.type;
    }

    public @NotNull List<@NotNull ValueValidator<T>> validators() {
        return this.validators;
    }

    public @Nullable T defaultValue() {
        return this.defaultValue;
    }

    public void value(@Nullable T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    final void setValue(@Nullable Object value) {
        this.value = (T) value;
    }

    public @Nullable T value() {
        return this.value;
    }
}

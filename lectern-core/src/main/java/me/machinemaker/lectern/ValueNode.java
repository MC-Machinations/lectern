/*
 * GNU General Public License v3
 *
 * lectern, a configuration utility
 *
 * Copyright (C) 2021 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.lectern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import me.machinemaker.lectern.contexts.SerializeContext;
import me.machinemaker.lectern.exceptions.validations.ValidationException;
import me.machinemaker.lectern.validations.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class ValueNode<T> extends Node {

    private final JavaType type;
    private List<ValueValidator<T>> validators;
    private final T defaultValue;
    private T value;
    Consumer<T> callback = t -> {};

    ValueNode(@NotNull String key, @NotNull SectionNode parent, @NotNull JavaType type, @Nullable T defaultValue) {
        this(key, parent, Collections.emptyList(), type, defaultValue);
    }

    ValueNode(@NotNull String key, @NotNull SectionNode parent, @Nullable String description, @NotNull JavaType type, @Nullable T defaultValue) {
        this(key, parent, description, Collections.emptyList(), type, defaultValue);
    }

    ValueNode(@NotNull String key, @NotNull SectionNode parent, @NotNull List<@NotNull ValueValidator<T>> validators, @NotNull JavaType type, @Nullable T defaultValue) {
        this(key, parent, null, validators, type, defaultValue);
    }

    ValueNode(@NotNull String key, @NotNull SectionNode parent, @Nullable String description, @NotNull List<@NotNull ValueValidator<T>> validators, @NotNull JavaType type, @Nullable T defaultValue) {
        super(key, parent, description);
        this.type = type;
        this.validators = List.copyOf(validators);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public JavaType type() {
        return this.type;
    }

    public @NotNull List<@NotNull ValueValidator<T>> validators() {
        return this.validators;
    }

    public void validators(@NotNull List<@NotNull ValueValidator<T>> validators) {
        this.validators = validators;
    }

    public @Nullable T defaultValue() {
        return this.defaultValue;
    }

    @SuppressWarnings("unchecked")
    public boolean isValid(Object value) {
        try {
            return this.validators.stream().allMatch(validator -> validator.validate((T) value));
        } catch (ClassCastException e) {
            return false;
        } catch (ValidationException e) {
            throw new IllegalArgumentException("Could not set value of " + this.path() + " to " + value + " from " + this.root().file() + " because " + e.getMessage(), e);
        }
    }

    public void value(@Nullable T value) {
        if (isValid(value)) {
            this.callback.accept(value);
            this.value = value;
        } else {
            throw new IllegalArgumentException(value + " is not a valid value for this node");
        }
    }

    public @Nullable T value() {
        return this.value;
    }

    @SuppressWarnings("unchecked")
    void setValue(@Nullable Object value) {
        if (isValid(value)) {
            this.callback.accept((T) value);
            this.value = (T) value;
        } else {
            throw new IllegalArgumentException(value + " is not a valid value for this node");
        }
    }

    @Override
    @NotNull String asString(@NotNull SerializeContext context, int indent) throws JsonProcessingException {
        if (this.value() != null) {
            String output = context.mapper().writeValueAsString(Map.of(this.key(), this.value()));
            if (this.description() != null) {
                output = context.commentChar() + " " + this.description() + "\n" + output;
            }
            return output.lines().map(s -> " ".repeat(indent) + s).collect(Collectors.joining("\n")) + "\n";
        }
        return "";
    }
}

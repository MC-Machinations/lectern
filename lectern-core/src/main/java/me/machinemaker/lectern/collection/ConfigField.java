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
package me.machinemaker.lectern.collection;

import com.fasterxml.jackson.databind.JavaType;
import me.machinemaker.lectern.validations.ValueValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public abstract class ConfigField {

    private final Field field;
    private final String description;
    private final String key;

    protected ConfigField(@NotNull Field field, @Nullable String description, @NotNull String key) {
        this.field = field;
        this.description = description;
        this.key = key;
    }

    public @Nullable Object get(@NotNull Object configInstance) {
        try {
            return this.field.get(configInstance);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Couldn't get the value of " + this.field + " from " + configInstance, e);
        }
    }

    public void set(@NotNull Object configInstance, @Nullable Object value) {
        try {
            this.field.set(configInstance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Couldn't set the value of " + this.field + " on " + configInstance, e);
        }
    }

    public abstract @NotNull Type type();

    public @NotNull Field field() {
        return field;
    }

    public @Nullable String description() {
        return description;
    }

    public @NotNull String key() {
        return key;
    }

    public static class Section extends ConfigField {

        private final Class<?> sectionType;

        public Section(@NotNull Field field, @Nullable String description, @NotNull String key, @NotNull Class<?> sectionType) {
            super(field, description, key);
            this.sectionType = sectionType;
        }

        @Override
        public @NotNull Type type() {
            return this.field().getType();
        }

        public @NotNull Class<?> sectionType() {
            return sectionType;
        }

        public @NotNull Object getOrCreateInstance() {
            try {
                var ctor = sectionType.getDeclaredConstructor();
                ctor.trySetAccessible();
                return ctor.newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new IllegalStateException("Could not create an instance of " + sectionType.getName(), e);
            }
        }
    }

    public static class Value extends ConfigField {

        private final JavaType type;
        private final List<ValueValidator<?>> validators;

        public Value(@NotNull Field field, @Nullable String description, @NotNull String key, @NotNull JavaType type, List<ValueValidator<?>> validators) {
            super(field, description, key);
            this.type = type;
            this.validators = Collections.unmodifiableList(validators);
        }

        @Override
        public @NotNull JavaType type() {
            return type;
        }

        public @NotNull List<ValueValidator<?>> validators() {
            return this.validators;
        }
    }
}

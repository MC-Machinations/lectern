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

import com.fasterxml.jackson.databind.type.TypeFactory;
import me.machinemaker.lectern.annotations.ConfigurationSection;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.validations.Validation;
import me.machinemaker.lectern.utils.StringUtils;
import me.machinemaker.lectern.validations.FieldValueValidator;
import me.machinemaker.lectern.validations.ValueValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FieldCollector {

    private final Class<?> configClass;
    private final Set<Class<?>> subClasses = new HashSet<>();

    public FieldCollector(Class<?> configClass) {
        this.configClass = configClass;
        for (Class<?> declaredClass : configClass.getDeclaredClasses()) {
            if (declaredClass.isSynthetic() || !declaredClass.isAnnotationPresent(ConfigurationSection.class)) continue;
            this.subClasses.add(declaredClass);
        }
    }

    public List<ConfigField> collectFields() {
        List<ConfigField> configFields = new ArrayList<>();
        for (Field field : configClass.getDeclaredFields()) {
            if (field.isSynthetic() || Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) continue;
            field.trySetAccessible();
            String key;
            String description;
            if (this.subClasses.contains(field.getType())) {
                if (!field.getType().isAnnotationPresent(ConfigurationSection.class)) {
                    throw new IllegalStateException("Configuration sections must be annotated with " + ConfigurationSection.class.getName());
                }
                ConfigurationSection section = field.getType().getAnnotation(ConfigurationSection.class);
                configFields.add(new ConfigField.Section(field, section.description(), section.path(), field.getType()));
            } else {
                key = field.isAnnotationPresent(Key.class) ? field.getAnnotation(Key.class).value() : StringUtils.camelCaseToHyphenSnakeCase(field.getName());
                description = field.isAnnotationPresent(Description.class) ? field.getAnnotation(Description.class).value() : null;
                List<ValueValidator<?>> validators = new ArrayList<>();
                for (Annotation annotation : field.getAnnotations()) {
                    if (annotation.annotationType().isAnnotationPresent(Validation.class)) {
                        FieldValueValidator<?, ?> validator;
                        Class<? extends FieldValueValidator<?, ?>> validatorClass = annotation.annotationType().getAnnotation(Validation.class).value();
                        try {
                            var ctor = validatorClass.getDeclaredConstructor(); // TODO validator factory for dep injectors
                            ctor.trySetAccessible();
                            validator = ctor.newInstance();
                        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                            throw new IllegalStateException("Could not construct new instance of " + validatorClass.getSimpleName(), e);
                        }
                        validators.add(validator.toWrapper(field, annotation));
                    }
                }
                configFields.add(new ConfigField.Value(field,
                        description,
                        key,
                        TypeFactory.defaultInstance().constructType(field.getGenericType()),
                        validators));
            }
        }
        return Collections.unmodifiableList(configFields);
    }

}

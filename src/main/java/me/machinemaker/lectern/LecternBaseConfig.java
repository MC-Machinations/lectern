/*
 * lectern, a configuration helper.
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

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.lectern.annotations.LecternConfigurationSection;
import me.machinemaker.lectern.annotations.Validate;
import me.machinemaker.lectern.exceptions.ConfigNotInitializedException;
import me.machinemaker.lectern.exceptions.RegexValidationException;
import me.machinemaker.lectern.utils.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Should be extended by configurations that are represented as an class.
 */
public abstract class LecternBaseConfig {

    private static final Logger LOGGER = Logger.getGlobal();

    @Nullable
    private LecternConfig config;

    /**
     * Save the configuration file with update values.
     */
    public void save() {
        if (this.config == null) {
            throw new ConfigNotInitializedException(getClass());
        }
        readFields(this, getClass(), config.root());
        readSubClasses(this, getClass(), config.root());
        config.save();
    }

    /**
     * Overwrites field values with current values in the configuration file.
     */
    public void reload() {
        if (this.config == null) {
            throw new ConfigNotInitializedException(getClass());
        }
        config.reload();
        loadFields(this, getClass(), config.root());
        loadSubClasses(this, getClass(), config.root());
    }

    final void init(File parentDir) throws IOException {
        LecternConfiguration lecternConfig;
        if (!getClass().isAnnotationPresent(LecternConfiguration.class)) {
            throw new RuntimeException(String.format("%s is missing the %s annotation!", getClass().getSimpleName(), LecternConfiguration.class.getSimpleName()));
        }
        lecternConfig = getClass().getAnnotation(LecternConfiguration.class);
        String fileName = lecternConfig.fileName();
        File file = new File(parentDir, fileName);

        config = Lectern.createConfig(file, lecternConfig.header().isBlank() ? null : lecternConfig.header());

        loadFields(this, getClass(), config.root());
        loadSubClasses(this, getClass(), config.root());

        if (file.exists()) {
            this.reload();
        } else {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(String.format("Could not create %s", fileName), e);
            }
            config.save();
        }
    }

    private void readSubClasses(Object instance, Class<?> clazz, SectionNode parentNode) {
        Arrays.stream(clazz.getDeclaredClasses()).filter(aClass -> aClass.isAnnotationPresent(LecternConfigurationSection.class)).forEach(subClass -> {
            Optional<Field> fieldOptional = Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.getType().equals(subClass)).findAny();
            if (fieldOptional.isEmpty()) {
                throw new RuntimeException(String.format("No field found for %s", subClass.getCanonicalName()));
            }
            Object subInstance;
            try {
                Field field = fieldOptional.get();
                field.trySetAccessible();
                subInstance = field.get(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Unable to get instance for %s:%s", clazz.getCanonicalName(), fieldOptional.get().getName()));
            }

            LecternConfigurationSection lecternConfigurationSection = subClass.getAnnotation(LecternConfigurationSection.class);
            SectionNode sectionNode = parentNode.get(lecternConfigurationSection.path());
            readFields(subInstance, subClass, sectionNode);
            readSubClasses(subInstance, subClass, sectionNode);
        });
    }

    private void loadSubClasses(Object instance, Class<?> clazz, SectionNode parentNode) {
        Arrays.stream(clazz.getDeclaredClasses()).filter(aClass -> aClass.isAnnotationPresent(LecternConfigurationSection.class)).forEach(subClass -> {
            Optional<Field> fieldOptional = Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.getType().equals(subClass)).findAny();
            if (fieldOptional.isEmpty()) {
                throw new RuntimeException(String.format("No field found for %s", subClass.getCanonicalName()));
            }
            Field subClassField = fieldOptional.get();
            subClassField.trySetAccessible();
            Object subInstance;
            try {
                subInstance = subClassField.getType().getDeclaredConstructor().newInstance();
                subClassField.set(instance, subInstance);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(String.format("Could not instantiate/set the field to %s", fieldOptional.get().getType().getCanonicalName()), e);
            }

            LecternConfigurationSection lecternConfigurationSection = subClass.getAnnotation(LecternConfigurationSection.class);
            SectionNode sectionNode;
            if (parentNode.get(lecternConfigurationSection.path()) instanceof SectionNode) {
                sectionNode = parentNode.get(lecternConfigurationSection.path());
            } else {
                sectionNode = parentNode.addSection(lecternConfigurationSection.path(), lecternConfigurationSection.description());
            }

            loadFields(subInstance, subClass, sectionNode);
            loadSubClasses(subInstance, subClass, sectionNode);
        });
    }

    private void readFields(Object instance, Class<?> clazz, SectionNode node) {
        Arrays.stream(clazz.getDeclaredFields()).filter(field -> !field.isSynthetic() && !Modifier.isTransient(field.getModifiers())).forEach(field -> {
            field.trySetAccessible();
            String key = field.isAnnotationPresent(Key.class) ? field.getAnnotation(Key.class).value() : StringUtils.camelCaseToHyphenSnakeCase(field.getName());
            Object value;
            try {
                value = field.get(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format("Unable to get value for %s:%s", clazz.getCanonicalName(), field.getName()), e);
            }
            if (field.isAnnotationPresent(Validate.class)) {
                Matcher matcher = Pattern.compile(field.getAnnotation(Validate.class).regex()).matcher(value.toString());
                if (!matcher.matches()) {
                    throw new RegexValidationException(value.toString(), matcher.pattern().pattern(), clazz, field);
                }
            }
            node.set(value, key);
        });
    }

    private void loadFields(Object instance, Class<?> clazz, SectionNode node) {
        Arrays.stream(clazz.getDeclaredFields()).filter(field -> !field.isSynthetic() && !Modifier.isTransient(field.getModifiers())).forEach(field -> {
            field.trySetAccessible();
            String key = field.isAnnotationPresent(Key.class) ? field.getAnnotation(Key.class).value() : StringUtils.camelCaseToHyphenSnakeCase(field.getName());
            if (node.children().get(key) instanceof ValueNode) {
                try {
                    field.set(instance, node.get(key));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(String.format("Unable to set value for %s:%s", clazz.getCanonicalName(), field.getName()), e);
                }
            } else {
                Object value;
                try {
                    value = field.get(instance);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(String.format("Unable to get value for %s:%s", clazz.getCanonicalName(), field.getName()), e);
                }
                if (field.isAnnotationPresent(Validate.class)) {
                    Matcher matcher = Pattern.compile(field.getAnnotation(Validate.class).regex()).matcher(value.toString());
                    if (!matcher.matches()) {
                        throw new RegexValidationException(value.toString(), matcher.pattern().pattern(), clazz, field);
                    }
                }
                node.addChild(key, value, field.isAnnotationPresent(Description.class) ? field.getAnnotation(Description.class).value() : "");
            }
        });
    }

}

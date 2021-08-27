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
import me.machinemaker.lectern.exceptions.ConfigNotInitializedException;
import me.machinemaker.lectern.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Should be extended by configurations that are represented as an class.
 */
public abstract class LecternBaseConfig {

    @Nullable
    private LecternConfig config;

    /**
     * Save the configuration file with update values.
     *
     * @throws ConfigNotInitializedException if the config has not been initialized
     * @throws me.machinemaker.lectern.exceptions.ConfigSaveException if there is an error saving the file
     */
    public void save() {
        if (this.config == null) {
            throw new ConfigNotInitializedException(getClass());
        }
        loadNode(this, this.config);
        this.config.save();
    }

    /**
     * Overwrites field values with current values in the configuration file.
     *
     * @throws ConfigNotInitializedException if the config has not been initialized
     * @throws me.machinemaker.lectern.exceptions.ConfigReloadException if there is an error reloading the file (like it doesn't exist)
     */
    public void reload() {
        if (this.config == null) {
            throw new ConfigNotInitializedException(getClass());
        }
        this.config.reload();
        loadFields(this, this.config);
    }

    /**
     * Reloads the config if the file exists. Saves the config if the file doesn't exist.
     * Meant for use just after creating the configuration to only
     * load values from the file if it's been created before.
     *
     * @throws ConfigNotInitializedException if the config has not been initialized
     * @throws me.machinemaker.lectern.exceptions.ConfigReloadException if there is an error reloading the file
     * @throws me.machinemaker.lectern.exceptions.ConfigSaveException if there is an error saving the file
     */
    public void reloadOrSave() {
        if (this.getFile().exists()) {
            this.reload();
        } else {
            this.save();
        }
    }

    /**
     * Returns the LecternConfig backing this class. Can only be called
     * after initialization.
     *
     * @return the backing config
     * @throws ConfigNotInitializedException if the config has not been initialized
     */
    @NotNull
    public final LecternConfig getConfig() {
        if (this.config == null) {
            throw new ConfigNotInitializedException(getClass());
        }
        return config;
    }

    /**
     * Gets the file for this configuration object.
     *
     * @return the config file
     */
    @NotNull
    public final File getFile() {
        if (this.config == null) {
            throw new ConfigNotInitializedException(getClass());
        }
        return this.config.getFile();
    }

    final void init(File parentDir) throws IOException {
        LecternConfiguration lecternConfig;
        if (!getClass().isAnnotationPresent(LecternConfiguration.class)) {
            throw new IllegalStateException(String.format("%s is missing the %s annotation!", getClass().getSimpleName(), LecternConfiguration.class.getSimpleName()));
        }
        lecternConfig = getClass().getAnnotation(LecternConfiguration.class);
        String fileName = lecternConfig.fileName();
        File file = new File(parentDir, fileName);

        config = Lectern.createConfig(file, lecternConfig.header().isBlank() ? null : lecternConfig.header());
        createNodeSchema(this, config);

        if (file.exists()) {
            this.reload();
        } else {
            loadNode(this, config);
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Could not create %s", fileName), e);
            }
            config.save();
        }
    }

    private static Stream<Field> getFields(Class<?> configClass) {
        return Arrays.stream(configClass.getFields())
                .filter(field -> !field.isSynthetic() && !Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()))
                .peek(Field::trySetAccessible);
    }

    private static Set<Class<?>> getSubClasses(Class<?> configClass) {
        return Arrays.stream(configClass.getClasses()).filter(clazz -> clazz.isAnnotationPresent(LecternConfigurationSection.class)).collect(Collectors.toUnmodifiableSet());
    }

    private static void createNodeSchema(Object configInstance, SectionNode rootNode) {
        Class<?> configClass = configInstance.getClass();
        Set<Class<?>> subClasses = getSubClasses(configClass);
        getFields(configClass).forEach(field -> {
            if (subClasses.contains(field.getType())) {
                LecternConfigurationSection sectionInfo = field.getType().getAnnotation(LecternConfigurationSection.class);
                SectionNode newSubSection = rootNode.addSection(sectionInfo.path(), sectionInfo.description());
                Object subSectionInstance;
                try {
                    subSectionInstance = field.getType().getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new IllegalStateException(String.format("Could not instantiate/set the field to %s", field.getType().getCanonicalName()), e);
                }
                createNodeSchema(subSectionInstance, newSubSection);
            } else {
                String key = rootNode.path() + (field.isAnnotationPresent(Key.class) ? field.getAnnotation(Key.class).value() : StringUtils.camelCaseToHyphenSnakeCase(field.getName()));
                try {
                    ValueNode<?> valueNode = rootNode.set(key, field.get(configInstance));
                    if (field.isAnnotationPresent(Description.class)) {
                        Description fieldDescription = field.getAnnotation(Description.class);
                        valueNode.description(fieldDescription.value());
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(String.format("Unable to get value for %s:%s", configClass.getCanonicalName(), field.getName()), e);
                }
            }
        });

    }

    private static void loadFields(Object configInstance, SectionNode rootNode) {
        Class<?> configClass = configInstance.getClass();
        Set<Class<?>> subSectionClasses = getSubClasses(configClass);
        getFields(configClass).forEach(field -> {
            if (subSectionClasses.contains(field.getType())) {
                LecternConfigurationSection sectionInfo = field.getType().getAnnotation(LecternConfigurationSection.class);
                Node node = rootNode.getNode(sectionInfo.path());
                if (!(node instanceof SectionNode)) {
                    throw new IllegalStateException(node + " should be a SectionNode");
                }
                Object subSectionInstance;
                try {
                    subSectionInstance = field.getType().getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new IllegalStateException(String.format("Could not instantiate/set the field to %s", field.getType().getCanonicalName()), e);
                }
                loadFields(subSectionInstance, (SectionNode) node);
            } else {
                String key = rootNode.path() + (field.isAnnotationPresent(Key.class) ? field.getAnnotation(Key.class).value() : StringUtils.camelCaseToHyphenSnakeCase(field.getName()));
                Node node = rootNode.getNode(key);
                if (node == null) {
                    throw new IllegalStateException(key + " doesn't exist on the configuration");
                } else if (node instanceof ValueNode) {
                    setFieldValue(field, configInstance, configClass, ((ValueNode<?>) node).value(), false);
                } else {
                    throw new IllegalStateException(String.format("Cannot set a value (%s:%s) for a SectionNode", configClass.getCanonicalName(), field.getName()));
                }
            }
        });
    }

    private static void loadNode(Object configInstance, SectionNode rootNode) {
        Class<?> configClass = configInstance.getClass();
        Set<Class<?>> subSectionClasses = getSubClasses(configClass);
        getFields(configClass).forEach(field -> {
            if (subSectionClasses.contains(field.getType())) {
                LecternConfigurationSection sectionInfo = field.getType().getAnnotation(LecternConfigurationSection.class);
                Node node = rootNode.getNode(sectionInfo.path());
                if (!(node instanceof SectionNode)) {
                    throw new IllegalStateException(node + " should be a SectionNode");
                }
                Object subSectionInstance;
                try {
                    subSectionInstance = field.get(configInstance);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not get the field value for " + configClass.getName() + "." + field.getName());
                }
                loadNode(subSectionInstance, (SectionNode) node);
            } else {
                String key = field.isAnnotationPresent(Key.class) ? field.getAnnotation(Key.class).value() : StringUtils.camelCaseToHyphenSnakeCase(field.getName());
                Object value;
                try {
                    value = field.get(configInstance);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(String.format("Unable to get value for %s:%s", configClass.getCanonicalName(), field.getName()), e);
                }
                rootNode.set(key, value);
            }
        });
    }

    private static <T> void setFieldValue(Field field, Object instance, Class<?> instanceType, T value, boolean cast) {
        try {
            if (field.getType().isPrimitive()) {
                if (value.getClass() == Double.class && field.getType() == float.class) {
                    field.set(instance, ((Double) value).floatValue());
                    return;
                }
                if (value.getClass() == Integer.class && field.getType() == long.class) {
                    field.set(instance, ((Integer) value).longValue());
                }
            }
            if (cast) {
                field.set(instance, field.getType().cast(value));
            } else {
                field.set(instance, value);
            }
        } catch (IllegalAccessException | IllegalArgumentException | ClassCastException e) {
            if (!cast) {
                setFieldValue(field, instance, instanceType, value, true);
            }
            throw new IllegalStateException(String.format("Unable to set value for %s:%s", instanceType.getCanonicalName(), field.getName()), e);
        }
    }

}

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

import me.machinemaker.lectern.annotations.Configuration;
import me.machinemaker.lectern.collection.ConfigField;
import me.machinemaker.lectern.collection.FieldCollector;
import me.machinemaker.lectern.exceptions.ConfigAlreadyInitializedException;
import me.machinemaker.lectern.exceptions.ConfigNotInitializedException;
import me.machinemaker.lectern.supplier.ConfigurationSupplier;
import me.machinemaker.lectern.validations.ValueValidator;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseConfig implements Reloadable {

    private Path file;
    private ConfigurationNode rootNode;

    @Override
    public @Nullable Path file() {
        return this.file;
    }

    public @NotNull ConfigurationNode rootNode() {
        this.checkInit();
        return this.rootNode;
    }

    @Override
    public void save() {
        this.checkInit();
        loadConfigTree(this, this.rootNode);
        this.rootNode.save();
    }

    @Override
    public void reload() {
        this.checkInit();
        this.rootNode.reload();
        loadFields(this, this.rootNode);
    }

    @Override
    public @NotNull Path checkInit() {
        if (this.rootNode == null) {
            throw new ConfigNotInitializedException(this.getClass());
        }
        return Reloadable.super.checkInit();
    }

    /**
     * Checks if this configuration is initialized.
     *
     * @return true if initialized
     */
    public boolean isInitialized() {
        return this.rootNode != null;
    }

    protected @NotNull Annotation getConfigurationAnnotation() {
        Annotation configurationAnnotation = null;
        for (Annotation annotation : getClass().getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(Configuration.class)) {
                configurationAnnotation = annotation;
                break;
            }
        }
        if (configurationAnnotation == null) {
            throw new IllegalStateException(String.format("%s is missing an annotation annotated with the %s annotation!", getClass().getSimpleName(), Configuration.class.getSimpleName()));
        }
        return configurationAnnotation;
    }

    public void createInitialFile(Path parentDir) {
        this.init(parentDir, true);
    }

    /**
     * Initializes the configuration. Should only be
     * called once.
     *
     * @param parentDir the parent directory of the config file
     */
    @MustBeInvokedByOverriders
    public void init(Path parentDir) {
        this.init(parentDir, false);
    }

    public final void init(Path parentDir, boolean justCreate) {
        if (this.rootNode != null) {
            throw new ConfigAlreadyInitializedException(this.rootNode);
        }
        final Annotation configurationAnnotation = this.getConfigurationAnnotation();
        createRootNode(parentDir, configurationAnnotation, configurationAnnotation.annotationType().getAnnotation(Configuration.class).supplier());
        createDefaultSectionNodeSchema(this, this.rootNode);
        this.handleFile(justCreate);
    }

    protected void handleFile(boolean justCreate) {
        if (!justCreate && Files.exists(this.file)) {
            this.reloadAndSave();
        } else if (justCreate && Files.exists(this.file)) {
            throw new IllegalArgumentException("Cannot use the justCreate if the file is already created");
        } else {
            loadConfigTree(this, this.rootNode);
            try {
                Files.createDirectories(this.file.getParent());
                Files.createFile(this.file);
            } catch (IOException e) {
                throw new IllegalStateException("Could not create " + this.file, e);
            }
            this.rootNode.save();
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> void createRootNode(Path parentDir, A configurationAnnotation, Class<? extends ConfigurationSupplier<? extends Annotation>> clazz) {
        try {
            var ctor = (Constructor<? extends ConfigurationSupplier<A>>) clazz.getDeclaredConstructor();
            ctor.trySetAccessible();
            this.rootNode = ctor.newInstance().createConfiguration(parentDir, configurationAnnotation);
            this.file = this.rootNode.file();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(String.format("%s does not have a valid no-args constructor", clazz.getSimpleName()), e);
        }
    }

    protected void createDefaultSectionNodeSchema(Object configInstance, SectionNode sectionNode) {
        sectionNode.clear();
        final FieldCollector collector = new FieldCollector(configInstance.getClass());
        for (ConfigField configField : collector.collectFields()) {
            if (configField instanceof ConfigField.Section section) {
                SectionNode newSection = sectionNode.addSection(section.key(), section.description());
                newSection.meta().putAll(section.meta());
                createDefaultSectionNodeSchema(section.getOrCreateInstance(), newSection);
            } else if (configField instanceof ConfigField.Value value) {
                setupValueNodeSchema(sectionNode, value, value.get(configInstance), configInstance);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @MustBeInvokedByOverriders
    protected <T> ValueNode<T> setupValueNodeSchema(SectionNode sectionNode, ConfigField.Value field, T value, Object configInstance) {
        ValueNode<T> valueNode = sectionNode.set(field.key(), field.type(), value);
        valueNode.callback = val -> field.set(configInstance, val);
        valueNode.description(field.description());
        valueNode.meta().putAll(field.meta());
        List<ValueValidator<T>> valueValidators = new ArrayList<>();
        for (ValueValidator<?> validator : field.validators()) {
            valueValidators.add((ValueValidator<T>) validator);
        }
        valueNode.validators(valueValidators);
        return valueNode;
    }

    private static void loadFields(Object configInstance, SectionNode sectionNode) {
        final FieldCollector collector = new FieldCollector(configInstance.getClass());
        for (ConfigField configField : collector.collectFields()) {
            final Node node = sectionNode.getNode(configField.key());
            if (node == null) {
                throw new IllegalStateException(configField.key() + " is null in the configuration tree");
            }
            if (configField instanceof ConfigField.Section section) {
                if (node instanceof SectionNode subSectionNode) {
                    var instance = section.getOrCreateInstance();
                    section.set(configInstance, instance);
                    loadFields(instance, subSectionNode);
                } else {
                    throw new IllegalStateException(node + " is not a section node");
                }
            } else if (configField instanceof ConfigField.Value value) {
                if (node instanceof ValueNode<?> valueNode) {
                    setFieldValue(value, configInstance, configInstance.getClass(), valueNode, false);
                } else {
                    throw new IllegalStateException(node + " is not a value node");
                }
            }
        }
    }

    private static void setFieldValue(ConfigField.Value value, Object instance, Class<?> instanceType, ValueNode<?> valueNode, boolean cast) {
        try {
            if (valueNode.value() != null) {
                if (valueNode.value().getClass() == Double.class && value.type().getRawClass() == float.class) {
                    value.set(instance, ((Double) valueNode.value()).floatValue());
                    return;
                }

                if (valueNode.value().getClass() == Integer.class && value.type().getRawClass() == long.class) {
                    value.set(instance, ((Integer) valueNode.value()).longValue());
                    return;
                }
            }

            if (cast) {
                value.set(instance, value.type().getRawClass().cast(valueNode.value()));
            } else {
                value.set(instance, valueNode.value());
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            if (!cast) {
                setFieldValue(value, instance, instanceType, valueNode, true);
            }
            throw new IllegalStateException(String.format("Unable to set value in %s for %s", instanceType.getCanonicalName(), value.key()), e);
        }
    }

    private static void loadConfigTree(Object configInstance, SectionNode sectionNode) {
        final FieldCollector collector = new FieldCollector(configInstance.getClass());
        for (ConfigField configField : collector.collectFields()) {
            final Node node = sectionNode.getNode(configField.key());
            if (node == null) {
                throw new IllegalStateException(configField.key() + " is null in the configuration tree");
            }
            if (configField instanceof ConfigField.Section section) {
                if (node instanceof SectionNode subSectionNode) {
                    loadConfigTree(section.get(configInstance), subSectionNode);
                } else {
                    throw new IllegalStateException(node + " is not a section node");
                }
            } else if (configField instanceof ConfigField.Value value) {
                if (node instanceof ValueNode<?> valueNode) {
                    valueNode.setValue(value.get(configInstance));
                } else {
                    throw new IllegalStateException(node + " is not a value node");
                }
            }
        }
    }

    /**
     * Instantiates a new Configuration class
     *
     * @param configClass the class to instantiate a new instance of
     * @param parentDir the parent directory for the file for this config
     * @return the new instance
     */
    public static <C extends BaseConfig> C create(@NotNull Class<C> configClass, @NotNull Path parentDir) {
        final C configInstance = BaseConfig.createInstance(configClass);
        configInstance.init(parentDir);
        return configInstance;
    }

    /**
     * Instantiates a new Configuration class. Useful for only creating the initial
     * configuration file.
     *
     * @param configClass the class to instantiate a new instance of
     * @param parentDir the parent directory for the file for this config
     * @return the new instance
     * @throws IllegalArgumentException if the file already exists for this config
     */
    public static <C extends BaseConfig> C createInitialFile(@NotNull Class<C> configClass, @NotNull Path parentDir) {
        final C configInstance = BaseConfig.createInstance(configClass);
        configInstance.createInitialFile(parentDir);
        return configInstance;
    }

    private static <C extends BaseConfig> C createInstance(@NotNull Class<C> configClass) {
        try {
            Constructor<C> ctor = configClass.getDeclaredConstructor();
            ctor.trySetAccessible();
            return ctor.newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Unable to create a new instance of %s", configClass.getSimpleName()), e);
        }
    }
}

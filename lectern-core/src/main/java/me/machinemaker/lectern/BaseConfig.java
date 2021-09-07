package me.machinemaker.lectern;

import me.machinemaker.lectern.annotations.Configuration;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public abstract class BaseConfig {

    private @Nullable ConfigurationNode backingNode;

    final void init(Path parentDir) {
        if (!getClass().isAnnotationPresent(Configuration.class)) {
            throw new IllegalStateException(String.format("%s is missing the %s annotation!", getClass().getSimpleName(), Configuration.class.getSimpleName()));
        }
        Configuration configuration = getClass().getAnnotation(Configuration.class);
        Path file = parentDir.resolve(configuration.fileName());

    }
}

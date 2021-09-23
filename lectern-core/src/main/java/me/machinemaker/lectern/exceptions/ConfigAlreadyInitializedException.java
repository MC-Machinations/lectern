package me.machinemaker.lectern.exceptions;

import me.machinemaker.lectern.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class ConfigAlreadyInitializedException extends ConfigException {

    public ConfigAlreadyInitializedException(@NotNull ConfigurationNode config) {
        super("This configuration was already initialized", config);
    }
}

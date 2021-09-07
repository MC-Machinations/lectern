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
package me.machinemaker.lectern.exceptions;

import me.machinemaker.lectern.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * For exceptions that contain a configuration.
 */
public abstract class ConfigException extends RuntimeException {

    static final long serialVersionUID = 2L;

    protected final transient ConfigurationNode config;

    protected ConfigException(String message, ConfigurationNode config, Throwable cause) {
        super(message, cause);
        this.config = config;
    }

    protected ConfigException(ConfigurationNode config, Throwable cause) {
        super("Exception relating to " + config.file(), cause);
        this.config = config;
    }

    protected ConfigException(@NotNull String message) {
        super(message);
        this.config = null;
    }

    /**
     * Get the configuration this exception pertains to.
     * Nullable when the exception occurs before the config is created.
     *
     * @return the config
     */
    @Nullable
    public ConfigurationNode getConfig() {
        return config;
    }
}

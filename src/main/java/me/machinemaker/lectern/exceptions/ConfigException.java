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

import me.machinemaker.lectern.LecternConfig;

/**
 * For exceptions that contain a configuration.
 */
public abstract class ConfigException extends RuntimeException {

    static final long serialVersionUID = 1L;

    protected final LecternConfig config;

    public ConfigException(String message, LecternConfig config, Throwable cause) {
        super(message, cause);
        this.config = config;
    }

    protected ConfigException(LecternConfig config, Throwable cause) {
        super("Exception relating to " + config.getFile(), cause);
        this.config = config;
    }

    /**
     * Get the configuration this exception pertains to.
     *
     * @return the config
     */
    public LecternConfig getConfig() {
        return config;
    }
}

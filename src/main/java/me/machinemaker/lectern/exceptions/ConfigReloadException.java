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
 * Exception when a config reload fails.
 */
public class ConfigReloadException extends ConfigException {

    static final long serialVersionUID = 1L;

    public ConfigReloadException(LecternConfig config, Throwable cause) {
        super("Error reloading " + config.getFile(), config, cause);
    }
}

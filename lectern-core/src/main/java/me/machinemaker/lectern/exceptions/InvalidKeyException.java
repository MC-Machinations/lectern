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
package me.machinemaker.lectern.exceptions;

import me.machinemaker.lectern.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class InvalidKeyException extends ConfigException {

    static final long serialVersionUID = 2L;

    private final String key;

    public InvalidKeyException(@NotNull String key, @NotNull ConfigurationNode config) {
        super(String.format("%s is not a recognized key, and was not loaded into the configuration", key), config);
        this.key = key;
    }

    public @NotNull String key() {
        return key;
    }
}

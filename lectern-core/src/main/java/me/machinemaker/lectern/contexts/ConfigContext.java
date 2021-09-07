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
package me.machinemaker.lectern.contexts;

import me.machinemaker.lectern.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigContext {

    protected final ConfigurationNode root;

    protected ConfigContext(@NotNull ConfigurationNode root) {
        this.root = root;
    }

    public @NotNull ConfigurationNode root() {
        return root;
    }

}

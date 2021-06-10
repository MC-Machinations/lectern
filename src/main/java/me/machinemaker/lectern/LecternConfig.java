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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;

/**
 * Holds nodes and values for a configuration object.
 */
public interface LecternConfig extends SectionNode.RootSectionNode, Serializable {

    /**
     * Saves the configuration to a file.
     *
     * @throws me.machinemaker.lectern.exceptions.ConfigSaveException if there is an error saving the file
     */
    void save();

    /**
     * Reloads the configuration file with values from the file.
     *
     * @throws me.machinemaker.lectern.exceptions.ConfigReloadException if there is an error reloading the file (like it doesn't exist)
     */
    void reload();

    /**
     * Reloads the config if the file exists. Saves the config if the file doesn't exist.
     * Meant for use just after creating the configuration to only
     * load values from the file if it's been created before.
     *
     * @throws me.machinemaker.lectern.exceptions.ConfigReloadException if there is an error reloading the file
     * @throws me.machinemaker.lectern.exceptions.ConfigSaveException if there is an error saving the file
     */
    void reloadOrSave();

    /**
     * Gets the file associated with this configuration
     *
     * @return the config file
     */
    @NotNull
    File getFile();
}

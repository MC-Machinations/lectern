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

import me.machinemaker.lectern.exceptions.ConfigNotInitializedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a reloadable object
 * (usually a {@link ConfigurationNode} or a {@link BaseConfig})
 */
public interface Reloadable {

    /**
     * Gets the file for this reloadable
     *
     * @return the file
     */
    @Contract(pure = true)
    @Nullable Path file();

    /**
     * Reloads this reloadable object
     */
    void reload();

    /**
     * Saves this reloadable object
     */
    void save();

    /**
     * Utility method for reloading if the file exists, saving if not
     * @see #reloadAndSave()
     */
    default void reloadOrSave() {
        final Path file = this.checkInit();
        if (Files.exists(file)) {
            this.reload();
        } else {
            this.save();
        }
    }

    /**
     * Utility method for reloading if the file exists, and then saving
     * regardless of the file existing or not
     * @see #reloadOrSave()
     */
    default void reloadAndSave() {
        final Path file = this.checkInit();
        if (Files.exists(file)) {
            this.reload();
        }
        this.save();
    }

    /**
     * Checks if this configuration has been initialized.
     *
     * @throws ConfigNotInitializedException if the config hasn't been initialized
     */
    default @NotNull Path checkInit() {
        if (this.file() == null) {
            throw new ConfigNotInitializedException(this.getClass());
        }
        return this.file();
    }
}

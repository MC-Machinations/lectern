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
package me.machinemaker.lectern.supplier;

import me.machinemaker.lectern.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.nio.file.Path;

public interface ConfigurationSupplier<A extends Annotation> {

    @NotNull ConfigurationNode createConfiguration(@NotNull Path parentDir, @NotNull A annotation);
}

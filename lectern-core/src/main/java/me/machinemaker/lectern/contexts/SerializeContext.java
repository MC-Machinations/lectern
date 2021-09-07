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

import com.fasterxml.jackson.databind.ObjectMapper;
import me.machinemaker.lectern.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

public class SerializeContext extends ConfigContext {

    private final ObjectMapper mapper;
    private final char commentChar;
    private final int indentSize;

    public SerializeContext(@NotNull ConfigurationNode root, @NotNull ObjectMapper mapper, char commentChar, int indentSize) {
        super(root);
        this.mapper = mapper;
        this.commentChar = commentChar;
        this.indentSize = indentSize;
    }

    public @NotNull ObjectMapper mapper() {
        return this.mapper;
    }

    public char commentChar() {
        return this.commentChar;
    }

    public int indentSize() {
        return this.indentSize;
    }
}

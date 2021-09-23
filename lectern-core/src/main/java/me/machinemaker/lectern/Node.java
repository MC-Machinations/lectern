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

import com.fasterxml.jackson.core.JsonProcessingException;
import me.machinemaker.lectern.contexts.SerializeContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class Node {

    protected final String key;
    protected final SectionNode parent;
    protected String description;
    protected final Map<String, Object> meta = new HashMap<>();

    protected Node(@NotNull String key, @Nullable SectionNode parent) {
        this(key, parent, null);
    }

    protected Node(@NotNull String key, @Nullable SectionNode parent, @Nullable String description) {
        this.key = key;
        this.parent = parent;
        this.description = description;
    }

    public @NotNull String key() {
        return this.key;
    }

    public @Nullable SectionNode parent() {
        return this.parent;
    }

    public @Nullable String description() {
        return this.description;
    }

    public void description(@Nullable String description) {
        this.description = description;
    }

    public @NotNull Map<String, Object> meta() {
        return this.meta;
    }

    /**
     * Gets the full path to this node.
     *
     * @return the full path
     */
    public @NotNull String path() {
        StringBuilder pathBuilder = new StringBuilder(this.key());
        SectionNode next = this.parent();
        while (next != null && !next.isRoot()) {
            pathBuilder.insert(0, next.key() + ".");
            next = next.parent();
        }
        return pathBuilder.toString();
    }

    /**
     * Gets the root node for this node.
     *
     * @return the root node
     * @throws IllegalStateException if none could be found
     */
    public @NotNull ConfigurationNode root() {
        SectionNode next = this.parent();
        do {
            if (next instanceof ConfigurationNode root) {
                return root;
            }
            next = next.parent();
        } while (next != null);
        throw new IllegalStateException("Could not find root node for " + this.path());
    }

    abstract @NotNull String asString(@NotNull SerializeContext context, int indent) throws JsonProcessingException;
}

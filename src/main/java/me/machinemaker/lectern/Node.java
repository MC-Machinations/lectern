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

/**
 * Represents a single node in a configuration structure
 */
public interface Node {

    /**
     * Get the key for this node.
     *
     * @return the key
     */
    @NotNull
    String key();

    /**
     * Get the parent {@link SectionNode} for this node.
     *
     * @return the parent node
     */
    @NotNull
    SectionNode parent();

    /**
     * Gets the description/comment for this node.
     *
     * @return the description
     */
    @Nullable
    String description();

    /**
     * Returns a dot-separated string path to this node.
     *
     * @return a dot-separated string
     */
    @NotNull
    default String path() {
        StringBuilder path = new StringBuilder(key());
        SectionNode current = parent();
        while (!current.isRoot()) {
            path.insert(0, current.key() + ".");
        }
        return path.toString();
    }

    /**
     * If this node is a section.
     *
     * @return true if a section
     */
    boolean isSection();

    /**
     * If this node is the root of a file.
     *
     * @return true if a root
     */
    boolean isRoot();
}

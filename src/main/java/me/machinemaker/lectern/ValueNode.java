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

/**
 * A configuration node that holds a value.
 *
 * @param <T> the value type
 */
public interface ValueNode<T> extends Node {

    @Override
    default boolean isSection() {
        return false;
    }

    @Override
    default boolean isRoot() {
        return false;
    }

    /**
     * Gets the value of this node.
     * @return the value
     */
    @NotNull
    T value();

    /**
     * Sets the value of this node.
     *
     * @param value the value to set
     */
    void value(@NotNull Object value);
}

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

import java.util.Map;
import java.util.stream.Collectors;

/**
 * A configuration node that holds a value.
 *
 * @param <T> the value type
 */
public class ValueNode<T> implements Node {

    private final String key;
    private final SectionNode parent;
    private T value;
    private String description;

    ValueNode(String key, SectionNode parent, T value) {
        this(key, parent, value, null);
    }

    ValueNode(String key, SectionNode parent, T value, String description) {
        this.key = key;
        this.value = value;
        this.parent = parent;
        this.description = description != null ? description.isBlank() ? null : description : null;
    }

    @Override
    public @NotNull String key() {
        return key;
    }

    @Override
    public @NotNull SectionNode parent() {
        return parent;
    }

    /**
     * Gets the value of this node.
     * @return the value
     */
    @NotNull
    public T value() {
        return value;
    }

    /**
     * Sets the value of this node.
     *
     * @param value the value to set
     */
    @SuppressWarnings("unchecked")
    public void value(@NotNull Object value) {
        this.value = (T) value;
    }

    @Override
    @Nullable
    public String description() {
        return description;
    }

    @Override
    public void description(@Nullable String description) {
        this.description = description;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @NotNull
    String asString(int indent) {
        String output = LecternConfig.LECTERN_YAML.dump(Map.of(key(), value()));
        if (description() != null) {
            output = "# " + description() + "\n" + output;
        }
        output = output.lines().map(s -> " ".repeat(indent) + s).collect(Collectors.joining("\n")) + '\n';
        return output;
    }

    @Override
    public String toString() {
        return "ValueNodeImpl{" +
                "key='" + key + '\'' +
                ", value=" + value +
                ", description='" + description + '\'' +
                '}';
    }
}

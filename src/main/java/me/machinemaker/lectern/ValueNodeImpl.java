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

class ValueNodeImpl<T> implements ValueNode<T> {

    private final String key;
    private final SectionNode parent;
    private T value;
    private final String description;

    protected ValueNodeImpl(String key, SectionNode parent, T value) {
        this(key, parent, value, null);
    }

    protected ValueNodeImpl(String key, SectionNode parent, T value, String description) {
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

    @Override
    public @NotNull T value() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void value(@NotNull Object value) {
        this.value = (T) value;
    }

    @Override
    @Nullable
    public String description() {
        return description;
    }

    @NotNull
    String asString(int indent) {
        var output = LecternConfigImpl.LECTERN_YAML.dump(Map.of(key(), value()));
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

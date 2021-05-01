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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

class SectionNodeImpl implements SectionNode {

    protected final String key;
    protected final Map<String, Node> children;
    protected final SectionNode parent;
    protected final String description;

    private SectionNodeImpl(Map<String, Node> children) {
        this(children, null);
    }

    private SectionNodeImpl(Map<String, Node> children, String description) {
        this.key = "";
        this.parent = this;
        this.children = children;
        this.description = description != null ? description.isBlank() ? null : description : null;
    }

    protected SectionNodeImpl(String key, SectionNode parent) {
        this(key, parent, null);
    }

    protected SectionNodeImpl(String key, SectionNode parent, String description) {
        this(key, parent, description, new LinkedHashMap<>());
    }

    protected SectionNodeImpl(String key, SectionNode parent, String description, Map<String, Node> children) {
        this.key = key;
        this.parent = parent;
        this.description = description != null ? description.isBlank() ? null : description : null;
        this.children = children;
    }

    @Override
    @NotNull
    public String key() {
        return key;
    }

    @Override
    @NotNull
    public SectionNode parent() {
        return parent;
    }

    @Override
    @Nullable
    public String description() {
        return description;
    }

    @NotNull
    public Map<String, Node> children() {
        return children;
    }

    @Override
    public void addChild(@NotNull Node node) {
        children.put(node.key(), node);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(@NotNull String...path) {
        if (path.length == 1) {
            if (!children.containsKey(path[0])) {
                return null;
            }
            if (!children.get(path[0]).isSection()) {
                return ((ValueNode<T>) children.get(path[0])).value();
            } else {
                return (T) children.get(path[0]);
            }
        } else {
            return ((SectionNode) children.get(path[0])).get(Arrays.copyOfRange(path, 1, path.length));
        }
    }

    @Override
    public <T> void set(@NotNull T value, @NotNull String...path) {
        if (path.length == 1) {
            if (children.get(path[0]).isSection()) {
                throw new RuntimeException("Path led to a SectionNode not a ValueNode");
            }
            ((ValueNode<?>) children.get(path[0])).value(value);
        } else {
            ((SectionNode) children.get(path[0])).get(Arrays.copyOfRange(path, 1, path.length));
        }
    }

    @SuppressWarnings("unchecked")
    public void load(@NotNull Map<String, ?> map) {
        map.forEach((s, o) -> {
            if (!this.children.containsKey(s)) {
                throw new RuntimeException(String.format("%s is not a recognized key, and was not loaded into the configuration", s));
            }
            if (o instanceof Map && this.children.get(s) instanceof SectionNode) {
                ((SectionNodeImpl) this.children.get(s)).load((Map<String, ?>) o);
            } else {
                try {
                    ((ValueNode<?>) this.children.get(s)).value(o);
                } catch (ClassCastException e) {
                    throw new RuntimeException(String.format("%s could not be set as the value for %s", o, s));
                }
            }
        });
    }

    @NotNull
    String asString(int indent) {
        StringBuilder builder = new StringBuilder();
        for (Node child : children().values()) {
            if (child instanceof ValueNodeImpl) {
                builder.append(((ValueNodeImpl<?>) child).asString(indent));
            } else if (child instanceof SectionNode) {
                if (child.description() != null) {
                    builder.append(" ".repeat(indent)).append("# ").append(child.description()).append('\n');
                }
                builder.append(" ".repeat(indent)).append(child.key()).append(":").append('\n');
                builder.append(((SectionNodeImpl) child).asString(indent + LecternConfigImpl.INDENT));
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "SectionNodeImpl{" +
                "key='" + key + '\'' +
                ", children=" + children +
                ", description='" + description + '\'' +
                '}';
    }

    static class RootSectionNodeImpl extends SectionNodeImpl implements RootSectionNode {

        RootSectionNodeImpl() {
            this((String) null);
        }

        RootSectionNodeImpl(String header) {
            this(new LinkedHashMap<>(), header);
        }

        RootSectionNodeImpl(@NotNull Map<String, Node> children) {
            this(children, null);
        }

        RootSectionNodeImpl(@NotNull Map<String, Node> children, String header) {
            super(children, header);
        }

        @Override
        public boolean isRoot() {
            return true;
        }

        @Override
        public String toString() {
            return "RootSectionNodeImpl{" +
                    "key='" + key + '\'' +
                    ", children=" + children +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

}

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

import me.machinemaker.lectern.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a section in a configuration structure
 */
public class SectionNode implements Node {

    protected final String key;
    protected final Map<String, Node> children;
    protected final SectionNode parent;
    protected String description;

    private SectionNode(Map<String, Node> children) {
        this(children, null);
    }

    private SectionNode(Map<String, Node> children, String description) {
        this.key = "";
        this.parent = this;
        this.children = children;
        this.description = StringUtils.emptyToNull(description);
    }

    protected SectionNode(String key, SectionNode parent) {
        this(key, parent, null);
    }

    protected SectionNode(String key, SectionNode parent, String description) {
        this(key, parent, description, new LinkedHashMap<>());
    }

    protected SectionNode(String key, SectionNode parent, String description, Map<String, Node> children) {
        this.key = key;
        this.parent = parent;
        this.description = StringUtils.emptyToNull(description);
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

    @Override
    public void description(@Nullable String description) {
        this.description = description;
    }

    /**
     * Get children Value and Section nodes of this section node.
     *
     * @return children nodes
     */
    @NotNull
    public Map<String, Node> children() {
        return children;
    }

    /**
     * Adds a child node.
     *
     * @param node the child
     */
    public void addChild(@NotNull Node node) {
        children.put(node.key(), node);
    }

    /**
     * Adds a child node.
     *
     * @param key the node key
     * @param value the node default value
     * @param <T> the node type
     * @return the section node for chaining
     */
    @NotNull
    public <T> SectionNode addChild(@NotNull String key, @NotNull T value) {
        addChild(new ValueNode<>(key, this, value));
        return this;
    }

    /**
     * Adds a child node.
     *
     * @param key the node key
     * @param value the node default value
     * @param description the node description/comment
     * @param <T> the node type
     * @return the section node for chaining
     */
    @NotNull
    public <T> SectionNode addChild(@NotNull String key, @NotNull T value, @NotNull String description) {
        addChild(new ValueNode<>(key, this, value, description));
        return this;
    }

    /**
     * Adds a new section.
     *
     * @param key the section node key
     * @return the newly created section
     */
    @NotNull
    public SectionNode addSection(@NotNull String key) {
        SectionNode sectionNode = new SectionNode(key, this);
        addChild(sectionNode);
        return sectionNode;
    }

    /**
     * Adds a new section.
     *
     * @param key the section node key
     * @param newSectionNodeConsumer provides the newly created section node
     * @return the current section node for chaining
     */
    @NotNull
    public SectionNode addSection(@NotNull String key, @NotNull Consumer<SectionNode> newSectionNodeConsumer) {
        newSectionNodeConsumer.accept(addSection(key));
        return this;
    }

    /**
     * Adds a new section.
     *
     * @param key the section node key
     * @param description the description for the section node
     * @return the newly created section
     */
    @NotNull
    public SectionNode addSection(@NotNull String key, @NotNull String description) {
        SectionNode sectionNode = new SectionNode(key, this, description);
        addChild(sectionNode);
        return sectionNode;
    }

    /**
     * Adds a new section.
     *
     * @param key the section node key
     * @param description the description for the section node
     * @param newSectionNodeConsumer provides the newly created section node
     * @return the current section node for chaining
     */
    @NotNull
    public SectionNode addSection(@NotNull String key, @NotNull String description, @NotNull Consumer<SectionNode> newSectionNodeConsumer) {
        newSectionNodeConsumer.accept(addSection(key, description));
        return this;
    }

    /**
     * Gets a node for an array of node keys.
     *
     * @param path {@code .}-separated path to lead to a node
     * @return the node
     */
    @Nullable
    public Node getNode(@NotNull String path) {
        String[] pathArr = path.split("\\.");
        if (!children.containsKey(pathArr[0])) {
            return null;
        }
        if (pathArr.length == 1) {
            return children.get(pathArr[0]);
        } else {
            return ((SectionNode) children.get(pathArr[0])).getNode(path.substring(path.indexOf('.') + 1));
        }
    }

    /**
     * Sets a value with an array of node keys.
     * @param <T> the value type
     * @param path {@code .}-separated path to lead to a node
     * @param value the value to set
     * @return the node changed
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> ValueNode<T> set(@NotNull String path, @NotNull T value) {
        String[] pathArr = path.split("\\.");
        Node node = children.get(pathArr[0]);
        if (pathArr.length == 1) {
            if (node == null) {
                addChild(pathArr[0], value);
                return (ValueNode<T>) children.get(pathArr[0]);
            } else {
                if (!(node instanceof ValueNode)) {
                    throw new RuntimeException("Path led to a SectionNode not a ValueNode"); // TODO custom exception
                } else {
                    ValueNode<T> child = (ValueNode<T>) children.get(pathArr[0]);
                    child.value(value);
                    return child;

                }
            }
        } else {
            if (node == null) {
                return addSection(pathArr[0]).set(path.substring(path.indexOf('.') + 1), value);
            } else {
                return ((SectionNode) children.get(pathArr[0])).set(path.substring(path.indexOf('.') + 1), value);
            }
        }
    }

    /**
     * Gets a value for an array of node keys.
     *
     * @param <T> the value type
     * @param path {@code .}-separated path to lead to a node
     * @return the value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull String path) {
        Node node = this.getNode(path);
        if (node == null) {
            return null;
        }
        if (!node.isSection()) {
            return ((ValueNode<T>) node).value();
        } else {
            return (T) node;
        }
    }

    @SuppressWarnings("unchecked")
    public void load(@Nullable Map<String, ?> map) {
        if (map != null) {
            map.forEach((s, o) -> {
                if (!this.children.containsKey(s)) {
                    throw new RuntimeException(String.format("%s is not a recognized key, and was not loaded into the configuration", s));
                }
                if (o instanceof Map && this.children.get(s) instanceof SectionNode) {
                    ((SectionNode) this.children.get(s)).load((Map<String, ?>) o);
                } else {
                    try {
                        ((ValueNode<?>) this.children.get(s)).value(o);
                    } catch (ClassCastException e) {
                        throw new RuntimeException(String.format("%s could not be set as the value for %s", o, s));
                    }
                }
            });
        }
    }

    @NotNull
    String asString(int indent) {
        StringBuilder builder = new StringBuilder();
        for (Node child : children().values()) {
            if (child instanceof ValueNode) {
                builder.append(((ValueNode<?>) child).asString(indent));
            } else if (child instanceof SectionNode) {
                if (child.description() != null) {
                    builder.append(" ".repeat(indent)).append("# ").append(child.description()).append('\n');
                }
                builder.append(" ".repeat(indent)).append(child.key()).append(":").append('\n');
                builder.append(((SectionNode) child).asString(indent + LecternConfig.INDENT));
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

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    /**
     * Returns a map of all keys and their values.
     * Ignores any {@link SectionNode}s that may be children.
     *
     * @return a key/value map
     */
    @NotNull
    public Map<String, Object> values() {
        return values(false);
    }

    /**
     * Returns a map of all keys and their values/sections.
     *
     * @param includeSections include {@link SectionNode}s in the map.
     * @return a key/value map
     */
    @NotNull
    public Map<String, Object> values(boolean includeSections) {
        Map<String, Object> map = new HashMap<>();
        for (Node child : children().values()) {
            if (child instanceof SectionNode && includeSections) {
                map.put(child.key(), child);
            }
            if (child instanceof ValueNode<?>) {
                map.put(child.key(), ((ValueNode<?>) child).value());
            }
        }
        return map;
    }

    /**
     * Represents the root section node for a file.
     * Only 1 of these per configuration file.
     */
    public static class RootSectionNode extends SectionNode {

        RootSectionNode() {
            this((String) null);
        }

        RootSectionNode(String header) {
            this(new LinkedHashMap<>(), header);
        }

        RootSectionNode(@NotNull Map<String, Node> children) {
            this(children, null);
        }

        RootSectionNode(@NotNull Map<String, Node> children, String header) {
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

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
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import me.machinemaker.lectern.contexts.LoadContext;
import me.machinemaker.lectern.contexts.SerializeContext;
import me.machinemaker.lectern.exceptions.validations.ValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a configuration section with children,
 * both other sections and key/value pairs
 */
public class SectionNode extends Node {

    private final Map<@NotNull String, @NotNull Node> children;

    protected SectionNode(@NotNull String key, @Nullable SectionNode parent) {
        this(key, parent, null);
    }

    protected SectionNode(@NotNull String key, @Nullable SectionNode parent, @Nullable String description) {
        this(key, parent, description, new LinkedHashMap<>());
    }

    protected SectionNode(@NotNull String key, @Nullable SectionNode parent, @Nullable String description, Map<@NotNull String, @NotNull Node> children) {
        super(key, parent, description);
        this.children = children;
    }

    public final @NotNull Map<@NotNull String, Node> children() {
        return this.children;
    }

    /**
     * Adds a child {@link ValueNode} with the specified key and value.
     *
     * @param key the key
     * @param value the default value
     * @param <T> the type of the default value
     * @return the section node for chaining
     */
    public <T> @NotNull SectionNode addChild(@NotNull String key, @NotNull T value) {
        return this.addChild(key, TypeFactory.defaultInstance().constructType(value.getClass()), value);
    }

    /**
     * Adds a child {@link ValueNode} with the specified key and type and a {@code null} default value.
     *
     * @param key the key
     * @param type the type of the default value
     * @param <T> tye type of the default value
     * @return the section node for chaining
     */
    public <T> @NotNull SectionNode addChild(@NotNull String key, @NotNull TypeReference<T> type) {
        return this.addChild(key, TypeFactory.defaultInstance().constructType(type));
    }

    /**
     * Adds a child {@link ValueNode} with the specified key and type and a {@code null} default value.
     *
     * @param key the key
     * @param type the type of the default value
     * @return the section node for chaining
     */
    public @NotNull SectionNode addChild(@NotNull String key, @NotNull JavaType type) {
        return this.addChild(key, type, null);
    }

    /**
     * Adds a child {@link ValueNode} with the specific key, description and value.
     *
     * @param key the key
     * @param description the description
     * @param value the default value
     * @param <T> the type of the default value
     * @return the section node for chaining
     */
    public <T> @NotNull SectionNode addChild(@NotNull String key, @Nullable String description, @NotNull T value) {
        return this.addChild(key, description, TypeFactory.defaultInstance().constructType(value.getClass()), value);
    }

    /**
     * Adds a child {@link ValueNode} with the specified key, type, and value
     *
     * @param key the key
     * @param type the type of the default value
     * @param value the default value
     * @param <T> the type of the default value
     * @return the section node for chaining
     */
    public <T> @NotNull SectionNode addChild(@NotNull String key, @NotNull TypeReference<T> type, @Nullable T value) {
        return this.addChild(key, TypeFactory.defaultInstance().constructType(type), value);
    }

    /**
     * Adds a child {@link ValueNode} with the specified key, type, and value
     *
     * @param key the key
     * @param type the type of the default value
     * @param value the default value
     * @param <T> the type of the default value
     * @return the section node for chaining
     */
    public <T> @NotNull SectionNode addChild(@NotNull String key, @NotNull JavaType type, @Nullable T value) {
        return this.addChild(key, null, type, value);
    }

    /**
     * Adds a child {@link ValueNode} with the specified key, description, type and value.
     *
     * @param key the key
     * @param description the description
     * @param type the type of the default value
     * @param value the default value
     * @param <T> the type of the default value
     * @return the section node for chaining
     */
    public <T> @NotNull SectionNode addChild(@NotNull String key, @Nullable String description, @NotNull TypeReference<T> type, @Nullable T value) {
        return this.addChild(key, description, TypeFactory.defaultInstance().constructType(type), value);
    }

    /**
     * Adds a child {@link ValueNode} with the specified key, description, type and value.
     *
     * @param key the key
     * @param description the description
     * @param type the type of the default value
     * @param value the default value
     * @param <T> the type of the default value
     * @return the section node for chaining
     */
    public <T> @NotNull SectionNode addChild(@NotNull String key, @Nullable String description, @NotNull JavaType type, @Nullable T value) {
        return this.addChild(new ValueNode<>(key, this, description, type, value));
    }

    /**
     * Adds a child node. Can be a {@link ValueNode} or {@link SectionNode}
     *
     * @param node the node to add
     * @return the section node for chaining
     */
    public @NotNull SectionNode addChild(@NotNull Node node) {
        this.children.put(node.key(), node);
        return this;
    }

    /**
     * Creates a new {@link SectionNode}.
     *
     * @param key the key for the new section
     * @return the new section
     */
    public @NotNull SectionNode addSection(@NotNull String key) {
        SectionNode section = new SectionNode(key, this);
        this.addChild(section);
        return section;
    }

    /**
     * Creates a new {@link SectionNode}.
     *
     * @param key the key for the new section
     * @param description the description for the new section
     * @return the new section
     */
    public @NotNull SectionNode addSection(@NotNull String key, @Nullable String description) {
        SectionNode section = new SectionNode(key, this, description);
        this.addChild(section);
        return section;
    }

    /**
     * Creates a new {@link SectionNode}.
     *
     * @param key the key for the new section
     * @param newSectionNodeConsumer a consumer for modifying the newly-created section node
     * @return the section node for chaining.
     */
    public @NotNull SectionNode addSection(@NotNull String key, @NotNull Consumer<@NotNull SectionNode> newSectionNodeConsumer) {
        newSectionNodeConsumer.accept(this.addSection(key));
        return this;
    }

    /**
     * Get a node from a `.`-separated path.
     *
     * @param path the path to search for the node on
     * @return the node, or null if none found
     */
    public @Nullable Node getNode(@NotNull String path) {
        final String[] pathArray = path.split("\\.");
        if (!children.containsKey(pathArray[0])) {
            return null;
        }
        if (pathArray.length == 1) {
            return children.get(pathArray[0]);
        } else {
            return ((SectionNode) children.get(pathArray[0])).getNode(path.substring(path.indexOf('.') + 1));
        }
    }

    /**
     * Get a value, either a {@link SectionNode} or the value of a value node
     * from a path
     *
     * @param path the path to search
     * @return the value, or {@link SectionNode} or null
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(String path) {
        final Node node = this.getNode(path);
        if (node != null) {
            if (node instanceof ValueNode<?> valueNode) {
                return (T) valueNode.value();
            } else {
                return (T) node;
            }
        }
        return null;
    }

    /**
     * Sets the (or adds a) value node at the path to a value.
     *
     * @param path the node path
     * @param value the value to set
     * @param <T> the value type
     * @return the newly-created {@link ValueNode}
     */
    public <T> @Nullable ValueNode<T> set(@NotNull String path, @NotNull T value) {
        return this.set(path, TypeFactory.defaultInstance().constructType(value.getClass()), value);
    }

    /**
     * Sets the (or adds a) value node at the path to a value.
     *
     * @param path the node path
     * @param type the value type
     * @param value the value to set
     * @param <T> the value type
     * @return the newly-created {@link ValueNode}
     */
    public <T> @Nullable ValueNode<T> set(@NotNull String path, @NotNull TypeReference<T> type, @Nullable T value) {
        return this.set(path, TypeFactory.defaultInstance().constructType(type), value);
    }

    /**
     * Sets the (or adds a) value node at the path to a value.
     *
     * @param path the node path
     * @param type the value type
     * @param value the value to set
     * @param <T> the value type
     * @return the newly-created {@link ValueNode}
     */
    @SuppressWarnings("unchecked")
    public <T> @NotNull ValueNode<T> set(@NotNull String path, @NotNull JavaType type, @Nullable T value) {
        final String[] pathArray = path.split("\\.");
        final Node node = this.children().get(pathArray[0]);
        if (node == null) {
            if (pathArray.length == 1) {
                this.addChild(pathArray[0], type, value);
                return (ValueNode<T>) this.children().get(pathArray[0]);
            } else {
                return this.addSection(pathArray[0]).set(path.substring(path.indexOf('.') + 1), type, value);
            }
        } else {
            if (node instanceof SectionNode sectionNode) {
                return sectionNode.set(path.substring(path.indexOf('.') + 1), type, value);
            } else {
                ValueNode<T> valueNode = (ValueNode<T>) node;
                valueNode.value(value);
                return valueNode;
            }
        }
    }

    /**
     * Checks if this section node is the root node of a file.
     *
     * @return true if a root node
     */
    public boolean isRoot() {
        return Objects.equals(this.key, "");
    }

    public void clear() {
        this.children().clear();
    }

    final void load(@NotNull TreeNode node, LoadContext context) {
        if (node instanceof ObjectNode objectNode) {
            objectNode.fields().forEachRemaining(entry -> {
                final String key = entry.getKey();
                final JsonNode value = entry.getValue();
                if (!this.children().containsKey(key)) {
                    context.invalidKeyHandler().handleInvalidKey(key, context);
                } else if (value instanceof ObjectNode childObjectNode && this.children().get(key) instanceof SectionNode sectionNode) {
                    sectionNode.load(childObjectNode, context);
                } else {
                    ValueNode<?> valueNode = (ValueNode<?>) this.children.get(key);
                    Object object;
                    try {
                        object = context.mapper().convertValue(value, valueNode.type());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Could not set value of " + valueNode.path() + " to " + value + " from " + context.root().file(), e.getCause() == null ? e : e.getCause());
                    }
                    valueNode.setValue(object);
                }
            });
        }
    }

    @Override
    final @NotNull String asString(@NotNull SerializeContext context, int indent) throws JsonProcessingException {
        StringBuilder builder = new StringBuilder();
        for (Node child : this.children().values()) {
            if (child instanceof ValueNode) {
                builder.append(child.asString(context, indent));
            } else {
                if (child.description() != null) {
                    builder.append(" ".repeat(indent)).append(context.commentChar()).append(" ").append(child.description()).append("\n");
                }
                builder.append(" ".repeat(indent)).append(child.key()).append(":\n")
                        .append(child.asString(context, indent + context.indentSize()));
            }
        }
        return builder.toString();
    }
}

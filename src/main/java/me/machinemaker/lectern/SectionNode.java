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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a section in a configuration structure
 */
public interface SectionNode extends Node {

    @Override
    default boolean isRoot() {
        return false;
    }

    @Override
    default boolean isSection() {
        return true;
    }

    /**
     * Get children Value and Section nodes of this section node.
     *
     * @return children nodes
     */
    @NotNull
    Map<String, Node> children();

    /**
     * Adds a child node.
     *
     * @param node the child
     */
    void addChild(@NotNull Node node);

    /**
     * Adds a child node.
     *
     * @param key the node key
     * @param value the node default value
     * @param <T> the node type
     * @return the section node for chaining
     */
    @NotNull
    default <T> SectionNode addChild(@NotNull String key, @NotNull T value) {
        addChild(new ValueNodeImpl<>(key, this, value));
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
    default <T> SectionNode addChild(@NotNull String key, @NotNull T value, @NotNull String description) {
        addChild(new ValueNodeImpl<>(key, this, value, description));
        return this;
    }

    /**
     * Adds a new section.
     *
     * @param key the section node key
     * @return the newly created section
     */
    @NotNull
    default SectionNode addSection(@NotNull String key) {
        SectionNode sectionNode = new SectionNodeImpl(key, this);
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
    default SectionNode addSection(@NotNull String key, @NotNull Consumer<SectionNode> newSectionNodeConsumer) {
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
    default SectionNode addSection(@NotNull String key, @NotNull String description) {
        SectionNode sectionNode = new SectionNodeImpl(key, this, description);
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
    default SectionNode addSection(@NotNull String key, @NotNull String description, @NotNull Consumer<SectionNode> newSectionNodeConsumer) {
        newSectionNodeConsumer.accept(addSection(key, description));
        return this;
    }

    /**
     * Gets a value for an array of node keys.
     *
     * @param path keys to lead to a value
     * @param <T> the value type
     * @return the value
     */
    @Nullable
    <T> T get(@NotNull String...path);

    /**
     * Sets a value with an array of node keys.
     *
     * @param value the value to set
     * @param path keys to lead to value
     * @param <T> the value type
     */
    <T> void set(@NotNull T value, @NotNull String...path);

    /**
     * Returns a map of all keys and their values.
     * Ignores any {@link SectionNode}s that may be children.
     *
     * @return a key/value map
     */
    @NotNull
    default Map<String, Object> values() {
        return values(false);
    }

    /**
     * Returns a map of all keys and their values/sections.
     *
     * @param includeSections include {@link SectionNode}s in the map.
     * @return a key/value map
     */
    @NotNull
    default Map<String, Object> values(boolean includeSections) {
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
    interface RootSectionNode extends SectionNode {

        @Override
        default boolean isRoot() {
            return true;
        }
    }
}

package me.machinemaker.lectern;

import io.leangen.geantyref.GenericTypeReflector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SectionNode extends Node {

    private final Map<@NotNull String, @NotNull Node> children;

    protected SectionNode(@NotNull String key) {
        this(key, new HashMap<>());
    }

    protected SectionNode(@NotNull String key, @NotNull Map<@NotNull String, @NotNull Node> children) {
        this(key, null, children);
    }

    protected SectionNode(@NotNull String key, @Nullable String description, Map<@NotNull String, @NotNull Node> children) {
        super(key, description);
        this.children = children;
    }
    public final @NotNull Map<@NotNull String, @NotNull Node> children() {
        return this.children;
    }

    public boolean isRoot() {
        return Objects.equals(this.key, "");
    }

    @SuppressWarnings("unchecked")
    final void load(Map<String, ?> map) {
        if (map != null) {
            map.forEach((key, value) -> {
                if (!this.children().containsKey(key)) {
                    // TODO throw config load exception
                    throw new RuntimeException(String.format("%s is not a recognized key, and was not loaded into the configuration", key));
                }
                if (value instanceof Map<?, ?> && this.children().get(key) instanceof SectionNode sectionNode) {
                    sectionNode.load((Map<String, ?>) value);
                } else {
                    ValueNode<?> node = (ValueNode<?>) this.children.get(key);
                    Class<?> erasure = GenericTypeReflector.erase(node.type().getType());
                    if (erasure.isInstance(value)) {
                        if (erasure.getTypeParameters().length == 0) {
                            node.setValue(value);
                        }
                    }
                    if (erasure.getTypeParameters().length == 0 && erasure.isInstance(value)) {

                    }
                    if (value.getClass().isAssignableFrom(GenericTypeReflector.erase(node.type().getType()))) {

                    }
                }
            });
        }
    }
}

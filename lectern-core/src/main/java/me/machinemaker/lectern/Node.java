package me.machinemaker.lectern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Node {

    protected final String key;
    protected String description;

    protected Node(@NotNull String key) {
        this(key, null);
    }

    protected Node(@NotNull String key, @Nullable String description) {
        this.key = key;
        this.description = description;
    }

    public @NotNull String key() {
        return this.key;
    }

    public @Nullable String description() {
        return this.description;
    }
}

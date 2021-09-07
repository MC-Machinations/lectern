package me.machinemaker.lectern;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ConfigurationNode extends SectionNode {

    private final Path file;

    protected ConfigurationNode(Path file) {
        super("");
        this.file = file;
    }

    public @NotNull Path file() {
        return this.file;
    }

    public abstract void save();

    public abstract void reload();

    public void reloadOrSave() {
        if (Files.exists(this.file)) {
            this.reload();
        } else {
            this.save();
        }
    }

    public void reloadOrSaveThenSave() {
        if (Files.exists(this.file)) {
            this.reload();
            this.save();
        } else {
            this.save();
        }
    }
}

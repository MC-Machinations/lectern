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

import me.machinemaker.lectern.SectionNode.RootSectionNode;
import me.machinemaker.lectern.exceptions.ConfigReloadException;
import me.machinemaker.lectern.exceptions.ConfigSaveException;
import me.machinemaker.lectern.yaml.LecternConstructor;
import me.machinemaker.lectern.yaml.LecternRepresenter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Holds nodes and values for a configuration object.
 */
public class LecternConfig extends RootSectionNode implements Serializable {

    private static final long serialVersionUID = 1L;

    static int INDENT = 2;
    private static final DumperOptions DUMPER_OPTIONS = new DumperOptions();

    static {
        DUMPER_OPTIONS.setIndentWithIndicator(true);
        DUMPER_OPTIONS.setIndicatorIndent(INDENT);
        DUMPER_OPTIONS.setPrettyFlow(true);
        DUMPER_OPTIONS.setDefaultFlowStyle(FlowStyle.BLOCK);
    }

    static final Yaml LECTERN_YAML = new Yaml(new LecternConstructor(), new LecternRepresenter(), DUMPER_OPTIONS);

    private final RootSectionNode root;
    private final File file;

    protected LecternConfig(File file) {
        this(file, null);
    }

    protected LecternConfig(File file, String header) {
        this.root = new RootSectionNode(header);
        this.file = file;
    }

    /**
     * Saves the configuration to a file.
     *
     * @throws me.machinemaker.lectern.exceptions.ConfigSaveException if there is an error saving the file
     */
    public void save() {
        file.getParentFile().mkdirs();
        String data = this.root.asString(0);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8);) {
            writer.write(data);
        } catch (IOException ioException) {
            throw new ConfigSaveException(this, ioException);
        }
    }

    /**
     * Reloads the configuration file with values from the file.
     *
     * @throws me.machinemaker.lectern.exceptions.ConfigReloadException if there is an error reloading the file (like it doesn't exist)
     */
    public void reload() {
        try {
            this.root.load(LECTERN_YAML.load(new FileInputStream(file)));
        } catch (IOException e) {
            throw new ConfigReloadException(this, e);
        }
    }

    /**
     * Reloads the config if the file exists. Saves the config if the file doesn't exist.
     * Meant for use just after creating the configuration to only
     * load values from the file if it's been created before.
     *
     * @throws me.machinemaker.lectern.exceptions.ConfigReloadException if there is an error reloading the file
     * @throws me.machinemaker.lectern.exceptions.ConfigSaveException if there is an error saving the file
     */
    public void reloadOrSave() {
        if (this.file.exists()) {
            this.reload();
        } else {
            this.save();
        }
    }

    /**
     * Gets the file associated with this configuration
     *
     * @return the config file
     */
    @NotNull
    public File getFile() {
        return this.file;
    }

    @Override
    public Node getNode(@NotNull String path) {
        return root.getNode(path);
    }

    @Override
    public <T> T get(@NotNull String path) {
        return root.get(path);
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    @NotNull
    @Override
    public String key() {
        return root.key();
    }

    @NotNull
    @Override
    public SectionNode parent() {
        return root.parent();
    }

    @Nullable
    @Override
    public String description() {
        return null;
    }

    @Override
    public void description(@Nullable String description) {
        // Ignore on root
    }

    @NotNull
    @Override
    public Map<String, Node> children() {
        return root.children();
    }

    @Override
    public void addChild(@NotNull Node node) {
        root.addChild(node);
    }

    @NotNull
    @Override
    public <T> ValueNode<T> set(@NotNull String path, @NotNull T value) {
        return root.set(path, value);
    }
}

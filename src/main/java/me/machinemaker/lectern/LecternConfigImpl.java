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

import me.machinemaker.lectern.SectionNodeImpl.RootSectionNodeImpl;
import me.machinemaker.lectern.exceptions.ConfigReloadException;
import me.machinemaker.lectern.exceptions.ConfigSaveException;
import me.machinemaker.lectern.yaml.LecternConstructor;
import me.machinemaker.lectern.yaml.LecternRepresenter;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

class LecternConfigImpl implements LecternConfig {

    static int INDENT = 2;
    private static final DumperOptions DUMPER_OPTIONS = new DumperOptions();

    static {
        DUMPER_OPTIONS.setIndentWithIndicator(true);
        DUMPER_OPTIONS.setIndicatorIndent(INDENT);
        DUMPER_OPTIONS.setPrettyFlow(true);
        DUMPER_OPTIONS.setDefaultFlowStyle(FlowStyle.BLOCK);
    }

    static final Yaml LECTERN_YAML = new Yaml(new LecternConstructor(), new LecternRepresenter(), DUMPER_OPTIONS);

    private final RootSectionNodeImpl root;
    private final File file;

    protected LecternConfigImpl(File file) {
        this(file, null);
    }

    protected LecternConfigImpl(File file, String header) {
        this.root = new RootSectionNodeImpl(header);
        this.file = file;
    }

    @Override
    public @NotNull SectionNode.RootSectionNode root() {
        return root;
    }

    @Override
    public void save() {
        file.getParentFile().mkdirs();
        String data = this.root.asString(0);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8);) {
            writer.write(data);
        } catch (IOException ioException) {
            throw new ConfigSaveException(this, ioException);
        }
    }

    @Override
    public void reload() {
        try {
            this.root.load(LECTERN_YAML.load(new FileInputStream(file)));
        } catch (Throwable e) {
            throw new ConfigReloadException(this, e);
        }
    }

    @Override
    public void reloadOrSave() {
        if (this.file.exists()) {
            this.reload();
        } else {
            this.save();
        }
    }

    @Override
    public <T> T get(@NotNull String... path) {
        return root.get(path);
    }

    @NotNull
    @Override
    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return this.root.toString();
    }
}

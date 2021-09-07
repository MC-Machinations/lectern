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

import com.fasterxml.jackson.databind.ObjectMapper;
import me.machinemaker.lectern.contexts.InvalidKeyHandler;
import me.machinemaker.lectern.contexts.LoadContext;
import me.machinemaker.lectern.contexts.SerializeContext;
import me.machinemaker.lectern.exceptions.ConfigSaveException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ConfigurationNode extends SectionNode implements Reloadable {

    private final Path file;
    private final ObjectMapper mapper;
    private final LoadContext loadContext;
    private final SerializeContext serializeContext;

    protected ConfigurationNode(Path file, @NotNull ObjectMapper mapper, @NotNull InvalidKeyHandler invalidKeyHandler, char commentChar, int indentSize) {
        super("", null);
        this.file = file;
        this.mapper = mapper;
        this.loadContext = new LoadContext(this, invalidKeyHandler, mapper);
        this.serializeContext = new SerializeContext(this, mapper, commentChar, indentSize);
    }

    @Override
    public @NotNull Path file() {
        return this.file;
    }

    @Override
    public void save() {
        try {
            Files.createDirectories(this.file().getParent());
            if (Files.notExists(this.file())) {
                Files.createFile(this.file());
            }
            Files.writeString(this.file(), this.asString(this.serializeContext, 0));
        } catch (IOException e) {
            throw new ConfigSaveException(this, e);
        }
    }

    @Override
    public void reload() {
        try {
            this.load(this.mapper.readTree(Files.newInputStream(this.file)), this.loadContext);
        } catch (IOException ioException) {
            throw new ConfigSaveException(this, ioException);
        }
    }

    protected abstract static class TypedBuilder<C extends ConfigurationNode, B extends TypedBuilder<C, B>> {

        private final Path file;
        private ObjectMapper mapper = new ObjectMapper();
        private InvalidKeyHandler invalidKeyHandler = InvalidKeyHandler.Preset.EXCEPTION;

        protected TypedBuilder(@NotNull Path file) {
            this.file = file;
        }

        @SuppressWarnings("unchecked")
        private @NotNull B self() {
            return (B) this;
        }

        protected final @NotNull Path file() {
            return this.file;
        }

        protected final @NotNull ObjectMapper mapper() {
            return this.mapper;
        }

        protected final @NotNull InvalidKeyHandler invalidKeyHandler() {
            return this.invalidKeyHandler;
        }

        public @NotNull B withMapper(@NotNull ObjectMapper mapper) {
            this.mapper = mapper;
            return self();
        }

        public @NotNull B withInvalidKeyHandler(InvalidKeyHandler invalidKeyHandler) {
            this.invalidKeyHandler = invalidKeyHandler;
            return self();
        }

        public abstract @NotNull C build();

    }
}

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
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import me.machinemaker.lectern.contexts.InvalidKeyHandler;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class YamlConfiguration extends ConfigurationNode {

    private YamlConfiguration(@NotNull Path file, @NotNull ObjectMapper mapper, @NotNull InvalidKeyHandler invalidKeyHandler, char commentChar, int indentSize) {
        super(file, mapper, invalidKeyHandler, commentChar, indentSize);
    }

    public static @NotNull Builder builder(@NotNull Path path) {
        return new Builder(path);
    }

    public static final class Builder extends TypedBuilder<YamlConfiguration, Builder> {

        private static final YAMLFactory YAML_FACTORY = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR);

        private ObjectMapper yamlMapper;
        private int indentSize = 2;

        private Builder(@NotNull Path file) {
            super(file);
        }

        public Builder withYamlMapper(ObjectMapper yamlMapper) {
            this.yamlMapper = yamlMapper;
            return this;
        }

        public Builder withIndentSize(int indentSize) {
            this.indentSize = indentSize;
            return this;
        }

        @Override
        public @NotNull YamlConfiguration build() {
            ObjectMapper mapper = yamlMapper != null ? yamlMapper : new ObjectMapper(YAML_FACTORY);
            return new YamlConfiguration(this.file(), mapper, this.invalidKeyHandler(), '#', this.indentSize);
        }
    }
}

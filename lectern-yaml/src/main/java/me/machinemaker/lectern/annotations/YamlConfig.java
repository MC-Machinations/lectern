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
package me.machinemaker.lectern.annotations;

import me.machinemaker.lectern.ConfigurationNode;
import me.machinemaker.lectern.contexts.InvalidKeyHandler;
import me.machinemaker.lectern.supplier.ConfigurationSupplier;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Configuration(supplier = YamlConfig.SimpleYamlConfigurationSupplier.class)
public @interface YamlConfig {

    String fileName() default "config.yml";

    int indent() default 2;

    InvalidKeyHandler.Preset invalidKeyHandler() default InvalidKeyHandler.Preset.EXCEPTION;

    class SimpleYamlConfigurationSupplier implements ConfigurationSupplier<YamlConfig> {

        @Override
        public @NotNull ConfigurationNode createConfiguration(@NotNull Path parentDir, @NotNull YamlConfig configuration) {
            return me.machinemaker.lectern.YamlConfiguration.builder(parentDir.resolve(configuration.fileName())).withInvalidKeyHandler(configuration.invalidKeyHandler()).withIndentSize(configuration.indent()).build();
        }
    }
}

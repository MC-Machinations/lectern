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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Manager for configurations
 */
public final class Lectern {

    private Lectern() {
    }

    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * For general customization of the object mapper.
     *
     * @param objectMapper custom ObjectMapper
     */
    public static void loadObjectMapper(@NotNull ObjectMapper objectMapper) {
        OBJECT_MAPPER = objectMapper;
    }

    /**
     * Creates a LecternConfig from a configuration file
     *
     * @param file the config file
     * @return the LecternConfig object
     */
    public static LecternConfig createConfig(File file) {
        return new LecternConfigImpl(file);
    }

    /**
     * Creates a LecternConfig from a configuration file.
     * Includes a multi-line header at the top of the file.
     *
     * @param file the config file
     * @param header the header text
     * @return the LecternConfig object
     */
    public static LecternConfig createConfig(File file, String header) {
        return new LecternConfigImpl(file, header);
    }

    /**
     * Registers and initializes a configuration class.
     *
     * @param configClass the configuration class
     * @param parentDir the parent directory for the resulting configuration file
     * @param <T> the configuration class type
     * @return the newly created instance of the configuration class
     */
    public static <T extends LecternBaseConfig> T registerConfig(Class<T> configClass, File parentDir) {
        T instance;
        try {
            instance = configClass.getDeclaredConstructor().newInstance();
            instance.init(parentDir);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | IOException e) {
            throw new RuntimeException(String.format("Unable to create new instance of %s", configClass.getSimpleName()), e);
        }

        return instance;
    }
}

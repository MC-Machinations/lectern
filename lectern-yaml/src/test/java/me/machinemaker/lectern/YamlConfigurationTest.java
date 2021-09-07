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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import me.machinemaker.lectern.exceptions.InvalidKeyException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YamlConfigurationTest {

    static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR));
    static final Path TEST_RESOURCES = Path.of("src", "test", "resources");

    static YamlConfiguration create(String fileName) {
        var config = YamlConfiguration.builder(TEST_RESOURCES.resolve(fileName)).withYamlMapper(mapper).build();
        config.addSection("section1", section -> {
            section.addChild("key1", true)
                    .addChild("key2", TypeFactory.defaultInstance().constructCollectionType(List.class, Fruit.class))
                    .addChild("key3", new TypeReference<List<String>>() {});
        }).addChild("key", new TypeReference<Double>() {});
        return config;
    }

    @Test
    void testInvalidKeyHandlerException() {
        var config = YamlConfiguration.builder(TEST_RESOURCES.resolve("config.yml")).withYamlMapper(mapper).build();
        assertThrows(InvalidKeyException.class, config::reload);
    }

    @Test
    void testReadingInYamlConfig() {
        var config = create("config.yml");
        config.reload();

        assertIterableEquals(List.of(Fruit.APPLE, Fruit.ORANGE), config.get("section1.key2"));
        assertIterableEquals(List.of("element1", "element2"), config.get("section1.key3"));
        assertEquals(Boolean.FALSE, config.get("section1.key1"));
        assertEquals(32.2, config.get("key"));
    }

    @Test
    void testWritingYamlConfig() throws IOException {
        Files.deleteIfExists(TEST_RESOURCES.resolve("config-write.yml"));
        var config = create("config-write.yml");
        config.set("section1.key1", false);
        config.set("section1.key2", TypeFactory.defaultInstance().constructCollectionType(List.class, Fruit.class), List.of(Fruit.APPLE, Fruit.ORANGE));
        config.set("section1.key3", new TypeReference<List<String>>() {}, List.of("element1", "element2"));
        config.set("key", 32.2);
        config.save();
        assertEquals(Files.readString(TEST_RESOURCES.resolve("config.yml")), Files.readString(TEST_RESOURCES.resolve("config-write.yml")));
    }

    enum Fruit {
        ORANGE,
        APPLE,
        PEAR
    }

    @Test
    void test() throws JsonProcessingException {
        System.out.println(mapper.writeValueAsString(Set.of("element1", "element2")));
    }
}

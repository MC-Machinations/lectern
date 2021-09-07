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

import me.machinemaker.lectern.annotations.ConfigurationSection;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.YamlConfig;
import me.machinemaker.lectern.annotations.validations.Size;
import me.machinemaker.lectern.annotations.validations.numbers.Positive;
import me.machinemaker.lectern.exceptions.validations.SizeValidationException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YamlObjectConfigTest {

    static final Path TEST_RESOURCES = Path.of("src", "test", "resources");

    @Test
    void testReadingIntoObject() {
        Config config = new Config();
        config.init(TEST_RESOURCES);
        assertEquals(32.2, config.key);
        assertFalse(config.section1.thisIsKey1);
        assertIterableEquals(List.of(YamlConfigurationTest.Fruit.APPLE, YamlConfigurationTest.Fruit.ORANGE), config.section1.fruitList);
    }

    @Test
    void testSettingValue() {
        Config config = new Config();
        config.init(TEST_RESOURCES);
        config.rootNode().set("key", 15.5D);
        assertEquals(15.5D, config.rootNode().get("key"));
        assertEquals(15.5D, config.key);
    }

    @Test
    void testSizeValidation() {
        Config config = new Config();
        config.init(TEST_RESOURCES);
        assertThrows(IllegalArgumentException.class, () -> config.rootNode().set("section1.key3", List.of("1", "2", "3", "4")));
    }

    @Test
    void testPositiveValidation() {
        Config config = new Config();
        config.init(TEST_RESOURCES);
        assertThrows(IllegalArgumentException.class, () -> config.rootNode().set("key", -23D));
    }

    @YamlConfig(fileName = "config-object.yml")
    static class Config extends BaseConfig {

        @Positive
        double key = 100.2; // should be 32.2 after read-in

        ConfigSection section1;

        @ConfigurationSection(path = "section1")
        static class ConfigSection {

            @Key("key1")
            boolean thisIsKey1 = true; // should be false

            @Key("key2")
            List<YamlConfigurationTest.Fruit> fruitList = List.of(YamlConfigurationTest.Fruit.ORANGE);

            @Size(max = 3)
            List<String> key3 = List.of("element1", "element3");
        }

    }
}

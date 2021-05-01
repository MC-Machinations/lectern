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

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.lectern.annotations.LecternConfigurationSection;
import me.machinemaker.lectern.annotations.Validate;
import me.machinemaker.lectern.exceptions.RegexValidationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LecternAnnotationTest {

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.deleteIfExists(Path.of("src", "test", "resources", "annotation", "config.yml"));
    }

    @Test
    void annotationConfig() {
        TestConfig testConfig = Lectern.registerConfig(TestConfig.class, LecternTest.getFile("annotation"));
        assertEquals("This is a 'default' string", testConfig.testString);
        assertEquals("1234", testConfig.mustBeNumber);

        assertEquals(3, testConfig.subTestConfig.value1);
        assertTrue(testConfig.subTestConfig.value2);
    }

    @Test
    void saveAnnotationConfig() {
        TestConfig testConfig = Lectern.registerConfig(TestConfig.class, LecternTest.getFile("annotation"));
        testConfig.testString = "WAY BETTER STRING";
        testConfig.subTestConfig.value2 = false;
        testConfig.save();
        testConfig = Lectern.registerConfig(TestConfig.class, LecternTest.getFile("annotation"));
        assertEquals("WAY BETTER STRING", testConfig.testString);
        assertFalse(testConfig.subTestConfig.value2);
    }

    @Test
    void regexValidation() {
        TestConfig testConfig = Lectern.registerConfig(TestConfig.class, LecternTest.getFile("annotation"));
        testConfig.mustBeNumber = "HEY!!!";
        assertThrows(RegexValidationException.class, testConfig::save);
    }

    @LecternConfiguration
    public static class TestConfig extends LecternBaseConfig {

        @Description("Comment above testString")
        public String testString = "This is a 'default' string";

        @Validate(regex = "\\d+")
        public String mustBeNumber = "1234";

        public transient SubTestConfig subTestConfig;

        @LecternConfigurationSection(path = "sub")
        public static class SubTestConfig {

            public int value1 = 3;
            @Description("Boolean comment")
            @Key("this-is-a-boolean")
            public boolean value2 = true;
        }
    }
}

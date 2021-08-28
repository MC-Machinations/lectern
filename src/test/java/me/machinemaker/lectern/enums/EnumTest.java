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
package me.machinemaker.lectern.enums;

import me.machinemaker.lectern.Lectern;
import me.machinemaker.lectern.LecternBaseConfig;
import me.machinemaker.lectern.LecternConfig;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.lectern.annotations.LecternConfigurationSection;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumTest {

    static final Path PATH = Path.of("src", "test", "resources", "enums");

    @Test
    void testWritingEnums() throws IOException {
        Files.deleteIfExists(PATH.resolve("config.yml"));
        LecternConfig config = Lectern.createConfig(PATH.resolve("config.yml").toFile());
        config.addChild("test-enum", TestEnum.ONE);
        config.addSection("section1").addChild("test-section-enum", TestEnum.THREE, "This is a description");
        config.save();
        assertTrue(FileUtils.contentEquals(PATH.resolve("config.yml").toFile(), PATH.resolve("expected-config.yml").toFile()));
    }

    @Test
    void testReadingEnums() {
        Config config = Lectern.registerConfig(Config.class, PATH.toFile());
        config.save();
        config.reload();
        assertEquals(TestEnum.ONE, config.testEnum);
        assertEquals(TestEnum.THREE, config.section1.section1TestEnum);
    }


    @LecternConfiguration(fileName = "reading-enums-config.yml")
    static class Config extends LecternBaseConfig {

        @Key("test-enum")
        public TestEnum testEnum = TestEnum.ONE;

        public Section1 section1;

        @LecternConfigurationSection(path = "section1")
        static class Section1 {

            @Key("test-section-enum")
            public TestEnum section1TestEnum = TestEnum.THREE;
        }
    }

    enum TestEnum {
        ONE,
        TWO,
        THREE
    }
}

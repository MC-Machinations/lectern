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
package me.machinemaker.lectern.object;

import me.machinemaker.lectern.Lectern;
import me.machinemaker.lectern.object.ObjectConfig.ObjectCopyConfig;
import me.machinemaker.lectern.object.ObjectConfig.ObjectModifiedConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestObjectConfig {

    static final Path PATH = Path.of("src", "test", "resources", "object");

    @Test
    void testInitializeConfig() {
        ObjectConfig config = Lectern.registerConfig(ObjectConfig.class, PATH.toFile());
        System.out.println(config.getConfig());

        assertTrue(config.getFile().exists());
    }

    @Test
    void testLoadFields() {
        ObjectConfig config = Lectern.registerConfig(ObjectConfig.class, PATH.toFile());
        assertEquals(1L, config.testLong);
        assertEquals(301, config.testInt);
        assertIterableEquals(List.of(1.2, 3.4, 5.6), config.doubleList);
    }

    @Test
    void testLoadChangedFields() {
        ObjectConfig config = Lectern.registerConfig(ObjectCopyConfig.class, PATH.toFile());

        assertEquals(5L, config.testLong);
        assertFalse(config.isTrue);
        assertIterableEquals(List.of("Rachel"), config.nameList);
    }

    @Test
    void testSavedChanges() throws IOException {
        Files.deleteIfExists(PATH.resolve("modifiedConfig.yml"));
        ObjectConfig config = Lectern.registerConfig(ObjectModifiedConfig.class, PATH.toFile());

        config.testInt = 5;
        config.testFloat = 865.32f;
        config.testDouble = 30.4D;
        config.save();

        config = Lectern.registerConfig(ObjectModifiedConfig.class, PATH.toFile());
        assertEquals(5, config.testInt);
        assertEquals(865.32f, config.testFloat);
        assertEquals(30.4D, config.testDouble);
    }

}

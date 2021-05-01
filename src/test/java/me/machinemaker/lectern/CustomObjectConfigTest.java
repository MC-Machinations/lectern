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

import me.machinemaker.lectern.LecternCustomObjectTest.GoodCustomObject;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.lectern.serialization.LecternSerialization;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomObjectConfigTest {

    static CustomObjectConfig customObjectConfig;
    static final List<GoodCustomObject> goodCustomObjectList = List.of(new GoodCustomObject(1, "A", List.of(true)), new GoodCustomObject(2, "B", List.of(false)));



    @BeforeAll
    static void beforeAll() throws IOException {
        Files.deleteIfExists(LecternTest.getFile("custom-objects", "config.yml").toPath());
        customObjectConfig = Lectern.registerConfig(CustomObjectConfig.class, LecternTest.getFile("custom-objects"));
        LecternSerialization.registerSerializable(GoodCustomObject.class);
    }

    @Test
    void writeConfig() throws IOException, URISyntaxException {
        customObjectConfig.save();
        List<String> testOutput = Files.readAllLines(Paths.get(getClass().getResource("/custom-objects/config.yml").toURI()), StandardCharsets.UTF_8);
        List<String> intendedOutput = Files.readAllLines(LecternTest.getFile("intended-outputs", "custom-objects", "config.yml").toPath(), StandardCharsets.UTF_8);
        assertEquals(intendedOutput, testOutput);
    }

    @Test
    void readConfig() throws IOException, URISyntaxException {
        customObjectConfig.save();
        customObjectConfig.goodCustomObjectList = List.of(new GoodCustomObject(26, "Z", List.of(false)), new GoodCustomObject(2, "B", List.of(true)));
        customObjectConfig.reload();
        var equals = true;
        assertEquals(CustomObjectConfigTest.goodCustomObjectList.size(), customObjectConfig.goodCustomObjectList.size());
        for (int i = 0; i < customObjectConfig.goodCustomObjectList.size(); i++) {
            assertEquals(CustomObjectConfigTest.goodCustomObjectList.get(i).toString(), customObjectConfig.goodCustomObjectList.get(i).toString());
        }
    }

    @LecternConfiguration
    public static class CustomObjectConfig extends LecternBaseConfig {

        @Description("List of custom objects")
        public List<GoodCustomObject> goodCustomObjectList = CustomObjectConfigTest.goodCustomObjectList;

        @Description("number")
        public List<Integer> list = List.of(3, 2, 1);
    }
}

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

import me.machinemaker.lectern.SectionNodeImpl.RootSectionNodeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LecternTest {

    private static final LecternConfig config = Lectern.createConfig(getFile("config.yml"));

    @BeforeEach
    void beforeEach() throws IOException {
        Files.deleteIfExists(getFile("config.yml").toPath());
        config
                .addChild("test", true)
                .addChild("test-list", List.of(1, 3, 5), "List of numbers")
                .addSection("section1", lecternSectionNode -> {
                    lecternSectionNode.addChild("section1deep", new String[] {"test1", "test2"}).addChild("key", "value");
                });
//        config.save();
    }

    @Test
    void save() throws IOException, URISyntaxException {
        config.save();
        List<String> testOutput = Files.readAllLines(getFile("config.yml").toPath(), StandardCharsets.UTF_8);
        List<String> intendedOutput = Files.readAllLines(getFile("intended-outputs", "config.yml").toPath(), StandardCharsets.UTF_8);
        assertEquals(intendedOutput, testOutput);
    }

    @Test
    void read() throws FileNotFoundException {
        Map<String, ?> map = LecternConfigImpl.LECTERN_YAML.load(new FileInputStream(getFile("read-config.yml")));
        ((RootSectionNodeImpl) ((LecternConfigImpl) config).root()).load(map);

        assertFalse(config.<Boolean>get("test"));
        assertEquals("wayBetterValue!", config.get("section1", "key"));
        assertEquals(List.of(5, 4, 3), config.get("test-list"));
        assertEquals(List.of("hello", "there"), config.get("section1", "section1deep"));
    }

    static File getFile(String...path) {
        String[] fullPath = Stream.concat(List.of("test", "resources").stream(), Arrays.stream(path)).toArray(String[]::new);
        return Paths.get("src", fullPath).toFile();
    }
}

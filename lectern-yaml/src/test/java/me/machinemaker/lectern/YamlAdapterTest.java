package me.machinemaker.lectern;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

class YamlAdapterTest {

    static final Path TEST_RESOURCES = Path.of("src", "test", "resources");

    @Test
    void test() throws IOException {
        Yaml yaml = new Yaml();
        System.out.println(yaml.loadAs(new ByteArrayInputStream(Files.readAllBytes(TEST_RESOURCES.resolve("config.yml"))), Map.class));
    }
}

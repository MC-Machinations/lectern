package me.machinemaker.lectern;

import me.machinemaker.lectern.exceptions.ConfigSaveException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlConfigurationNode extends ConfigurationNode {

    private static final Yaml YAML;
    static {
        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setIndentWithIndicator(true);
        dumperOptions.setIndicatorIndent(2);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setProcessComments(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        YAML = new Yaml(dumperOptions);
    }

    public YamlConfigurationNode(Path file) {
        super(file);
    }

    @Override
    public void save() {
        try {
            Files.createFile(this.file());
            // Files.writeString(this.file(), )

        } catch (IOException e) {
            throw new ConfigSaveException(this, e);
        }
    }

    @Override
    public void reload() {

    }
}

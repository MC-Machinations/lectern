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
package me.machinemaker.lectern.yaml;

import me.machinemaker.lectern.serialization.LecternSerialization;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.LinkedHashMap;
import java.util.Map;

public class LecternConstructor extends SafeConstructor {

    public LecternConstructor() {
        this.yamlConstructors.put(Tag.MAP, new LecternSerializableConstruct());
    }

    private class LecternSerializableConstruct extends ConstructYamlMap {

        @Override
        public Object construct(Node node) {
            if (node.isTwoStepsConstruction()) {
                throw new YAMLException("No two-step constructions");
            }

            Map<?, ?> yamlMap = (Map<?, ?>) super.construct(node);
            if (yamlMap.containsKey(LecternSerialization.SERIALIZABLE_KEY)) {
                Map<String, Object> map = new LinkedHashMap<>(yamlMap.size());
                yamlMap.forEach((key, value) -> {
                    map.put(key.toString(), value);
                });

                return LecternSerialization.deserialize(map);
            }

            return super.construct(node);
        }
    }
}

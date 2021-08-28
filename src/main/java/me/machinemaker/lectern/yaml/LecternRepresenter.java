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

import me.machinemaker.lectern.serialization.LecternSerializable;
import me.machinemaker.lectern.serialization.LecternSerialization;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashMap;
import java.util.Map;

public class LecternRepresenter extends Representer {

    public LecternRepresenter() {
        this.multiRepresenters.put(LecternSerializable.class, new RepresentLecternSerializable());
        this.multiRepresenters.put(Enum.class, new RepresentEnum());
    }

    private class RepresentLecternSerializable extends RepresentMap {

        @Override
        public Node representData(Object data) {
            LecternSerializable serializable = (LecternSerializable) data;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put(LecternSerialization.SERIALIZABLE_KEY, LecternSerialization.getKeyValue(serializable.getClass()));
            map.putAll(serializable.serialize());
            return super.representData(map);
        }
    }

    private class RepresentEnum extends RepresentString {

        @Override
        public Node representData(Object data) {
            Enum<?> e = (Enum<?>) data;
            return super.representData(e.name());
        }
    }
}

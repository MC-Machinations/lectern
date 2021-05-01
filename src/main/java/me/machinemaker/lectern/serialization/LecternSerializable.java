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
package me.machinemaker.lectern.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import me.machinemaker.lectern.Lectern;

import java.util.Map;

/**
 * Must be implemented by all classes that
 * wish to be serialized properly in a configuration
 */
public interface LecternSerializable {

    /**
     * Serializes this object to a String, Object map.
     *
     * @return the map
     */
    default Map<String, Object> serialize() {
        return Lectern.OBJECT_MAPPER.convertValue(this, new TypeReference<Map<String, Object>>() {});
    }
}

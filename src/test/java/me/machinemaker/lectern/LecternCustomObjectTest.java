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

import me.machinemaker.lectern.annotations.Deserializer;
import me.machinemaker.lectern.serialization.LecternSerializable;
import me.machinemaker.lectern.serialization.LecternSerialization;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LecternCustomObjectTest {

    @Test
    void testFailRegisterCustomObject() {
        assertThrows(RuntimeException.class, () -> LecternSerialization.registerSerializable(BadCustomObject.class));
    }

    @Test
    void failNoAnnotation() {
        assertThrows(RuntimeException.class, () -> LecternSerialization.registerSerializable(OtherBadCustomObject.class));
    }

    @Test
    void serialization() {
        LecternSerialization.registerSerializable(GoodCustomObject.class);
        GoodCustomObject goodCustomObject = new GoodCustomObject(3, "hello there", List.of(true, false, true));
        Map<String, Object> stringObjectMap = goodCustomObject.serialize();
        stringObjectMap.put(LecternSerialization.SERIALIZABLE_KEY, LecternSerialization.getKeyValue(GoodCustomObject.class));
        GoodCustomObject deserialized = (GoodCustomObject) LecternSerialization.deserialize(stringObjectMap);

        assertEquals(goodCustomObject.value1, deserialized.value1);
        assertEquals(goodCustomObject.value2, deserialized.value2);
        assertEquals(goodCustomObject.booleanList, deserialized.booleanList);
    }

    public static class BadCustomObject implements LecternSerializable {

    }

    public static class OtherBadCustomObject implements LecternSerializable {
        int value1;

        public OtherBadCustomObject(int value1) {
            this.value1 = value1;
        }

        public static OtherBadCustomObject create(Map<String, Object> map) {
            return new OtherBadCustomObject((Integer) map.get("value1"));
        }
    }

    public static class GoodCustomObject implements LecternSerializable {
        int value1;
        String value2;
        List<Boolean> booleanList;

        public GoodCustomObject(int value1, String value2, List<Boolean> booleanList) {
            this.value1 = value1;
            this.value2 = value2;
            this.booleanList = booleanList;
        }

        public int getValue1() {
            return value1;
        }

        public String getValue2() {
            return value2;
        }

        public List<Boolean> getBooleanList() {
            return booleanList;
        }

        @SuppressWarnings("unchecked")
        @Deserializer
        public static GoodCustomObject create(Map<String, Object> map) {
            return new GoodCustomObject((int) map.get("value1"), (String) map.get("value2"), (List<Boolean>) map.get("booleanList"));
        }

        @Override
        public String toString() {
            return "GoodCustomObject{" +
                    "value1=" + value1 +
                    ", value2='" + value2 + '\'' +
                    ", booleanList=" + booleanList +
                    '}';
        }
    }
}

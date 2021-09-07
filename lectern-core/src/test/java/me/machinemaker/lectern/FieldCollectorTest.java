/*
 * GNU General Public License v3
 *
 * lectern, a configuration utility
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

import com.fasterxml.jackson.databind.type.TypeFactory;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.collection.ConfigField;
import me.machinemaker.lectern.collection.FieldCollector;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldCollectorTest {

    @Test
    void testFieldCollection() {
        FieldCollector collector = new FieldCollector(TestConfig.class);
        List<ConfigField> fields = collector.collectFields();
        assertEquals(4, fields.size());
        assertEquals("this-is-a-public-field", fields.get(0).key());
        assertEquals("test description", fields.get(1).description());
        assertEquals("private-field", fields.get(1).key());
        assertEquals(TypeFactory.defaultInstance().constructType(Boolean.TYPE), fields.get(1).type());
        assertEquals(TypeFactory.defaultInstance().constructCollectionType(List.class, String.class), fields.get(2).type());
        assertEquals(TypeFactory.defaultInstance().constructMapType(Map.class, TypeFactory.defaultInstance().constructType(String.class), TypeFactory.defaultInstance().constructCollectionType(List.class, Integer.class)), fields.get(3).type());
    }

    private static class TestConfig extends BaseConfig {

        @Key("this-is-a-public-field")
        public String publicField;

        @Description("test description")
        private boolean privateField;

        protected List<String> protectedField = List.of("testString");

        protected Map<String, List<Integer>> map;
    }
}

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
package me.machinemaker.lectern.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringUtilsTest {

    @Test
    void testCamelCaseToHyphenSnakeCase() {
        assertEquals("hello-there-buddy", StringUtils.camelCaseToHyphenSnakeCase("helloThereBuddy"));
        assertEquals("i'm-over-here-now", StringUtils.camelCaseToHyphenSnakeCase("i'mOverHereNow"));
    }

    @Test
    void testEmptyToNull() {
        assertNull(StringUtils.emptyToNull(""));
        assertNull(StringUtils.emptyToNull("  "));
        assertNotNull(StringUtils.emptyToNull("HEY"));
    }
}

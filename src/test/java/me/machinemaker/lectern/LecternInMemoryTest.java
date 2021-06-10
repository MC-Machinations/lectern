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

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class LecternInMemoryTest {

    @Test
    void testSettingDeepValue() {
        LecternConfig config = Lectern.createConfig(new File("doesn't matter"));
        config.set(false, "section1", "section2");
        assertFalse(() -> config.get("section1", "section2"));
        assertNull(config.get("test1", "test2", "test3"));
        System.out.println(config);
    }
}

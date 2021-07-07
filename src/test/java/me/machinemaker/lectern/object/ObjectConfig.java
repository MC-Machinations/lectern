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
package me.machinemaker.lectern.object;

import me.machinemaker.lectern.LecternBaseConfig;
import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.lectern.annotations.LecternConfiguration;
import me.machinemaker.lectern.annotations.LecternConfigurationSection;

import java.nio.file.Path;
import java.util.List;

@LecternConfiguration
class ObjectConfig extends LecternBaseConfig {



    @Key("test.number.long")
    @Description("This is a test long!")
    public long testLong = 1L;

    @Key("test.number.int")
    public Integer testInt = 301;

    public boolean isTrue = true;

    @Key("list.double")
    public List<Double> doubleList = List.of(1.2, 3.4, 5.6);

    @Key("test.number.double")
    public double testDouble = 4.56;

    @Key("test.number.float")
    public float testFloat = 5.2F;

    @Key("list.string")
    public List<String> nameList = List.of("John", "Adam", "Fred");

    @LecternConfigurationSection(path = "sub-section")
    static class TestObjectSubConfig {

        @Key("subDouble")
        public Double aDouble = 59.8;
    }

    @LecternConfiguration(fileName = "configCopy.yml")
    static class ObjectCopyConfig extends ObjectConfig {}

    @LecternConfiguration(fileName = "modifiedConfig.yml")
    static class ObjectModifiedConfig extends ObjectConfig {}
}

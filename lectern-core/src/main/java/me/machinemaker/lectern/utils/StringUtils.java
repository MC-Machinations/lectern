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
package me.machinemaker.lectern.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utils specific to lectern.
 */
public final class StringUtils {

    static final Pattern LEADING_CAPS = Pattern.compile("(?<=[a-z])[A-Z]");

    private StringUtils() {
    }

    /**
     * Convert camel case text to hyphen-snake case.
     *
     * @param input text to convert
     * @return converted text
     */
    @NotNull
    public static String camelCaseToHyphenSnakeCase(String input) {
        Matcher m = LEADING_CAPS.matcher(input);
        return m.replaceAll(match -> "-" + match.group().toLowerCase(Locale.ROOT));
    }

    /**
     * Convert a blank/empty string to null.
     *
     * @param string string input
     * @return string output or null if blank/empty
     */
    @Nullable
    public static String emptyToNull(@Nullable String string) {
        if (string == null || string.isBlank()) {
            return null;
        }
        return string;
    }
}

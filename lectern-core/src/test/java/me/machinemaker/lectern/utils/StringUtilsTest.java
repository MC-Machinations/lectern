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

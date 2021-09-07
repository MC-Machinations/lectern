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
package me.machinemaker.lectern.exceptions.validations;

import java.lang.reflect.Field;

/**
 * Base exception for validation exceptions.
 */
public abstract class ValidationException extends RuntimeException {

    static final long serialVersionUID = 2L;

    private final transient Object value;
    private final transient Field field;

    protected ValidationException(String message, Object value, Field field) {
        super(message + String.format(" (value: %s)", value));
        this.value = value;
        this.field = field;
    }

    /**
     * Get the value for which validation failed.
     *
     * @return the invalid value
     */
    public Object getValue() {
        return value;
    }
    /**
     * Get the field for which this value was trying to be loaded into.
     *
     * @return the field
     */
    public Field getField() {
        return field;
    }
}

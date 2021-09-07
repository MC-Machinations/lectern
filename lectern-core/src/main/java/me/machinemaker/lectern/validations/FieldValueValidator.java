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
package me.machinemaker.lectern.validations;

import me.machinemaker.lectern.exceptions.validations.ValidationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class FieldValueValidator<T, A extends Annotation> implements ValueValidator<T> {

    @Override
    public final boolean validate(T object) {
        throw new UnsupportedOperationException();
    }

    /**
     * Validates the object according to the configured annotation
     *
     * @param object object to validate
     * @param field field the object will be set in
     * @param annotation annotation
     * @return true for successful validation, false for unsuccessful
     * @throws ValidationException throw custom Exception to control specific failure message
     */
    public abstract boolean validate(T object, Field field, A annotation) throws ValidationException;

    @SuppressWarnings("unchecked")
    public final FieldValueValidatorWrapper<T, A> toWrapper(Field field, Annotation annotation) {
        return new FieldValueValidatorWrapper<>(field, (A) annotation, this);
    }
}

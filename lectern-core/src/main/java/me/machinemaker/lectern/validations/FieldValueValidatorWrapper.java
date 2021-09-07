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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FieldValueValidatorWrapper<T, A extends Annotation> implements ValueValidator<T> {

    private final Field field;
    private final A annotation;
    private final FieldValueValidator<T, A> wrapped;

    @SuppressWarnings("unchecked")
    FieldValueValidatorWrapper(Field field, A annotation, FieldValueValidator<T, A> wrapped) {
        this.field = field;
        this.annotation = annotation;
        this.wrapped = wrapped;
    }

    @Override
    public boolean validate(T object) {
        return this.wrapped.validate(object, this.field, this.annotation);
    }
}

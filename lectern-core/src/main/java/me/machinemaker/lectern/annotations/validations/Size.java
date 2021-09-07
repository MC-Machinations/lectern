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
package me.machinemaker.lectern.annotations.validations;

import me.machinemaker.lectern.exceptions.validations.SizeValidationException;
import me.machinemaker.lectern.exceptions.validations.ValidationException;
import me.machinemaker.lectern.validations.FieldValueValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Validation(Size.Validator.class)
public @interface Size {

    /**
     * the min length of the string or size of collection
     */
    long min() default 0;

    /**
     * the max length of the string or size of collection
     */
    long max() default Long.MAX_VALUE;

    class Validator extends FieldValueValidator<Object, Size> {

        @Override
        public boolean validate(Object object, Field field, Size annotation) throws ValidationException {
            final SizeValidationException exception = new SizeValidationException(String.format("Failed to meet the size constraints, min: %d max: %d", annotation.min(), annotation.max()), object, field);
            if (object instanceof CharSequence str) {
                if (str.length() > annotation.min() && str.length() < annotation.max()) {
                    return true;
                }
            } else if (object instanceof Collection<?> collection) {
                if (collection.size() > annotation.min() && collection.size() < annotation.max()) {
                    return true;
                }
            }
            throw exception;
        }
    }
}

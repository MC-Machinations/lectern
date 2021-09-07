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
package me.machinemaker.lectern.annotations.validations.numbers;

import me.machinemaker.lectern.annotations.validations.Validation;
import me.machinemaker.lectern.exceptions.validations.SizeValidationException;
import me.machinemaker.lectern.exceptions.validations.ValidationException;
import me.machinemaker.lectern.validations.FieldValueValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Validation(Max.Validator.class)
public @interface Max {

    long value();

    class Validator extends FieldValueValidator<Number, Max> {

        @Override
        public boolean validate(Number object, Field field, Max annotation) throws ValidationException {
            if (object.longValue() > annotation.value()) {
                throw new SizeValidationException(object + " is greater than the maximum value " + annotation.value(), object, field);
            }
            return true;
        }
    }
}

package me.machinemaker.lectern.validations;

import java.lang.reflect.Field;

public interface ValueValidator<T> {

    boolean validate(T object, Field field);
}

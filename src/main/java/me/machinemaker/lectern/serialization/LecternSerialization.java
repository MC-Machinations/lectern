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
package me.machinemaker.lectern.serialization;

import me.machinemaker.lectern.annotations.Deserializer;
import me.machinemaker.lectern.exceptions.InvalidDeserializationMethodException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Serialization manager for lectern.
 */
public final class LecternSerialization {

    private LecternSerialization() {
    }

    public static final String SERIALIZABLE_KEY = "==";
    static final Map<String, SerializableMetadata<? extends LecternSerializable>> SERIALIZABLE_MAP = new LinkedHashMap<>();

    /**
     * Gets the key value for a class. Defaults to the clazz name.
     *
     * @param clazz the class
     * @return the key value for that class
     */
    public static String getKeyValue(Class<? extends LecternSerializable> clazz) {
        return clazz.getName();
    }

    /**
     * Register an object as serializable.
     *
     * @param serializableClass the class to register
     * @param <T> the type
     */
    public static <T extends LecternSerializable> void registerSerializable(Class<T> serializableClass) {
        SERIALIZABLE_MAP.put(getKeyValue(serializableClass), new SerializableMetadata<>(getKeyValue(serializableClass),serializableClass));
    }

    /**
     * Deserializes an object.
     *
     * @param map map of object properties
     * @param <T> the type of the deserialized object
     * @throws IllegalArgumentException if the map is invalid
     * @return the object
     */
    @SuppressWarnings("unchecked")
    public static <T extends LecternSerializable> T deserialize(Map<String, Object> map) {
        if (map.containsKey(SERIALIZABLE_KEY)) {
            SerializableMetadata<T> metadata = (SerializableMetadata<T>) SERIALIZABLE_MAP.get((String) map.get(SERIALIZABLE_KEY)); // Keep String cast cause of this "suspicious call" warning
            return metadata.deserialize(map);
        } else {
            throw new IllegalArgumentException(String.format("Map does not contain required serializable key: %s", SERIALIZABLE_KEY));
        }
    }

    private final static class SerializableMetadata<T extends LecternSerializable> {
        final String key;
        private final Constructor<T> constructor;
        private final Method method;

        @SuppressWarnings("unchecked")
        private SerializableMetadata(String key, Class<T> clazz) {
            this.key = key;
            Optional<Method> optionalMethod = Arrays.stream(clazz.getDeclaredMethods()).filter(method1 -> method1.isAnnotationPresent(Deserializer.class)).findAny();
            if (optionalMethod.isPresent()) {
                this.method = optionalMethod.get();
                this.constructor = null;
                if (method.getParameterCount() != 1 || !Map.class.isAssignableFrom(method.getParameterTypes()[0]) || !Modifier.isStatic(method.getModifiers()) || !LecternSerializable.class.isAssignableFrom(method.getReturnType())) {
                    throw new InvalidDeserializationMethodException(method, method.getParameterTypes()[0], String.format("Reminder: method must be static and return an object that extends %s!", LecternSerializable.class.getSimpleName()));
                }
            } else {
                Optional<Constructor<T>> optionalConstructor = Arrays.stream(clazz.getDeclaredConstructors()).filter(constructor1 -> constructor1.isAnnotationPresent(Deserializer.class)).map(constructor1 -> (Constructor<T>) constructor1).findAny();
                if (optionalConstructor.isPresent()) {
                    this.constructor = optionalConstructor.get();
                    this.method = null;
                    if (constructor.getParameterCount() != 1 || !Map.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
                        throw new InvalidDeserializationMethodException(constructor, constructor.getParameterTypes()[0]);
                    }
                } else {
                    throw new RuntimeException(String.format("Could not find a valid deserialization method for %s", clazz.getName()));
                }
            }
        }

        @SuppressWarnings("unchecked")
        private T deserialize(Map<String, Object> map) {
            try {
                if (constructor != null) {
                    return constructor.newInstance(map);
                } else {
                    return (T) method.invoke(null, map);
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(String.format("Could not deserialize %s", map), e);
            }
        }
    }
}

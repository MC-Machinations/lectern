package me.machinemaker.lectern;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GenericTypesTest {

    @Test
    void test() throws NoSuchMethodException {
        Map<String, Integer> map = new HashMap<>();
        System.out.println(Arrays.toString(map.getClass().getTypeParameters()));

        TypeToken<List<Map<String, Integer>>> type = new TypeToken<List<Map<String, Integer>>>() {};

        System.out.println(GenericTypeReflector.erase(type.getType()).isInstance(map));
        // System.out.println(TypeToken.get(map.getClass()).getType());
    }

}

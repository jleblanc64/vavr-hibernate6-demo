package com.demo.lib_override;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class FieldMocked {
    @SneakyThrows
    static Field getSimple(Field f, Type typeArg) {
        var mock = mock(Field.class);

        doReturn(typeArg).when(mock).getGenericType();

        mockSimple(mock, f, typeArg);
        return mock;
    }

    private static void mockSimple(Field mock, Field f, Type typeArg) {
        doReturn(typeArg).when(mock).getType();
        doReturn(f.getName()).when(mock).getName();
        doReturn(f.getDeclaringClass()).when(mock).getDeclaringClass();
    }

    @SneakyThrows
    public static <T> T getRefl(Object o, Field f) {
        f.setAccessible(true);
        return (T) f.get(o);
    }

    @SneakyThrows
    public static void setRefl(Object o, Field f, Object value) {
        f.setAccessible(true);
        f.set(o, value);
    }
}

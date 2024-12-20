package com.demo.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.mock;

public class MethodCustomType {

    public static Method create(Method mOrig, Type type) {
        return mock(Method.class, invocation -> {
            var args = invocation.getRawArguments();
            var m = invocation.getMethod();
            m.setAccessible(true);
            var name = m.getName();

            var result = m.invoke(mOrig, args);
            if (name.equals("getGenericReturnType"))
                return type;
            if (name.equals("getReturnType"))
                return List.class;

            return result;
        });
    }
}

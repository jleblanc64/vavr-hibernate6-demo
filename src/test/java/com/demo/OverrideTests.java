package com.demo;

import com.demo.lib_override.LibCustom;
import org.junit.jupiter.api.Test;

import static com.demo.functional.Functor.catchEx;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OverrideTests {
    @Test
    void test() {
        var ex = catchEx(() -> LibCustom.overrideWithSelf(A.class, "f", x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);

        assertEquals(0, A.f());
        LibCustom.override(A.class, "f", x -> 1);
        LibCustom.load();
        assertEquals(1, A.f());

        LibCustom.reset();
        assertEquals(0, A.f());
        LibCustom.override(A.class, "f", x -> 3);
        LibCustom.load();
        assertEquals(3, A.f());
    }

    static class A {
        static int f() {
            return 0;
        }
    }
}

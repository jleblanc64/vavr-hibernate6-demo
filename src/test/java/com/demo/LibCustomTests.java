package com.demo;

import com.demo.lib_override.LibCustom;
import org.junit.jupiter.api.Test;

import static com.demo.functional.Functor.catchEx;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomTests {
    @Test
    void test() {
        var ex = catchEx(() -> LibCustom.overrideWithSelf(A.class, "f", x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);

        ex = catchEx(() -> LibCustom.modifyArgWithSelf(A.class, "f", 0, x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);

        assertEquals(0, A.f());
        assertEquals(3, B.g(3));
        LibCustom.override(A.class, "f", x -> 1);
        LibCustom.modifyArg(B.class, "g", 0, args -> {
            var i = (int) args[0];
            return i + 1;
        });
        LibCustom.load();
        assertEquals(1, A.f());
        assertEquals(4, B.g(3));

        LibCustom.reset();
        assertEquals(0, A.f());
        assertEquals(3, B.g(3));

        LibCustom.reset();
        LibCustom.override(A.class, "f", x -> -1);
        LibCustom.modifyArg(B.class, "g", 0, args -> {
            var i = (int) args[0];
            return i + 2;
        });
        LibCustom.load();
        assertEquals(-1, A.f());
        assertEquals(6, B.g(4));
    }

    static class A {
        static int f() {
            return 0;
        }
    }

    static class B {
        static int g(int i) {
            return i;
        }
    }
}

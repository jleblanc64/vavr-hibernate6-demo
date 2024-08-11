package com.demo;

import com.demo.lib_override.LibCustom;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibCustomTests {
    @Test
    void test() {
        assertEquals(0, A.f());
        assertEquals(8, A.g());
        assertEquals(3, B.g(3));
        assertEquals(4, C.f());
        LibCustom.override(A.class, "f", args -> 1);
        LibCustom.modifyArg(B.class, "g", 0, args -> {
            var i = (int) args[0];
            return i + 1;
        });
        LibCustom.override(C.class, "f", args -> 5);
        LibCustom.load();

        assertEquals(1, A.f());
        assertEquals(8, A.g());
        assertEquals(4, B.g(3));
        assertEquals(5, C.f());

        //
        LibCustom.reset();
        assertEquals(0, A.f());
        assertEquals(3, B.g(3));

        //
        LibCustom.reset();
        LibCustom.override(A.class, "f", args -> -1);
        LibCustom.override(A.class, "g", args -> -2);
        LibCustom.modifyArg(B.class, "g", 0, args -> {
            var i = (int) args[0];
            return i + 2;
        });
        LibCustom.load();

        assertEquals(-1, A.f());
        assertEquals(-2, A.g());
        assertEquals(6, B.g(4));

        //
        LibCustom.reset();
        assertEquals(8, A.g());
        LibCustom.modifyReturn(A.class, "g", argsReturned -> {
            var returned = (int) argsReturned.returned;
            return 2 * returned;
        });
        LibCustom.load();

        assertEquals(16, A.g());
    }

    static class A {
        static int f() {
            return 0;
        }

        static int g() {
            return 8;
        }
    }

    static class B {
        static int g(int i) {
            return i;
        }
    }

    static class C {
        static int f() {
            return 4;
        }
    }
}

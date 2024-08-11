package com.demo;

import com.demo.lib_override.OverrideLibs;
import org.junit.jupiter.api.Test;

import static com.demo.functional.Functor.catchEx;
import static com.demo.lib_override.OverrideLibs.override;
import static com.demo.lib_override.OverrideLibs.overrideWithSelf;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OverrideTests {
    @Test
    void test() {
        var ex = catchEx(() -> overrideWithSelf(A.class, "f", x -> null));
        assertEquals("WithSelf doesn't work for static methods", ex);

        assertEquals(0, A.f());
        override(A.class, "f", x -> 1);
        OverrideLibs.override();
        assertEquals(1, A.f());
    }

    static class A {
        static int f() {
            return 0;
        }
    }
}

package com.demo.custom;

import io.github.jleblanc64.libcustom.LibCustom;
import io.vavr.control.Option;
import org.hibernate.type.descriptor.jdbc.BasicBinder;

public class HibernateOption {
    public static Class<?> class_ = Option.class;

    public static void override() {
        LibCustom.modifyArg(BasicBinder.class, "bind", 1, args -> {
            var value = args[1];

            if (value instanceof Option) {
                var opt = (Option) value;
                return opt.getOrElse(() -> null);
            }

            return value;
        });
    }
}

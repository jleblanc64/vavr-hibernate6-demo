package com.demo.lib_override.sub;

import com.demo.functional.ListF;
import com.demo.functional.OptionF;
import com.demo.lib_override.LibCustom;
import com.demo.lib_override.ValueWrapper;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.property.access.spi.SetterFieldImpl;
import org.hibernate.type.descriptor.java.spi.UnknownBasicJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.lang.reflect.Field;

import static com.demo.functional.ListF.f;
import static com.demo.functional.OptionF.o;
import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.LibCustom.modifyArgWithSelf;
import static com.demo.lib_override.LibCustom.overrideWithSelf;

public class Hibernate {
    public static void override() {
        LibCustom.override(UnknownBasicJavaType.class, "getRecommendedJdbcType", args -> {
            var ind = args[0];
            if (!(ind instanceof BasicValue))
                return null;

            var b = (BasicValue) ind;

            var s = b.getColumn();
            if (!(s instanceof Column))
                return null;

            var c = (Column) s;

            if ("customers".equals(b.getTable().getName()) && "name".equals(c.getName()))
                return new VarcharJdbcType();

            return null;
        });

        overrideWithSelf(UnknownBasicJavaType.class, "unwrap", argsSelf -> {
            var args = argsSelf.args;
            var v = args[0];
            var type = (Class<?>) args[1];

            if (type == String.class && v instanceof OptionF) {

                var o = (OptionF<?>) v;
                if (o.isPresent())
                    return o.get();

                return new ValueWrapper(null);
            }

            if (type == OptionF.class && !(v instanceof OptionF))
                return o(v);

            return v;
        });

        overrideWithSelf(UnknownBasicJavaType.class, "wrap", argsSelf -> {
            var args = argsSelf.args;
            var u = (UnknownBasicJavaType) argsSelf.self;
            var v = args[0];
            var type = u.getJavaTypeClass();

            if (type == String.class && v instanceof OptionF) {
                var o = (OptionF<?>) v;
                if (o.isPresent())
                    return o.get();

                return new ValueWrapper(null);
            }

            if (type == OptionF.class && !(v instanceof OptionF))
                return o(v);

            return v;
        });
    }

    public static void overrideListF() {
        modifyArgWithSelf(SetterFieldImpl.class, "set", 1, argsSelf -> {
            var args = argsSelf.args;
            var self = argsSelf.self;
            var field = (Field) getRefl(self, SetterFieldImpl.class.getDeclaredField("field"));

            if (field.getType() == ListF.class) {
                var bag = (PersistentBag) args[1];
                return f(bag);
            }

            return null;
        });
    }
}

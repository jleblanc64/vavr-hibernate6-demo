package com.demo.lib_override.sub;

import com.demo.functional.IListF;
import com.demo.lib_override.ValueWrapper;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.property.access.spi.SetterFieldImpl;
import org.hibernate.type.descriptor.java.spi.UnknownBasicJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.lang.reflect.Field;
import java.util.Optional;

import static com.demo.functional.ListF.f;
import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.OverrideLibs.*;

public class Hibernate {
    public static void override() {
        m(UnknownBasicJavaType.class, "getRecommendedJdbcType", args -> {
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

        mSelf(UnknownBasicJavaType.class, "unwrap", argsSelf -> {
            var args = argsSelf.args;
            var v = args[0];
            var type = (Class<?>) args[1];

            if (type.equals(String.class) && v instanceof Optional) {

                var o = (Optional<?>) v;
                if (o.isPresent())
                    return o.get();

                return new ValueWrapper(null);
            }

            if (type.equals(Optional.class) && !(v instanceof Optional))
                return Optional.of(v);

            return v;
        });

        mSelf(UnknownBasicJavaType.class, "wrap", argsSelf -> {
            var args = argsSelf.args;
            var u = (UnknownBasicJavaType) argsSelf.self;
            var v = args[0];
            var type = u.getJavaTypeClass();

            if (type.equals(String.class) && v instanceof Optional) {
                var o = (Optional<?>) v;
                if (o.isPresent())
                    return o.get();

                return new ValueWrapper(null);
            }

            if (type.equals(Optional.class) && !(v instanceof Optional))
                return Optional.of(v);

            return v;
        });
    }

    public static void overrideIListF() {
        mArgsModSelf(SetterFieldImpl.class, "set", 1, argsSelf -> {
            var args = argsSelf.args;
            var self = argsSelf.self;
            var field = (Field) getRefl(self, SetterFieldImpl.class.getDeclaredField("field"));

            if (field.getType().equals(IListF.class)) {
                var bag = (PersistentBag) args[1];
                return f(bag);
            }

            return null;
        });
    }
}

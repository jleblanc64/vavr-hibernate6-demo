package com.demo.lib_override.sub;

import com.demo.lib_override.ValueWrapper;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.type.descriptor.java.spi.UnknownBasicJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.util.Optional;

import static com.demo.lib_override.OverrideLibs.m;
import static com.demo.lib_override.OverrideLibs.mSelf;

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
}

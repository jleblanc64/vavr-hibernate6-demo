package com.demo.lib_override.sub;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.ValueWrapper;
import io.github.jleblanc64.libcustom.functional.OptionF;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.type.descriptor.java.spi.UnknownBasicJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.reflections.Reflections;

import static io.github.jleblanc64.libcustom.LibCustom.overrideWithSelf;
import static io.github.jleblanc64.libcustom.functional.ListF.f;
import static io.github.jleblanc64.libcustom.functional.OptionF.o;

public class HibernateOption {
    public static void override() {
        var rootPackage = "com.demo";
        var optionClass = OptionF.class;

        var tableToEntity = f(new Reflections(rootPackage).getTypesAnnotatedWith(Entity.class))
                .toMap(x -> x.getAnnotation(Table.class).name(), x -> x);

        LibCustom.override(UnknownBasicJavaType.class, "getRecommendedJdbcType", args -> {
            var ind = args[0];
            if (!(ind instanceof BasicValue))
                return null;

            var b = (BasicValue) ind;

            var s = b.getColumn();
            if (!(s instanceof Column))
                return null;

            var c = (Column) s;

            var entity = tableToEntity.get(b.getTable().getName());
            var fields = f(entity.getDeclaredFields());
            var field = fields.findSafe(x -> x.getName().equals(c.getName()));
            if (field.getType() == optionClass)
                return new VarcharJdbcType();

            return null;
        });

        overrideWithSelf(UnknownBasicJavaType.class, "unwrap", argsSelf -> {
            var args = argsSelf.args;
            var v = args[0];
            var type = (Class<?>) args[1];

            OptionF<?> o;
            if (type == String.class && instanceOf(v, optionClass)) {

                o = (OptionF<?>) v;
                if (o.isPresent())
                    return o.get();

                return new ValueWrapper(null);
            }

            if (type == optionClass && !instanceOf(v, optionClass))
                return o(v);

            return v;
        });

        overrideWithSelf(UnknownBasicJavaType.class, "wrap", argsSelf -> {
            var args = argsSelf.args;
            var u = (UnknownBasicJavaType) argsSelf.self;
            var v = args[0];
            var type = u.getJavaTypeClass();

            if (type == String.class && instanceOf(v, optionClass)) {
                var o = (OptionF<?>) v;
                if (o.isPresent())
                    return o.get();

                return new ValueWrapper(null);
            }

            if (type == optionClass && !instanceOf(v, optionClass))
                return o(v);

            return v;
        });
    }

    static boolean instanceOf(Object o, Class<?> c) {
        return o != null && o.getClass() == c;
    }
}

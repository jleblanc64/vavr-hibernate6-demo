package com.demo.lib_override.sub;

import com.demo.functional.OptionF;
import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.ValueWrapper;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.type.descriptor.java.spi.UnknownBasicJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.reflections.Reflections;

import static com.demo.functional.ListF.f;
import static com.demo.functional.OptionF.o;
import static io.github.jleblanc64.libcustom.LibCustom.overrideWithSelf;

public class HibernateOption {
    public static void override() {
        var optionClass = OptionF.class;
        var tableToEntity = f(new Reflections("com.demo").getTypesAnnotatedWith(Entity.class))
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

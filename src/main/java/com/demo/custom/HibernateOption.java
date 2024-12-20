package com.demo.custom;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.functional.OptionF;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.SneakyThrows;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Column;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.java.spi.UnknownBasicJavaType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static io.github.jleblanc64.libcustom.functional.ListF.f;
import static io.github.jleblanc64.libcustom.functional.OptionF.o;

public class HibernateOption {
    public static void override() {
        // replace with your own values
        var rootPackage = "com.demo";
        var optionClass = OptionF.class;

        var tableToEntity = f(new Reflections(rootPackage).getTypesAnnotatedWith(Entity.class))
                .toMap(x -> x.getAnnotation(Table.class).name(), x -> x);

        LibCustom.override(UnknownBasicJavaType.class, "getRecommendedJdbcType", args -> {
            var context = args[0];
            if (!(context instanceof BasicValue))
                return LibCustom.ORIGINAL;

            var b = (BasicValue) context;

            var s = b.getColumn();
            if (!(s instanceof Column))
                return LibCustom.ORIGINAL;

            var c = (Column) s;

            var entity = tableToEntity.get(b.getTable().getName());
            var fields = f(entity.getDeclaredFields());
            var field = fields.findSafe(x -> x.getName().equals(c.getName()));
            if (field.getType() != optionClass)
                return LibCustom.ORIGINAL;

            var typeParam = typeParam(field);
            if (typeParam == String.class)
                return new VarcharJdbcType();
            else if (typeParam == Integer.class)
                return new IntegerJdbcType();

            return LibCustom.ORIGINAL;
        });

        LibCustom.override(UnknownBasicJavaType.class, "unwrap", args -> {
            var v = args[0];
            var type = (Class<?>) args[1];

            OptionF<?> o;
            if (instanceOf(v, optionClass)) {

                o = (OptionF<?>) v;
                if (o.isPresent())
                    return o.get();

                return null;
            }

            if (type == optionClass && !instanceOf(v, optionClass))
                // return Option from nullable value v
                return o(v);

            return v;
        });

        LibCustom.overrideWithSelf(UnknownBasicJavaType.class, "wrap", argsSelf -> {
            var args = argsSelf.args;
            var u = (UnknownBasicJavaType) argsSelf.self;
            var v = args[0];
            var type = u.getJavaTypeClass();

            if (instanceOf(v, optionClass)) {
                var o = (OptionF<?>) v;
                if (o.isPresent())
                    return o.get();

                return null;
            }

            if (type == optionClass && !instanceOf(v, optionClass))
                // return Option from nullable value v
                return o(v);

            return v;
        });

        // https://github.com/hibernate/hibernate-orm/blob/4fc56653a6a6de631012e9ae43f8e6a8c52ea2d7/hibernate-core/src/main/java/org/hibernate/type/descriptor/jdbc/IntegerJdbcType.java#L65-L78
        LibCustom.overrideWithSelf(IntegerJdbcType.class, "getBinder", argSelf -> {
            var arg = (JavaType) argSelf.args[0];
            if (arg.getJavaTypeClass() != OptionF.class)
                return LibCustom.ORIGINAL;

            var javaType = (JavaType<OptionF<Integer>>) arg;
            var self = (IntegerJdbcType) argSelf.self;

            return new BasicBinder<>(javaType, self) {
                @Override
                protected void doBind(PreparedStatement st, OptionF<Integer> value, int index, WrapperOptions options) throws SQLException {
                    if (value == null || !value.isPresent())
                        st.setObject(index, null);
                    else
                        st.setInt(index, javaType.unwrap(value, Integer.class, options));
                }

                @Override
                protected void doBind(CallableStatement st, OptionF<Integer> value, String name, WrapperOptions options)
                        throws SQLException {
                    if (value == null || !value.isPresent())
                        st.setObject(name, null);
                    else
                        st.setInt(name, javaType.unwrap(value, Integer.class, options));
                }
            };
        });
    }

    static boolean instanceOf(Object o, Class<?> c) {
        return o != null && o.getClass() == c;
    }

    @SneakyThrows
    static Class<?> typeParam(Field field) {
        var name = field.getGenericType().getTypeName();
        return typeParam(name);
    }

    public static Class<?> typeParam(String name) throws ClassNotFoundException {
        var pattern = "(?<=\\<).*(?=\\>)";
        var subType = regex0(name, pattern);
        return Class.forName(subType);
    }

    static String regex0(String s, String pattern) {
        var matcher = Pattern.compile(pattern).matcher(s);
        matcher.find();
        return matcher.group(0);
    }
}

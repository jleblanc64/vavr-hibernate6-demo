package com.demo.custom;

import com.demo.utils.FieldCustomType;
import com.demo.utils.MethodCustomType;
import com.demo.utils.TypeImpl;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.property.access.spi.SetterFieldImpl;
import org.hibernate.type.BagType;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import static io.github.jleblanc64.libcustom.FieldMocked.getRefl;

public class HibernateList {
    @SneakyThrows
    public static void override() {
        LibCustom.modifyArgWithSelf(SetterFieldImpl.class, "set", 1, argsSelf -> {
            var args = argsSelf.args;
            var self = argsSelf.self;
            var field = (Field) getRefl(self, SetterFieldImpl.class.getDeclaredField("field"));

            if (field.getType() != io.vavr.collection.List.class)
                return LibCustom.ORIGINAL;

            var bag = (PersistentBag) args[1];
            return io.vavr.collection.List.ofAll(bag);
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.annotations.common.reflection.java.JavaXProperty"), "create", 0, args -> {
            var member = args[0];
            if (member instanceof java.lang.reflect.Method) {
                var method = (java.lang.reflect.Method) member;
                var type = method.getGenericReturnType().toString();
                if (!type.contains("vavr"))
                    return member;

                var typeParam = HibernateOption.typeParam(type);
                return MethodCustomType.create(method, new TypeImpl(List.class, new Type[]{typeParam}, null));
            } else if (member instanceof java.lang.reflect.Field) {
                var field = (java.lang.reflect.Field) member;
                var type = field.getGenericType().toString();
                if (!type.contains("vavr"))
                    return member;

                var typeParam = HibernateOption.typeParam(type);
                return FieldCustomType.create(field, new TypeImpl(List.class, new Type[]{typeParam}, null));
            }

            return member;
        });

        LibCustom.modifyReturn(Class.forName("org.hibernate.metamodel.internal.BaseAttributeMetadata"), "getJavaType", argRet -> {
            var clazz = argRet.returned;
            if (clazz.toString().contains("vavr"))
                return List.class;

            return clazz;
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.type.CollectionType"), "getElementsIterator", 0, args -> {
            var collection = args[0];
            if (collection instanceof io.vavr.collection.List)
                return ((io.vavr.collection.List) collection).toJavaList();

            return collection;
        });

        LibCustom.modifyArg(BagType.class, "wrap", 1, args -> {
            var collection = args[1];
            if (collection instanceof io.vavr.collection.List)
                return ((io.vavr.collection.List) collection).toJavaList();

            return collection;
        });
    }
}

package com.demo.custom;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.functional.ListF;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.property.access.spi.SetterFieldImpl;

import java.lang.reflect.Field;

import static io.github.jleblanc64.libcustom.FieldMocked.getRefl;
import static io.github.jleblanc64.libcustom.LibCustom.modifyArgWithSelf;
import static io.github.jleblanc64.libcustom.functional.ListF.f;

public class HibernateList {
    public static void override() {
        modifyArgWithSelf(SetterFieldImpl.class, "set", 1, argsSelf -> {
            var args = argsSelf.args;
            var self = argsSelf.self;
            var field = (Field) getRefl(self, SetterFieldImpl.class.getDeclaredField("field"));

            if (field.getType() != ListF.class)
                return LibCustom.ORIGINAL;

            var bag = (PersistentBag) args[1];
            return f(bag);
        });
    }
}

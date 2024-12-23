package com.demo.serializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import io.vavr.control.Option;

import java.io.Serializable;
import java.lang.reflect.Type;

// https://github.com/FasterXML/jackson-modules-java8/blob/2.13/datatypes/src/main/java/com/fasterxml/jackson/datatype/jdk8/Jdk8TypeModifier.java
public class VavrOptionTypeModifier extends TypeModifier implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings, TypeFactory typeFactory) {
        if (type.getRawClass() == Option.class)
            return ReferenceType.upgradeFrom(type, type.containedTypeOrUnknown(0));

        return type;
    }
}

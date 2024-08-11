package com.demo.lib_override.ser;

import com.demo.functional.ListF;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import java.util.Collection;

import static com.demo.functional.ListF.f;

public class IListFConverter implements Converter<Collection, ListF> {
    @Override
    public ListF convert(Collection value) {
        return f(value);
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionLikeType(Collection.class, Object.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionLikeType(ListF.class, Object.class);
    }
}

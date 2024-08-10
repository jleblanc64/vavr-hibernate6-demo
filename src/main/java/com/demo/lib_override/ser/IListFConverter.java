package com.demo.lib_override.ser;

import com.demo.functional.IListF;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import java.util.Collection;

import static com.demo.functional.ListF.f;

public class IListFConverter implements Converter<Collection, IListF> {
    @Override
    public IListF convert(Collection value) {
        return f(value);
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionLikeType(Collection.class, Object.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionLikeType(IListF.class, Object.class);
    }
}

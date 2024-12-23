package com.demo.serializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import io.vavr.collection.List;

import java.util.Collection;

public class VavrListConverter implements Converter<Collection<?>, List<?>> {
    @Override
    public List<?> convert(Collection value) {
        return List.ofAll(value);
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionLikeType(Collection.class, Object.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionLikeType(List.class, Object.class);
    }
}

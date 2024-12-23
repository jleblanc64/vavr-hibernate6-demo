package com.demo.serializer;

import com.fasterxml.jackson.databind.util.StdConverter;
import io.vavr.collection.List;

import java.util.Collection;

public class VavrListConverterBack extends StdConverter<List<?>, Collection<?>> {
    @Override
    public Collection<?> convert(List value) {
        return value.toJavaList();
    }
}

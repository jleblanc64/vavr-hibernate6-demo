package com.demo.serializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;

public class VavrListSerializer extends StdDelegatingSerializer {
    public VavrListSerializer() {
        super(new VavrListConverter.ToCollec());
    }

    @Override
    protected StdDelegatingSerializer withDelegate(Converter<Object, ?> c, JavaType t, JsonSerializer<?> deser) {
        return new StdDelegatingSerializer(c, t, deser);
    }
}

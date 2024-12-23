package com.demo.serializer;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;

public class VavrListSerializer extends StdDelegatingSerializer {
    SerializerProvider ser;

    public VavrListSerializer(SerializerProvider ser) {
        super(new VavrListConverterBack());
        this.ser = ser;
    }

    @Override
    protected StdDelegatingSerializer withDelegate(Converter<Object, ?> c, JavaType t, JsonSerializer<?> deser) {
        return new StdDelegatingSerializer(c, t, deser);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        return super.createContextual(ser, property);
    }
}

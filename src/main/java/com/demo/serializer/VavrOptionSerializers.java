package com.demo.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;
import io.vavr.control.Option;

import java.io.Serializable;

public class VavrOptionSerializers extends Serializers.Base implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public JsonSerializer<?> findReferenceSerializer(SerializationConfig config,
                                                     ReferenceType refType, BeanDescription beanDesc,
                                                     TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer) {

        if (!Option.class.isAssignableFrom(refType.getRawClass()))
            return null;

        var staticTyping = contentTypeSerializer == null && config.isEnabled(MapperFeature.USE_STATIC_TYPING);
        return new VavrOptionSerializer(refType, staticTyping, contentTypeSerializer, contentValueSerializer);
    }
}

package com.demo.lib_override.ser;

import com.demo.functional.IOptionF;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.DoubleStreamSerializer;
import com.fasterxml.jackson.datatype.jdk8.IntStreamSerializer;
import com.fasterxml.jackson.datatype.jdk8.LongStreamSerializer;
import com.fasterxml.jackson.datatype.jdk8.StreamSerializer;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class IOptionFSerializers extends Serializers.Base
        implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public JsonSerializer<?> findReferenceSerializer(SerializationConfig config,
                                                     ReferenceType refType, BeanDescription beanDesc,
                                                     TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer) {
        final Class<?> raw = refType.getRawClass();
        if (IOptionF.class.isAssignableFrom(raw)) {
            boolean staticTyping = (contentTypeSerializer == null)
                    && config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            return new IOptionFSerializer(refType, staticTyping,
                    contentTypeSerializer, contentValueSerializer);
        }
        return null;
    }

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type,
                                            BeanDescription beanDesc) {
        Class<?> raw = type.getRawClass();
        if (LongStream.class.isAssignableFrom(raw)) {
            return LongStreamSerializer.INSTANCE;
        }
        if (IntStream.class.isAssignableFrom(raw)) {
            return IntStreamSerializer.INSTANCE;
        }
        if (DoubleStream.class.isAssignableFrom(raw)) {
            return DoubleStreamSerializer.INSTANCE;
        }
        if (Stream.class.isAssignableFrom(raw)) {
            JavaType[] params = config.getTypeFactory().findTypeParameters(type, Stream.class);
            JavaType vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
            return new StreamSerializer(config.getTypeFactory().constructParametricType(Stream.class, vt), vt);
        }
        return null;
    }
}

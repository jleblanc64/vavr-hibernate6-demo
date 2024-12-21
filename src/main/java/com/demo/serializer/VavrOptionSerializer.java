package com.demo.serializer;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import io.vavr.control.Option;

public class VavrOptionSerializer
        extends ReferenceTypeSerializer<Option<?>> // since 2.9
{
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Constructors, factory methods
    /**********************************************************
     */

    protected VavrOptionSerializer(ReferenceType fullType, boolean staticTyping,
                                   TypeSerializer vts, JsonSerializer<Object> ser) {
        super(fullType, staticTyping, vts, ser);
    }

    protected VavrOptionSerializer(VavrOptionSerializer base, BeanProperty property,
                                   TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper,
                                   Object suppressableValue, boolean suppressNulls) {
        super(base, property, vts, valueSer, unwrapper,
                suppressableValue, suppressNulls);
    }

    @Override
    protected ReferenceTypeSerializer<Option<?>> withResolved(BeanProperty prop,
                                                              TypeSerializer vts, JsonSerializer<?> valueSer,
                                                              NameTransformer unwrapper) {
        return new VavrOptionSerializer(this, prop, vts, valueSer, unwrapper,
                _suppressableValue, _suppressNulls);
    }

    @Override
    public ReferenceTypeSerializer<Option<?>> withContentInclusion(Object suppressableValue,
                                                                   boolean suppressNulls) {
        return new VavrOptionSerializer(this, _property, _valueTypeSerializer,
                _valueSerializer, _unwrapper,
                suppressableValue, suppressNulls);
    }

    /*
    /**********************************************************
    /* Abstract method impls
    /**********************************************************
     */

    @Override
    protected boolean _isValuePresent(Option<?> value) {
        return value.isDefined();
    }

    @Override
    protected Object _getReferenced(Option<?> value) {
        return value.getOrElse(() -> null);
    }

    @Override
    protected Object _getReferencedIfPresent(Option<?> value) {
        return value.getOrElse(() -> null);
    }
}

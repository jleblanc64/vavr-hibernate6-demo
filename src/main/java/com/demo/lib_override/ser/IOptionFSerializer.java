package com.demo.lib_override.ser;

import com.demo.functional.IOptionF;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class IOptionFSerializer
        extends ReferenceTypeSerializer<IOptionF<?>> // since 2.9
{
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Constructors, factory methods
    /**********************************************************
     */

    protected IOptionFSerializer(ReferenceType fullType, boolean staticTyping,
                                 TypeSerializer vts, JsonSerializer<Object> ser) {
        super(fullType, staticTyping, vts, ser);
    }

    protected IOptionFSerializer(IOptionFSerializer base, BeanProperty property,
                                 TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper,
                                 Object suppressableValue, boolean suppressNulls) {
        super(base, property, vts, valueSer, unwrapper,
                suppressableValue, suppressNulls);
    }

    @Override
    protected ReferenceTypeSerializer<IOptionF<?>> withResolved(BeanProperty prop,
                                                                TypeSerializer vts, JsonSerializer<?> valueSer,
                                                                NameTransformer unwrapper) {
        return new IOptionFSerializer(this, prop, vts, valueSer, unwrapper,
                _suppressableValue, _suppressNulls);
    }

    @Override
    public ReferenceTypeSerializer<IOptionF<?>> withContentInclusion(Object suppressableValue,
                                                                     boolean suppressNulls) {
        return new IOptionFSerializer(this, _property, _valueTypeSerializer,
                _valueSerializer, _unwrapper,
                suppressableValue, suppressNulls);
    }

    /*
    /**********************************************************
    /* Abstract method impls
    /**********************************************************
     */

    @Override
    protected boolean _isValuePresent(IOptionF<?> value) {
        return value.get() != null;
    }

    @Override
    protected Object _getReferenced(IOptionF<?> value) {
        return value.get();
    }

    @Override
    protected Object _getReferencedIfPresent(IOptionF<?> value) {
        return value.get();
    }
}

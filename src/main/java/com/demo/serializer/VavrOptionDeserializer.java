package com.demo.serializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import io.vavr.control.Option;

final class VavrOptionDeserializer
        extends ReferenceTypeDeserializer<Option<?>> {
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    /**
     * @since 2.9
     */
    public VavrOptionDeserializer(JavaType fullType, ValueInstantiator inst,
                                  TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(fullType, inst, typeDeser, deser);
    }

    /*
    /**********************************************************
    /* Abstract method implementations
    /**********************************************************
     */

    @Override
    public VavrOptionDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new VavrOptionDeserializer(_fullType, _valueInstantiator,
                typeDeser, valueDeser);
    }

    @Override
    public Option<?> getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        return Option.of(_valueDeserializer.getNullValue(ctxt));
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return getNullValue(ctxt);
    }

    @Override
    public Option<?> referenceValue(Object contents) {
        return Option.of(contents);
    }

    @Override
    public Object getReferenced(Option<?> reference) {
        // 23-Apr-2021, tatu: [modules-java8#214] Need to support empty
        //    for merging too
        return reference.getOrElse(() -> null);
    }

    @Override // since 2.9
    public Option<?> updateReference(Option<?> reference, Object contents) {
        return Option.of(contents);
    }
}
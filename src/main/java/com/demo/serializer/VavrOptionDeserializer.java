package com.demo.serializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import io.vavr.control.Option;

class VavrOptionDeserializer extends ReferenceTypeDeserializer<Option<?>> {
    private static final long serialVersionUID = 1L;

    public VavrOptionDeserializer(JavaType fullType, ValueInstantiator inst, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(fullType, inst, typeDeser, deser);
    }

    @Override
    public VavrOptionDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new VavrOptionDeserializer(_fullType, _valueInstantiator, typeDeser, valueDeser);
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
        return reference.getOrNull();
    }

    @Override
    public Option<?> updateReference(Option<?> reference, Object contents) {
        return Option.of(contents);
    }
}
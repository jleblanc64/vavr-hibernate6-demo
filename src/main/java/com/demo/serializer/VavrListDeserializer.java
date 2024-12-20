package com.demo.serializer;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.util.Converter;
import io.vavr.collection.List;

import java.util.Collection;

public class VavrListDeserializer extends StdDelegatingDeserializer<List> {
    public VavrListDeserializer() {
        super(new VavrListConverter());
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        // Slightly modified version of the original implementation
        // First: if already got deserializer to delegate to, contextualize it:
        if (_delegateDeserializer != null) {
            JsonDeserializer<?> deser = ctxt.handleSecondaryContextualization(_delegateDeserializer,
                    property, _delegateType);
            if (deser != _delegateDeserializer) {
                return withDelegate(_converter, _delegateType, deser);
            }
            return this;
        }
        // Otherwise: figure out what is the fully generic delegate type, then find deserializer
        JavaType delegateType = ctxt.getTypeFactory().constructCollectionLikeType(Collection.class,
                property.getType().getBindings().getBoundType(0));
        return withDelegate(_converter, delegateType,
                ctxt.findContextualValueDeserializer(delegateType, property));
    }

    @Override
    protected StdDelegatingDeserializer<List> withDelegate(Converter<Object, List> converter, JavaType delegateType,
                                                            JsonDeserializer<?> delegateDeserializer) {
        return new StdDelegatingDeserializer<>(converter, delegateType, delegateDeserializer);
    }
}

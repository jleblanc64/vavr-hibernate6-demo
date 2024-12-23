package com.demo.serializer;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.util.Converter;
import io.vavr.collection.List;

import java.util.Collection;

public class VavrListDeserializer extends StdDelegatingDeserializer<List<?>> {
    public VavrListDeserializer() {
        super(new VavrListConverter.FromCollec());
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        var elementType = property.getType().getBindings().getBoundType(0);
        var delegateType = ctxt.getTypeFactory().constructCollectionLikeType(Collection.class, elementType);
        return withDelegate(_converter, delegateType, ctxt.findContextualValueDeserializer(delegateType, property));
    }

    @Override
    protected StdDelegatingDeserializer<List<?>> withDelegate(Converter<Object, List<?>> c, JavaType t, JsonDeserializer<?> deser) {
        return new StdDelegatingDeserializer<>(c, t, deser);
    }
}

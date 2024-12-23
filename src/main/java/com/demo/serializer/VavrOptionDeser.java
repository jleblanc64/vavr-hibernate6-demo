package com.demo.serializer;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.Collection;

public class VavrOptionDeser {
   public static class Deserializer extends StdDelegatingDeserializer<Option<?>> {
        public Deserializer() {
            super(new VavrOptionConverter.ToOption());
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            var elementType = property.getType().getBindings().getBoundType(0);
//            var delegateType = ctxt.getTypeFactory().constructCollectionLikeType(Collection.class, elementType);
            return withDelegate(_converter, elementType, ctxt.findContextualValueDeserializer(elementType, property));
        }

        @Override
        protected StdDelegatingDeserializer<Option<?>> withDelegate(Converter<Object, Option<?>> c, JavaType t, JsonDeserializer<?> deser) {
            return new StdDelegatingDeserializer<>(c, t, deser);
        }
    }

    public static class Serializer extends StdDelegatingSerializer {
        public Serializer() {
            super(new VavrOptionConverter.FromOption());
        }

        @Override
        protected StdDelegatingSerializer withDelegate(Converter<Object, ?> c, JavaType t, JsonSerializer<?> deser) {
            return new StdDelegatingSerializer(c, t, deser);
        }
    }
}

package com.demo.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import io.vavr.control.Option;

// https://github.com/FasterXML/jackson-modules-java8/blob/2.13/datatypes/src/main/java/com/fasterxml/jackson/datatype/jdk8/Jdk8Deserializers.java
public class VavrOptionDeserializerBase
        extends Deserializers.Base
        implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Override // since 2.7
    public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType,
                                                         DeserializationConfig config, BeanDescription beanDesc,
                                                         TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) {
        if (refType.hasRawClass(Option.class))
            return new VavrOptionDeserializer(refType, null, contentTypeDeserializer, contentDeserializer);

        return null;
    }
}
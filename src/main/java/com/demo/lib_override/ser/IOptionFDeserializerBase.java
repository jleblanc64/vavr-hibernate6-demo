package com.demo.lib_override.ser;

import com.demo.functional.IOptionF;
import com.demo.functional.OptionF;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;

// https://github.com/FasterXML/jackson-modules-java8/blob/2.13/datatypes/src/main/java/com/fasterxml/jackson/datatype/jdk8/Jdk8Deserializers.java
public class IOptionFDeserializerBase
        extends Deserializers.Base
        implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Override // since 2.7
    public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType,
                                                         DeserializationConfig config, BeanDescription beanDesc,
                                                         TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) {
        if (refType.hasRawClass(OptionF.class)) {
            return new IOptionFDeserializer(refType, null, contentTypeDeserializer, contentDeserializer);
        }
        return null;
    }
}
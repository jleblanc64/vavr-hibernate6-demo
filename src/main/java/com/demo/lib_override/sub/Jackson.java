package com.demo.lib_override.sub;

import com.demo.functional.IListF;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

import java.util.Optional;

import static com.demo.lib_override.FieldMocked.*;
import static com.demo.lib_override.OverrideLibs.m;

public class Jackson {
    public static void override() {
        m(AbstractJackson2HttpMessageConverter.class, "readJavaType", args -> {
            var javaType = (JavaType) args[0];
            var input = (HttpInputMessage) args[1];
            var inputStream = StreamUtils.nonClosing(input.getBody());

            var om = new ObjectMapper();
            om.registerModule(new Jdk8Module());

            var simpleModule = new SimpleModule().addDeserializer(IListF.class, new IListFDeserializer());
            om.registerModule(simpleModule);

            var deser = om.readValue(inputStream, javaType.getRawClass());
            fields(deser).forEach(f -> {
                if (!f.getType().equals(Optional.class))
                    return;

                var opt = getRefl(deser, f);
                if (opt == null)
                    setRefl(deser, f, Optional.empty());
            });

            return deser;
        });

        m(AbstractJackson2HttpMessageConverter.class, "canRead", args -> true);
    }
}

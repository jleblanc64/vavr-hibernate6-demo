package com.demo.lib_override.sub;

import com.demo.functional.ListF;
import com.demo.functional.OptionF;
import com.demo.lib_override.ser.ListFDeserializer;
import com.demo.lib_override.ser.OptionFModule;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;

import static com.demo.functional.ListF.empty;
import static com.demo.functional.OptionF.emptyO;
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
            om.registerModule(new OptionFModule());

            var simpleModule = new SimpleModule().addDeserializer(ListF.class, new ListFDeserializer());
            om.registerModule(simpleModule);

            // init null values
            var deser = om.readValue(inputStream, javaType.getRawClass());
            fields(deser).forEach(f -> {
                var type = f.getType();
                Object empty;
                if (type == OptionF.class)
                    empty = emptyO();
                else if (type == ListF.class)
                    empty = empty();
                else
                    return;

                var o = getRefl(deser, f);
                if (o == null)
                    setRefl(deser, f, empty);
            });

            return deser;
        });

        // be tolerant, still try to deser if mediaType == null
        m(AbstractJackson2HttpMessageConverter.class, "canRead", args -> {
            if (args.length != 3)
                return null;

            var mediaType = (MediaType) args[2];
            return mediaType == null || mediaType.toString().toLowerCase().contains("application/json");
        });
    }
}

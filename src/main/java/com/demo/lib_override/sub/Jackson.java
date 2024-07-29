package com.demo.lib_override.sub;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.gson.JsonParser;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static com.demo.functional.Functor.minus;
import static com.demo.functional.ListF.f;
import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.OverrideLibs.mSelf;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Jackson {
    public static void override() {
        // TODO augment AbstractJackson2HttpMessageConverter.readJavaType() instead
        mSelf(ObjectReader.class, "readValue", argsS -> {
            HttpMessageNotReadableException h;
            var args = argsS.args;
            var self = argsS.self;

            InputStream in;
            if (args[0] instanceof InputStream)
                in = (InputStream) args[0];
            else if (args[0] instanceof Reader)
                in = IOUtils.toInputStream(IOUtils.toString((Reader) args[0]), UTF_8);
            else
                return null;

            var jt = (JavaType) getRefl(self, "_valueType", JavaType.class);
            var clazz = jt.getRawClass();
            if (List.class.isAssignableFrom(clazz))
                return null;

            var s = fillMissingFields(IOUtils.toString(in, UTF_8), clazz);
            return ((ObjectReader) self).readValue(s);
        });
    }

    public static String fillMissingFields(String s, Class<?> clazz) {
        var jo = JsonParser.parseString(s).getAsJsonObject();

        var fields = f(clazz.getDeclaredFields()).filter(f -> Optional.class.equals(f.getType())).mapS(Field::getName);
        var fieldsJo = jo.keySet();
        var missingFields = minus(fields, fieldsJo);

        missingFields.forEach(f -> jo.addProperty(f, (String) null));
        return jo.toString();
    }
}

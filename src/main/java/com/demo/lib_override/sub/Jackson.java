package com.demo.lib_override.sub;

import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import java.util.Optional;

import static com.demo.functional.ListF.f;
import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.FieldMocked.setRefl;
import static com.demo.lib_override.OverrideLibs.mExit;
import static org.reflections.ReflectionUtils.Fields;
import static org.reflections.ReflectionUtils.get;

public class Jackson {
    public static void overrideOptionEmpty() {
        mExit(AbstractJackson2HttpMessageConverter.class, "readJavaType", returned -> {

            var fields = f(get(Fields.of(returned.getClass())));
            fields.forEach(f -> {
                if (!f.getType().equals(Optional.class))
                    return;

                var opt = getRefl(returned, f);
                if (opt == null)
                    setRefl(returned, f, Optional.empty());
            });

            return returned;
        });
    }
}

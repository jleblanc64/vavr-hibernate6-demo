package com.demo.lib_override.sub;

import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import java.util.Optional;

import static com.demo.functional.ListF.f;
import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.FieldMocked.setRefl;
import static com.demo.lib_override.OverrideLibs.mExit;

public class Jackson {
    public static void override() {
        mExit(AbstractJackson2HttpMessageConverter.class, "readJavaType", returned -> {

            f(returned.getClass().getDeclaredFields()).forEach(f -> {
                if (!f.getType().equals(Optional.class))
                    return;

                var opt = getRefl(returned, f.getName(), Optional.class);
                if (opt == null)
                    setRefl(returned, f.getName(), Optional.empty());
            });

            return returned;
        });
    }
}

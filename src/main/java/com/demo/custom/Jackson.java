package com.demo.custom;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.functional.ListF;
import io.github.jleblanc64.libcustom.functional.OptionF;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import static io.github.jleblanc64.libcustom.FieldMocked.*;
import static io.github.jleblanc64.libcustom.functional.ListF.empty;
import static io.github.jleblanc64.libcustom.functional.OptionF.emptyO;

public class Jackson {
    public static void override() {
        // replace null with empty OptionF or ListF
        LibCustom.modifyReturn(AbstractJackson2HttpMessageConverter.class, "readJavaType", argsR -> {
            var returned = argsR.returned;
            if (returned == null)
                return returned;

            fields(returned).forEach(f -> {
                var type = f.getType();
                Object empty;
                if (type == OptionF.class)
                    empty = emptyO();
                else if (type == ListF.class)
                    empty = empty();
                else
                    return;

                var o = getRefl(returned, f);
                if (o == null)
                    setRefl(returned, f, empty);
            });

            return returned;
        });

        // accept text/plain content-type as json
        LibCustom.modifyReturn(HttpHeaders.class, "getContentType", argsR -> {
            var mediaType = argsR.returned;
            if (mediaType != null && mediaType.toString().toLowerCase().startsWith("text/plain"))
                return MediaType.parseMediaType("application/json");

            return mediaType;
        });
    }
}

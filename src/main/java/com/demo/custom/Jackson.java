package com.demo.custom;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.functional.ListF;
import io.github.jleblanc64.libcustom.functional.OptionF;
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

        // be tolerant, still try to deser if mediaType == null
        LibCustom.override(AbstractJackson2HttpMessageConverter.class, "canRead", args -> {
            if (args.length != 3)
                return LibCustom.ORIGINAL;

            var mediaType = (MediaType) args[2];
            return mediaType == null || mediaType.toString().toLowerCase().contains("application/json");
        });
    }
}

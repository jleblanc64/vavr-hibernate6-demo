package com.demo;

import com.demo.serializer.VavrListDeser;
import com.demo.serializer.VavrOptionDeser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    ObjectMapper om;

    @Override
    public void extendMessageConverters(java.util.List<HttpMessageConverter<?>> converters) {
        var simpleModule = new SimpleModule()
                .addDeserializer(List.class, new VavrListDeser.Deserializer())
                .addSerializer(List.class, new VavrListDeser.Serializer());
        om.registerModule(simpleModule);

        simpleModule = new SimpleModule()
                .addDeserializer(Option.class, new VavrOptionDeser.Deserializer())
                .addSerializer(Option.class, new VavrOptionDeser.Serializer());
        om.registerModule(simpleModule);

        List.ofAll(converters).filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .forEach(c -> ((MappingJackson2HttpMessageConverter) c).setObjectMapper(om));
    }
}

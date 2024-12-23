package com.demo;

import com.demo.serializer.VavrListDeser;
import com.demo.serializer.VavrOptionModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    ObjectMapper om;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        om.registerModule(new VavrOptionModule());

        var simpleModule = new SimpleModule()
                .addDeserializer(io.vavr.collection.List.class, new VavrListDeser.Deserializer())
                .addSerializer(io.vavr.collection.List.class, new VavrListDeser.Serializer());
        om.registerModule(simpleModule);

        io.vavr.collection.List.ofAll(converters).filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .forEach(c -> ((MappingJackson2HttpMessageConverter) c).setObjectMapper(om));
    }
}

package com.demo;

import com.demo.serializer.VavrListDeserializer;
import com.demo.serializer.VavrListSerializer;
import com.demo.serializer.OptionFModule;
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
        var ser = om.getSerializerProviderInstance();
        om.registerModule(new OptionFModule());

        var simpleModule = new SimpleModule()
                .addDeserializer(io.vavr.collection.List.class, new VavrListDeserializer())
                .addSerializer(io.vavr.collection.List.class, new VavrListSerializer(ser));
        om.registerModule(simpleModule);

        converters.stream().filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .map(c -> (MappingJackson2HttpMessageConverter) c)
                .forEach(c -> c.setObjectMapper(om));
    }
}

package com.demo.config;

import com.demo.vavr.MetaListImpl;
import com.demo.vavr.MetaOptionImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jleblanc64.hibernate6.jackson.deser.UpdateOM;
import io.github.jleblanc64.libcustom.LibCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
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
        var metaList = new MetaListImpl();
        var metaOption = new MetaOptionImpl();
        UpdateOM.update(om, converters, metaOption, metaList);
        LibCustom.load();
    }
}

package com.demo.config;

import com.demo.custom.jackson.deser.UpdateOM;
import com.demo.implem.MetaListImpl;
import com.demo.implem.MetaOptionImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.custom.jackson.VavrJackson2;
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
        VavrJackson2.override(converters);

        var metaList = new MetaListImpl();
        var metaOption = new MetaOptionImpl();
        UpdateOM.update(om, converters, metaOption, metaList);
        LibCustom.load();
    }
}

package com.demo.spring;

import io.github.jleblanc64.hibernate6.hibernate.VavrHibernate6;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ConfigInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        VavrHibernate6.override();
    }
}

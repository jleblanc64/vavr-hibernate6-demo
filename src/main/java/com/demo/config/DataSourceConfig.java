package com.demo.config;

import com.demo.vavr.MetaListImpl;
import com.demo.vavr.MetaOptionImpl;
import com.zaxxer.hikari.HikariDataSource;
import io.github.jleblanc64.hibernate6.custom.hibernate.VavrHibernate;
import io.github.jleblanc64.hibernate6.custom.jackson.VavrJackson;
import io.github.jleblanc64.hibernate6.custom.spring.OverrideContentType;
import io.github.jleblanc64.hibernate6.custom.spring.VavrSpring;
import io.github.jleblanc64.libcustom.LibCustom;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    String url;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Bean
    public DataSource getDataSource() {
        var metaList = new MetaListImpl();
        var metaOption = new MetaOptionImpl();

        VavrHibernate.override(metaList);
        VavrSpring.override(metaList);
        VavrJackson.override(metaList);

        VavrHibernate.override(metaOption);
        VavrSpring.override(metaOption);
        VavrJackson.override(metaOption);

        OverrideContentType.override();
        LibCustom.load();

        // Hikari
        var ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        // Flyway migration
        var config = Flyway.configure().dataSource(url, username, password);
        config.load().migrate();

        return ds;
    }
}

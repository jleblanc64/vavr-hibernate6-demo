package com.demo.config;

import com.demo.custom.hibernate.VavrHibernate;
import com.demo.custom.jackson.VavrJackson;
import com.demo.custom.spring.OverrideSpring;
import com.demo.implem.MetaListImpl;
import com.demo.implem.MetaOptionImpl;
import com.demo.custom.spring.OverrideContentType;
import com.zaxxer.hikari.HikariDataSource;
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
        var metaOption = new MetaOptionImpl();
        var metaList = new MetaListImpl();

        VavrHibernate.override(metaList);
        VavrHibernate.override(metaOption);

        VavrJackson.override(metaOption, metaList);

        OverrideSpring.override(metaOption);

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

package com.demo.spring;

import com.demo.custom.hibernate.Utils;
import com.demo.custom.hibernate.VavrHibernate;
import com.demo.custom.jackson.VavrJackson;
import com.demo.implem.MetaListImpl;
import com.demo.implem.MetaOptionImpl;
import com.zaxxer.hikari.HikariDataSource;
import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.custom.hibernate.duplicate.ParameterizedTypeImpl;
import io.github.jleblanc64.libcustom.meta.MetaOption;
import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;

import javax.sql.DataSource;
import java.util.Optional;

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

//        VavrHibernate6.override(metaOption, metaList);
        VavrHibernate.override(metaList);
        VavrHibernate.override(metaOption);

        VavrJackson.override(metaOption, metaList);

        overrideSpring(metaOption);

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

    @SneakyThrows
    static void overrideSpring(MetaOption metaOption) {
        var clazz = Class.forName("org.springframework.data.util.TypeDiscoverer");
//        LibCustom.modifyArg(clazz, "createInfo", 0, args -> {
//            var type = args[0].toString();
//            if (type.startsWith(metaOption.monadClass().getName() + "<")) {
//                var paramClass = Utils.paramClass(type);
//                var isEntity = Utils.isEntity(paramClass.getDeclaredAnnotations());
//                if (isEntity)
//                    return ParameterizedTypeImpl.of(Optional.class, paramClass, null);
//            }
//
//            return LibCustom.ORIGINAL;
//        });

        LibCustom.modifyReturn(MethodParameter.class, "getGenericParameterType", argsR -> {
            var returned = argsR.returned.toString();
            if (returned.startsWith(metaOption.monadClass().getName() + "<")) {
                var paramClass = Utils.paramClass(returned);
                var isEntity = Utils.isEntity(paramClass.getDeclaredAnnotations());
                if (isEntity)
                    return ParameterizedTypeImpl.of(Optional.class, paramClass, null);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyReturn(DefaultMethodInvokingMethodInterceptor.class, "invoke", argsR -> {
            var returned = argsR.returned;
            var invocation = argsR.args[0];

            if (invocation.toString().contains(metaOption.monadClass().getName())) {
                if (metaOption.isSuperClassOf(returned))
                    return returned;

                var o = (Optional<?>) returned;
                var v = o == null || o.isEmpty() ? null : o.get();
                return metaOption.fromValue(v);
            }

            return returned;
        });

//        clazz = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");
//        LibCustom.modifyArgWithSelf(clazz, "invoke", 2, argsS -> {
//            var args = argsS.args;
//            var self = argsS.self;
//
//            var proxiedInterfaces = f((Class[]) Utils.getRefl(self, "proxiedInterfaces"));
//            if (!proxiedInterfaces.contains(Repository.class))
//                return args[2];
//
//            if (args[2] == null)
//                return args[2];
//
//            var argsL = f((Object[]) args[2]);
//            return argsL.map(arg -> {
//                if (!metaOption.isSuperClassOf(arg) || arg instanceof List)
//                    return arg;
//
//                return metaOption.getOrNull(arg);
//            }).toArray();
//        });

        // request params
        var methodParameterClass = Class.forName("org.springframework.core.MethodParameter");
        var argResolverClass = Class.forName("org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver");

        LibCustom.modifyReturn(argResolverClass, "resolveArgument", argsRet -> {
            var args = argsRet.args;
            var returned = argsRet.returned;
            var type = methodParameterClass.getMethod("getParameterType").invoke(args[0]);

            if (metaOption.isSuperClassOf(type) && !metaOption.isSuperClassOf(returned))
                return metaOption.fromValue(returned);

            return returned;
        });

        LibCustom.modifyArg(Class.forName("org.springframework.beans.TypeConverterDelegate"), "doConvertValue", 1, args -> {
            var newValue = args[1];
            var requiredType = (Class<?>) args[2];

            if (metaOption.isSuperClassOf(requiredType))
                return metaOption.fromValue(newValue);

            return LibCustom.ORIGINAL;
        });
    }
}

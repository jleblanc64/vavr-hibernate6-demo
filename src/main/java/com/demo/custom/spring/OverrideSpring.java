package com.demo.custom.spring;

import com.demo.custom.hibernate.Utils;
import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.custom.hibernate.duplicate.ParameterizedTypeImpl;
import io.github.jleblanc64.libcustom.meta.MetaOption;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor;

import java.util.Optional;

public class OverrideSpring {
    @SneakyThrows
    public static void override(MetaOption metaOption) {
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

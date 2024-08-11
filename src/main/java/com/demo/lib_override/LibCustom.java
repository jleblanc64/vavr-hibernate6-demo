package com.demo.lib_override;

import lombok.SneakyThrows;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import static com.demo.functional.Functor.ThrowingFunction;
import static com.demo.functional.ListF.f;
import static com.demo.lib_override.Internal.*;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static org.reflections.ReflectionUtils.Methods;
import static org.reflections.ReflectionUtils.get;

public class LibCustom {
    public static void override(Class<?> clazz, String name, ThrowingFunction<Object[], Object> method) {
        Internal.methods.add(new Internal.MethodDesc(name, method, clazz));
    }

    public static void overrideWithSelf(Class<?> clazz, String name, ThrowingFunction<ArgsSelf, Object> method) {
        var methods = f(get(Methods.of(clazz)));
        var m = methods.findSafe(x -> x.getName().equals(name));
        var isStatic = Modifier.isStatic(m.getModifiers());
        if (isStatic)
            throw new RuntimeException("WithSelf doesn't work for static methods");

        methodsSelf.add(new MethodDescSelf(name, method, clazz));
    }

    public static void modifyReturned(Class<?> clazz, String name, ThrowingFunction<ArgsReturned, Object> method) {
        methodsExitArgs.add(new MethodDescExitArgs(name, method, clazz));
    }

    public static void modifyArgs(Class<?> clazz, String name, int argIdx, ThrowingFunction<Object[], Object> method) {
        methodsArgsMod.add(new MethodDescArgsMod(name, new MethodArgIdx(argIdx, method), clazz));
    }

    public static void modifyArgsWithSelf(Class<?> clazz, String name, int argIdx, ThrowingFunction<ArgsSelf, Object> method) {
        methodsArgsModSelf.add(new MethodDescArgsModSelf(name, new MethodArgIdxSelf(argIdx, method), clazz));
    }

    @SneakyThrows
    public static void load() {
        // fill nameToMethod
        nameToMethod = methods.toMap(m -> m.name, m -> m.method);
        nameToMethodExit = methodsExit.toMap(m -> m.name, m -> m.method);
        nameToMethodExitArgs = methodsExitArgs.toMap(m -> m.name, m -> m.method);
        nameToMethodArgsMod = methodsArgsMod.toMap(m -> m.name, m -> m.method);

        nameToMethodSelf = methodsSelf.toMap(m -> m.name, m -> m.method);
        nameToMethodArgsModSelf = methodsArgsModSelf.toMap(m -> m.name, m -> m.method);

        Internal.instru = ByteBuddyAgent.install();

        var methodMetas = new ArrayList<MethodMeta>(methods);
        methodMetas.addAll(methodsExit);
        methodMetas.addAll(methodsExitArgs);
        methodMetas.addAll(methodsArgsMod);
        var classToMethods = f(methodMetas).groupBy(MethodMeta::getClazz, MethodMeta::getName);
        classToMethods.forEach((c, m) -> agent(c, m, AdviceGeneric.class));

        // self
        var methodMetasSelf = new ArrayList<MethodMeta>(methodsSelf);
        methodMetasSelf.addAll(methodsArgsModSelf);
        var classToMethodsSelf = f(methodMetasSelf).groupBy(MethodMeta::getClazz, MethodMeta::getName);
        classToMethodsSelf.forEach((c, m) -> agent(c, m, AdviceGenericSelf.class));
    }

    public static void reset() {
        agents.forEach(a -> a.reset(instru, RETRANSFORMATION));
        agents.clear();
    }
}

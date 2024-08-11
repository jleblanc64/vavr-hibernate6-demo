package com.demo.lib_override;

import com.demo.functional.ListF;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.demo.functional.ListF.empty;
import static net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.NoOp.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy.Default.REDEFINE;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

public class Internal {
    public static Map<String, Function<Object[], Object>> nameToMethod;
    public static Map<String, Function<ArgsSelf, Object>> nameToMethodSelf;
    public static Map<String, Function<Object, Object>> nameToMethodExit;
    public static Map<String, Function<ArgsReturned, Object>> nameToMethodExitArgs;
    public static Map<String, MethodArgIdx> nameToMethodArgsMod;
    public static Map<String, MethodArgIdxSelf> nameToMethodArgsModSelf;
    public static ListF<MethodDesc> methods = empty();
    public static ListF<MethodDescSelf> methodsSelf = empty();
    public static ListF<MethodDescExit> methodsExit = empty();
    public static ListF<MethodDescExitArgs> methodsExitArgs = empty();
    public static ListF<MethodDescArgsMod> methodsArgsMod = empty();
    public static ListF<MethodDescArgsModSelf> methodsArgsModSelf = empty();

    static volatile Instrumentation instru;
    static List<ResettableClassFileTransformer> agents = new ArrayList<>();

    static void agent(Class<?> clazz, ListF<String> methods, Class<?> adviceClass) {
        ElementMatcher.Junction<NamedElement> named = methods.fold(none(), (acc, m) -> acc.or(named(m)));
        var agent = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(RETRANSFORMATION)
                .with(INSTANCE)
                .with(REDEFINE)
                .type(named(clazz.getName()))
                .transform((b, type, classLoader, module, x) -> b.visit(Advice.to(adviceClass).on(named)))
                .installOnByteBuddyAgent();

        agents.add(agent);
    }

    interface MethodMeta {
        String getName();

        Class<?> getClazz();
    }

    @AllArgsConstructor
    @Getter
    static class MethodDesc implements MethodMeta {
        String name;
        Function<Object[], Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescSelf implements MethodMeta {
        String name;
        Function<ArgsSelf, Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescExit implements MethodMeta {
        String name;
        Function<Object, Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescExitArgs implements MethodMeta {
        String name;
        Function<ArgsReturned, Object> method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescArgsMod implements MethodMeta {
        String name;
        MethodArgIdx method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    static class MethodDescArgsModSelf implements MethodMeta {
        String name;
        MethodArgIdxSelf method;
        Class<?> clazz;
    }

    @AllArgsConstructor
    @Getter
    public static class ArgsReturned {
        public Object[] args;
        public Object returned;
    }

    @AllArgsConstructor
    @Getter
    public static class ArgsSelf {
        public Object[] args;
        public Object self;
    }

    @AllArgsConstructor
    @Getter
    public static class MethodArgIdx {
        public int argIdx;
        public Function<Object[], Object> method;
    }

    @AllArgsConstructor
    @Getter
    public static class MethodArgIdxSelf {
        public int argIdx;
        public Function<ArgsSelf, Object> method;
    }
}

package com.demo.functional;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.demo.functional.ListF.f;
import static java.util.Optional.ofNullable;

public interface IListF<T> extends List<T> {
    default <U> ListF<U> map(Functor.ThrowingFunction<T, U> fun) {
        return f(stream().map(fun).collect(Collectors.toList()));
    }

    default void forEachIdx(Functor.ThrowingBiConsumer<T, Integer> f) {
        List<T> l = copy();
        for (int i = 0; i < l.size(); i++)
            f.accept(l.get(i), i);
    }

    default boolean allMatch(Predicate<T> p) {
        return stream().allMatch(p);
    }

    default ListF<T> copy() {
        return f(stream().collect(Collectors.toList()));
    }
}

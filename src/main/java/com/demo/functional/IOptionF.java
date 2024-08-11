package com.demo.functional;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.demo.functional.ListF.f;

public interface IOptionF<T> {
    OptionF<T> o();

    default <U> OptionF<U> flatMap(Function<T, OptionF<U>> f) {
        return new OptionF<>(o().opt().flatMap(t -> f.apply(t).opt()));
    }

    default T orElse(T t) {
        return o().opt().orElse(t);
    }

    default T get() {
        return o().get();
    }
}

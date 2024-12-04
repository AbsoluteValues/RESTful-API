package com.AbsoluteValue.RESTful.common.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Converter {

    public static <T, R> R convert(T source, Function<T, R> converter) {
        return converter.apply(source);
    }

    public static <T, R> List<R> convertList(List<T> sourceList, Function<T, R> converter) {
        return sourceList.stream()
                .map(converter)
                .collect(Collectors.toList());
    }
}
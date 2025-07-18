package com.AbsoluteValue.RESTful.common.converter;

public interface Converter<S, T> {

    T convert(S source);
}

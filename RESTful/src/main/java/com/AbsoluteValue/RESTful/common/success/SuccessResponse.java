package com.AbsoluteValue.RESTful.common.success;

import org.springframework.http.HttpStatus;

public record SuccessResponse(HttpStatus status, String message, String code) {

    public static SuccessResponse build(SuccessCode successCode) {
        return new SuccessResponse(successCode.getStatus(), successCode.getMessage(), successCode.getData());
    }
}

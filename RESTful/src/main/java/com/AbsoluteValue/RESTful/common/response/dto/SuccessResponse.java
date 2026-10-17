package com.AbsoluteValue.RESTful.common.response.dto;

import com.AbsoluteValue.RESTful.common.response.code.Code;
import java.time.LocalDateTime;
public record SuccessResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        Object data
) {

    public SuccessResponse(Code code, Object data) {
        this(
                LocalDateTime.now(),
                code.getStatus().value(),
                code.getMessage(),
                data
        );
    }
}

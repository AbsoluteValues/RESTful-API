package com.AbsoluteValue.RESTful.common.response.dto;

import com.AbsoluteValue.RESTful.common.response.code.Code;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.web.context.request.WebRequest;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String code,
        Map<String, Object> additionalData
) {

    public ErrorResponse(Code code, WebRequest request, Map<String, Object> additionalData) {
        this(code, getPathFromRequest(request), additionalData);
    }

    public ErrorResponse(Code code, String path, Map<String, Object> additionalData) {
        this(
                LocalDateTime.now(),
                code.getStatus().value(),
                code.getStatus().getReasonPhrase(),
                code.getMessage(),
                path,
                code.getErrorCode(),
                additionalData
        );
    }

    private static String getPathFromRequest(WebRequest request) {
        return request.getDescription(false).replaceFirst("^uri=", "");
    }
}

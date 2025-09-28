package com.AbsoluteValue.RESTful.common.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

@NoArgsConstructor
@Getter
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String code;
    private Map<String, Object> additionalData;

    private static String getPathFromRequest(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    public ErrorResponse(HttpStatus status, String message, WebRequest request, String code) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = getPathFromRequest(request);
        this.code = code;
        this.additionalData = null;
    }

    public ErrorResponse(HttpStatus status, String message, WebRequest request, String code, Map<String, Object> additionalData) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = getPathFromRequest(request);
        this.code = code;
        this.additionalData = additionalData;
    }
}
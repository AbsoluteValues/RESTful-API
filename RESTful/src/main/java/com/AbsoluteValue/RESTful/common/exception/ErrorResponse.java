package com.AbsoluteValue.RESTful.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@Getter
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;
    private Map<String, Object> additionalData;



    private ErrorResponse(int status, String error, String message, String path, String errorCode, Map<String, Object> additionalData) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errorCode = errorCode;
        this.additionalData = additionalData;
    }



    // 경로 추출 메서드
    private static String getPathFromRequest(WebRequest request) {
        return request.getDescription(false).substring(4);
    }

    // 추가 데이터 없음
    protected static ErrorResponse build(HttpStatus status, String message, WebRequest request, String errorCode) {
        return new ErrorResponse(status.value(), status.getReasonPhrase(), message, getPathFromRequest(request), errorCode, null);
    }

    // 추가 데이터 존재
    protected static ErrorResponse build(HttpStatus status, String message, WebRequest request, String errorCode, Map<String, Object> additionalData) {
        return new ErrorResponse(status.value(), status.getReasonPhrase(), message, getPathFromRequest(request), errorCode, additionalData);
    }
}
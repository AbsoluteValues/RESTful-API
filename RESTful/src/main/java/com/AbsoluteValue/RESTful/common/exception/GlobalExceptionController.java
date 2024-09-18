package com.AbsoluteValue.RESTful.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

    // 전역 예외 처리 (모든 예외 처리)
    @ExceptionHandler(Exception.class)
    private ErrorResponse handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unhandled exception: ", ex);

        return ErrorResponse.build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support.",
                request,
                "INTERNAL_SERVER_ERROR"
        );
    }

    // 유효성 검사 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Validation failed: ", ex);

        Map<String, Object> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ErrorResponse.build(
                HttpStatus.BAD_REQUEST,
                "Validation error",
                request,
                "VALIDATION_ERROR",
                Map.of("errors", errors)
        );
    }

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    private ErrorResponse handleCustomException(CustomException ex, WebRequest request) {
        logger.warn("Resource not found: ", ex);
        HttpStatus status = ex.getStatus();

        System.out.println(status);

        return ErrorResponse.build(
                status,
                ex.getMessage(),
                request,
                "RESOURCE_NOT_FOUND"
        );
    }

    // 보안 위험 방지를 위한 권한 관련 예외 처리 (Spring Security 적용 필수)
//    @ExceptionHandler(AccessDeniedException.class)
//    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
//        logger.warn("Access denied: ", ex);
//
//        return ErrorResponse.build(
//                HttpStatus.FORBIDDEN,
//                "You do not have permission to access this resource.",
//                getPathFromRequest(request),
//                null,
//                "ACCESS_DENIED"
//        );
//    }
}
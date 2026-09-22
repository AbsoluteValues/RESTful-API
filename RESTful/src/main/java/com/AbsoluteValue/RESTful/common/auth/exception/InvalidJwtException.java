package com.AbsoluteValue.RESTful.common.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtException extends AuthenticationException {

    public InvalidJwtException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJwtException(String message) {
        super(message);
    }
}

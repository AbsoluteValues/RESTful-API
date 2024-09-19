package com.AbsoluteValue.RESTful.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    //404 NOT_FOUND 잘못된 리소스 접근
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저 정보입니다.", "173");

    private final HttpStatus status;
    private final String message;
    private final String errorCode;
}

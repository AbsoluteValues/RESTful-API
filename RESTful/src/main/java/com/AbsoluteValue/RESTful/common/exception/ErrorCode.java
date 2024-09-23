package com.AbsoluteValue.RESTful.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    //404 NOT_FOUND 잘못된 리소스 접근
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저 정보입니다.", "173"),

    //500 SAVE FAILED 데이터 베이스 저장 오류
    SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 베이스에 저장을 실패했습니다.", "096");

    private final HttpStatus status;
    private final String message;
    private final String errorCode;
}

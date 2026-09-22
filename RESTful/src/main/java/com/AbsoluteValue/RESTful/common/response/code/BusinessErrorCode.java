package com.AbsoluteValue.RESTful.common.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum BusinessErrorCode implements Code {

    SESSION_INVALID(HttpStatus.UNAUTHORIZED, "세션에 정보가 없습니다.", "682"),

    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.", "684"),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 정보입니다.", "173"),

    SAVE_FAILED(HttpStatus.BAD_REQUEST, "이미 존재하는 정보입니다.", "096"),

    UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 수정을 실패했습니다.", "049"),

    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 삭제를 실패했습니다.", "079");

    private final HttpStatus status;
    private final String message;
    private final String errorCode;
}

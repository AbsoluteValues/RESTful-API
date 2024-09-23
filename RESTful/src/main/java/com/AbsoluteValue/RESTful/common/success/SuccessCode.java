package com.AbsoluteValue.RESTful.common.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SuccessCode {

    SIGN_UP_USER(HttpStatus.CREATED, "회원 가입이 완료되었습니다.", null);

    private final HttpStatus status;
    private final String message;
    private final String data;
}

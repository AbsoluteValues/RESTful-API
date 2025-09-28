package com.AbsoluteValue.RESTful.common.response.exception;

import com.AbsoluteValue.RESTful.common.response.code.BusinessErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessErrorCode errorCode;

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

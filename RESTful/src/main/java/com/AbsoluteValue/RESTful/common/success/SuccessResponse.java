package com.AbsoluteValue.RESTful.common.success;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SuccessResponse {

    private int status;
    private String message;
    private Object data;

    public SuccessResponse(SuccessCode successCode) {
        this.status = successCode.getStatus().value();
        this.message = successCode.getMessage();
        this.data = null;
    }

    public SuccessResponse(SuccessCode successCode, Object data) {
        this.status = successCode.getStatus().value();
        this.message = successCode.getMessage();
        this.data = data;
    }
}
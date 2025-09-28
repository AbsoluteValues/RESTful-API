package com.AbsoluteValue.RESTful.common.response.handler;

import com.AbsoluteValue.RESTful.common.response.code.Code;
import com.AbsoluteValue.RESTful.common.response.dto.ErrorResponse;
import com.AbsoluteValue.RESTful.common.response.dto.SuccessResponse;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

public class ResponseHandler {

    public static ResponseEntity<SuccessResponse> success(Code code) {
        SuccessResponse response = new SuccessResponse(code, null);
        return ResponseEntity
                .status(code.getStatus())
                .body(response);
    }

    public static ResponseEntity<SuccessResponse> success(Code code, Object data) {
        SuccessResponse response = new SuccessResponse(code, data);
        return ResponseEntity
                .status(code.getStatus())
                .body(response);
    }

    public static ResponseEntity<ErrorResponse> error(Code code, WebRequest request) {
        ErrorResponse response = new ErrorResponse(code, request, null);
        return ResponseEntity
                .status(code.getStatus())
                .body(response);
    }

    public static ResponseEntity<ErrorResponse> error(Code code, WebRequest request,
                                                      Map<String, Object> additionalData) {
        ErrorResponse response = new ErrorResponse(code, request, additionalData);
        return ResponseEntity
                .status(code.getStatus())
                .body(response);
    }
}

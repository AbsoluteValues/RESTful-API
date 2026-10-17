package com.AbsoluteValue.RESTful.common.response.handler;

import com.AbsoluteValue.RESTful.common.response.code.Code;
import com.AbsoluteValue.RESTful.common.response.dto.ErrorResponse;
import com.AbsoluteValue.RESTful.common.response.dto.SuccessResponse;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@UtilityClass
public class ResponseHandler {

    public ResponseEntity<SuccessResponse> success(Code code) {
        return success(code, null);
    }

    public ResponseEntity<SuccessResponse> success(Code code, Object data) {
        return build(code, new SuccessResponse(code, data));
    }

    public ResponseEntity<ErrorResponse> error(Code code, WebRequest request) {
        return error(code, request, null);
    }

    public ResponseEntity<ErrorResponse> error(
            Code code, WebRequest request, Map<String, Object> additionalData
    ) {
        return build(code, new ErrorResponse(code, request, additionalData));
    }

    private <T> ResponseEntity<T> build(Code code, T response) {
        return ResponseEntity.status(code.getStatus()).body(response);
    }
}

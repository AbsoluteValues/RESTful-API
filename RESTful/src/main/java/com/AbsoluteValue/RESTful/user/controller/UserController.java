package com.AbsoluteValue.RESTful.user.controller;

import com.AbsoluteValue.RESTful.common.response.code.SuccessCode;
import com.AbsoluteValue.RESTful.common.response.dto.SuccessResponse;
import com.AbsoluteValue.RESTful.common.response.handler.ResponseHandler;
import com.AbsoluteValue.RESTful.user.dto.FindUserResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    @Operation(summary = "회원 가입", description = "신규 회원이 가입할 수 있도록 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", ref = "Created"),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    public ResponseEntity<SuccessResponse> signUpUser(@RequestBody @Valid SignUpUserRequest signUpUserRequest) {
        userService.signUpUser(signUpUserRequest);
        return ResponseHandler.success(SuccessCode.REGISTER_SUCCESS);
    }

    @GetMapping("/user/profile/{id}")
    @Operation(summary = "회원 정보 단건 조회", description = "특정 회원의 정보를 조회할 수 있도록 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "Ok"),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    public ResponseEntity<SuccessResponse> findUser(@PathVariable @NotBlank String id) {
        FindUserResponse response = userService.findUser(id);
        return ResponseHandler.success(SuccessCode.RESOURCE_FOUND, response);
    }

    @GetMapping("/users")
    @Operation(summary = "회원 정보 다건 조회", description = "[Deprecated] 모든 회원의 정보를 조회할 수 있도록 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "Ok"),
            @ApiResponse(responseCode = "400", ref = "BadRequest"),
            @ApiResponse(responseCode = "500", ref = "InternalServerError")
    })
    public ResponseEntity<SuccessResponse> findUsers() {
        List<FindUserResponse> response = userService.findUsers();
        return ResponseHandler.success(SuccessCode.RESOURCE_FOUND, response);
    }
}

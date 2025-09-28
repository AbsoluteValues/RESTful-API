package com.AbsoluteValue.RESTful.user.controller;

import com.AbsoluteValue.RESTful.common.response.code.SuccessCode;
import com.AbsoluteValue.RESTful.common.response.dto.SuccessResponse;
import com.AbsoluteValue.RESTful.common.response.handler.ResponseHandler;
import com.AbsoluteValue.RESTful.user.dto.FindUserResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.service.UserService;
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
    public ResponseEntity<SuccessResponse> signUpUser(@RequestBody @Valid SignUpUserRequest signUpUserRequest) {
        userService.signUpUser(signUpUserRequest);
        return ResponseHandler.success(SuccessCode.REGISTER_SUCCESS);
    }

    @GetMapping("/user/profile/{id}")
    public ResponseEntity<SuccessResponse> findUser(@PathVariable @NotBlank String id) {
        FindUserResponse response = userService.findUser(id);
        return ResponseHandler.success(SuccessCode.RESOURCE_FOUND, response);
    }

    @GetMapping("/users")
    public ResponseEntity<SuccessResponse> findUsers() {
        List<FindUserResponse> response = userService.findUsers();
        return ResponseHandler.success(SuccessCode.RESOURCE_FOUND, response);
    }
}

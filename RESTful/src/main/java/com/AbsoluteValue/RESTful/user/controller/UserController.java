package com.AbsoluteValue.RESTful.user.controller;

import com.AbsoluteValue.RESTful.common.exception.CustomException;
import com.AbsoluteValue.RESTful.common.exception.ErrorCode;
import com.AbsoluteValue.RESTful.common.success.SuccessCode;
import com.AbsoluteValue.RESTful.common.success.SuccessResponse;
import com.AbsoluteValue.RESTful.user.dto.FindUserResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.entity.User;
import com.AbsoluteValue.RESTful.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public SuccessResponse signUpUser(@RequestBody SignUpUserRequest signUpUserRequest) {
        int result = userService.signUpUser(signUpUserRequest);
        if (result > 0) {
            return new SuccessResponse(SuccessCode.REGISTER_SUCCESS);
        } else {
            throw new CustomException(ErrorCode.SAVE_FAILED);
        }
    }

    @GetMapping("/user/profile/{id}")
    public SuccessResponse getUser(@PathVariable String id) {
        GetUserResponse response = userService.getUser(id);
        if (response != null) {
            return new SuccessResponse(SuccessCode.RESOURCE_FOUND, response);
        } else {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    @GetMapping("/users")
    public SuccessResponse getUsers() {
        List<FindUserResponse> response = userService.findUsers();
        return new SuccessResponse(SuccessCode.RESOURCE_FOUND, response);
    }
}

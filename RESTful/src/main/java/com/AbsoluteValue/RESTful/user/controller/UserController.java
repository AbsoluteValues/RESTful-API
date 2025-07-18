package com.AbsoluteValue.RESTful.user.controller;

import com.AbsoluteValue.RESTful.common.success.SuccessCode;
import com.AbsoluteValue.RESTful.common.success.SuccessResponse;
import com.AbsoluteValue.RESTful.user.dto.FindUserResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
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
        userService.signUpUser(signUpUserRequest);
        return new SuccessResponse(SuccessCode.REGISTER_SUCCESS);
    }

    @GetMapping("/user/profile/{id}")
    public SuccessResponse findUser(@PathVariable String id) {
        FindUserResponse response = userService.findUser(id);
        return new SuccessResponse(SuccessCode.RESOURCE_FOUND, response);
    }

    @GetMapping("/users")
    public SuccessResponse findUsers() {
        List<FindUserResponse> response = userService.findUsers();
        return new SuccessResponse(SuccessCode.RESOURCE_FOUND, response);
    }
}

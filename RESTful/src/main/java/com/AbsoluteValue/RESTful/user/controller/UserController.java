package com.AbsoluteValue.RESTful.user.controller;

import com.AbsoluteValue.RESTful.common.exception.CustomException;
import com.AbsoluteValue.RESTful.common.exception.ErrorCode;
import com.AbsoluteValue.RESTful.common.success.SuccessCode;
import com.AbsoluteValue.RESTful.common.success.SuccessResponse;
import com.AbsoluteValue.RESTful.user.dto.GetUserInfoResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.service.UserService;
import com.AbsoluteValue.RESTful.user.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    public UserController() {}
    @Autowired
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping("/user")
    public SuccessResponse signUpUser(@RequestBody SignUpUserRequest signUpUserRequest) {
        int result = userService.signUpUser(signUpUserRequest);
        if (result > 0) {
            return new SuccessResponse(SuccessCode.SIGN_UP_USER);
        } else {
            throw new CustomException(ErrorCode.SAVE_FAILED);
        }
    }

    @GetMapping("/user/profile/{id}")
    public SuccessResponse getUserInfo(@PathVariable String id) {
        User userInfo = userService.getUserInfo(id);
        if (userInfo != null) {
            GetUserInfoResponse response = new GetUserInfoResponse();
            response.setId(userInfo.getId());
            response.setNickname(userInfo.getNickname());
            return new SuccessResponse(SuccessCode.GET_USER_INFO, response);
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }
}

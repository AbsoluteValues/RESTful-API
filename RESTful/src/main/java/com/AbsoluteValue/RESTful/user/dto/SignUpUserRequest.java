package com.AbsoluteValue.RESTful.user.dto;

public record SignUpUserRequest(
        String id,
        String password,
        String nickname
) {
}
package com.AbsoluteValue.RESTful.user.dto;

import com.AbsoluteValue.RESTful.user.vo.User;

public record SignUpUserRequest (
    String id,
    String password,
    String nickname
) {
    public User toEntity() {
        return User.builder()
                .id(id)
                .password(password)
                .nickname(nickname)
                .build();
    }
}

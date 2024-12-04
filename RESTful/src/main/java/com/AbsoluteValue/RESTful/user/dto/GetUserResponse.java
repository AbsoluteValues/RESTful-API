package com.AbsoluteValue.RESTful.user.dto;

import com.AbsoluteValue.RESTful.user.vo.User;

public record GetUserResponse(
    String id,
    String nickname
) {
    public static GetUserResponse from(User user) {
        return new GetUserResponse(user.getId(), user.getNickname());
    }
}

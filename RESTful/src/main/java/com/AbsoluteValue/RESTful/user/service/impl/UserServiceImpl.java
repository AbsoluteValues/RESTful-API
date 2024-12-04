package com.AbsoluteValue.RESTful.user.service.impl;

import com.AbsoluteValue.RESTful.user.dto.GetUserResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.mapper.UserMapper;
import com.AbsoluteValue.RESTful.user.service.UserService;
import com.AbsoluteValue.RESTful.user.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public int signUpUser(SignUpUserRequest signUpUserRequest) {
        User user = SignUpUserRequest.toEntity();
//        User user = User.builder()
//                .id(signUpUserRequest.id())
//                .password(signUpUserRequest.password())
//                .nickname(signUpUserRequest.nickname())
//                .build();
        return userMapper.signUpUser(user);
    }

    @Override
    public GetUserResponse getUser(String id) {
        User user = userMapper.getUser(id);
        return GetUserResponse.from(user);
    }

    @Override
    public List<GetUserResponse> getUsers() {
        List<User> users = userMapper.getUsers();
        return Collections.singletonList(GetUserResponse.from((User) users));
    }
}

package com.AbsoluteValue.RESTful.user.service.impl;

import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.mapper.UserMapper;
import com.AbsoluteValue.RESTful.user.service.UserService;
import com.AbsoluteValue.RESTful.user.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    public UserServiceImpl() {}
    @Autowired
    public UserServiceImpl(UserMapper userMapper) { this.userMapper = userMapper; }


    @Override
    public int signUpUser(SignUpUserRequest signUpUserRequest) {
        int result = userMapper.signUpUser(signUpUserRequest);
        return result;
    }

    @Override
    public User getUserInfo(String id) {
        User userInfo = userMapper.getUserInfo(id);
        return userInfo;
    }
}

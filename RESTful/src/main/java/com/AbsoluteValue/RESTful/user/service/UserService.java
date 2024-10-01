package com.AbsoluteValue.RESTful.user.service;

import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.vo.User;

public interface UserService {

    /**
     * 회원 가입 Service
     * @param user
     * @return int
     */
    int signUpUser(SignUpUserRequest signUpUserRequest);

    /**
     * 회원 정보 조회 Service
     * @param id
     * @return int
     */
    User getUserInfo(String id);
}

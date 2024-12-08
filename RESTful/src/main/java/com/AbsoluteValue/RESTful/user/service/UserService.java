package com.AbsoluteValue.RESTful.user.service;

import com.AbsoluteValue.RESTful.user.dto.GetUserResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;

import java.util.List;

public interface UserService {

    /**
     * 회원 가입 Service
     * @param signUpUserRequest
     * @return int
     */
    int signUpUser(SignUpUserRequest signUpUserRequest);

    /**
     * 회원 정보 단건 조회 Service
     * @param id
     * @return GetUserInfoResponse
     */
    GetUserResponse getUser(String id);

    /**
     * 회원 정보 다건 조회 Service
     * @return List<GetUserInfoResponse>
     */
    List<GetUserResponse> getUsers();
}

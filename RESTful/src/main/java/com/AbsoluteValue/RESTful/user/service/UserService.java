package com.AbsoluteValue.RESTful.user.service;

import com.AbsoluteValue.RESTful.user.dto.FindUserResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import java.util.List;

public interface UserService {

    /**
     * 회원 가입 Service
     *
     * @param signUpUserRequest
     * @return void
     */
    void signUpUser(SignUpUserRequest signUpUserRequest);

    /**
     * 회원 정보 단건 조회 Service
     *
     * @param id
     * @return FindUserResponse
     */
    FindUserResponse findUser(String id);

    /**
     * 회원 정보 다건 조회 Service
     *
     * @return List<FindUserResponse>
     */
    List<FindUserResponse> findUsers();
}

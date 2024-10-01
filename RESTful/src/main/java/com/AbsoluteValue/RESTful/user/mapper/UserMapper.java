package com.AbsoluteValue.RESTful.user.mapper;

import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.vo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    /**
     * 회원 가입 Mapper
     * @param signUpUserRequest
     * @return int
     */
    int signUpUser(SignUpUserRequest signUpUserRequest);

    /**
     * 회원 정보 조회 Mapper
     * @param id
     * @return int
     */
    User getUserInfo(String id);
}

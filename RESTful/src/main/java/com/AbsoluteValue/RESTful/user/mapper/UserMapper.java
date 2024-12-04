package com.AbsoluteValue.RESTful.user.mapper;

import com.AbsoluteValue.RESTful.user.vo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 회원 가입 Mapper
     * @param user
     * @return int
     */
    int signUpUser(User user);

    /**
     * 회원 정보 단건 조회 Mapper
     * @param id
     * @return int
     */
    User getUser(String id);

    /**
     * 회원 정보 다건 조회 Mapper
     * @return List<User>
     */
    List<User> getUsers();
}
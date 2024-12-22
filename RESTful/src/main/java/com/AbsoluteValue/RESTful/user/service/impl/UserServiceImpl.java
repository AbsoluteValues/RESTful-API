package com.AbsoluteValue.RESTful.user.service.impl;

import com.AbsoluteValue.RESTful.user.dto.GetUserResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.entity.User;
import com.AbsoluteValue.RESTful.user.mapper.UserMapper;
import com.AbsoluteValue.RESTful.user.repository.UserRepository;
import com.AbsoluteValue.RESTful.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional()
    public void signUpUser(SignUpUserRequest signUpUserRequest) {
        User user = User.builder()
                .id(signUpUserRequest.id())
                .password(signUpUserRequest.password())
                .nickname(signUpUserRequest.nickname())
                .build();
        userRepository.save(user);
    }

    @Override
    public GetUserResponse getUser(String id) {
        User user = userMapper.getUser(id);
        return new GetUserResponse(user.getId(), user.getNickname());
    }

    @Override
    public List<User> getUsers() {
        List<User> users = userMapper.getUsers();
        return users;
    }
}

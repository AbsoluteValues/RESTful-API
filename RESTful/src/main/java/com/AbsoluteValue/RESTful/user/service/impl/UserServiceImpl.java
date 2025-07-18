package com.AbsoluteValue.RESTful.user.service.impl;

import com.AbsoluteValue.RESTful.common.converter.ConverterUtil;
import com.AbsoluteValue.RESTful.common.exception.CustomException;
import com.AbsoluteValue.RESTful.common.exception.ErrorCode;
import com.AbsoluteValue.RESTful.user.converter.UserToDtoConverter;
import com.AbsoluteValue.RESTful.user.dto.FindUserResponse;
import com.AbsoluteValue.RESTful.user.dto.SignUpUserRequest;
import com.AbsoluteValue.RESTful.user.entity.User;
import com.AbsoluteValue.RESTful.user.repository.UserRepository;
import com.AbsoluteValue.RESTful.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserToDtoConverter userToDtoConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signUpUser(SignUpUserRequest signUpUserRequest) {
        if (userRepository.existsUserById(signUpUserRequest.id())) {
            throw new CustomException(ErrorCode.SAVE_FAILED);
        }
        User user = User.builder()
                .id(signUpUserRequest.id())
                .password(signUpUserRequest.password())
                .nickname(signUpUserRequest.nickname())
                .build();
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public FindUserResponse findUser(String id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        return userToDtoConverter.convert(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindUserResponse> findUsers() {
        List<User> users = userRepository.findAll();
        return ConverterUtil.convertList(users, userToDtoConverter);
    }
}

package com.AbsoluteValue.RESTful.user.converter;

import com.AbsoluteValue.RESTful.common.converter.Converter;
import com.AbsoluteValue.RESTful.user.dto.FindUserResponse;
import com.AbsoluteValue.RESTful.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserToDtoConverter implements Converter<User, FindUserResponse> {

    @Override
    public FindUserResponse convert(User source) {
        return new FindUserResponse(source.getId(), source.getNickname());
    }
}

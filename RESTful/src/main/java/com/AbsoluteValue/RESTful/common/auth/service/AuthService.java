package com.AbsoluteValue.RESTful.common.auth.service;

import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import com.AbsoluteValue.RESTful.common.auth.dto.LoginRequest;

public interface AuthService {
    JwtToken login(LoginRequest loginRequest);
}

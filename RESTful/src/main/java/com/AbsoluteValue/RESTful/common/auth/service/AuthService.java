package com.AbsoluteValue.RESTful.common.auth.service;

import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import com.AbsoluteValue.RESTful.common.auth.dto.LoginRequest;
import com.AbsoluteValue.RESTful.common.auth.dto.RefreshTokenRequest;

public interface AuthService {
    JwtToken login(LoginRequest loginRequest);

    JwtToken refresh(RefreshTokenRequest request);

}

package com.AbsoluteValue.RESTful.common.auth.service.impl;

import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import com.AbsoluteValue.RESTful.common.auth.dto.LoginRequest;
import com.AbsoluteValue.RESTful.common.auth.service.AuthService;
import com.AbsoluteValue.RESTful.common.auth.util.JwtUtil;
import com.AbsoluteValue.RESTful.common.response.code.BusinessErrorCode;
import com.AbsoluteValue.RESTful.common.response.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public JwtToken login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.id(), loginRequest.password())
            );
            return jwtUtil.issueTokens(authentication);
        } catch (AuthenticationException exception) {
            throw new BusinessException(BusinessErrorCode.AUTHENTICATION_FAILED);
        }
    }
}

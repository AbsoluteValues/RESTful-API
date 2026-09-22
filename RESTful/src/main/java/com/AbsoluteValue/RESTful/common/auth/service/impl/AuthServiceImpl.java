package com.AbsoluteValue.RESTful.common.auth.service.impl;

import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import com.AbsoluteValue.RESTful.common.auth.dto.LoginRequest;
import com.AbsoluteValue.RESTful.common.auth.entity.RefreshToken;
import com.AbsoluteValue.RESTful.common.auth.repository.RefreshTokenRepository;
import com.AbsoluteValue.RESTful.common.auth.service.AuthService;
import com.AbsoluteValue.RESTful.common.auth.token.IssuedTokens;
import com.AbsoluteValue.RESTful.common.auth.util.JwtUtil;
import com.AbsoluteValue.RESTful.common.auth.util.RefreshTokenHasher;
import com.AbsoluteValue.RESTful.common.response.code.BusinessErrorCode;
import com.AbsoluteValue.RESTful.common.response.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenHasher refreshTokenHasher;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public JwtToken login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.id(), loginRequest.password())
            );
            return issueAndStore(authentication, null).response();
        } catch (AuthenticationException exception) {
            throw new BusinessException(BusinessErrorCode.AUTHENTICATION_FAILED);
        }
    }

    private IssuedTokens issueAndStore(Authentication authentication, String refreshTokenFamilyId) {
        IssuedTokens issuedTokens = jwtUtil.issueTokens(authentication, refreshTokenFamilyId);
        String userId = ((UserDetails) authentication.getPrincipal()).getUsername();
        RefreshToken refreshToken = new RefreshToken(
                issuedTokens.refreshTokenId(),
                userId,
                issuedTokens.refreshTokenFamilyId(),
                refreshTokenHasher.hash(issuedTokens.response().refreshToken()),
                issuedTokens.refreshTokenExpiresAt()
        );
        refreshTokenRepository.save(refreshToken);
        return issuedTokens;
    }

}

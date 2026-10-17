package com.AbsoluteValue.RESTful.common.auth.service.impl;

import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import com.AbsoluteValue.RESTful.common.auth.dto.LoginRequest;
import com.AbsoluteValue.RESTful.common.auth.dto.RefreshTokenRequest;
import com.AbsoluteValue.RESTful.common.auth.repository.RefreshTokenStore;
import com.AbsoluteValue.RESTful.common.auth.service.AuthService;
import com.AbsoluteValue.RESTful.common.auth.token.IssuedTokens;
import com.AbsoluteValue.RESTful.common.auth.token.RefreshTokenState;
import com.AbsoluteValue.RESTful.common.auth.util.JwtUtil;
import com.AbsoluteValue.RESTful.common.auth.util.RefreshTokenHasher;
import com.AbsoluteValue.RESTful.common.config.JwtConfig;
import com.AbsoluteValue.RESTful.common.response.code.BusinessErrorCode;
import com.AbsoluteValue.RESTful.common.response.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenHasher refreshTokenHasher;
    private final RefreshTokenStore refreshTokenStore;

    @Override
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

    @Override
    public JwtToken refresh(RefreshTokenRequest request) {
        Jwt claims = parseRefreshToken(request.refreshToken());
        String familyId = claims.getClaimAsString(JwtConfig.FAMILY_ID_CLAIM);
        RefreshTokenState presented = tokenState(claims, request.refreshToken());
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        } catch (AuthenticationException exception) {
            refreshTokenStore.revoke(presented);
            throw invalidToken();
        }
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        IssuedTokens replacement = jwtUtil.issueTokens(authentication, familyId);
        if (!refreshTokenStore.rotate(presented, tokenState(replacement, userDetails.getUsername()))) {
            throw invalidToken();
        }
        return replacement.response();
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        Jwt claims = parseRefreshToken(request.refreshToken());
        if (!refreshTokenStore.revoke(tokenState(claims, request.refreshToken()))) {
            throw invalidToken();
        }
    }

    private IssuedTokens issueAndStore(Authentication authentication, String refreshTokenFamilyId) {
        IssuedTokens issuedTokens = jwtUtil.issueTokens(authentication, refreshTokenFamilyId);
        String userId = ((UserDetails) authentication.getPrincipal()).getUsername();
        refreshTokenStore.save(tokenState(issuedTokens, userId));
        return issuedTokens;
    }

    private RefreshTokenState tokenState(IssuedTokens issuedTokens, String userId) {
        return new RefreshTokenState(
                issuedTokens.refreshTokenId(), userId, issuedTokens.refreshTokenFamilyId(),
                refreshTokenHasher.hash(issuedTokens.response().refreshToken()),
                issuedTokens.refreshTokenExpiresAt()
        );
    }

    private RefreshTokenState tokenState(Jwt claims, String rawToken) {
        return new RefreshTokenState(
                claims.getId(), claims.getSubject(), claims.getClaimAsString(JwtConfig.FAMILY_ID_CLAIM),
                refreshTokenHasher.hash(rawToken), claims.getExpiresAt()
        );
    }

    private BusinessException invalidToken() {
        return new BusinessException(BusinessErrorCode.TOKEN_INVALID);
    }

    private Jwt parseRefreshToken(String token) {
        try {
            return jwtUtil.parseRefreshToken(token);
        } catch (JwtException exception) {
            throw invalidToken();
        }
    }
}

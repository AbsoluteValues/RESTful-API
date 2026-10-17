package com.AbsoluteValue.RESTful.common.auth.service.impl;

import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import com.AbsoluteValue.RESTful.common.auth.dto.LoginRequest;
import com.AbsoluteValue.RESTful.common.auth.dto.RefreshTokenRequest;
import com.AbsoluteValue.RESTful.common.auth.entity.RefreshToken;
import com.AbsoluteValue.RESTful.common.auth.exception.InvalidJwtException;
import com.AbsoluteValue.RESTful.common.auth.repository.RefreshTokenRepository;
import com.AbsoluteValue.RESTful.common.auth.service.AuthService;
import com.AbsoluteValue.RESTful.common.auth.token.IssuedTokens;
import com.AbsoluteValue.RESTful.common.auth.util.JwtUtil;
import com.AbsoluteValue.RESTful.common.auth.util.RefreshTokenHasher;
import com.AbsoluteValue.RESTful.common.config.JwtConfig;
import com.AbsoluteValue.RESTful.common.response.code.BusinessErrorCode;
import com.AbsoluteValue.RESTful.common.response.exception.BusinessException;
import java.time.Clock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenHasher refreshTokenHasher;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

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

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public JwtToken refresh(RefreshTokenRequest request) {
        Jwt claims = parseRefreshToken(request.refreshToken());
        String familyId = claims.getClaimAsString(JwtConfig.FAMILY_ID_CLAIM);
        Optional<RefreshToken> storedTokenResult = refreshTokenRepository.findByTokenIdForUpdate(claims.getId());
        if (storedTokenResult.isEmpty()) {
            refreshTokenRepository.deleteByFamilyId(familyId);
            throw invalidToken();
        }
        RefreshToken storedToken = storedTokenResult.get();

        if (!storedToken.getUserId().equals(claims.getSubject())
                || !storedToken.getFamilyId().equals(familyId)
                || storedToken.isExpired(clock.instant())
                || !refreshTokenHasher.matches(request.refreshToken(), storedToken.getTokenHash())) {
            refreshTokenRepository.deleteByFamilyId(familyId);
            throw invalidToken();
        }

        refreshTokenRepository.delete(storedToken);
        refreshTokenRepository.flush();
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        } catch (AuthenticationException exception) {
            throw invalidToken();
        }
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        return issueAndStore(authentication, familyId).response();
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        Jwt claims = parseRefreshToken(request.refreshToken());
        RefreshToken storedToken = refreshTokenRepository.findByTokenIdForUpdate(claims.getId())
                .orElseThrow(this::invalidToken);
        String familyId = claims.getClaimAsString(JwtConfig.FAMILY_ID_CLAIM);
        if (!storedToken.getUserId().equals(claims.getSubject())
                || !storedToken.getFamilyId().equals(familyId)
                || !refreshTokenHasher.matches(request.refreshToken(), storedToken.getTokenHash())) {
            throw invalidToken();
        }
        refreshTokenRepository.deleteByFamilyId(familyId);
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

    private BusinessException invalidToken() {
        return new BusinessException(BusinessErrorCode.TOKEN_INVALID);
    }

    private Jwt parseRefreshToken(String token) {
        try {
            return jwtUtil.parseRefreshToken(token);
        } catch (InvalidJwtException exception) {
            throw invalidToken();
        }
    }
}

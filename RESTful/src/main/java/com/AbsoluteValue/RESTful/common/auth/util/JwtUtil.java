package com.AbsoluteValue.RESTful.common.auth.util;

import com.AbsoluteValue.RESTful.common.auth.config.JwtProperties;
import com.AbsoluteValue.RESTful.common.auth.config.JwtConfig;
import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import com.AbsoluteValue.RESTful.common.auth.token.IssuedTokens;
import com.AbsoluteValue.RESTful.common.auth.token.TokenType;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JwtProperties properties;
    private final JwtEncoder encoder;
    private final Clock clock;

    public JwtUtil(JwtProperties properties, JwtEncoder encoder, Clock clock) {
        this.properties = properties;
        this.encoder = encoder;
        this.clock = clock;
    }

    public JwtToken issueTokens(Authentication authentication) {
        return issueTokens(authentication, null).response();
    }

    public IssuedTokens issueTokens(Authentication authentication, String refreshTokenFamilyId) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Instant now = clock.instant();
        Instant accessExpiresAt = now.plus(properties.accessTokenExpiration());
        Instant refreshExpiresAt = now.plus(properties.refreshTokenExpiration());
        String accessTokenId = UUID.randomUUID().toString();
        String refreshTokenId = UUID.randomUUID().toString();
        String familyId = refreshTokenFamilyId == null ? UUID.randomUUID().toString() : refreshTokenFamilyId;

        List<String> authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(principal.getUsername())
                .id(accessTokenId)
                .audience(List.of(properties.audience()))
                .claim(JwtConfig.TOKEN_TYPE_CLAIM, TokenType.ACCESS.name())
                .claim(JwtConfig.AUTHORITIES_CLAIM, authorities)
                .issuedAt(now)
                .expiresAt(accessExpiresAt)
                .build();

        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(principal.getUsername())
                .id(refreshTokenId)
                .audience(List.of(properties.audience()))
                .claim(JwtConfig.TOKEN_TYPE_CLAIM, TokenType.REFRESH.name())
                .claim(JwtConfig.FAMILY_ID_CLAIM, familyId)
                .issuedAt(now)
                .expiresAt(refreshExpiresAt)
                .build();

        JwsHeader headers = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(properties.keyId())
                .type("JWT")
                .build();
        String accessToken = encoder.encode(JwtEncoderParameters.from(headers, accessClaims)).getTokenValue();
        String refreshToken = encoder.encode(JwtEncoderParameters.from(headers, refreshClaims)).getTokenValue();

        JwtToken response = new JwtToken(
                "Bearer",
                accessToken,
                refreshToken,
                properties.accessTokenExpiration().toSeconds()
        );
        return new IssuedTokens(response, refreshTokenId, familyId, refreshExpiresAt);
    }

}

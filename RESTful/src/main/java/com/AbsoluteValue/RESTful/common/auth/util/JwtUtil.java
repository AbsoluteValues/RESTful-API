package com.AbsoluteValue.RESTful.common.auth.util;

import com.AbsoluteValue.RESTful.common.auth.config.JwtConfig;
import com.AbsoluteValue.RESTful.common.auth.config.JwtProperties;
import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import com.AbsoluteValue.RESTful.common.auth.token.TokenType;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties properties;
    private final JwtEncoder encoder;
    private final Clock clock;

    public JwtToken issueAccessToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Instant now = clock.instant();
        List<String> authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(principal.getUsername())
                .id(UUID.randomUUID().toString())
                .audience(List.of(properties.audience()))
                .claim(JwtConfig.TOKEN_TYPE_CLAIM, TokenType.ACCESS.name())
                .claim(JwtConfig.AUTHORITIES_CLAIM, authorities)
                .issuedAt(now)
                .expiresAt(now.plus(properties.accessTokenExpiration()))
                .build();
        JwsHeader headers = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(properties.keyId())
                .type("JWT")
                .build();
        return new JwtToken(
                "Bearer",
                encoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue(),
                properties.accessTokenExpiration().toSeconds()
        );
    }
}

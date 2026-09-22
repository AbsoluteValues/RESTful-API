package com.AbsoluteValue.RESTful.common.auth.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        @NotNull Resource privateKeyLocation,
        @NotNull Resource publicKeyLocation,
        @NotBlank String keyId,
        @NotBlank String issuer,
        @NotBlank String audience,
        @NotNull Duration accessTokenExpiration,
        @NotNull Duration refreshTokenExpiration
) {
    public JwtProperties {
        if (accessTokenExpiration != null
                && (accessTokenExpiration.isZero() || accessTokenExpiration.isNegative())) {
            throw new IllegalArgumentException("Access token expiration must be positive");
        }
        if (refreshTokenExpiration != null
                && (refreshTokenExpiration.isZero() || refreshTokenExpiration.isNegative())) {
            throw new IllegalArgumentException("Refresh token expiration must be positive");
        }
    }
}

package com.AbsoluteValue.RESTful.common.auth.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        @NotNull Resource privateKeyLocation,
        @NotEmpty List<@NotNull Resource> publicKeyLocations,
        @NotEmpty List<@NotBlank String> publicKeyIds,
        @NotBlank String keyId,
        @NotBlank String issuer,
        @NotBlank String audience,
        @NotNull Duration accessTokenExpiration,
        @NotNull Duration refreshTokenExpiration
) {
    public JwtProperties {
        publicKeyLocations = publicKeyLocations == null ? List.of() : List.copyOf(publicKeyLocations);
        publicKeyIds = publicKeyIds == null ? List.of() : List.copyOf(publicKeyIds);
        if (publicKeyLocations.size() != publicKeyIds.size()) {
            throw new IllegalArgumentException("JWT public key locations and key IDs must have the same size");
        }
        if (keyId != null && !publicKeyIds.contains(keyId)) {
            throw new IllegalArgumentException("JWT signing key ID must be included in the public key IDs");
        }
        if (accessTokenExpiration != null && (accessTokenExpiration.isZero() || accessTokenExpiration.isNegative())) {
            throw new IllegalArgumentException("Access token expiration must be positive");
        }
        if (refreshTokenExpiration != null
                && (refreshTokenExpiration.isZero() || refreshTokenExpiration.isNegative())) {
            throw new IllegalArgumentException("Refresh token expiration must be positive");
        }
    }
}

package com.AbsoluteValue.RESTful.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT 토큰 응답")
public record JwtToken(
        @Schema(description = "토큰 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        String grantType,

        @Schema(description = "Access Token", requiredMode = Schema.RequiredMode.REQUIRED)
        String accessToken,

        @Schema(description = "Refresh Token", requiredMode = Schema.RequiredMode.REQUIRED)
        String refreshToken,

        @Schema(description = "Access Token 만료 시간(초)", requiredMode = Schema.RequiredMode.REQUIRED)
        long expiresIn
) {
}

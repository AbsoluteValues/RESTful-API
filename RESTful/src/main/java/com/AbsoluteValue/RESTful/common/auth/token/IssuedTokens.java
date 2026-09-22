package com.AbsoluteValue.RESTful.common.auth.token;

import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import java.time.Instant;

public record IssuedTokens(
        JwtToken response,
        String refreshTokenId,
        String refreshTokenFamilyId,
        Instant refreshTokenExpiresAt
) {
}

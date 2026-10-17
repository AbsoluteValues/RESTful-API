package com.AbsoluteValue.RESTful.common.auth.token;

import java.time.Instant;

public record RefreshTokenState(
        String tokenId,
        String userId,
        String familyId,
        String tokenHash,
        Instant expiresAt
) {
}

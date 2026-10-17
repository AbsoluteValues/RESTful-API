package com.AbsoluteValue.RESTful.common.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_token_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @Column(name = "token_id", length = 36, nullable = false)
    private String tokenId;

    @Column(name = "user_id", length = 10, nullable = false)
    private String userId;

    @Column(name = "family_id", length = 36, nullable = false, unique = true)
    private String familyId;

    @Column(name = "token_hash", length = 64, nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public RefreshToken(String tokenId, String userId, String familyId, String tokenHash, Instant expiresAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.familyId = familyId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }
}

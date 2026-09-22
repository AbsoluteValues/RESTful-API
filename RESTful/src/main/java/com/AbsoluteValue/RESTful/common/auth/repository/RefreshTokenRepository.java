package com.AbsoluteValue.RESTful.common.auth.repository;

import com.AbsoluteValue.RESTful.common.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenId(String tokenId);
}

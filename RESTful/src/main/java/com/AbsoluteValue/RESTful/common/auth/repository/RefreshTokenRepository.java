package com.AbsoluteValue.RESTful.common.auth.repository;

import com.AbsoluteValue.RESTful.common.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenId(String tokenId);

    @Modifying
    @Query("delete from RefreshToken token where token.familyId = :familyId")
    void deleteByFamilyId(@Param("familyId") String familyId);
}

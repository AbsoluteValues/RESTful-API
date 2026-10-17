package com.AbsoluteValue.RESTful.common.auth.repository;

import com.AbsoluteValue.RESTful.common.auth.token.RefreshTokenState;

public interface RefreshTokenStore {

    void save(RefreshTokenState token);

    boolean rotate(RefreshTokenState presented, RefreshTokenState replacement);

    boolean revoke(RefreshTokenState presented);
}

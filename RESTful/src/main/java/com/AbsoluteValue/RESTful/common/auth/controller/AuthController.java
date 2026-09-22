package com.AbsoluteValue.RESTful.common.auth.controller;

import com.AbsoluteValue.RESTful.common.auth.dto.JwtToken;
import com.AbsoluteValue.RESTful.common.auth.dto.LoginRequest;
import com.AbsoluteValue.RESTful.common.auth.dto.RefreshTokenRequest;
import com.AbsoluteValue.RESTful.common.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtToken> login(@Valid @RequestBody LoginRequest request) {
        return tokenResponse(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtToken> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return tokenResponse(authService.refresh(request));
    }

    private ResponseEntity<JwtToken> tokenResponse(JwtToken token) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(token);
    }
}

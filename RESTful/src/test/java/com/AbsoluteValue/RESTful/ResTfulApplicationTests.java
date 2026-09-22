package com.AbsoluteValue.RESTful;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.AbsoluteValue.RESTful.common.auth.config.JwtConfig;
import com.AbsoluteValue.RESTful.common.auth.config.JwtProperties;
import com.AbsoluteValue.RESTful.common.auth.token.TokenType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ResTfulApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtProperties jwtProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void loginReturnsRs256TokenPair() throws Exception {
        signUp("testuser", "password123", "tester");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"id":"testuser","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grantType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andReturn();

        String accessToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
        JsonNode header = objectMapper.readTree(
                java.util.Base64.getUrlDecoder().decode(accessToken.substring(0, accessToken.indexOf('.')))
        );
        assertEquals("RS256", header.get("alg").asText());
        assertEquals("test-key-1", header.get("kid").asText());
    }

    @Test
    void invalidCredentialsReturnUnauthorized() throws Exception {
        signUp("testuser", "password123", "tester");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"id":"testuser","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("684"));
    }

    @Test
    void profileRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/user/profile/testuser"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("007"))
                .andExpect(header().string("WWW-Authenticate", "Bearer"));
    }

    @Test
    void invalidBearerTokenReturnsStandardChallenge() throws Exception {
        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("007"));
    }

    @Test
    void expiredAccessTokenIsRejected() throws Exception {
        Instant now = Instant.now();
        String token = signedToken(jwtProperties.audience(), now.minusSeconds(7200), now.minusSeconds(3600));

        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessTokenWithWrongAudienceIsRejected() throws Exception {
        Instant now = Instant.now();
        String token = signedToken("another-api", now, now.plusSeconds(900));

        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void profileCanBeAccessedWithOwnToken() throws Exception {
        signUp("testuser", "password123", "tester");
        String token = signedToken(jwtProperties.audience(), Instant.now(), Instant.now().plusSeconds(900));

        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("testuser"));
    }

    @Test
    void userCannotReadAnotherUsersProfile() throws Exception {
        signUp("testuser", "password123", "tester");
        signUp("otheruser", "password123", "other");
        String token = signedToken(jwtProperties.audience(), Instant.now(), Instant.now().plusSeconds(900));

        mockMvc.perform(get("/api/user/profile/otheruser")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("006"));
    }

    private void signUp(String id, String password, String nickname) throws Exception {
        mockMvc.perform(post("/api/user")
                        .contentType("application/json")
                        .content("""
                                {"id":"%s","password":"%s","nickname":"%s"}
                                """.formatted(id, password, nickname)))
                .andExpect(status().isCreated());
    }

    private String signedToken(String audience, Instant issuedAt, Instant expiresAt) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject("testuser")
                .id(UUID.randomUUID().toString())
                .audience(List.of(audience))
                .claim(JwtConfig.TOKEN_TYPE_CLAIM, TokenType.ACCESS.name())
                .claim(JwtConfig.AUTHORITIES_CLAIM, List.of("ROLE_USER"))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();
        JwsHeader headers = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(jwtProperties.keyId())
                .type("JWT")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }
}

package com.AbsoluteValue.RESTful;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.AbsoluteValue.RESTful.common.auth.properties.JwtProperties;
import com.AbsoluteValue.RESTful.common.auth.token.TokenType;
import com.AbsoluteValue.RESTful.common.config.JwtConfig;
import com.AbsoluteValue.RESTful.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityApiIntegrationTests extends JwtIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    @Qualifier("refreshTokenDecoder")
    private JwtDecoder refreshTokenDecoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void openApiUsesTheCurrentOrigin() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servers[0].url").value("/"));
    }

    @Test
    void loginReturnsJwtToken() throws Exception {
        signUp("testuser", "password123", "tester");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": "testuser",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grantType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andReturn();

        String accessToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken")
                .asText();
        JsonNode jwtHeader = objectMapper.readTree(
                java.util.Base64.getUrlDecoder().decode(accessToken.substring(0, accessToken.indexOf('.')))
        );
        assertEquals("RS256", jwtHeader.get("alg").asText());
        assertEquals("test-key-1", jwtHeader.get("kid").asText());
    }

    @Test
    void profileRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/user/profile/testuser"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("007"))
                .andExpect(header().string("WWW-Authenticate", "Bearer"));
    }

    @Test
    void profileCanBeAccessedWithOwnToken() throws Exception {
        signUp("testuser", "password123", "tester");
        Tokens tokens = login("testuser", "password123");

        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("testuser"))
                .andExpect(jsonPath("$.data.nickname").value("tester"));
    }

    @Test
    void refreshRotatesTokenAndRejectsReuse() throws Exception {
        signUp("testuser", "password123", "tester");
        Tokens original = login("testuser", "password123");

        MvcResult refreshResult = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content(refreshRequest(original.refreshToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        JsonNode refreshedJson = objectMapper.readTree(refreshResult.getResponse().getContentAsString());
        String rotatedRefreshToken = refreshedJson.get("refreshToken").asText();
        assertNotEquals(original.refreshToken(), rotatedRefreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content(refreshRequest(original.refreshToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("683"));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content(refreshRequest(rotatedRefreshToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("683"));
    }

    @Test
    void expiredAccessTokenIsRejected() throws Exception {
        Instant now = Instant.now();
        String expiredToken = signedToken(
                TokenType.ACCESS,
                jwtProperties.issuer(),
                List.of(jwtProperties.audience()),
                now.minusSeconds(7200),
                now.minusSeconds(3600)
        );

        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("007"));
    }

    @Test
    void refreshDecoderRejectsExpiredToken() {
        Instant now = Instant.now();
        String expiredToken = signedToken(
                TokenType.REFRESH,
                jwtProperties.issuer(),
                List.of(jwtProperties.audience()),
                now.minusSeconds(7200),
                now.minusSeconds(3600)
        );

        assertThrows(JwtException.class, () -> refreshTokenDecoder.decode(expiredToken));
    }

    @Test
    void forgedAccessTokenIsRejected() throws Exception {
        signUp("testuser", "password123", "tester");
        Tokens tokens = login("testuser", "password123");

        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer " + forgeSignature(tokens.accessToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("007"));
    }

    @Test
    void accessTokenWithWrongAudienceIsRejected() throws Exception {
        Instant now = Instant.now();
        String wrongAudienceToken = signedToken(
                TokenType.ACCESS,
                jwtProperties.issuer(),
                List.of("another-api"),
                now,
                now.plusSeconds(900)
        );

        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer " + wrongAudienceToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("007"));
    }

    @Test
    void accessTokenSignedWithPreviousRotationKeyIsAccepted() throws Exception {
        signUp("testuser", "password123", "tester");
        Instant now = Instant.now();
        String previousKeyToken = signedToken(
                previousKeyEncoder(),
                "test-key-0",
                TokenType.ACCESS,
                jwtProperties.issuer(),
                List.of(jwtProperties.audience()),
                now,
                now.plusSeconds(900)
        );

        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer " + previousKeyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("testuser"));
    }

    @Test
    void accessTokenCannotBeUsedForRefresh() throws Exception {
        signUp("testuser", "password123", "tester");
        Tokens tokens = login("testuser", "password123");

        assertThrows(JwtException.class, () -> refreshTokenDecoder.decode(tokens.accessToken()));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content(refreshRequest(tokens.accessToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("683"));
    }

    @Test
    void refreshTokenCannotAccessProtectedApi() throws Exception {
        signUp("testuser", "password123", "tester");
        Tokens tokens = login("testuser", "password123");

        mockMvc.perform(get("/api/user/profile/testuser")
                        .header("Authorization", "Bearer " + tokens.refreshToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("007"));
    }

    @Test
    void invalidCredentialsReturnUnauthorized() throws Exception {
        signUp("testuser", "password123", "tester");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": "testuser",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("684"));
    }

    @Test
    void signupStoresEncodedPassword() throws Exception {
        signUp("testuser", "password123", "tester");

        String storedPassword = userRepository.findUserById("testuser").orElseThrow().getPassword();
        assertNotEquals("password123", storedPassword);
        assertTrue(passwordEncoder.matches("password123", storedPassword));
    }

    @Test
    void duplicateSignupIsRejectedWithoutReplacingTheUser() throws Exception {
        signUp("testuser", "password123", "tester");
        String originalPassword = userRepository.findUserById("testuser").orElseThrow().getPassword();

        mockMvc.perform(post("/api/v1/user")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": "testuser",
                                  "password": "another-password",
                                  "nickname": "another"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("096"));

        assertEquals(1, userRepository.count());
        assertEquals(originalPassword, userRepository.findUserById("testuser").orElseThrow().getPassword());
    }

    @Test
    void signupRejectsPasswordLongerThanBcryptByteLimit() throws Exception {
        String password = "a".repeat(73);

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": "testuser",
                                  "password": "%s",
                                  "nickname": "tester"
                                }
                                """.formatted(password)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signupRejectsNonAsciiAndWhitespacePasswords() throws Exception {
        for (String password : List.of("비밀번호123!", "abc 123!")) {
            mockMvc.perform(post("/api/v1/user")
                            .with(csrf())
                            .contentType("application/json")
                            .content("""
                                    {
                                      "id": "testuser",
                                      "password": "%s",
                                      "nickname": "tester"
                                    }
                                    """.formatted(password)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void signupAcceptsAsciiLettersNumbersAndSymbols() throws Exception {
        signUp("testuser", "Az09!@#-$%", "tester");
    }

    @Test
    void userCannotReadAnotherUsersProfile() throws Exception {
        signUp("testuser", "password123", "tester");
        signUp("otheruser", "password123", "other");
        Tokens tokens = login("testuser", "password123");

        mockMvc.perform(get("/api/user/profile/otheruser")
                        .header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("006"));
    }

    @Test
    void logoutRevokesRefreshToken() throws Exception {
        signUp("testuser", "password123", "tester");
        Tokens tokens = login("testuser", "password123");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType("application/json")
                        .content(refreshRequest(tokens.refreshToken())))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content(refreshRequest(tokens.refreshToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("683"));
    }

    private void signUp(String id, String password, String nickname) throws Exception {
        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": "%s",
                                  "password": "%s",
                                  "nickname": "%s"
                                }
                                """.formatted(id, password, nickname)))
                .andExpect(status().isCreated());
    }

    private Tokens login(String id, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": "%s",
                                  "password": "%s"
                                }
                                """.formatted(id, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return new Tokens(json.get("accessToken").asText(), json.get("refreshToken").asText());
    }

    private String refreshRequest(String token) {
        return """
                {
                  "refreshToken": "%s"
                }
                """.formatted(token);
    }

    private String signedToken(TokenType tokenType, String issuer, List<String> audience,
                               Instant issuedAt, Instant expiresAt) {
        return signedToken(jwtEncoder, jwtProperties.keyId(), tokenType, issuer, audience, issuedAt, expiresAt);
    }

    private String signedToken(JwtEncoder encoder, String keyId, TokenType tokenType, String issuer,
                               List<String> audience, Instant issuedAt, Instant expiresAt) {
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("testuser")
                .id(java.util.UUID.randomUUID().toString())
                .audience(audience)
                .claim(JwtConfig.TOKEN_TYPE_CLAIM, tokenType.name())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt);
        if (tokenType == TokenType.ACCESS) {
            claims.claim(JwtConfig.AUTHORITIES_CLAIM, List.of("ROLE_USER"));
        } else {
            claims.claim(JwtConfig.FAMILY_ID_CLAIM, java.util.UUID.randomUUID().toString());
        }

        JwsHeader headers = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(keyId)
                .type("JWT")
                .build();
        return encoder.encode(JwtEncoderParameters.from(headers, claims.build())).getTokenValue();
    }

    private String forgeSignature(String token) {
        String[] parts = token.split("\\.");
        char replacement = parts[2].charAt(0) == 'A' ? 'B' : 'A';
        parts[2] = replacement + parts[2].substring(1);
        return String.join(".", parts);
    }

    private record Tokens(String accessToken, String refreshToken) {
    }

}

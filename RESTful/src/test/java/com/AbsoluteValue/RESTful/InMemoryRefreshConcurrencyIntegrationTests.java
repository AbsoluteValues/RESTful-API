package com.AbsoluteValue.RESTful;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.AbsoluteValue.RESTful.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InMemoryRefreshConcurrencyIntegrationTests extends JwtIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void concurrentRefreshAllowsOneRequestAndRevokesTheTokenFamily() throws Exception {
        signUp();
        String refreshToken = loginRefreshToken();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            List<CompletableFuture<RefreshResult>> futures = List.of(
                    refreshAsync(executor, ready, start, refreshToken),
                    refreshAsync(executor, ready, start, refreshToken)
            );
            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();

            List<RefreshResult> results = new ArrayList<>();
            for (CompletableFuture<RefreshResult> future : futures) {
                results.add(future.get(10, TimeUnit.SECONDS));
            }

            assertEquals(List.of(200, 401), results.stream().map(RefreshResult::status).sorted().toList());
            String rotatedToken = results.stream()
                    .filter(result -> result.status() == 200)
                    .map(RefreshResult::refreshToken)
                    .findFirst()
                    .orElseThrow();

            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType("application/json")
                            .content(refreshRequest(rotatedToken)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("683"));
        } finally {
            executor.shutdownNow();
        }
    }

    private CompletableFuture<RefreshResult> refreshAsync(ExecutorService executor, CountDownLatch ready,
                                                           CountDownLatch start, String refreshToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ready.countDown();
                start.await(5, TimeUnit.SECONDS);
                MvcResult result = mockMvc.perform(post("/api/v1/auth/refresh")
                                .contentType("application/json")
                                .content(refreshRequest(refreshToken)))
                        .andReturn();
                JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
                String rotatedToken = body.has("refreshToken") ? body.get("refreshToken").asText() : null;
                return new RefreshResult(result.getResponse().getStatus(), rotatedToken);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        }, executor);
    }

    private void signUp() throws Exception {
        mockMvc.perform(post("/api/v1/user")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": "raceuser",
                                  "password": "password123",
                                  "nickname": "racer"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    private String loginRefreshToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": "raceuser",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("refreshToken")
                .asText();
    }

    private String refreshRequest(String token) {
        return """
                {
                  "refreshToken": "%s"
                }
                """.formatted(token);
    }

    private record RefreshResult(int status, String refreshToken) {
    }
}

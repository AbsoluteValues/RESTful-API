package com.AbsoluteValue.RESTful.common.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class MetricsSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain metricsFilterChain(HttpSecurity http, Environment environment,
                                                   PasswordEncoder passwordEncoder) {
        String username = environment.getRequiredProperty("spring.security.user.name");
        String password = environment.getRequiredProperty("spring.security.user.password");
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Prometheus credentials must not be blank");
        }

        var metricsUser = new InMemoryUserDetailsManager(User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("METRICS")
                .build());
        var metricsProvider = new DaoAuthenticationProvider(metricsUser);
        metricsProvider.setPasswordEncoder(passwordEncoder);

        return http
                .securityMatcher(EndpointRequest.to("prometheus"))
                .authenticationManager(new ProviderManager(metricsProvider))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("METRICS"))
                .httpBasic(basic -> basic.authenticationEntryPoint((request, response, exception) -> {
                    response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"metrics\"");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                }))
                .build();
    }
}

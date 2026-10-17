package com.AbsoluteValue.RESTful.common.config;

import com.AbsoluteValue.RESTful.common.auth.properties.JwtProperties;
import com.AbsoluteValue.RESTful.common.auth.token.TokenType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    public static final String TOKEN_TYPE_CLAIM = "token_type";
    public static final String AUTHORITIES_CLAIM = "authorities";
    public static final String FAMILY_ID_CLAIM = "family_id";

    @Bean
    public JWKSource<SecurityContext> jwtJwkSource(JwtProperties properties) throws IOException {
        RSAPrivateKey privateKey;
        try (InputStream input = properties.privateKeyLocation().getInputStream()) {
            privateKey = RsaKeyConverters.pkcs8().convert(input);
        }

        List<JWK> keys = new ArrayList<>();
        for (int index = 0; index < properties.publicKeyLocations().size(); index++) {
            RSAPublicKey publicKey;
            try (InputStream input = properties.publicKeyLocations().get(index).getInputStream()) {
                publicKey = RsaKeyConverters.x509().convert(input);
            }
            RSAKey.Builder builder = new RSAKey.Builder(publicKey)
                    .keyID(properties.publicKeyIds().get(index))
                    .algorithm(JWSAlgorithm.RS256);
            if (properties.keyId().equals(properties.publicKeyIds().get(index))) {
                if (!privateKey.getModulus().equals(publicKey.getModulus())) {
                    throw new IllegalArgumentException("JWT signing private key does not match its public key");
                }
                builder.privateKey(privateKey);
            }
            keys.add(builder.build());
        }
        return new ImmutableJWKSet<>(new JWKSet(keys));
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwtJwkSource) {
        return new NimbusJwtEncoder(jwtJwkSource);
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwtJwkSource, JwtProperties properties) {
        return tokenDecoder(jwtJwkSource, properties, TokenType.ACCESS);
    }

    @Bean
    public JwtDecoder refreshTokenDecoder(JWKSource<SecurityContext> jwtJwkSource, JwtProperties properties) {
        return tokenDecoder(jwtJwkSource, properties, TokenType.REFRESH);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_CLAIM);
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }

    private JwtDecoder tokenDecoder(JWKSource<SecurityContext> jwkSource, JwtProperties properties,
                                    TokenType tokenType) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSource(jwkSource)
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .build();
        OAuth2TokenValidator<Jwt> defaults = JwtValidators.createDefaultWithIssuer(properties.issuer());
        OAuth2TokenValidator<Jwt> audience = new JwtClaimValidator<List<String>>(
                JwtClaimNames.AUD,
                claim -> claim != null && claim.contains(properties.audience())
        );
        OAuth2TokenValidator<Jwt> type = new JwtClaimValidator<>(TOKEN_TYPE_CLAIM, tokenType.name()::equals);
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaults, audience, type));
        return decoder;
    }
}

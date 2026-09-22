package com.AbsoluteValue.RESTful.common.auth.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public JWKSource<SecurityContext> jwtJwkSource(JwtProperties properties) throws IOException {
        RSAPrivateKey privateKey;
        RSAPublicKey publicKey;
        try (InputStream input = properties.privateKeyLocation().getInputStream()) {
            privateKey = RsaKeyConverters.pkcs8().convert(input);
        }
        try (InputStream input = properties.publicKeyLocation().getInputStream()) {
            publicKey = RsaKeyConverters.x509().convert(input);
        }
        if (!privateKey.getModulus().equals(publicKey.getModulus())) {
            throw new IllegalArgumentException("JWT signing private key does not match its public key");
        }
        RSAKey key = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(properties.keyId())
                .algorithm(JWSAlgorithm.RS256)
                .build();
        return new ImmutableJWKSet<>(new JWKSet(key));
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwtJwkSource) {
        return new NimbusJwtEncoder(jwtJwkSource);
    }

}

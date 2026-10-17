package com.AbsoluteValue.RESTful;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

abstract class JwtIntegrationTestSupport {

    private static final TestKeyMaterial CURRENT_KEY = TestKeyMaterial.generate("current");
    private static final TestKeyMaterial PREVIOUS_KEY = TestKeyMaterial.generate("previous");

    @DynamicPropertySource
    static void registerJwtKeyLocations(DynamicPropertyRegistry registry) {
        registry.add("jwt.private-key-location", () -> CURRENT_KEY.privateKeyLocation());
        registry.add("jwt.public-key-locations", () -> String.join(",",
                CURRENT_KEY.publicKeyLocation(), PREVIOUS_KEY.publicKeyLocation()));
    }

    protected JwtEncoder previousKeyEncoder() {
        return NimbusJwtEncoder.withKeyPair(PREVIOUS_KEY.publicKey(), PREVIOUS_KEY.privateKey())
                .algorithm(SignatureAlgorithm.RS256)
                .jwkPostProcessor(builder -> builder.keyID("test-key-0"))
                .build();
    }

    private record TestKeyMaterial(
            java.security.interfaces.RSAPublicKey publicKey,
            java.security.interfaces.RSAPrivateKey privateKey,
            String publicKeyLocation,
            String privateKeyLocation
    ) {
        private static TestKeyMaterial generate(String name) {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                KeyPair keyPair = generator.generateKeyPair();
                Path directory = Files.createTempDirectory("restful-jwt-test-");
                Path publicKeyPath = writePem(directory.resolve(name + "-public.pem"),
                        "PUBLIC KEY", keyPair.getPublic().getEncoded());
                Path privateKeyPath = writePem(directory.resolve(name + "-private.pem"),
                        "PRIVATE KEY", keyPair.getPrivate().getEncoded());
                return new TestKeyMaterial(
                        (java.security.interfaces.RSAPublicKey) keyPair.getPublic(),
                        (java.security.interfaces.RSAPrivateKey) keyPair.getPrivate(),
                        publicKeyPath.toUri().toString(),
                        privateKeyPath.toUri().toString()
                );
            } catch (NoSuchAlgorithmException | IOException exception) {
                throw new ExceptionInInitializerError(exception);
            }
        }

        private static Path writePem(Path path, String type, byte[] encoded) throws IOException {
            String body = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII))
                    .encodeToString(encoded);
            Files.writeString(path, "-----BEGIN " + type + "-----\n"
                    + body + "\n-----END " + type + "-----\n", StandardCharsets.US_ASCII);
            path.toFile().deleteOnExit();
            return path;
        }
    }
}

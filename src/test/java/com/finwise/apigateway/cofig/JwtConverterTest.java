package com.finwise.apigateway.cofig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtConverterTest {

    private JwtConverter jwtConverter;

    @BeforeEach
    void convert() {
        jwtConverter = new JwtConverter();
    }

    @Test
    void shouldConvertJwtWithRoles() {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("ADMIN", "USER"));

        Jwt jwt = new Jwt(
                "tokenValue",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("realm_access", realmAccess)
        );

        Mono<AbstractAuthenticationToken> result = jwtConverter.convert(jwt);

        StepVerifier.create(result)
                .assertNext(token -> {
                    Collection<? extends GrantedAuthority> authorities = token.getAuthorities();
                    assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyAuthoritiesIfNoRoles() {
        Jwt jwt = new Jwt(
                "fake-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"), // headers
                Map.of("realm_access", Map.of("roles", List.of())) // claims valides mais sans rôles
        );

        Mono<AbstractAuthenticationToken> result = jwtConverter.convert(jwt);

        StepVerifier.create(result)
                .assertNext(token -> {
                    assertThat(token.getAuthorities()).isEmpty();
                })
                .verifyComplete();
    }



}

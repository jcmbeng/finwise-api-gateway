package com.finwise.apigateway.cofig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class JwtConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private static final Logger logger = LoggerFactory.getLogger(JwtConverter.class);

    public JwtConverter() {

        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

    }

    @Override
    public Mono<AbstractAuthenticationToken> convert(final Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }

    private Collection<GrantedAuthority> extractAuthorities(final Jwt jwt) {

        List<String> roles = Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
                .map(claims -> (List<String>) claims.get("roles"))
                .orElse(List.of());

        logger.debug("roles: {}", roles);

        return roles.stream()
                .map(role -> "ROLE_" + role) // Important: préfixer avec "ROLE_"
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

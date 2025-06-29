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


/**
 * Convertisseur JWT personnalisé qui extrait les rôles depuis le claim "realm_access"
 * et les transforme en autorités Spring Security.
 *
 * Cette classe implémente le pattern Converter de Spring pour transformer un JWT
 * en token d'authentification contenant les autorités appropriées.
 *
 * @author Jean-Claude MBENG
 */
public class JwtConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    /** Logger pour tracer les opérations de conversion */
    private static final Logger logger = LoggerFactory.getLogger(JwtConverter.class);

    /**
     * Constructeur par défaut.
     *
     * Note: Le JwtGrantedAuthoritiesConverter est créé mais non utilisé dans l'implémentation actuelle.
     * Cette instance pourrait être utilisée comme fallback ou pour une configuration alternative.
     */
    public JwtConverter() {
        // Configuration d'un convertisseur d'autorités par défaut (non utilisé actuellement)
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    }

    /**
     * Convertit un JWT en token d'authentification Spring Security.
     *
     * Cette méthode extrait les rôles du JWT et les transforme en autorités
     * pour créer un JwtAuthenticationToken utilisable par Spring Security.
     *
     * @param jwt Le token JWT à convertir, ne doit pas être null
     * @return Un Mono contenant le token d'authentification avec les autorités extraites
     * @throws IllegalArgumentException si le JWT est null
     */
    @Override
    public Mono<AbstractAuthenticationToken> convert(final Jwt jwt) {
        // Extraction des autorités depuis le JWT
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

        // Création et retour du token d'authentification
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }

    /**
     * Extrait les autorités (rôles) depuis le claim "realm_access" du JWT.
     *
     * Cette méthode recherche les rôles dans la structure suivante du JWT :
     *  {
     *   "realm_access" : {
     *     "roles" : ["role1", "role2", ...]
     *   }
     *  }
     *
     * Chaque rôle est préfixé avec "ROLE_" pour respecter les conventions Spring Security.
     *
     * @param jwt Le token JWT contenant les claims, ne doit pas être null
     * @return Une collection d'autorités extraites du JWT, peut être vide, mais jamais null
     */
    private Collection<GrantedAuthority> extractAuthorities(final Jwt jwt) {
        // Extraction des rôles depuis le claim "realm_access.roles"
        // Utilisation d'Optional pour gérer les cas où le claim n'existe pas
        List<String> roles = Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
                .map(claims -> (List<String>) claims.get("roles"))
                .orElse(List.of()); // Liste vide par défaut si aucun rôle trouvé

        // Log des rôles extraits pour le débogage
        logger.debug("Rôles extraits du JWT: {}", roles);

        // Transformation des rôles en autorités Spring Security
        return roles.stream()
                .map(role -> "ROLE_" + role) // Préfixale obligatoire pour Spring Security
                .map(SimpleGrantedAuthority::new) // Création des objets GrantedAuthority
                .collect(Collectors.toList());
    }
}
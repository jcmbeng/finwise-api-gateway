package com.finwise.apigateway.cofig;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configuration de sécurité Spring Security pour une application WebFlux réactive.
 *
 * Cette classe configure :
 * - L'autorisation basée sur les rôles avec des chemins configurables
 * - L'authentification OAuth2 JWT avec convertisseur personnalisé
 * - La configuration CORS pour les requêtes cross-origin
 *
 * La configuration utilise des propriétés externes pour définir les chemins
 * et les rôles requis, permettant une gestion flexible de la sécurité.
 *
 * @author Jean-Claude MBENG
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres de sécurité pour l'application WebFlux.
     *
     * Cette méthode définit les règles d'autorisation en trois niveaux :
     * 1. Chemins publics (permitAll) - aucune authentification requise
     * 2. Chemins protégés par rôles - authentification et autorisation requises
     * 3. Tous les autres chemins - accès refusé par défaut (principe de sécurité)
     *
     * L'authentification se base sur des tokens JWT OAuth2 avec un convertisseur
     * personnalisé pour extraire les rôles depuis les claims Keycloak.
     *
     * @param http L'objet ServerHttpSecurity pour configurer la sécurité
     * @param config Les propriétés de configuration des chemins et rôles
     * @return La chaîne de filtres de sécurité configurée
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, SecurityPathsProperties config) {
        // Configuration des règles d'autorisation
        http.authorizeExchange(exchange -> {

            // Configuration des chemins publics (pas d'authentification requise)
            // Ces chemins sont généralement utilisés pour les endpoints de santé,
            // la documentation API, ou les ressources statiques
            config.getPermitAllPaths().forEach(path ->
                    exchange.pathMatchers(path).permitAll()
            );

            // Configuration des chemins protégés par rôles
            // Chaque route définit un chemin et les rôles autorisés à y accéder
            config.getRoleBasedRoutes().forEach(route -> {
                // Transformation des rôles en ajoutant le préfixe "ROLE_"
                // requis par Spring Security pour la reconnaissance des autorités
                String[] roles = route.getRoles().stream()
                        .map(role -> "ROLE_" + role) // Préfixage obligatoire pour Spring Security
                        .toArray(String[]::new);

                // Application de la restriction d'accès basée sur les rôles
                exchange.pathMatchers(route.getPath()).hasAnyAuthority(roles);
            });

            // Principe de sécurité par défaut : refuser l'accès à tout le reste
            // Cela garantit qu'aucun endpoint non configuré ne soit accessible
            exchange.anyExchange().denyAll();
        });

        // Configuration du serveur de ressources OAuth2 avec JWT
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                        // Utilisation du convertisseur JWT personnalisé pour extraire les rôles
                        // depuis les claims "realm_access.roles" de Keycloak
                        .jwtAuthenticationConverter(new JwtConverter())
                )
        );

        return http.build();
    }

    /**
     * Configure la politique CORS (Cross-Origin Resource Sharing) de l'application.
     *
     * Cette configuration permet aux applications frontend d'accéder à l'API
     * depuis différents domaines. La configuration actuelle est permissive
     * et autorise tous les origins, méthodes et headers.
     *
     * ATTENTION : Cette configuration est adaptée pour le développement.
     * En production, il est recommandé de restreindre les origins autorisés
     * à des domaines spécifiques pour des raisons de sécurité.
     *
     * @return La source de configuration CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Autoriser tous les domaines d'origine (à restreindre en production)
        configuration.addAllowedOriginPattern("*");

        // Autoriser toutes les méthodes HTTP (GET, POST, PUT, DELETE, etc.)
        configuration.addAllowedMethod("*");

        // Autoriser tous les headers dans les requêtes
        configuration.addAllowedHeader("*");

        // Permettre l'envoi de cookies et credentials dans les requêtes cross-origin
        // Nécessaire pour l'authentification basée sur les cookies ou les tokens
        configuration.setAllowCredentials(true);

        // Enregistrement de la configuration CORS pour tous les chemins
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
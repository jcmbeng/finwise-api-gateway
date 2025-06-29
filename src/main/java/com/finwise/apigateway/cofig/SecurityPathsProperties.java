package com.finwise.apigateway.cofig;


import com.finwise.apigateway.dtos.RoleBasedRoute;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


/**
 * Classe de configuration pour gérer les propriétés de sécurité de l'application.
 *
 * Cette classe utilise le mécanisme Spring Boot @ConfigurationProperties pour
 * mapper les propriétés de configuration externe (application.yml/properties)
 * vers des objets Java typés.
 *
 * Structure de configuration attendue dans application.yml :
 * <pre>
 * security:
 *   permit-all-paths:
 *     - "/actuator/**"
 *     - "/swagger-ui/**"
 *     - "/v3/api-docs/**"
 *   role-based-routes:
 *     - path: "/api/admin/**"
 *       roles: ["ADMIN"]
 *     - path: "/api/user/**"
 *       roles: ["USER", "ADMIN"]
 * </pre>
 *
 * Cette approche permet de :
 * - Centraliser la configuration de sécurité
 * - Modifier les règles d'accès sans recompilation
 * - Avoir des configurations différentes par environnement
 * - Valider la configuration au démarrage de l'application
 *
 * @author Votre nom
 * @version 1.0
 * @since 1.0
 * @see RoleBasedRoute
 * @see SecurityConfig
 */
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityPathsProperties {

    /**
     * Liste des chemins d'accès publics ne nécessitant aucune authentification.
     *
     * Ces chemins sont généralement utilisés pour :
     * - Les endpoints de monitoring (actuator).
     * - La documentation API (Swagger/OpenAPI).
     * - Les ressources statiques
     * - Les endpoints de santé de l'application.
     *
     * Initialisée avec une liste vide par défaut pour éviter les NullPointerException.
     */
    private List<String> permitAllPaths = new ArrayList<>();

    /**
     * Liste des routes protégées avec leurs rôles requis.
     *
     * Chaque route définit un pattern de chemin et les rôles autorisés
     * à accéder à ces ressources. Permet une gestion fine des autorisations
     * basée sur les rôles utilisateur.
     *
     * Initialisée avec une liste vide par défaut pour éviter les NullPointerException.
     */
    private List<RoleBasedRoute> roleBasedRoutes = new ArrayList<>();

    /**
     * Retourne la liste des chemins d'accès publics.
     *
     * Ces chemins seront configurés avec .permitAll() dans Spring Security,
     * permettant l'accès sans authentification ni autorisation.
     *
     * @return Liste des patterns de chemins publics, jamais null
     */
    public List<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    /**
     * Définit la liste des chemins d'accès publics.
     *
     * Utilisé par Spring Boot lors du binding des propriétés de configuration
     * depuis les fichiers application.yml/properties.
     *
     * @param permitAllPaths La nouvelle liste des chemins publics, ne doit pas être null
     * @throws IllegalArgumentException si la liste est null
     */
    public void setPermitAllPaths(List<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths != null ? permitAllPaths : new ArrayList<>();
    }

    /**
     * Retourne la liste des routes protégées par rôles.
     *
     * Chaque route contient un pattern de chemin et les rôles requis
     * pour y accéder. Ces informations sont utilisées pour configurer
     * les règles d'autorisation dans Spring Security.
     *
     * @return Liste des routes basées sur les rôles, jamais null
     */
    public List<RoleBasedRoute> getRoleBasedRoutes() {
        return roleBasedRoutes;
    }

    /**
     * Définit la liste des routes protégées par rôles.
     *
     * Utilisé par Spring Boot lors du binding des propriétés de configuration
     * depuis les fichiers application.yml/properties.
     *
     * @param roleBasedRoutes La nouvelle liste des routes protégées, ne doit pas être null
     * @throws IllegalArgumentException si la liste est null
     */
    public void setRoleBasedRoutes(List<RoleBasedRoute> roleBasedRoutes) {
        this.roleBasedRoutes = roleBasedRoutes != null ? roleBasedRoutes : new ArrayList<>();
    }
}

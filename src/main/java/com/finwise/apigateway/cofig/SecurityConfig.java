package com.finwise.apigateway.cofig;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {



    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, SecurityPathsProperties config) {


        http.authorizeExchange(exchange -> {
            // permitAll
            config.getPermitAllPaths().forEach(path ->
                    exchange.pathMatchers(path).permitAll()
            );

            // pathMatchers avec rôles
            config.getRoleBasedRoutes().forEach(route -> {
                String[] roles = route.getRoles().stream()
                        .map(role -> "ROLE_" + role) // important : le prefixe ROLE_
                        .toArray(String[]::new);

                exchange.pathMatchers(route.getPath()).hasAnyAuthority(roles);
            });

            // tout le reste
            exchange.anyExchange().denyAll();
        });


        http
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtConverter()))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }
}
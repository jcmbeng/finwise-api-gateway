package com.finwise.apigateway.cofig;


import com.finwise.apigateway.dtos.RoleBasedRoute;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityPathsProperties {
    private List<String> permitAllPaths = new ArrayList<>();
    private List<RoleBasedRoute> roleBasedRoutes = new ArrayList<>();

    public List<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    public void setPermitAllPaths(List<String> permitAllPaths) {
        this.permitAllPaths = permitAllPaths;
    }

    public List<RoleBasedRoute> getRoleBasedRoutes() {
        return roleBasedRoutes;
    }

    public void setRoleBasedRoutes(List<RoleBasedRoute> roleBasedRoutes) {
        this.roleBasedRoutes = roleBasedRoutes;
    }
}

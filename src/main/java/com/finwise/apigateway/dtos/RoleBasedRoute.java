package com.finwise.apigateway.dtos;

import java.util.ArrayList;
import java.util.List;

public class RoleBasedRoute {

    private String path;
    private List<String> roles = new ArrayList<>();

    // Getters & Setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}

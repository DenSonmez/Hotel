package dk.lyngby.security;

import io.javalin.security.RouteRole;

public enum RouteRoles implements RouteRole {
    ANYONE("anyone"), USER("user"), ADMIN("admin"), MANAGER("manager");

    private final String role;

    RouteRoles(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }
}
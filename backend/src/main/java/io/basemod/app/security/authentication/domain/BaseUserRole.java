package io.basemod.app.security.authentication.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BaseUserRole {

    @JsonProperty("modeller")
    MODELLER("modeller"),
    @JsonProperty("reviewer")
    REVIEWER("reviewer"),
    @JsonProperty("admin")
    ADMIN("admin");

    private String roleName;

    private BaseUserRole(String roleName) {
        this.roleName = roleName;
    }

    public String toString() {
        return roleName;
    }
}

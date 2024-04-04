package io.basemod.app.security.authentication.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class BaseUserInformation implements Serializable {

    private Instant lastLogin;
    private List<BaseUserRole> baseUserRoles;

    public BaseUserInformation(Instant lastLogin, List<BaseUserRole> baseUserRoles) {
        this.lastLogin = lastLogin;
        this.baseUserRoles = baseUserRoles;
    }



    public BaseUserInformation() {
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<BaseUserRole> getBaseUserRoles() {
        return baseUserRoles;
    }

    public void setBaseUserRoles(List<BaseUserRole> baseUserRoles) {
        this.baseUserRoles = baseUserRoles;
    }

    @Override
    public String toString() {
        return "BaseUserInformation{" +
                "lastLogin=" + lastLogin +
                ", baseUserRoles=" + baseUserRoles +
                '}';
    }
}

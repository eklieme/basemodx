package io.basemod.app.security.authentication.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class BaseSessionInformation implements Serializable {

    private Instant lastLogin;
    private List<BaseUserRole> baseUserRoles;
    private String socialLoginProvider;
    private String socialLoginUserId;

    //named for easy access in social login on frontend side
    private String access_token;

    public BaseSessionInformation(Instant lastLogin, List<BaseUserRole> baseUserRoles, String socialLoginProvider, String socialLoginUserId, String access_token) {
        this.lastLogin = lastLogin;
        this.baseUserRoles = baseUserRoles;
        this.socialLoginProvider = socialLoginProvider;
        this.socialLoginUserId = socialLoginUserId;
        this.access_token = access_token;
    }

    public BaseSessionInformation() {
    }

    public BaseSessionInformation(BaseUser baseUser, String jwtToken) {
        this.lastLogin = baseUser.getLastLogin();
        this.baseUserRoles = baseUser.getBaseUserRoles();
        this.socialLoginProvider = baseUser.getSocialLoginProvider();
        this.socialLoginUserId = baseUser.getSocialLoginUserId();
        this.access_token = jwtToken;
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

    public String getSocialLoginProvider() {
        return socialLoginProvider;
    }

    public void setSocialLoginProvider(String socialLoginProvider) {
        this.socialLoginProvider = socialLoginProvider;
    }

    public String getSocialLoginUserId() {
        return socialLoginUserId;
    }

    public void setSocialLoginUserId(String socialLoginUserId) {
        this.socialLoginUserId = socialLoginUserId;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public String toString() {
        return "BaseSessionInformation{" +
                "lastLogin=" + lastLogin +
                ", baseUserRoles=" + baseUserRoles +
                ", socialLoginProvider='" + socialLoginProvider + '\'' +
                ", userId='" + socialLoginUserId + '\'' +
                ", access_token='" + access_token + '\'' +
                '}';
    }
}

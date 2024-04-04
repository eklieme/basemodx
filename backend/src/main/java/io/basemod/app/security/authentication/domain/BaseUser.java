package io.basemod.app.security.authentication.domain;

import io.basemod.app.domain.DomainElement;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.basemod.app.repository.ModelledElementService.ANONYMOUS_USER_ID;

public class BaseUser extends DomainElement {

    private String socialLoginProvider;
    private String socialLoginUserId;

    private String uniqueId;

    private List<BaseUserRole> baseUserRoles;

    private Instant lastLogin;

    @CreatedDate
    private Instant createdDate;

    public BaseUser() {

    }

    public BaseUser(String socialLoginUserId, String socialLoginProvider) {
        this.baseUserRoles = new ArrayList<>();
        this.baseUserRoles.add(BaseUserRole.MODELLER);
        this.lastLogin = Instant.now();
        this.socialLoginUserId = socialLoginUserId;
        this.socialLoginProvider = socialLoginProvider;
        this.uniqueId = socialLoginProvider+"_"+socialLoginUserId;
    }

    public BaseUser(String socialLoginProvider, String socialLoginUserId, BaseUserRole baseUserRole, Instant lastLogin) {
        this.baseUserRoles = new ArrayList<>();
        this.baseUserRoles.add(baseUserRole);
        this.lastLogin = Instant.now();
        this.socialLoginUserId = socialLoginUserId;
        this.socialLoginProvider = socialLoginProvider;
        this.uniqueId = socialLoginProvider+"_"+socialLoginUserId;
    }

    public static BaseUser getDefaultModeller() {
        return new BaseUser("github", "eklieme", BaseUserRole.ADMIN, Instant.now());
    }

    public BaseUser(String id, String socialLoginProvider, String socialLoginUserId, List<BaseUserRole> baseUserRoles, Instant lastLogin, Instant createdDate) {
        super(id);
        this.socialLoginProvider = socialLoginProvider;
        this.socialLoginUserId = socialLoginUserId;
        this.baseUserRoles = baseUserRoles;
        this.lastLogin = lastLogin;
        this.createdDate = createdDate;
    }

    public BaseUser(String socialLoginProvider, String socialLoginUserId, List<BaseUserRole> baseUserRoles, Instant lastLogin, Instant createdDate) {
        this.socialLoginProvider = socialLoginProvider;
        this.socialLoginUserId = socialLoginUserId;
        this.baseUserRoles = baseUserRoles;
        this.lastLogin = lastLogin;
        this.createdDate = createdDate;
    }

    public BaseUser(boolean defaultUser) {
        if(defaultUser) {
            this.socialLoginProvider = BaseUser.getDefaultModeller().getSocialLoginProvider();
            this.socialLoginUserId = BaseUser.getDefaultModeller().getSocialLoginUserId();
        } else {
            this.uniqueId = ANONYMOUS_USER_ID;
        }
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

    public List<BaseUserRole> getBaseUserRoles() {
        return baseUserRoles;
    }

    public void setBaseUserRoles(List<BaseUserRole> baseUserRoles) {
        this.baseUserRoles = baseUserRoles;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<GrantedAuthority> getRolesForSecurityContext() {
        return this.getBaseUserRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "BaseUser{" +
                "socialLoginProvider='" + socialLoginProvider + '\'' +
                ", socialLoginUserId='" + socialLoginUserId + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", baseUserRoles=" + baseUserRoles +
                ", lastLogin=" + lastLogin +
                ", createdDate=" + createdDate +
                "} " + super.toString();
    }
}

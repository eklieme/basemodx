package io.basemod.app.security.authentication.social;

import io.basemod.app.domain.DomainElement;

public class SocialLoginUserInformation extends DomainElement {

    private String socialLoginProvider;
    private String socialLoginUserId;
    private String accessToken;

    public SocialLoginUserInformation() {
    }

    public SocialLoginUserInformation(String socialLoginProvider, String accessToken) {
        this.socialLoginProvider = socialLoginProvider;
        this.accessToken = accessToken;
    }

    public String getSocialLoginProvider() {
        return socialLoginProvider;
    }

    public void setSocialLoginProvider(String socialLoginProvider) {
        this.socialLoginProvider = socialLoginProvider;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSocialLoginUserId() {
        return socialLoginUserId;
    }

    public void setSocialLoginUserId(String socialLoginUserId) {
        this.socialLoginUserId = socialLoginUserId;
    }

    public SocialLoginUserInformation(String socialLoginProvider, String socialLoginUserId, String accessToken) {
        this.socialLoginProvider = socialLoginProvider;
        this.socialLoginUserId = socialLoginUserId;
        this.accessToken = accessToken;
    }

    public String inferUniqueId() {
        return this.socialLoginProvider+"_"+this.socialLoginUserId;
    }

    @Override
    public String toString() {
        return "SocialLoginUserInformation{" +
                "socialLoginProvider='" + socialLoginProvider + '\'' +
                ", userId='" + socialLoginUserId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}

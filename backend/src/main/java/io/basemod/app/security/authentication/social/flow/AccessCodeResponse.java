package io.basemod.app.security.authentication.social.flow;

public class AccessCodeResponse {

    private String code;

    public AccessCodeResponse(String code) {
        this.code = code;
    }

    public AccessCodeResponse() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

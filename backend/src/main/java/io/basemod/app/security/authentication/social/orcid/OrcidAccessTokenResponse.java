package io.basemod.app.security.authentication.social.orcid;

import io.basemod.app.security.authentication.social.flow.AccessTokenResponse;

public class OrcidAccessTokenResponse extends AccessTokenResponse {

    private String name;
    private String orcid;

    public OrcidAccessTokenResponse(String access_token, int expires_in, String refresh_token, int refresh_token_expires_in, String scope, String token_type, String name, String orcid) {
        super(access_token, expires_in, refresh_token, refresh_token_expires_in, scope, token_type);
        this.name = name;
        this.orcid = orcid;
    }

    public OrcidAccessTokenResponse() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Override
    public String toString() {
        return "OrcidAccessTokenResponse{" +
                "name='" + name + '\'' +
                ", orcid='" + orcid + '\'' +
                '}';
    }
}

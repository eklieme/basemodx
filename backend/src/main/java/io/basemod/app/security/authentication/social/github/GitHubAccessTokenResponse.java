package io.basemod.app.security.authentication.social.github;

import io.basemod.app.security.authentication.social.flow.AccessTokenResponse;

public class GitHubAccessTokenResponse extends AccessTokenResponse {

    private int refresh_token_expires_in;

    public GitHubAccessTokenResponse(String access_token, int expires_in, String refresh_token, int refresh_token_expires_in, String scope, String token_type, int refresh_token_expires_in1) {
        super(access_token, expires_in, refresh_token, refresh_token_expires_in, scope, token_type);
        this.refresh_token_expires_in = refresh_token_expires_in1;
    }

    public GitHubAccessTokenResponse() {
    }

    public int getRefresh_token_expires_in() {
        return refresh_token_expires_in;
    }

    public void setRefresh_token_expires_in(int refresh_token_expires_in) {
        this.refresh_token_expires_in = refresh_token_expires_in;
    }

    @Override
    public String toString() {
        return "GitHubAccessTokenResponse{" +
                "refresh_token_expires_in=" + refresh_token_expires_in +
                '}';
    }
}

package io.basemod.app.security.authentication.social.flow;

public class ExtendedAccessTokenRequest extends AccessTokenRequest {

    private String grant_type;
    private String redirect_uri;

    public ExtendedAccessTokenRequest(String client_id, String client_secret, String code, String grant_type, String redirect_uri) {
        super(client_id, client_secret, code);
        this.grant_type = grant_type;
        this.redirect_uri = redirect_uri;
    }

    public ExtendedAccessTokenRequest() {
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }

    @Override
    public String toString() {
        return "ExtendedAccessTokenRequest{" +
                "grant_type='" + grant_type + '\'' +
                ", redirect_uri='" + redirect_uri + '\'' +
                "} " + super.toString();
    }
}

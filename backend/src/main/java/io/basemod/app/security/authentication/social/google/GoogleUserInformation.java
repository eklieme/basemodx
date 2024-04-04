package io.basemod.app.security.authentication.social.google;

public class GoogleUserInformation {

    private String email;

    public GoogleUserInformation() {
    }

    public GoogleUserInformation(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "GoogleUserInformation{" +
                "email='" + email + '\'' +
                '}';
    }
}

package io.basemod.app.security.authentication.social.github;

public class GitHubUserInformation {

    private String login;

    public GitHubUserInformation() {
    }

    public GitHubUserInformation(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "GitHubUserInformation{" +
                "login='" + login + '\'' +
                '}';
    }
}

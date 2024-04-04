package io.basemod.app.evaluation.criteria.grant;

import java.io.Serializable;

public class GrantingRule implements Serializable {

    private GrantingLevel grantingLevel;
    private String rule;

    public GrantingRule(GrantingLevel grantingLevel, String rule) {
        this.grantingLevel = grantingLevel;
        this.rule = rule;
    }

    public GrantingRule() {
    }

    public GrantingLevel getGrantingLevel() {
        return grantingLevel;
    }

    public void setGrantingLevel(GrantingLevel grantingLevel) {
        this.grantingLevel = grantingLevel;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}

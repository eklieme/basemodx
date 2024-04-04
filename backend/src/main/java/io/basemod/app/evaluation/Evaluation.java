package io.basemod.app.evaluation;

import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.security.authentication.domain.BaseUser;

public abstract class Evaluation extends ModelledDomainElement {

    public Evaluation() {
    }

    @Override
    public String toString() {
        return "Evaluation{} " + super.toString();
    }
}

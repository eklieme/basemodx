package io.basemod.app.domain;

import io.basemod.app.security.authentication.domain.BaseUser;
import io.basemod.app.base.Reference;

public class ReferencedModelledDomainElement extends ModelledDomainElement {

    private Reference reference;

    public ReferencedModelledDomainElement(Reference reference) {
        super();
        this.reference = reference;
    }

    public ReferencedModelledDomainElement() {
    }

    @Override
    public String toString() {
        return "ReferencedModelledNamedDomainElement{" +
                "reference=" + reference +
                "} " + super.toString();
    }
}

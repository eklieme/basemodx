package io.basemod.app.domain;

import io.basemod.app.base.Reference;

public class  ReferencedNamedModelledDomainElement extends NamedModelledDomainElement {

    private Reference reference;

    public ReferencedNamedModelledDomainElement(Reference reference) {
        this.reference = reference;
    }

    public ReferencedNamedModelledDomainElement(String id, ModelledElementDetail modelledElementDetail, String name, Reference reference) {
        super(id, modelledElementDetail, name);
        this.reference = reference;
    }

    public ReferencedNamedModelledDomainElement(ModelledElementDetail modelledElementDetail, String name, Reference reference) {
        super(modelledElementDetail, name);
        this.reference = reference;
    }

    public ReferencedNamedModelledDomainElement(String name, Reference reference) {
        super(name);
        this.reference = reference;
    }

    public ReferencedNamedModelledDomainElement(String id, String name, Reference reference) {
        super(id, name);
        this.reference = reference;
    }

    public ReferencedNamedModelledDomainElement() {
        super();
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "ReferencedModelledNamedDomainElement{" +
                "reference=" + reference +
                "} " + super.toString();
    }
}

package io.basemod.app.base;

import io.basemod.app.domain.DomainElement;

public class FurtherInformationReference extends DomainElement {

    // right now, this can refer to anything, e.g. a repository, a paper at some conference etc.
    private String reference;
    private FurtherInformationReferenceType furtherInformationReferenceType;

    public FurtherInformationReference(String reference, FurtherInformationReferenceType furtherInformationReferenceType) {
        this.reference = reference;
        this.furtherInformationReferenceType = furtherInformationReferenceType;
    }

    public FurtherInformationReference() {
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public FurtherInformationReferenceType getFurtherInformationReferenceType() {
        return furtherInformationReferenceType;
    }

    public void setFurtherInformationReferenceType(FurtherInformationReferenceType furtherInformationReferenceType) {
        this.furtherInformationReferenceType = furtherInformationReferenceType;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "reference='" + reference + '\'' +
                ", referenceType=" + furtherInformationReferenceType +
                '}';
    }
}

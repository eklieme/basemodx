package io.basemod.app.base;

import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.security.authentication.domain.BaseUser;

import java.util.List;

public class Reference  extends ModelledDomainElement {

    private List<FurtherInformationReference> furtherInformationReferences;
    private boolean assetModelledByOriginator;
    private String originatorEmail;
    private ReferenceAssetType referenceAssetType;

    public Reference() {
    }

    public Reference(List<FurtherInformationReference> furtherInformationReferences, boolean assetModelledByOriginator, String originatorEmail, ReferenceAssetType referenceAssetType) {
        super();
        this.furtherInformationReferences = furtherInformationReferences;
        this.assetModelledByOriginator = assetModelledByOriginator;
        this.originatorEmail = originatorEmail;
        this.referenceAssetType = referenceAssetType;
    }

    public ReferenceAssetType getReferenceAssetType() {
        return referenceAssetType;
    }

    public void setReferenceAssetType(ReferenceAssetType referenceAssetType) {
        this.referenceAssetType = referenceAssetType;
    }

    public List<FurtherInformationReference> getFurtherInformationReferences() {
        return furtherInformationReferences;
    }

    public void setFurtherInformationReferences(List<FurtherInformationReference> furtherInformationReferences) {
        this.furtherInformationReferences = furtherInformationReferences;
    }

    public String getOriginatorEmail() {
        return originatorEmail;
    }

    public void setOriginatorEmail(String originatorEmail) {
        this.originatorEmail = originatorEmail;
    }

    public boolean isAssetModelledByOriginator() {
        return assetModelledByOriginator;
    }

    public void setAssetModelledByOriginator(boolean assetModelledByOriginator) {
        this.assetModelledByOriginator = assetModelledByOriginator;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "furtherInformationReferences=" + furtherInformationReferences +
                ", isOriginator=" + assetModelledByOriginator +
                ", originatorEmail='" + originatorEmail + '\'' +
                ", referenceAssetType=" + referenceAssetType +
                "} " + super.toString();
    }
}

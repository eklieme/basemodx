package io.basemod.app.architecture;

import io.basemod.app.domain.DomainElement;

import java.util.ArrayList;
import java.util.List;

public class TargetArchitecture extends DomainElement {

    private List<String> signatureCreationDeviceCategoryIds;
    private List<String> dataStorageDeviceCategoryIds;
    private List<String> signalProcessingDeviceCategoryIds;
    private List<String> matchingDeviceCategoryIds;
    private List<String> decisionDeviceCategoryIds;
    private List<String> dataCapturingDeviceCategoryIds;

    private String resourceToProtectId;
    private boolean skipTargetArchitecture;

    public TargetArchitecture() {

        signalProcessingDeviceCategoryIds = new ArrayList<>();
        dataStorageDeviceCategoryIds = new ArrayList<>();
        signatureCreationDeviceCategoryIds = new ArrayList<>();
        matchingDeviceCategoryIds = new ArrayList<>();
        decisionDeviceCategoryIds = new ArrayList<>();
        dataCapturingDeviceCategoryIds = new ArrayList<>();
    }

    public TargetArchitecture(List<String> signatureCreationDeviceCategoryIds, List<String> dataStorageDeviceCategoryIds, List<String> signalProcessingDeviceCategoryIds, List<String> matchingDeviceCategoryIds, List<String> decisionDeviceCategoryIds, List<String> dataCapturingDeviceCategoryIds, String resourceToProtectId, boolean skipTargetArchitecture) {
        this.signatureCreationDeviceCategoryIds = signatureCreationDeviceCategoryIds;
        this.dataStorageDeviceCategoryIds = dataStorageDeviceCategoryIds;
        this.signalProcessingDeviceCategoryIds = signalProcessingDeviceCategoryIds;
        this.matchingDeviceCategoryIds = matchingDeviceCategoryIds;
        this.decisionDeviceCategoryIds = decisionDeviceCategoryIds;
        this.dataCapturingDeviceCategoryIds = dataCapturingDeviceCategoryIds;
        this.resourceToProtectId = resourceToProtectId;
        this.skipTargetArchitecture = skipTargetArchitecture;
    }

    public List<String> getSignatureCreationDeviceCategoryIds() {
        return signatureCreationDeviceCategoryIds;
    }

    public void setSignatureCreationDeviceCategoryIds(List<String> signatureCreationDeviceCategoryIds) {
        this.signatureCreationDeviceCategoryIds = signatureCreationDeviceCategoryIds;
    }

    public List<String> getDataStorageDeviceCategoryIds() {
        return dataStorageDeviceCategoryIds;
    }

    public void setDataStorageDeviceCategoryIds(List<String> dataStorageDeviceCategoryIds) {
        this.dataStorageDeviceCategoryIds = dataStorageDeviceCategoryIds;
    }

    public List<String> getSignalProcessingDeviceCategoryIds() {
        return signalProcessingDeviceCategoryIds;
    }

    public void setSignalProcessingDeviceCategoryIds(List<String> signalProcessingDeviceCategoryIds) {
        this.signalProcessingDeviceCategoryIds = signalProcessingDeviceCategoryIds;
    }

    public List<String> getMatchingDeviceCategoryIds() {
        return matchingDeviceCategoryIds;
    }

    public void setMatchingDeviceCategoryIds(List<String> matchingDeviceCategoryIds) {
        this.matchingDeviceCategoryIds = matchingDeviceCategoryIds;
    }

    public List<String> getDecisionDeviceCategoryIds() {
        return decisionDeviceCategoryIds;
    }

    public void setDecisionDeviceCategoryIds(List<String> decisionDeviceCategoryIds) {
        this.decisionDeviceCategoryIds = decisionDeviceCategoryIds;
    }

    public List<String> getDataCapturingDeviceCategoryIds() {
        return dataCapturingDeviceCategoryIds;
    }

    public void setDataCapturingDeviceCategoryIds(List<String> dataCapturingDeviceCategoryIds) {
        this.dataCapturingDeviceCategoryIds = dataCapturingDeviceCategoryIds;
    }

    public String getResourceToProtectId() {
        return resourceToProtectId;
    }

    public void setResourceToProtectId(String resourceToProtectId) {
        this.resourceToProtectId = resourceToProtectId;
    }

    public boolean isSkipTargetArchitecture() {
        return skipTargetArchitecture;
    }

    public void setSkipTargetArchitecture(boolean skipTargetArchitecture) {
        this.skipTargetArchitecture = skipTargetArchitecture;
    }

    @Override
    public String toString() {
        return "TargetArchitecture{" +
                "signatureCreationDeploymentLocations=" + signatureCreationDeviceCategoryIds +
                ", signatureStorageDeploymentLocations=" + dataStorageDeviceCategoryIds +
                ", preProcessingDeploymentLocations=" + signalProcessingDeviceCategoryIds +
                ", matchingDeploymentLocations=" + matchingDeviceCategoryIds +
                ", sampleAcquisitionDeploymentLocations=" + dataCapturingDeviceCategoryIds +
                ", protectedResource='" + resourceToProtectId + '\'' +
                '}';
    }
}

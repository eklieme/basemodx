package io.basemod.app.architecture;

import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.EnrichedDeviceCategory;

import java.util.ArrayList;
import java.util.List;


public class EnrichedTargetArchitecture extends TargetArchitecture {

    private List<EnrichedDeviceCategory> signatureCreationDeviceCategories;
    private List<EnrichedDeviceCategory> dataStorageDeviceCategories;
    private List<EnrichedDeviceCategory> signalProcessingDeviceCategories;
    private List<EnrichedDeviceCategory> matchingDeviceCategories;
    private List<EnrichedDeviceCategory> decisionDeviceCategories;
    private List<EnrichedDeviceCategory> dataCapturingDeviceCategories;

    private String resourceToProtect;

    public EnrichedTargetArchitecture() {
        this.dataCapturingDeviceCategories = new ArrayList<>();
        this.signalProcessingDeviceCategories = new ArrayList<>();
        this.signatureCreationDeviceCategories = new ArrayList<>();
        this.dataStorageDeviceCategories = new ArrayList<>();
        this.matchingDeviceCategories = new ArrayList<>();
        this.decisionDeviceCategories = new ArrayList<>();
        this.resourceToProtect = "";

    }

    public List<EnrichedDeviceCategory> getSignatureCreationDeviceCategories() {
        return signatureCreationDeviceCategories;
    }

    public void setSignatureCreationDeviceCategories(List<EnrichedDeviceCategory> signatureCreationDeviceCategories) {
        this.signatureCreationDeviceCategories = signatureCreationDeviceCategories;
    }

    public List<EnrichedDeviceCategory> getDataStorageDeviceCategories() {
        return dataStorageDeviceCategories;
    }

    public void setDataStorageDeviceCategories(List<EnrichedDeviceCategory> dataStorageDeviceCategories) {
        this.dataStorageDeviceCategories = dataStorageDeviceCategories;
    }

    public List<EnrichedDeviceCategory> getSignalProcessingDeviceCategories() {
        return signalProcessingDeviceCategories;
    }

    public void setSignalProcessingDeviceCategories(List<EnrichedDeviceCategory> signalProcessingDeviceCategories) {
        this.signalProcessingDeviceCategories = signalProcessingDeviceCategories;
    }

    public List<EnrichedDeviceCategory> getMatchingDeviceCategories() {
        return matchingDeviceCategories;
    }

    public void setMatchingDeviceCategories(List<EnrichedDeviceCategory> matchingDeviceCategories) {
        this.matchingDeviceCategories = matchingDeviceCategories;
    }

    public List<EnrichedDeviceCategory> getDecisionDeviceCategories() {
        return decisionDeviceCategories;
    }

    public void setDecisionDeviceCategories(List<EnrichedDeviceCategory> decisionDeviceCategories) {
        this.decisionDeviceCategories = decisionDeviceCategories;
    }

    public List<EnrichedDeviceCategory> getDataCapturingDeviceCategories() {
        return dataCapturingDeviceCategories;
    }

    public void setDataCapturingDeviceCategories(List<EnrichedDeviceCategory> dataCapturingDeviceCategories) {
        this.dataCapturingDeviceCategories = dataCapturingDeviceCategories;
    }

    public String getResourceToProtect() {
        return resourceToProtect;
    }

    public void setResourceToProtect(String resourceToProtect) {
        this.resourceToProtect = resourceToProtect;
    }
}

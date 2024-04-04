package io.basemod.app.evaluation.experiment.biometricsystem;

import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.domain.NamedModelledDomainElement;
import io.basemod.app.evaluation.experiment.biometricsystem.feature.Feature;
import io.basemod.app.evaluation.experiment.biometricsystem.step.BiometricSystemProcessingStep;

import java.util.List;

public class EnrichedBiometricSystem extends NamedModelledDomainElement {

    private String parentBaseName;
    private List<String> dataInputIds;
    private List<BiometricSystemProcessingStep> signalProcessingSteps;
    private List<Feature> features;
    private List<BiometricSystemProcessingStep> furtherProcessingSteps;
    private FusionType fusionType;
    private List<String> fusedSystems;

    public EnrichedBiometricSystem() {

    }

    public EnrichedBiometricSystem(String name) {
        super(name);
    }

    public EnrichedBiometricSystem(String name, String parentBaseName, List<String> dataInputIds, List<BiometricSystemProcessingStep> signalProcessingSteps, List<Feature> features, List<BiometricSystemProcessingStep> furtherProcessingSteps, FusionType fusionType, List<String> fusedSystems) {
        super(name);
        this.parentBaseName = parentBaseName;
        this.dataInputIds = dataInputIds;
        this.signalProcessingSteps = signalProcessingSteps;
        this.features = features;
        this.furtherProcessingSteps = furtherProcessingSteps;
        this.fusionType = fusionType;
        this.fusedSystems = fusedSystems;
    }

    public EnrichedBiometricSystem(String id, String name, String parentBaseName, List<String> dataInputIds, List<BiometricSystemProcessingStep> signalProcessingSteps, List<Feature> features, List<BiometricSystemProcessingStep> furtherProcessingSteps, FusionType fusionType, List<String> fusedSystems) {
        super(id, name);
        this.parentBaseName = parentBaseName;
        this.dataInputIds = dataInputIds;
        this.signalProcessingSteps = signalProcessingSteps;
        this.features = features;
        this.furtherProcessingSteps = furtherProcessingSteps;
        this.fusionType = fusionType;
        this.fusedSystems = fusedSystems;
    }

    public EnrichedBiometricSystem(String parentBaseName, List<String> dataInputIds, List<BiometricSystemProcessingStep> signalProcessingSteps, List<Feature> features, List<BiometricSystemProcessingStep> furtherProcessingSteps, FusionType fusionType, List<String> fusedSystems) {
        this.parentBaseName = parentBaseName;
        this.dataInputIds = dataInputIds;
        this.signalProcessingSteps = signalProcessingSteps;
        this.features = features;
        this.furtherProcessingSteps = furtherProcessingSteps;
        this.fusionType = fusionType;
        this.fusedSystems = fusedSystems;
    }

    public EnrichedBiometricSystem(String id, ModelledElementDetail modelledElementDetail, String name, String parentBaseName, List<String> dataInputIds, List<BiometricSystemProcessingStep> signalProcessingSteps, List<Feature> features, List<BiometricSystemProcessingStep> furtherProcessingSteps, FusionType fusionType, List<String> fusedSystems) {
        super(id, modelledElementDetail, name);
        this.parentBaseName = parentBaseName;
        this.dataInputIds = dataInputIds;
        this.signalProcessingSteps = signalProcessingSteps;
        this.features = features;
        this.furtherProcessingSteps = furtherProcessingSteps;
        this.fusionType = fusionType;
        this.fusedSystems = fusedSystems;
    }

    public EnrichedBiometricSystem(ModelledElementDetail modelledElementDetail, String name, String parentBaseName, List<String> dataInputIds, List<BiometricSystemProcessingStep> signalProcessingSteps, List<Feature> features, List<BiometricSystemProcessingStep> furtherProcessingSteps, FusionType fusionType, List<String> fusedSystems) {
        super(modelledElementDetail, name);
        this.parentBaseName = parentBaseName;
        this.dataInputIds = dataInputIds;
        this.signalProcessingSteps = signalProcessingSteps;
        this.features = features;
        this.furtherProcessingSteps = furtherProcessingSteps;
        this.fusionType = fusionType;
        this.fusedSystems = fusedSystems;
    }

    public String getParentBaseName() {
        return parentBaseName;
    }

    public void setParentBaseName(String parentBaseName) {
        this.parentBaseName = parentBaseName;
    }

    public List<String> getDataInputIds() {
        return dataInputIds;
    }

    public void setDataInputIds(List<String> dataInputIds) {
        this.dataInputIds = dataInputIds;
    }

    public List<BiometricSystemProcessingStep> getSignalProcessingSteps() {
        return signalProcessingSteps;
    }

    public void setSignalProcessingSteps(List<BiometricSystemProcessingStep> signalProcessingSteps) {
        this.signalProcessingSteps = signalProcessingSteps;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<BiometricSystemProcessingStep> getFurtherProcessingSteps() {
        return furtherProcessingSteps;
    }

    public void setFurtherProcessingSteps(List<BiometricSystemProcessingStep> furtherProcessingSteps) {
        this.furtherProcessingSteps = furtherProcessingSteps;
    }

    public FusionType getFusionType() {
        return fusionType;
    }

    public void setFusionType(FusionType fusionType) {
        this.fusionType = fusionType;
    }

    public List<String> getFusedSystems() {
        return fusedSystems;
    }

    public void setFusedSystems(List<String> fusedSystems) {
        this.fusedSystems = fusedSystems;
    }

    @Override
    public String toString() {
        return "EnrichedBiometricSystem{" +
                "parentBaseName='" + parentBaseName + '\'' +
                ", dataInputs=" + dataInputIds +
                ", signalProcessingSteps=" + signalProcessingSteps +
                ", features=" + features +
                ", furtherProcessingSteps=" + furtherProcessingSteps +
                ", fusionType=" + fusionType +
                ", fusedSystems=" + fusedSystems +
                "} " + super.toString();
    }
}

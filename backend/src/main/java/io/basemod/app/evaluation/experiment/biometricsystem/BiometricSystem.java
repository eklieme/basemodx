package io.basemod.app.evaluation.experiment.biometricsystem;

import io.basemod.app.domain.NamedModelledDomainElement;

import java.util.ArrayList;
import java.util.List;


public class BiometricSystem extends NamedModelledDomainElement {

    private String parentBaseName;
    private List<String> dataInputIds;
    private List<String> signalProcessingStepIds;
    private List<String> featureIds;
    private List<String> furtherProcessingStepIds;
    private FusionType fusionType;
    private List<String> fusedSystemIds;


    public BiometricSystem() {
    }

    public BiometricSystem(String name, String parentBaseName, List<String> dataInputIds, List<String> signalProcessingStepIds, List<String> featureIds, List<String> furtherProcessingStepIds, FusionType fusionType, List<String> fusedSystemIds) {
        super(name);
        this.parentBaseName = parentBaseName;
        this.dataInputIds = dataInputIds;
        this.signalProcessingStepIds = signalProcessingStepIds;
        this.featureIds = featureIds;
        this.furtherProcessingStepIds = furtherProcessingStepIds;
        this.fusionType = fusionType;
        this.fusedSystemIds = fusedSystemIds;
    }

    public List<String> getFurtherProcessingStepIds() {
        return furtherProcessingStepIds;
    }

    public void setFurtherProcessingStepIds(List<String> furtherProcessingStepIds) {
        this.furtherProcessingStepIds = furtherProcessingStepIds;
    }

    public void addFurtherProcessingStepId(String furtherProcessingStepId) {
        if(this.furtherProcessingStepIds==null) {
            this.furtherProcessingStepIds = new ArrayList<>();
        }
        this.furtherProcessingStepIds.add(furtherProcessingStepId);
    }

    public FusionType getFusionType() {
        return fusionType;
    }

    public void setFusionType(FusionType fusionType) {
        this.fusionType = fusionType;
    }

    public List<String> getDataInputIds() {
        return dataInputIds;
    }

    public void setDataInputIds(List<String> dataInputIds) {
        this.dataInputIds = dataInputIds;
    }

    public List<String> getSignalProcessingStepIds() {
        return signalProcessingStepIds;
    }

    public void setSignalProcessingStepIds(List<String> signalProcessingStepIds) {
        this.signalProcessingStepIds = signalProcessingStepIds;
    }

    public void addSignalProcessingStepId(String signalProcessingStepId) {
        if(this.signalProcessingStepIds==null) {
            this.signalProcessingStepIds = new ArrayList<>();
        }
        this.signalProcessingStepIds.add(signalProcessingStepId);
    }

    public List<String> getFusedSystemIds() {
        return fusedSystemIds;
    }

    public void setFusedSystemIds(List<String> fusedSystemIds) {
        this.fusedSystemIds = fusedSystemIds;
    }

    public String getParentBaseName() {
        return parentBaseName;
    }

    public void setParentBaseName(String parentBaseName) {
        this.parentBaseName = parentBaseName;
    }

    public List<String> getFeatureIds() {
        return featureIds;
    }

    public void setFeatureIds(List<String> featureIds) {
        this.featureIds = featureIds;
    }

    public void addFeatureId(String featureId) {
        if(this.featureIds==null) {
            this.featureIds = new ArrayList<>();
        }
        this.featureIds.add(featureId);
    }

    @Override
    public String toString() {
        return "BiometricSystem{" +
                "parentBaseName='" + parentBaseName + '\'' +
                ", dataInputs=" + dataInputIds +
                ", signalProcessingSteps=" + signalProcessingStepIds +
                ", features=" + featureIds +
                ", furtherProcessingSteps=" + furtherProcessingStepIds +
                ", fusionType=" + fusionType +
                ", fusedSystems=" + fusedSystemIds +
                "} " + super.toString();
    }
}

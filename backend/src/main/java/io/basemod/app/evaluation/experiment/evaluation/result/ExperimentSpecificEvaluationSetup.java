package io.basemod.app.evaluation.experiment.evaluation.result;

import io.basemod.app.domain.ModelledDomainElement;

import java.util.List;

public class ExperimentSpecificEvaluationSetup extends ModelledDomainElement {

    private String description;
    private List<String> biometricSystemIds;
    private List<SpecificResult> specificResults;

    public ExperimentSpecificEvaluationSetup() {
    }

    public ExperimentSpecificEvaluationSetup(String description, List<SpecificResult> specificResults) {
        this.description = description;
        this.specificResults = specificResults;
    }

    public ExperimentSpecificEvaluationSetup(String description, List<String> biometricSystemIds, List<SpecificResult> specificResults) {
        this.description = description;
        this.biometricSystemIds = biometricSystemIds;
        this.specificResults = specificResults;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getBiometricSystemIds() {
        return biometricSystemIds;
    }

    public void setBiometricSystemIds(List<String> biometricSystemIds) {
        this.biometricSystemIds = biometricSystemIds;
    }

    public List<SpecificResult> getSpecificResults() {
        return specificResults;
    }

    public void setSpecificResults(List<SpecificResult> specificResults) {
        this.specificResults = specificResults;
    }

    @Override
    public String toString() {
        return "ExperimentSpecificEvaluationResult{" +
                "description='" + description + '\'' +
                ", biometricSystems=" + biometricSystemIds +
                ", specificResults=" + specificResults +
                "} " + super.toString();
    }
}

package io.basemod.app.evaluation.experiment.evaluation.result;

import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.evaluation.experiment.biometricsystem.BiometricSystem;
import io.basemod.app.evaluation.experiment.biometricsystem.EnrichedBiometricSystem;

import java.util.ArrayList;
import java.util.List;

public class EnrichedExperimentSpecificEvaluationSetup extends ModelledDomainElement {

    private String description;
    private List<EnrichedBiometricSystem> biometricSystems;
    private List<EnrichedSpecificResult> specificResults;

    public EnrichedExperimentSpecificEvaluationSetup() {
    }

    public EnrichedExperimentSpecificEvaluationSetup(ExperimentSpecificEvaluationSetup experimentSpecificEvaluationSetup) {
        this.setId(experimentSpecificEvaluationSetup.getId());
        this.description = experimentSpecificEvaluationSetup.getDescription();
        this.biometricSystems = new ArrayList<>();
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EnrichedBiometricSystem> getBiometricSystems() {
        return biometricSystems;
    }

    public void setBiometricSystems(List<EnrichedBiometricSystem> biometricSystems) {
        this.biometricSystems = biometricSystems;
    }

    public List<EnrichedSpecificResult> getSpecificResults() {
        return specificResults;
    }

    public void setSpecificResults(List<EnrichedSpecificResult> specificResults) {
        this.specificResults = specificResults;
    }

    @Override
    public String toString() {
        return "EnrichedExperimentSpecificEvaluationSetup{" +
                "description='" + description + '\'' +
                ", biometricSystems=" + biometricSystems +
                ", specificResults=" + specificResults +
                "} " + super.toString();
    }
}

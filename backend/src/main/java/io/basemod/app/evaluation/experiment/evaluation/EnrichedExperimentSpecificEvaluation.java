package io.basemod.app.evaluation.experiment.evaluation;

import io.basemod.app.evaluation.experiment.biometricsystem.BiometricSystem;
import io.basemod.app.evaluation.experiment.biometricsystem.EnrichedBiometricSystem;
import io.basemod.app.evaluation.experiment.dataset.DataSet;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.evaluation.result.EnrichedExperimentSpecificEvaluationSetup;

import java.util.List;

public class EnrichedExperimentSpecificEvaluation {

    private List<EnrichedDataSet> usedDatasets;
    private List<EnrichedBiometricSystem> biometricSystems;
    private List<EnrichedExperimentSpecificEvaluationSetup> experimentSpecificEvaluationSetups;

    public EnrichedExperimentSpecificEvaluation() {
    }


    public EnrichedExperimentSpecificEvaluation(List<EnrichedDataSet> usedDatasets, List<EnrichedBiometricSystem> biometricSystems, List<EnrichedExperimentSpecificEvaluationSetup> experimentSpecificEvaluationSetups) {
        this.usedDatasets = usedDatasets;
        this.biometricSystems = biometricSystems;
        this.experimentSpecificEvaluationSetups = experimentSpecificEvaluationSetups;
    }

    public List<EnrichedDataSet> getUsedDatasets() {
        return usedDatasets;
    }

    public void setUsedDatasets(List<EnrichedDataSet> usedDatasets) {
        this.usedDatasets = usedDatasets;
    }


    public List<EnrichedExperimentSpecificEvaluationSetup> getExperimentSpecificEvaluationSetups() {
        return experimentSpecificEvaluationSetups;
    }

    public void setExperimentSpecificEvaluationSetups(List<EnrichedExperimentSpecificEvaluationSetup> experimentSpecificEvaluationSetups) {
        this.experimentSpecificEvaluationSetups = experimentSpecificEvaluationSetups;
    }

    @Override
    public String toString() {
        return "SpecificEvaluation{" +
                "dataSets=" + usedDatasets +
                ", biometricSystems=" + biometricSystems +
                ", evaluationSetups=" + experimentSpecificEvaluationSetups +
                '}';
    }

    public List<EnrichedBiometricSystem> getBiometricSystems() {
        return biometricSystems;
    }

    public void setBiometricSystems(List<EnrichedBiometricSystem> biometricSystems) {
        this.biometricSystems = biometricSystems;
    }
}

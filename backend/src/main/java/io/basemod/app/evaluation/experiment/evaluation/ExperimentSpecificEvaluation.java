package io.basemod.app.evaluation.experiment.evaluation;

import io.basemod.app.evaluation.Evaluation;
import io.basemod.app.evaluation.experiment.evaluation.result.ExperimentSpecificEvaluationSetup;

import java.util.List;

public class ExperimentSpecificEvaluation extends Evaluation {

    private List<String> usedDatasetIds;
    private List<String> biometricSystemIds;
    private List<ExperimentSpecificEvaluationSetup> experimentSpecificEvaluationSetups;

    public ExperimentSpecificEvaluation() {
    }

    public ExperimentSpecificEvaluation(List<String> usedDatasetIds, List<String> biometricSystemIds, List<ExperimentSpecificEvaluationSetup> experimentSpecificEvaluationSetups) {
        this.usedDatasetIds = usedDatasetIds;
        this.biometricSystemIds = biometricSystemIds;
        this.experimentSpecificEvaluationSetups = experimentSpecificEvaluationSetups;
    }

    public List<String> getUsedDatasetIds() {
        return usedDatasetIds;
    }

    public void setUsedDatasetIds(List<String> usedDatasetIds) {
        this.usedDatasetIds = usedDatasetIds;
    }


    public List<ExperimentSpecificEvaluationSetup> getExperimentSpecificEvaluationSetups() {
        return experimentSpecificEvaluationSetups;
    }

    public void setExperimentSpecificEvaluationSetups(List<ExperimentSpecificEvaluationSetup> experimentSpecificEvaluationSetups) {
        this.experimentSpecificEvaluationSetups = experimentSpecificEvaluationSetups;
    }

    @Override
    public String toString() {
        return "ExperimentSpecificEvaluation{" +
                "usedDatasets=" + usedDatasetIds +
                ", biometricSystems=" + biometricSystemIds +
                ", experimentSpecificEvaluationSetups=" + experimentSpecificEvaluationSetups +
                "} " + super.toString();
    }

    public List<String> getBiometricSystemIds() {
        return biometricSystemIds;
    }

    public void setBiometricSystemIds(List<String> biometricSystemIds) {
        this.biometricSystemIds = biometricSystemIds;
    }
}

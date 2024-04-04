package io.basemod.app.repository.import_.domain;

import io.basemod.app.base.BiometricAuthenticationSystemAndEvaluation;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.biometricsystem.BiometricSystem;
import io.basemod.app.evaluation.experiment.dataset.DataSet;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;

import java.util.List;

public class BaseImportArtefact {

    private BiometricAuthenticationSystemAndEvaluation biometricAuthenticationSystemAndEvaluation;
    private List<DataSet> datasets;
    private List<BiometricSystem> biometricSystems;
    private List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteria;
    private List<ExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteria;

    public BaseImportArtefact(BiometricAuthenticationSystemAndEvaluation biometricAuthenticationSystemAndEvaluation, List<DataSet> datasets, List<BiometricSystem> biometricSystems, List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteria, List<ExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteria) {
        this.biometricAuthenticationSystemAndEvaluation = biometricAuthenticationSystemAndEvaluation;
        this.datasets = datasets;
        this.biometricSystems = biometricSystems;
        this.implementationSpecificEvaluationCriteria = implementationSpecificEvaluationCriteria;
        this.experimentSpecificEvaluationCriteria = experimentSpecificEvaluationCriteria;
    }

    public BaseImportArtefact() {
    }

    public BiometricAuthenticationSystemAndEvaluation getBehavioralAuthenticationSystem() {
        return biometricAuthenticationSystemAndEvaluation;
    }

    public void setBehavioralAuthenticationSystem(BiometricAuthenticationSystemAndEvaluation biometricAuthenticationSystemAndEvaluation) {
        this.biometricAuthenticationSystemAndEvaluation = biometricAuthenticationSystemAndEvaluation;
    }

    public List<DataSet> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<DataSet> datasets) {
        this.datasets = datasets;
    }

    public List<BiometricSystem> getBiometricSystems() {
        return biometricSystems;
    }

    public void setBiometricSystems(List<BiometricSystem> biometricSystems) {
        this.biometricSystems = biometricSystems;
    }

    public List<ImplementationSpecificEvaluationCriteria> getImplementationSpecificEvaluationCriteria() {
        return implementationSpecificEvaluationCriteria;
    }

    public void setImplementationSpecificEvaluationCriteria(List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteria) {
        this.implementationSpecificEvaluationCriteria = implementationSpecificEvaluationCriteria;
    }

    @Override
    public String toString() {
        return "BaseImportArtefact{" +
                "behavioralAuthenticationSystem=" + biometricAuthenticationSystemAndEvaluation +
                ", datasets=" + datasets +
                ", biometricSystems=" + biometricSystems +
                ", implementationSpecificEvaluationCriteria=" + implementationSpecificEvaluationCriteria +
                ", experimentSpecificEvaluationCriteria=" + experimentSpecificEvaluationCriteria +
                '}';
    }

    public List<ExperimentSpecificEvaluationCriteria> getExperimentSpecificEvaluationCriteria() {
        return experimentSpecificEvaluationCriteria;
    }

    public void setExperimentSpecificEvaluationCriteria(List<ExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteria) {
        this.experimentSpecificEvaluationCriteria = experimentSpecificEvaluationCriteria;
    }
}

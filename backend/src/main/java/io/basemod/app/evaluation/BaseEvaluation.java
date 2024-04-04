package io.basemod.app.evaluation;

import io.basemod.app.evaluation.experiment.evaluation.ExperimentSpecificEvaluation;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationResult;

import java.util.List;

public class BaseEvaluation {

    private List<ImplementationSpecificEvaluationResult> implementationSpecificEvaluationResults;
    private ExperimentSpecificEvaluation experimentSpecificEvaluation;


    public BaseEvaluation() {
    }

    public BaseEvaluation(List<ImplementationSpecificEvaluationResult> implementationSpecificEvaluationResults, ExperimentSpecificEvaluation experimentSpecificEvaluation) {
        this.implementationSpecificEvaluationResults = implementationSpecificEvaluationResults;
        this.experimentSpecificEvaluation = experimentSpecificEvaluation;
    }

    public List<ImplementationSpecificEvaluationResult> getImplementationSpecificEvaluationResults() {
        return implementationSpecificEvaluationResults;
    }

    public void setImplementationSpecificEvaluationResults(List<ImplementationSpecificEvaluationResult> implementationSpecificEvaluationResults) {
        this.implementationSpecificEvaluationResults = implementationSpecificEvaluationResults;
    }

    public ExperimentSpecificEvaluation getExperimentSpecificEvaluation() {
        return experimentSpecificEvaluation;
    }

    public void setExperimentSpecificEvaluation(ExperimentSpecificEvaluation experimentSpecificEvaluation) {
        this.experimentSpecificEvaluation = experimentSpecificEvaluation;
    }

    @Override
    public String toString() {
        return "BaseEvaluation{" +
                "implementationSpecificEvaluationResults=" + implementationSpecificEvaluationResults +
                ", experimentSpecificEvaluation=" + experimentSpecificEvaluation +
                '}';
    }
}

package io.basemod.app.evaluation;

import io.basemod.app.evaluation.experiment.evaluation.EnrichedExperimentSpecificEvaluation;
import io.basemod.app.evaluation.implementation.EnrichedImplementationSpecificEvaluationResult;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationResult;

import java.util.List;

public class EnrichedBaseEvaluation {

    private List<EnrichedImplementationSpecificEvaluationResult> implementationSpecificEvaluationResults;
    private EnrichedExperimentSpecificEvaluation experimentSpecificEvaluation;

    public EnrichedBaseEvaluation() {
    }
    public EnrichedBaseEvaluation(List<EnrichedImplementationSpecificEvaluationResult> implementationSpecificEvaluationResults, EnrichedExperimentSpecificEvaluation experimentSpecificEvaluation) {
        this.implementationSpecificEvaluationResults = implementationSpecificEvaluationResults;
        this.experimentSpecificEvaluation = experimentSpecificEvaluation;
    }

    public List<EnrichedImplementationSpecificEvaluationResult> getImplementationSpecificEvaluationResults() {
        return implementationSpecificEvaluationResults;
    }

    public void setImplementationSpecificEvaluationResults(List<EnrichedImplementationSpecificEvaluationResult> implementationSpecificEvaluationResults) {
        this.implementationSpecificEvaluationResults = implementationSpecificEvaluationResults;
    }

    public EnrichedExperimentSpecificEvaluation getExperimentSpecificEvaluation() {
        return experimentSpecificEvaluation;
    }

    public void setExperimentSpecificEvaluation(EnrichedExperimentSpecificEvaluation experimentSpecificEvaluation) {
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

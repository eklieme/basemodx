package io.basemod.app.evaluation.experiment.biometricsystem.step;

import io.basemod.app.evaluation.experiment.biometricsystem.DecisionMode;
import io.basemod.app.evaluation.experiment.biometricsystem.step.MachineLearningProcessingStep;
import io.basemod.app.evaluation.experiment.biometricsystem.step.ProcessingType;
import io.basemod.app.base.Reference;

public class DecisionProcessingStep extends MachineLearningProcessingStep {

    private DecisionMode decisionMode;

    public DecisionProcessingStep(DecisionMode decisionMode) {
        this.decisionMode = decisionMode;
    }

    public DecisionProcessingStep(String name, Reference reference, ProcessingType processingType, boolean isDeepLearning, String modelUrl, DecisionMode decisionMode) {
        super(name, reference, processingType, isDeepLearning, modelUrl);
        this.decisionMode = decisionMode;
    }

    public DecisionProcessingStep() {
    }

    @Override
    public String getType() {
        return DECISION_STEP;
    }

    public DecisionMode getDecisionMode() {
        return decisionMode;
    }

    public void setDecisionMode(DecisionMode decisionMode) {
        this.decisionMode = decisionMode;
    }

    @Override
    public String toString() {
        return "DecisionProcessingStep{" +
                "decisionMode=" + decisionMode +
                "} " + super.toString();
    }
}

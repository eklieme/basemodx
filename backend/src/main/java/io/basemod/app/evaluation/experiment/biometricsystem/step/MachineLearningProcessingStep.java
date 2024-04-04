package io.basemod.app.evaluation.experiment.biometricsystem.step;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.basemod.app.base.Reference;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DecisionProcessingStep.class, name = "decision")
})
public class MachineLearningProcessingStep extends BiometricSystemProcessingStep {

    private boolean isDeepLearning;
    private String modelUrl;

    public MachineLearningProcessingStep(String name, Reference reference, ProcessingType processingType, boolean isDeepLearning, String modelUrl) {
        super(name, reference, processingType);
        this.isDeepLearning = isDeepLearning;
        this.modelUrl = modelUrl;
    }

    public MachineLearningProcessingStep() {
    }

    public boolean isDeepLearning() {
        return isDeepLearning;
    }

    public void setDeepLearning(boolean deepLearning) {
        isDeepLearning = deepLearning;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    @Override
    public String getType() {
        return BiometricSystemProcessingStep.MATCHING_STEP;
    }

    @Override
    public String toString() {
        return "MachineLearningProcessingStep{" +
                "isDeepLearning=" + isDeepLearning +
                ", modelUrl='" + modelUrl + '\'' +
                "} " + super.toString();
    }
}

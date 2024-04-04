package io.basemod.app.evaluation.experiment.biometricsystem.step;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.basemod.app.base.*;
import io.basemod.app.domain.ReferencedNamedModelledDomainElement;

import java.util.Objects;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BiometricSystemProcessingStep.class, name = BiometricSystemProcessingStep.SIMPLE_STEP)
        ,@JsonSubTypes.Type(value = MachineLearningProcessingStep.class, name = BiometricSystemProcessingStep.MATCHING_STEP)
})
public class BiometricSystemProcessingStep extends ReferencedNamedModelledDomainElement {

    private ProcessingType processingType;

    public static final String SIMPLE_STEP = "simple";
    public static final String MATCHING_STEP = "matching";
    public static final String DECISION_STEP = "decision";

    public BiometricSystemProcessingStep() {
    }

    public BiometricSystemProcessingStep(String name, Reference reference, ProcessingType processingType) {
        super(name, reference);
        this.processingType = processingType;
    }

    public ProcessingType getProcessingType() {
        return processingType;
    }

    public void setProcessingType(ProcessingType processingType) {
        this.processingType = processingType;
    }

    public String getType() {
        return SIMPLE_STEP;
    }

    @Override
    public String toString() {
        return "BiometricSystemProcessingStep{" +
                "processingType=" + processingType +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BiometricSystemProcessingStep that = (BiometricSystemProcessingStep) o;
        return processingType.equals(that.processingType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), processingType);
    }
}

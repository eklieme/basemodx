package io.basemod.app.evaluation.experiment.dataset.sampling.context;

import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.domain.ModelledElementDetail;

import java.util.Objects;

public class SamplingContext extends ModelledDomainElement {

    private String description;

    public SamplingContext(String description) {
        this.description = description;
    }

    public SamplingContext() {
    }

    public SamplingContext(String id, ModelledElementDetail modelledElementDetail, String description) {
        super(id, modelledElementDetail);
        this.description = description;
    }

    public SamplingContext(ModelledElementDetail modelledElementDetail, String description) {
        super(modelledElementDetail);
        this.description = description;
    }

    public SamplingContext(String id, String description) {
        super(id);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SamplingContext{" +
                "description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        SamplingContext that = (SamplingContext) other;
        return description.replace(" ","").equalsIgnoreCase(that.description.replace(" ", ""));
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }
}

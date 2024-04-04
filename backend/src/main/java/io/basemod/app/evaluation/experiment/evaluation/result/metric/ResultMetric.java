package io.basemod.app.evaluation.experiment.evaluation.result.metric;

import io.basemod.app.base.Reference;
import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.domain.NamedModelledDomainElement;
import io.basemod.app.domain.ReferencedNamedModelledDomainElement;
import io.basemod.app.evaluation.experiment.evaluation.result.ResultUnit;

import java.util.Objects;

public class ResultMetric extends ReferencedNamedModelledDomainElement {

    private String description;
    private String parentEvaluationCriteriaId;
    private ResultUnit unit;

    public ResultMetric() {
    }

    public ResultUnit getUnit() {
        return unit;
    }

    public void setUnit(ResultUnit unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResultMetric(Reference reference, String description, String parentEvaluationCriteriaId, ResultUnit unit) {
        super(reference);
        this.description = description;
        this.parentEvaluationCriteriaId = parentEvaluationCriteriaId;
        this.unit = unit;
    }

    public ResultMetric(String id, ModelledElementDetail modelledElementDetail, String name, Reference reference, String description, String parentEvaluationCriteriaId, ResultUnit unit) {
        super(id, modelledElementDetail, name, reference);
        this.description = description;
        this.parentEvaluationCriteriaId = parentEvaluationCriteriaId;
        this.unit = unit;
    }

    public ResultMetric(ModelledElementDetail modelledElementDetail, String name, Reference reference, String description, String parentEvaluationCriteriaId, ResultUnit unit) {
        super(modelledElementDetail, name, reference);
        this.description = description;
        this.parentEvaluationCriteriaId = parentEvaluationCriteriaId;
        this.unit = unit;
    }

    public ResultMetric(String name, Reference reference, String description, String parentEvaluationCriteriaId, ResultUnit unit) {
        super(name, reference);
        this.description = description;
        this.parentEvaluationCriteriaId = parentEvaluationCriteriaId;
        this.unit = unit;
    }

    public ResultMetric(String id, String name, Reference reference, String description, String parentEvaluationCriteriaId, ResultUnit unit) {
        super(id, name, reference);
        this.description = description;
        this.parentEvaluationCriteriaId = parentEvaluationCriteriaId;
        this.unit = unit;
    }

    public ResultMetric(String description, String parentEvaluationCriteriaId, ResultUnit unit) {
        this.description = description;
        this.parentEvaluationCriteriaId = parentEvaluationCriteriaId;
        this.unit = unit;
    }

    public String getParentEvaluationCriteriaId() {
        return parentEvaluationCriteriaId;
    }

    public void setParentEvaluationCriteriaId(String parentEvaluationCriteriaId) {
        this.parentEvaluationCriteriaId = parentEvaluationCriteriaId;
    }

    @Override
    public String toString() {
        return "ResultMetric{" +
                "description='" + description + '\'' +
                ", parentEvaluationCriteriaId='" + parentEvaluationCriteriaId + '\'' +
                ", unit=" + unit +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ResultMetric that = (ResultMetric) other;
        return description.equalsIgnoreCase(that.description) &&
                unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, parentEvaluationCriteriaId, unit);
    }
}

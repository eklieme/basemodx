package io.basemod.app.evaluation.implementation;

import io.basemod.app.domain.ModelledDomainElement;

import java.util.List;

public class ImplementationSpecificEvaluationResult extends ModelledDomainElement {

    private String criteriaId;
    private List<String> affectedDeviceCategoryIds;
    private String description;

    public ImplementationSpecificEvaluationResult() {
    }

    public ImplementationSpecificEvaluationResult(String criteriaId, List<String> affectedDeviceCategoryIds, String description) {
        this.criteriaId = criteriaId;
        this.affectedDeviceCategoryIds = affectedDeviceCategoryIds;
        this.description = description;
    }

    public String getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(String criteriaId) {
        this.criteriaId = criteriaId;
    }

    public List<String> getAffectedDeviceCategoryIds() {
        return affectedDeviceCategoryIds;
    }

    public void setAffectedDeviceCategoryIds(List<String> affectedDeviceCategoryIds) {
        this.affectedDeviceCategoryIds = affectedDeviceCategoryIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ImplementationSpecificEvaluationResult{" +
                "criteria=" + criteriaId +
                ", affectedDeploymentLocations=" + affectedDeviceCategoryIds +
                ", description='" + description + '\'' +
                "} " + super.toString();
    }
}

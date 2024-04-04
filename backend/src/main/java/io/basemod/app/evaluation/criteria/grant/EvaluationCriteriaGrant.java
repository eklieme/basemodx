package io.basemod.app.evaluation.criteria.grant;

import io.basemod.app.base.*;
import io.basemod.app.domain.ReferencedModelledDomainElement;

public class EvaluationCriteriaGrant extends ReferencedModelledDomainElement {

    private String experimentSpecificEvaluationCriteriaId;
    private String implementationSpecificEvaluationCriteriaId;
    private GrantingLevel grantingLevel;

    public EvaluationCriteriaGrant(Reference reference, String experimentSpecificEvaluationCriteriaId, String implementationSpecificEvaluationCriteriaId, GrantingLevel grantingLevel) {
        super(reference);
        this.experimentSpecificEvaluationCriteriaId = experimentSpecificEvaluationCriteriaId;
        this.implementationSpecificEvaluationCriteriaId = implementationSpecificEvaluationCriteriaId;
        this.grantingLevel = grantingLevel;
    }

    public EvaluationCriteriaGrant() {
    }


    public String getExperimentSpecificEvaluationCriteriaId() {
        return experimentSpecificEvaluationCriteriaId;
    }

    public void setExperimentSpecificEvaluationCriteriaId(String experimentSpecificEvaluationCriteriaId) {
        this.experimentSpecificEvaluationCriteriaId = experimentSpecificEvaluationCriteriaId;
    }

    public GrantingLevel getGrantingLevel() {
        return grantingLevel;
    }

    public void setGrantingLevel(GrantingLevel grantingLevel) {
        this.grantingLevel = grantingLevel;
    }

    public String getImplementationSpecificEvaluationCriteriaId() {
        return implementationSpecificEvaluationCriteriaId;
    }

    public void setImplementationSpecificEvaluationCriteriaId(String implementationSpecificEvaluationCriteriaId) {
        this.implementationSpecificEvaluationCriteriaId = implementationSpecificEvaluationCriteriaId;
    }

    @Override
    public String toString() {
        return "EvaluationCriteriaGrant{" +
                "evaluationCriteria=" + experimentSpecificEvaluationCriteriaId +
                ", grantingLevel=" + grantingLevel +
                "} " + super.toString();
    }
}

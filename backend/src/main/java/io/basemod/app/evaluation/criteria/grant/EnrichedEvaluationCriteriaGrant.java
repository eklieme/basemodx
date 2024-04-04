package io.basemod.app.evaluation.criteria.grant;

import io.basemod.app.base.Reference;
import io.basemod.app.evaluation.criteria.EvaluationCriteria;

public class EnrichedEvaluationCriteriaGrant extends EvaluationCriteriaGrant {

    private EvaluationCriteria evaluationCriteria;

    public EnrichedEvaluationCriteriaGrant() {

    }

    public EnrichedEvaluationCriteriaGrant(Reference reference, String experimentSpecificEvaluationCriteriaId, String implementationSpecificEvaluationCriteriaId, GrantingLevel grantingLevel, EvaluationCriteria evaluationCriteria) {
        super(reference, experimentSpecificEvaluationCriteriaId, implementationSpecificEvaluationCriteriaId, grantingLevel);
        this.evaluationCriteria = evaluationCriteria;
    }

    public EnrichedEvaluationCriteriaGrant(EvaluationCriteria evaluationCriteria) {
        this.evaluationCriteria = evaluationCriteria;
    }

    public EvaluationCriteria getEvaluationCriteria() {
        return evaluationCriteria;
    }

    public void setEvaluationCriteria(EvaluationCriteria evaluationCriteria) {
        this.evaluationCriteria = evaluationCriteria;
    }
}

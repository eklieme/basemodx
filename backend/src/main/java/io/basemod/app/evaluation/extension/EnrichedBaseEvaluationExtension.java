package io.basemod.app.evaluation.extension;

import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.evaluation.BaseEvaluation;
import io.basemod.app.evaluation.EnrichedBaseEvaluation;
import io.basemod.app.evaluation.criteria.grant.EnrichedEvaluationCriteriaGrant;

import java.util.List;

public class EnrichedBaseEvaluationExtension extends ModelledDomainElement {

    private EnrichedBaseEvaluation baseEvaluationToMerge;
    private String baseNameForExtension;

    private List<EnrichedEvaluationCriteriaGrant> evaluationCriteriaGrantsToMerge;

    public EnrichedBaseEvaluationExtension() {

    }

    public EnrichedBaseEvaluationExtension(String id, EnrichedBaseEvaluation baseEvaluationToMerge, String baseNameForExtension) {
        super(id);
        this.baseEvaluationToMerge = baseEvaluationToMerge;
        this.baseNameForExtension = baseNameForExtension;
    }

    public EnrichedBaseEvaluationExtension(EnrichedBaseEvaluation baseEvaluationToMerge, String baseNameForExtension) {
        this.baseEvaluationToMerge = baseEvaluationToMerge;
        this.baseNameForExtension = baseNameForExtension;
    }

    public EnrichedBaseEvaluation getBaseEvaluationToMerge() {
        return baseEvaluationToMerge;
    }

    public void setBaseEvaluationToMerge(EnrichedBaseEvaluation baseEvaluationToMerge) {
        this.baseEvaluationToMerge = baseEvaluationToMerge;
    }

    public String getBaseNameForExtension() {
        return baseNameForExtension;
    }

    public void setBaseNameForExtension(String baseNameForExtension) {
        this.baseNameForExtension = baseNameForExtension;
    }

    public List<EnrichedEvaluationCriteriaGrant> getEvaluationCriteriaGrantsToMerge() {
        return evaluationCriteriaGrantsToMerge;
    }

    public void setEvaluationCriteriaGrantsToMerge(List<EnrichedEvaluationCriteriaGrant> evaluationCriteriaGrantsToMerge) {
        this.evaluationCriteriaGrantsToMerge = evaluationCriteriaGrantsToMerge;
    }

    @Override
    public String toString() {
        return "EnrichedBaseEvaluationExtension{" +
                "baseEvaluationToMerge=" + baseEvaluationToMerge +
                ", baseNameForExtension='" + baseNameForExtension + '\'' +
                ", evaluationCriteriaGrantsToMerge=" + evaluationCriteriaGrantsToMerge +
                "} " + super.toString();
    }
}

package io.basemod.app.evaluation.extension;

import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.evaluation.BaseEvaluation;
import io.basemod.app.evaluation.criteria.grant.EvaluationCriteriaGrant;

import java.util.ArrayList;
import java.util.List;

public class BaseEvaluationExtension extends ModelledDomainElement {

    private BaseEvaluation baseEvaluationToMerge;

    private List<EvaluationCriteriaGrant> evaluationCriteriaGrantsToMerge;
    private String baseNameForExtension;

    public BaseEvaluationExtension() {
        this.evaluationCriteriaGrantsToMerge = new ArrayList<>();
    }

    public BaseEvaluation getBaseEvaluationToMerge() {
        return baseEvaluationToMerge;
    }

    public void setBaseEvaluationToMerge(BaseEvaluation baseEvaluationToMerge) {
        this.baseEvaluationToMerge = baseEvaluationToMerge;
    }

    public String getBaseNameForExtension() {
        return baseNameForExtension;
    }

    public void setBaseNameForExtension(String baseNameForExtension) {
        this.baseNameForExtension = baseNameForExtension;
    }

    public List<EvaluationCriteriaGrant> getEvaluationCriteriaGrantsToMerge() {
        return evaluationCriteriaGrantsToMerge;
    }

    public void setEvaluationCriteriaGrantsToMerge(List<EvaluationCriteriaGrant> evaluationCriteriaGrantsToMerge) {
        this.evaluationCriteriaGrantsToMerge = evaluationCriteriaGrantsToMerge;
    }

}

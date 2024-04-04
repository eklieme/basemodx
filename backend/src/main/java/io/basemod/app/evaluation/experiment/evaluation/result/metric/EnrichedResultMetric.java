package io.basemod.app.evaluation.experiment.evaluation.result.metric;

import io.basemod.app.evaluation.criteria.EvaluationCriteria;

public class EnrichedResultMetric extends ResultMetric {

    private EvaluationCriteria parentCriteria;

    public EnrichedResultMetric() {
    }

    public EvaluationCriteria getParentCriteria() {
        return parentCriteria;
    }

    public void setParentCriteria(EvaluationCriteria parentCriteria) {
        this.parentCriteria = parentCriteria;
    }
}

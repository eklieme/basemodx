package io.basemod.app.evaluation.implementation;

import io.basemod.app.evaluation.criteria.EvaluationCriteria;

public class ImplementationSpecificEvaluationCriteria extends EvaluationCriteria {

    @Override
    public String getType() {
        return EvaluationCriteria.IMPLEMENTATION_SPECIFIC_TYPE;
    }
}

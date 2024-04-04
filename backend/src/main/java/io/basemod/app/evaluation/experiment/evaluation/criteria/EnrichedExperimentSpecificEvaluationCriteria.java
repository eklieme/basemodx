package io.basemod.app.evaluation.experiment.evaluation.criteria;

import io.basemod.app.evaluation.criteria.EvaluationCriteria;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetric;

import java.util.List;

public class EnrichedExperimentSpecificEvaluationCriteria extends ExperimentSpecificEvaluationCriteria {

    private List<ResultMetric> resultMetrics;

    public EnrichedExperimentSpecificEvaluationCriteria() {
        super();
    }

    public EnrichedExperimentSpecificEvaluationCriteria(List<ResultMetric> resultMetrics) {
        this.resultMetrics = resultMetrics;
    }

    public EnrichedExperimentSpecificEvaluationCriteria(List<String> resultMetricIds, List<ResultMetric> resultMetrics) {
        super(resultMetricIds);
        this.resultMetrics = resultMetrics;
    }

    public List<ResultMetric> getResultMetrics() {
        return resultMetrics;
    }

    public void setResultMetrics(List<ResultMetric> resultMetrics) {
        this.resultMetrics = resultMetrics;
    }

    @Override
    public String getType() {
        return EvaluationCriteria.ENRICHED_EXPERIMENT_SPECIFIC_TYPE;
    }
}

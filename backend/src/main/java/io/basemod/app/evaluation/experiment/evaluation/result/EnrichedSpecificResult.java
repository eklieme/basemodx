package io.basemod.app.evaluation.experiment.evaluation.result;

import io.basemod.app.evaluation.experiment.evaluation.result.SpecificResult;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.EnrichedResultMetric;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetric;

public class EnrichedSpecificResult extends SpecificResult {



    private EnrichedResultMetric resultMetric;

    public EnrichedSpecificResult() {

    }

    public EnrichedSpecificResult(EnrichedResultMetric resultMetric) {
        this.resultMetric = resultMetric;
    }

    public EnrichedSpecificResult(String resultMetricId, ResultGranularity resultGranularity, float result, float from, float to, String description, EnrichedResultMetric resultMetric) {
        super(resultMetricId, resultGranularity, result, from, to, description);
        this.resultMetric = resultMetric;
    }

    public EnrichedResultMetric getResultMetric() {
        return resultMetric;
    }

    public void setResultMetric(EnrichedResultMetric resultMetric) {
        this.resultMetric = resultMetric;
    }
}

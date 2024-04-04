package io.basemod.app.evaluation.experiment.evaluation.criteria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.basemod.app.evaluation.criteria.EvaluationCriteria;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;

import java.util.List;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExperimentSpecificEvaluationCriteria.class, name = EvaluationCriteria.EXPERIMENT_SPECIFIC_TYPE),
        @JsonSubTypes.Type(value = EnrichedExperimentSpecificEvaluationCriteria.class, name = EvaluationCriteria.ENRICHED_EXPERIMENT_SPECIFIC_TYPE)}
)
public class ExperimentSpecificEvaluationCriteria extends EvaluationCriteria {

    private List<String> resultMetricIds;

    public ExperimentSpecificEvaluationCriteria() {
        super();
    }

    public ExperimentSpecificEvaluationCriteria(List<String> resultMetricIds) {
        super();
        this.resultMetricIds = resultMetricIds;
    }

    public List<String> getResultMetricIds() {
        return resultMetricIds;
    }

    @Override
    public String getType() {
        return EvaluationCriteria.EXPERIMENT_SPECIFIC_TYPE;
    }

    public void setResultMetricIds(List<String> resultMetricIds) {
        this.resultMetricIds = resultMetricIds;
    }

    @Override
    public String toString() {
        return "ExperimentSpecificCriteria{" +
                ", resultMetrics=" + resultMetricIds +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ExperimentSpecificEvaluationCriteria that = (ExperimentSpecificEvaluationCriteria) other;
        return Objects.equals(resultMetricIds, that.resultMetricIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resultMetricIds);
    }
}

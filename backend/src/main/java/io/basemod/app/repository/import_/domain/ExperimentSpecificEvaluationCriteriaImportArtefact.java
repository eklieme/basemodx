package io.basemod.app.repository.import_.domain;

import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetric;

import java.util.ArrayList;
import java.util.List;

public class ExperimentSpecificEvaluationCriteriaImportArtefact {

    public List<ExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteriaToImport;
    public List<ResultMetric> resultMetricsToImport;

    public ExperimentSpecificEvaluationCriteriaImportArtefact(List<ExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteriaToImport, List<ResultMetric> resultMetricsToImport) {
        this.experimentSpecificEvaluationCriteriaToImport = experimentSpecificEvaluationCriteriaToImport;
        this.resultMetricsToImport = resultMetricsToImport;
    }

    public ExperimentSpecificEvaluationCriteriaImportArtefact() {
        experimentSpecificEvaluationCriteriaToImport = new ArrayList<>();
        resultMetricsToImport = new ArrayList<>();
    }

    public List<ExperimentSpecificEvaluationCriteria> getExperimentSpecificEvaluationCriteriaToImport() {
        return experimentSpecificEvaluationCriteriaToImport;
    }

    public void setExperimentSpecificEvaluationCriteriaToImport(List<ExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteriaToImport) {
        this.experimentSpecificEvaluationCriteriaToImport = experimentSpecificEvaluationCriteriaToImport;
    }

    public List<ResultMetric> getResultMetricsToImport() {
        return resultMetricsToImport;
    }

    public void setResultMetricsToImport(List<ResultMetric> resultMetricsToImport) {
        this.resultMetricsToImport = resultMetricsToImport;
    }
}

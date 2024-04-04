package io.basemod.app.evaluation.experiment.evaluation.result;

public class SpecificResult {

    private String resultMetricId;
    private ResultGranularity resultGranularity;
    private float result;
    private float from;
    private float to;
    private String description;

    public SpecificResult() {
    }

    public SpecificResult(String resultMetricId, ResultGranularity resultGranularity, float result, float from, float to, String description) {
        this.resultMetricId = resultMetricId;
        this.resultGranularity = resultGranularity;
        this.result = result;
        this.from = from;
        this.to = to;
        this.description = description;
    }

    public String getResultMetricId() {
        return resultMetricId;
    }

    public void setResultMetricId(String resultMetricId) {
        this.resultMetricId = resultMetricId;
    }

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getFrom() {
        return from;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public ResultGranularity getResultGranularity() {
        return resultGranularity;
    }

    public void setResultGranularity(ResultGranularity resultGranularity) {
        this.resultGranularity = resultGranularity;
    }

    @Override
    public String toString() {
        return "SpecificResult{" +
                "metric=" + resultMetricId +
                ", resultGranularity=" + resultGranularity +
                ", result=" + result +
                ", from=" + from +
                ", to=" + to +
                ", description='" + description + '\'' +
                '}';
    }
}

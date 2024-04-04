package io.basemod.app.base;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReferenceAssetType {

    @JsonProperty("dataset")
    DATASET,
    @JsonProperty("base")
    BASE,
    @JsonProperty("processingStep")
    PROCESSING_STEP,
    @JsonProperty("evaluationCriteria")
    EVALUATION_CRITERIA,
    @JsonProperty("resultMetric")
    RESULT_METRIC,
    @JsonProperty("unspecified")
    UNSPECIFIED,

}

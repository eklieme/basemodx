package io.basemod.app.evaluation.experiment.evaluation.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResultGranularity {

    @JsonProperty("exact")
    EXACT,
    @JsonProperty("range")
    RANGE,
}

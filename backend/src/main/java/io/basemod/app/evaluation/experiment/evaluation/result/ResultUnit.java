package io.basemod.app.evaluation.experiment.evaluation.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResultUnit {
    @JsonProperty("percent")
    PERCENT,
    @JsonProperty("milliwatt")
    MILLIWATT,
    @JsonProperty("none")
    NONE,
    @JsonProperty("milliseconds")
    MILLISECONDS,
    @JsonProperty("seconds")
    SECONDS,
    @JsonProperty("kilobytes")
    KILOBYTES,
}

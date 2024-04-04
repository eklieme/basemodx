package io.basemod.app.evaluation.experiment.biometricsystem.step;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProcessingType {

    @JsonProperty("windowing")
    WINDOWING,
    @JsonProperty("feature")
    FEATURE,
    @JsonProperty("transformer")
    TRANSFORMER,
    @JsonProperty("filter")
    FILTER,
    @JsonProperty("matching")
    MATCHING,
    @JsonProperty("storage")
    STORAGE,
    @JsonProperty("decision")
    DECISION;

}

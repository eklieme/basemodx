package io.basemod.app.evaluation.experiment.biometricsystem;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DecisionMode {

    @JsonProperty("identification")
    IDENTIFICATION,
    @JsonProperty("verification")
    VERIFICATION,

}

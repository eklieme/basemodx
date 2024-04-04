package io.basemod.app.base;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum  ModellingProgress {

    @JsonProperty("characteristics")
    CHARACTERISTICS,
    @JsonProperty("targetArchitecture")
    TARGETARCHITECTURE,
    @JsonProperty("evaluation")
    EVALUATION,
    @JsonProperty("evaluationCriteriaGrants")
    EVALUATION_CRITERIA_GRANTS,
    @JsonProperty("reference")
    REFERENCE,



}

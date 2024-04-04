package io.basemod.app.evaluation.criteria;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EvaluationCriteriaCategory {

    @JsonProperty("usability")
    USABILITY,
    @JsonProperty("deployability")
    DEPLOYABILITY,
    @JsonProperty("security")
    SECURITY;

}

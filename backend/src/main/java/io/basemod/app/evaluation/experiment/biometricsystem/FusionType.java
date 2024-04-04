package io.basemod.app.evaluation.experiment.biometricsystem;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FusionType {

    @JsonProperty("noFusion")
    NO_FUSION,
    @JsonProperty("decisionFusion")
    DECISION_FUSION,
    @JsonProperty("scoreFusion")
    SCORE_FUSION,
    @JsonProperty("preparesDecisionFusion")
    PREPARES_DECISION_FUSION,
    @JsonProperty("preparesScoreFusion")
    PREPARES_SCORE_FUSION,

}

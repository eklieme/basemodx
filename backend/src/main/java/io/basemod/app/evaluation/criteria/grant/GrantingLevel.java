package io.basemod.app.evaluation.criteria.grant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GrantingLevel {

    @JsonProperty("notDecidedYet")
    NOT_DECIDED_YET,
    @JsonProperty("notGranted")
    NOT_GRANTED,
    @JsonProperty("quasiGranted")
    QUASI_GRANTED,
    @JsonProperty("granted")
    GRANTED,

}

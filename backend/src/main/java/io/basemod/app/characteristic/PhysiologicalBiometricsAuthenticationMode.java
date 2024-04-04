package io.basemod.app.characteristic;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PhysiologicalBiometricsAuthenticationMode {

    @JsonProperty("one-off")
    ONE_OFF,
    @JsonProperty("continuous")
    CONTINUOUS,

}

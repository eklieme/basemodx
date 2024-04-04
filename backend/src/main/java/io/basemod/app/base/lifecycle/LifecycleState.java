package io.basemod.app.base.lifecycle;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LifecycleState {

    @JsonProperty("created")
    CREATED,
    @JsonProperty("reviewed")
    REVIEWED,
    @JsonProperty("verified")
    VERIFIED,

    @JsonProperty("rejected")
    REJECTED,

}

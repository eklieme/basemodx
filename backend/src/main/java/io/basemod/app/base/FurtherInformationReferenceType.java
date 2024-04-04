package io.basemod.app.base;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FurtherInformationReferenceType {

    @JsonProperty("paper")
    PAPER,
    @JsonProperty("weblink")
    WEBLINK,

}

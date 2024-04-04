package io.basemod.app.repository.import_.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ImportRemarkLevel {

    @JsonProperty("info")
    INFO,
    @JsonProperty("warning")
    WARNING,
    @JsonProperty("critical")
    CRITICAL,
}

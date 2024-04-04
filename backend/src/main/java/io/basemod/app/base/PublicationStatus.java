package io.basemod.app.base;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PublicationStatus {

    @JsonProperty("created")
    CREATED,
    @JsonProperty("published")
    PUBLISHED,
    @JsonProperty("in_review")
    IN_REVIEW,
    @JsonProperty("published_extended")
    PUBLISHED_EXTENDED,

}

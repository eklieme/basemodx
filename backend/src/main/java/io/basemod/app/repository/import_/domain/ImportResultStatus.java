package io.basemod.app.repository.import_.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ImportResultStatus {

    @JsonProperty("successful")
    SUCCESS,
    @JsonProperty("partially successful")
    PARTIAL_SUCCESS,
    @JsonProperty("not successful")
    NOT_SUCCESSFUL,
    @JsonProperty("done with no changes")
    NO_CHANGES_IN_REPOSITORY,

}

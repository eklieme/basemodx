package io.basemod.app.evaluation.implementation.feature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.basemod.app.evaluation.Evaluation;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceCategorySpecificImplementationFeature.class, name = "deploymentLocation"),
        @JsonSubTypes.Type(value = TwoNodeImplementationFeature.class, name = "twoNode"),
        @JsonSubTypes.Type(value = MultiNodeImplementationFeature.class, name = "multiNode")}
)
public abstract class ImplementationFeature extends Evaluation {

    private String featureDescription;

    public ImplementationFeature(String featureDescription) {
        this.featureDescription = featureDescription;
    }

    public String getFeatureDescription() {
        return featureDescription;
    }

    public void setFeatureDescription(String featureDescription) {
        this.featureDescription = featureDescription;
    }


}

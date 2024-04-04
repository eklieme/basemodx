package io.basemod.app.repository.import_.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ArtefactType {

    @JsonProperty("base")
    BASE,
    @JsonProperty("dataset")
    DATASET,
    @JsonProperty("experimental_evaluation")
    EXPERIMENTAL_EVALUATION,
    @JsonProperty("evaluation_criteria")
    EVALUATION_CRITERIA,
    @JsonProperty("sample_device")
    SAMPLE_DEVICE,
    @JsonProperty("sensor")
    SENSOR,
    @JsonProperty("sensor_dimension")
    SENSOR_DIMENSION,
    @JsonProperty("biometric_system")
    BIOMETRIC_SYSTEM,
    @JsonProperty("feature")
    FEATURE,
    @JsonProperty("result_metric")
    RESULT_METRIC,
    @JsonProperty("processing_step")
    PROCESSING_STEP,
    @JsonProperty("sampling_context")
    SAMPLING_CONTEXT,
    @JsonProperty("target_architecture")
    TARGET_ARCHITECTURE,
    @JsonProperty("behavioral_biometric_characteristic")
    BEHAVIOURAL_BIOMETRIC_CHARACTERISTIC,
    @JsonProperty("device_category")
    DEVICE_CATEGORY,
    @JsonProperty("resource_to_protect")
    RESOURCE_TO_PROTECT,
    @JsonProperty("unknown")
    UNKNOWN;



    public String getNamePlural() {
        switch(this) {
            case BASE:
                return "behavioral authentication system and evaluations";
            case DATASET:
                return "datasets";
            case EXPERIMENTAL_EVALUATION:
                return "experimental specific evaluations";
            case EVALUATION_CRITERIA:
                return "evaluation criteria";
            case SAMPLE_DEVICE:
                return "sample devices";
            case SENSOR:
                return "sensors";
            case SENSOR_DIMENSION:
                return "sensor dimensions";
            case BIOMETRIC_SYSTEM:
                return "biometric systems";
            case FEATURE:
                return "features";
            case RESULT_METRIC:
                return "result metrics";
            case PROCESSING_STEP:
                return "processing steps";
            case SAMPLING_CONTEXT:
                return "sampling contexts";
            case TARGET_ARCHITECTURE:
                return "target architectures";
            case DEVICE_CATEGORY:
                return "device categories";
            case RESOURCE_TO_PROTECT:
                return "resource to protect";
            case BEHAVIOURAL_BIOMETRIC_CHARACTERISTIC:
                return "behavioral biometric characteristics";
            case UNKNOWN:
                return "unknown";
        }

        return "unknown";
    }
}

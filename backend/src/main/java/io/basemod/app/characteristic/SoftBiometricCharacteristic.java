package io.basemod.app.characteristic;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.basemod.app.characteristic.BiometricCharacteristic;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BehavioralBiometricCharacteristic.class, name = BiometricCharacteristic.ENRICHED_SOFT)}
)
public class SoftBiometricCharacteristic extends BiometricCharacteristic {

    private String name;
    private String sourceBiometricCharacteristicId;

    public SoftBiometricCharacteristic(String uniqueIdentifier, String name) {
        super(uniqueIdentifier);
        this.name = name;
    }

    public SoftBiometricCharacteristic(String name) {
        this.name = name;
        this.setUniqueIdentifier(this.name+"-");
    }

    public SoftBiometricCharacteristic() {
    }

    @Override
    public String getType() {
        return SOFT;
    }

    public String getName() {
        return name;
    }

    public String getSourceBiometricCharacteristicId() {
        return sourceBiometricCharacteristicId;
    }

    public void setSourceBiometricCharacteristicId(String sourceBiometricCharacteristicId) {
        this.sourceBiometricCharacteristicId = sourceBiometricCharacteristicId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PhysiologicalBiometricCharacteristic{" +
                ", name='" + name + '\'' +
                '}';
    }
}

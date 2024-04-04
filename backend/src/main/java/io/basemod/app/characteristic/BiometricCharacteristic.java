package io.basemod.app.characteristic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.basemod.app.domain.ModelledDomainElement;

import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BehavioralBiometricCharacteristic.class, name = BiometricCharacteristic.BEHAVIORAL),
        @JsonSubTypes.Type(value = SoftBiometricCharacteristic.class, name = BiometricCharacteristic.SOFT),
        @JsonSubTypes.Type(value = PhysiologicalBiometricCharacteristic.class, name = BiometricCharacteristic.PHYSIOLOGICAL) }
)
public abstract class BiometricCharacteristic extends ModelledDomainElement {

    public static final String BEHAVIORAL = "behavioral";
    public static final String PHYSIOLOGICAL = "physiological";
    public static final String SOFT = "soft";
    protected static final String ENRICHED_SOFT = "enriched_soft";

    public BiometricCharacteristic() {
    }

    public BiometricCharacteristic(String uniqueIdentifier) {
        super();
        this.uniqueIdentifier = uniqueIdentifier;
    }

    private String uniqueIdentifier;

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public abstract String getType();

    public abstract String getName();

    @Override
    public String toString() {
        return "BiometricCharacteristic{" +
                "uniqueIdentifier='" + uniqueIdentifier + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        BiometricCharacteristic that = (BiometricCharacteristic) o;
        return getUniqueIdentifier().equals(that.getUniqueIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uniqueIdentifier);
    }
}

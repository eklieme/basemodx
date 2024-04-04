package io.basemod.app.characteristic;

public class EnrichedSoftBiometricCharacteristic extends SoftBiometricCharacteristic{

    private BiometricCharacteristic sourceBiometricCharacteristic;

    public EnrichedSoftBiometricCharacteristic() {

    }
    public BiometricCharacteristic getSourceBiometricCharacteristic() {
        return sourceBiometricCharacteristic;
    }

    public void setSourceBiometricCharacteristic(BiometricCharacteristic sourceBiometricCharacteristic) {
        this.sourceBiometricCharacteristic = sourceBiometricCharacteristic;
    }

    public EnrichedSoftBiometricCharacteristic(String uniqueIdentifier, String name, BiometricCharacteristic sourceBiometricCharacteristic) {
        super(uniqueIdentifier, name);
        this.sourceBiometricCharacteristic = sourceBiometricCharacteristic;
    }

    public EnrichedSoftBiometricCharacteristic(String name, BiometricCharacteristic sourceBiometricCharacteristic) {
        super(name);
        this.sourceBiometricCharacteristic = sourceBiometricCharacteristic;
    }

    public EnrichedSoftBiometricCharacteristic(BiometricCharacteristic sourceBiometricCharacteristic) {
        this.sourceBiometricCharacteristic = sourceBiometricCharacteristic;
    }

    @Override
    public String getType() {
        return BiometricCharacteristic.ENRICHED_SOFT;
    }
}

package io.basemod.app.characteristic;


public class PhysiologicalBiometricCharacteristic extends BiometricCharacteristic {

    private PhysiologicalBiometricsAuthenticationMode authenticationMode;
    private String name;

    public PhysiologicalBiometricCharacteristic(String uniqueIdentifier, PhysiologicalBiometricsAuthenticationMode authenticationMode, String name) {
        super(uniqueIdentifier);
        this.authenticationMode = authenticationMode;
        this.name = name;
    }

    public PhysiologicalBiometricCharacteristic(String name, PhysiologicalBiometricsAuthenticationMode authenticationMode) {
        this.name = name;
        this.authenticationMode = authenticationMode;
        this.setUniqueIdentifier(this.name+"-"+this.authenticationMode);
    }

    public PhysiologicalBiometricCharacteristic() {
    }

    public PhysiologicalBiometricsAuthenticationMode getAuthenticationMode() {
        return authenticationMode;
    }

    public void setAuthenticationMode(PhysiologicalBiometricsAuthenticationMode authenticationMode) {
        this.authenticationMode = authenticationMode;
    }

    @Override
    public String getType() {
        return PHYSIOLOGICAL;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PhysiologicalBiometricCharacteristic{" +
                "authenticationMode=" + authenticationMode +
                ", name='" + name + '\'' +
                '}';
    }
}

package io.basemod.app.characteristic;

import java.util.Objects;

public class BehavioralBiometricCharacteristic extends BiometricCharacteristic {

    private boolean isRoutineBased;
    private UserActivity userActivity;

    public BehavioralBiometricCharacteristic() {
    }

    public BehavioralBiometricCharacteristic(String uniqueIdentifier, boolean isRoutineBased, UserActivity userActivity) {
        super(uniqueIdentifier);
        this.isRoutineBased = isRoutineBased;
        this.userActivity = userActivity;
    }

    public BehavioralBiometricCharacteristic(boolean isRoutineBased, UserActivity userActivity) {
        this.isRoutineBased = isRoutineBased;
        this.userActivity = userActivity;
    }

    public BehavioralBiometricCharacteristic(BehavioralBiometricCharacteristic toClone) {
        this(toClone.isRoutineBased(), toClone.getUserActivity());
    }

    public boolean isRoutineBased() {
        return isRoutineBased;
    }

    public void setRoutineBased(boolean routineBased) {
        isRoutineBased = routineBased;
    }

    public UserActivity getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(UserActivity userActivity) {
        this.userActivity = userActivity;
    }

    @Override
    public String toString() {
        return "BehavioralBiometricCharacteristic{" +
                "id='" + super.getId() + '\'' +
                ", isProfileBased=" + isRoutineBased +
                ", userActivity=" + userActivity +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        BehavioralBiometricCharacteristic that = (BehavioralBiometricCharacteristic) other;
        return getUniqueIdentifier() == that.getUniqueIdentifier();
    }

    @Override
    public String getType() {
        return BEHAVIORAL;
    }

    @Override
    public String getName() {
        return this.userActivity.getName();
    }

}

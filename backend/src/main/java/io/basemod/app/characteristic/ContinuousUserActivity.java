package io.basemod.app.characteristic;

import java.util.Objects;

public class ContinuousUserActivity extends UserActivity {

    private String recurrentAction;

    public ContinuousUserActivity(String name, String recurrentAction) {
        super(name);
        this.recurrentAction = recurrentAction;
    }

    public ContinuousUserActivity(String recurrentAction) {
        this.recurrentAction = recurrentAction;
    }

    public ContinuousUserActivity() {
    }

    @Override
    public String getType() {
        return UserActivity.CONTINUOUS_TYPE;
    }

    public String getRecurrentAction() {
        return recurrentAction;
    }

    public void setRecurrentAction(String recurrentAction) {
        this.recurrentAction = recurrentAction;
    }

    @Override
    public String toString() {
        return "ContinuousUserActivity{" +
                "recurrentAction='" + recurrentAction + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ContinuousUserActivity that = (ContinuousUserActivity) other;
        return recurrentAction.equalsIgnoreCase(that.recurrentAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recurrentAction);
    }
}

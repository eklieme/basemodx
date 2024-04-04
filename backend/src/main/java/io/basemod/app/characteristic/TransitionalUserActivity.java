package io.basemod.app.characteristic;

public class TransitionalUserActivity extends UserActivity {

    private String situationBefore;
    private String situationAfterwards;

    public TransitionalUserActivity(String name, String situationBefore, String situationAfterwards) {
        super(name);
        this.situationBefore = situationBefore;
        this.situationAfterwards = situationAfterwards;
    }

    public TransitionalUserActivity(String situationBefore, String situationAfterwards) {
        this.situationBefore = situationBefore;
        this.situationAfterwards = situationAfterwards;
    }

    public TransitionalUserActivity() {
    }

    @Override
    public String getType() {
        return UserActivity.TRANSITIONAL_TYPE;
    }

    public String getSituationBefore() {
        return situationBefore;
    }

    public void setSituationBefore(String situationBefore) {
        this.situationBefore = situationBefore;
    }

    public String getSituationAfterwards() {
        return situationAfterwards;
    }

    public void setSituationAfterwards(String situationAfterwards) {
        this.situationAfterwards = situationAfterwards;
    }

    @Override
    public String toString() {
        return "TransitionalUserActivity{" +
                "situationBefore='" + situationBefore + '\'' +
                ", situationAfterwards='" + situationAfterwards + '\'' +
                '}';

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransitionalUserActivity that = (TransitionalUserActivity) o;
        return situationBefore.equalsIgnoreCase(that.situationBefore) &&
                situationAfterwards.equalsIgnoreCase(that.situationAfterwards);
    }

}

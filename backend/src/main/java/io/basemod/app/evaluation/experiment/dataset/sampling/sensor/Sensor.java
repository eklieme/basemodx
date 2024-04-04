package io.basemod.app.evaluation.experiment.dataset.sampling.sensor;

import io.basemod.app.domain.NamedModelledDomainElement;

import java.util.List;
import java.util.Objects;

public class Sensor extends NamedModelledDomainElement {


    private List<String> sensorDimensionIds;
    private boolean continuous;

    public Sensor() {
    }

    public Sensor(String name, List<String> sensorDimensionIds, boolean continuous) {
        super(name);
        this.sensorDimensionIds = sensorDimensionIds;
        this.continuous = continuous;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public List<String> getSensorDimensionIds() {
        return sensorDimensionIds;
    }

    public void setSensorDimensionIds(List<String> sensorDimensionIds) {
        this.sensorDimensionIds = sensorDimensionIds;
    }

    public void addSensorDimensionId(String sensorDimensionId) {
        this.sensorDimensionIds.add(sensorDimensionId);
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "dimensionIds=" + sensorDimensionIds +
                ", continuous=" + continuous +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Sensor sensor = (Sensor) other;
        return continuous == sensor.continuous &&
                Objects.equals(sensorDimensionIds, sensor.sensorDimensionIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sensorDimensionIds, continuous);
    }
}

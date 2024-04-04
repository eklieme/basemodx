package io.basemod.app.evaluation.experiment.dataset.sampling.sensor;

import java.util.ArrayList;
import java.util.List;

public class EnrichedSensor extends Sensor {

    private List<SensorDimension> sensorDimensions;



    public EnrichedSensor(String name, List<String> sensorDimensionIds, boolean continuous, List<SensorDimension> sensorDimensions) {
        super(name, sensorDimensionIds, continuous);
        this.sensorDimensions = sensorDimensions;
    }

    public List<SensorDimension> getSensorDimensions() {
        return sensorDimensions;
    }

    public void setSensorDimensions(List<SensorDimension> sensorDimensions) {
        this.sensorDimensions = sensorDimensions;
    }

    public void addSensorDimension(SensorDimension sensorDimension) {
        if(sensorDimensions == null) {
            this.sensorDimensions = new ArrayList<>();
        }
        this.sensorDimensions.add(sensorDimension);
    }

    public EnrichedSensor() {
    }

    @Override
    public String toString() {
        return "EnrichedSensor{" +
                "sensorDimensions=" + sensorDimensions +
                "} " + super.toString();
    }
}

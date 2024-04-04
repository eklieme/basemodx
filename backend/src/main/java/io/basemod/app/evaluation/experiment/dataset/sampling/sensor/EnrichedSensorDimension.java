package io.basemod.app.evaluation.experiment.dataset.sampling.sensor;

public class EnrichedSensorDimension extends SensorDimension{

    private Sensor sensor;

    public EnrichedSensorDimension() {

    }

    public EnrichedSensorDimension(String name, Sensor sensor) {
        super(name);
        this.sensor = sensor;
    }

    public EnrichedSensorDimension(Sensor sensor) {
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}

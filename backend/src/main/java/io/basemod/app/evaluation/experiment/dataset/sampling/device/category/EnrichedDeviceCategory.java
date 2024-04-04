package io.basemod.app.evaluation.experiment.dataset.sampling.device.category;

import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.EnrichedSensor;

import java.util.List;

public class EnrichedDeviceCategory extends DeviceCategory {

    private List<EnrichedSensor> sensors;

    public EnrichedDeviceCategory() {

    };

    public EnrichedDeviceCategory(String name, List<String> sensorIds, List<EnrichedSensor> sensors) {
        super(name, sensorIds);
        this.sensors = sensors;
    }

    public EnrichedDeviceCategory(String id, String name, List<String> sensorIds, List<EnrichedSensor> sensors) {
        super(id, name, sensorIds);
        this.sensors = sensors;
    }

    public EnrichedDeviceCategory(List<String> sensorIds, List<EnrichedSensor> sensors) {
        super(sensorIds);
        this.sensors = sensors;
    }

    public EnrichedDeviceCategory(String id, ModelledElementDetail modelledElementDetail, String name, List<String> sensorIds, List<EnrichedSensor> sensors) {
        super(id, modelledElementDetail, name, sensorIds);
        this.sensors = sensors;
    }

    public EnrichedDeviceCategory(ModelledElementDetail modelledElementDetail, String name, List<String> sensorIds, List<EnrichedSensor> sensors) {
        super(modelledElementDetail, name, sensorIds);
        this.sensors = sensors;
    }

    public EnrichedDeviceCategory(String name, List<EnrichedSensor> sensors) {
        super(name);
        this.sensors = sensors;
    }

    public EnrichedDeviceCategory(List<EnrichedSensor> sensors) {
        this.sensors = sensors;
    }

    public List<EnrichedSensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<EnrichedSensor> sensors) {
        this.sensors = sensors;
    }

    @Override
    public String toString() {
        return "EnrichedDeviceCategory{" +
                "sensors=" + sensors +
                "} " + super.toString();
    }
}

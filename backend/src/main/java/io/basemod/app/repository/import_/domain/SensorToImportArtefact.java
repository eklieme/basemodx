package io.basemod.app.repository.import_.domain;

import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.Sensor;

public class SensorToImportArtefact {

    private String parentDeviceId;
    private Sensor sensorToImport;

    public SensorToImportArtefact(String parentDeviceId, Sensor sensorToImport) {
        this.parentDeviceId = parentDeviceId;
        this.sensorToImport = sensorToImport;
    }

    public String getParentDeviceId() {
        return parentDeviceId;
    }

    public void setParentDeviceId(String parentDeviceId) {
        this.parentDeviceId = parentDeviceId;
    }

    public Sensor getSensorToImport() {
        return sensorToImport;
    }

    public void setSensorToImport(Sensor sensorToImport) {
        this.sensorToImport = sensorToImport;
    }
}

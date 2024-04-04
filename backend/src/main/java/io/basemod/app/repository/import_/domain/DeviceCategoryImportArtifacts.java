package io.basemod.app.repository.import_.domain;

import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.EnrichedDeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.EnrichedSensor;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.EnrichedSensorDimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceCategoryImportArtifacts {

    List<EnrichedDeviceCategory> deviceCategoriesToNewlyImport;
    Map<String,List<EnrichedSensor>> sensorsToFinallyImportToExistingDeviceCategory;
    List<EnrichedSensorDimension> sensorDimensionsToFinallyImport;

    public DeviceCategoryImportArtifacts(List<EnrichedDeviceCategory> deviceCategoriesToNewlyImport, Map<String, List<EnrichedSensor>> sensorsToFinallyImportToExistingDeviceCategory, List<EnrichedSensorDimension> sensorDimensionsToFinallyImport) {
        this.deviceCategoriesToNewlyImport = deviceCategoriesToNewlyImport;
        this.sensorsToFinallyImportToExistingDeviceCategory = sensorsToFinallyImportToExistingDeviceCategory;
        this.sensorDimensionsToFinallyImport = sensorDimensionsToFinallyImport;
    }

    public List<EnrichedDeviceCategory> getDeviceCategoriesToNewlyImport() {
        return deviceCategoriesToNewlyImport;
    }

    public void setDeviceCategoriesToNewlyImport(List<EnrichedDeviceCategory> deviceCategoriesToNewlyImport) {
        this.deviceCategoriesToNewlyImport = deviceCategoriesToNewlyImport;
    }

    public Map<String, List<EnrichedSensor>> getSensorsToFinallyImportToExistingDeviceCategory() {
        return sensorsToFinallyImportToExistingDeviceCategory;
    }

    public void setSensorsToFinallyImportToExistingDeviceCategory(Map<String, List<EnrichedSensor>> sensorsToFinallyImportToExistingDeviceCategory) {
        this.sensorsToFinallyImportToExistingDeviceCategory = sensorsToFinallyImportToExistingDeviceCategory;
    }

    public List<EnrichedSensorDimension> getSensorDimensionsToFinallyImport() {
        return sensorDimensionsToFinallyImport;
    }

    public void setSensorDimensionsToFinallyImport(List<EnrichedSensorDimension> sensorDimensionsToFinallyImport) {
        this.sensorDimensionsToFinallyImport = sensorDimensionsToFinallyImport;
    }
}

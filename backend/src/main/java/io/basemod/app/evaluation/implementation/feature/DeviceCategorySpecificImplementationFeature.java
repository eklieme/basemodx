package io.basemod.app.evaluation.implementation.feature;

import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;

public class DeviceCategorySpecificImplementationFeature extends ImplementationFeature {

    public DeviceCategory deviceCategory;

    public DeviceCategorySpecificImplementationFeature(String featureDescription, DeviceCategory deviceCategory) {
        super(featureDescription);
        this.deviceCategory = deviceCategory;
    }

    public DeviceCategory getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(DeviceCategory deploymentLocation) {
        this.deviceCategory = deploymentLocation;
    }
}

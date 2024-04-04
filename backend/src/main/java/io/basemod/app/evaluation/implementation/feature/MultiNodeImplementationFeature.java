package io.basemod.app.evaluation.implementation.feature;

import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;

import java.util.List;

public class MultiNodeImplementationFeature extends ImplementationFeature {

    private List<DeviceCategory> affectedDeviceCategories;

    public MultiNodeImplementationFeature(String featureDescription, List<DeviceCategory> affectedDeviceCategories) {
        super(featureDescription);
        this.affectedDeviceCategories = affectedDeviceCategories;
    }

    public List<DeviceCategory> getAffectedDeviceCategories() {
        return affectedDeviceCategories;
    }

    public void setAffectedDeviceCategories(List<DeviceCategory> affectedDeviceCategories) {
        this.affectedDeviceCategories = affectedDeviceCategories;
    }

    @Override
    public String toString() {
        return "MultiNodeImplementationFeature{" +
                "affectedDeviceCategories=" + affectedDeviceCategories +
                "} " + super.toString();
    }
}

package io.basemod.app.evaluation.experiment.dataset.sampling.device.device;

import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;

public class EnrichedSampleDevice extends SampleDevice {

    private DeviceCategory deviceCategory;

    public EnrichedSampleDevice() {

    }

    public EnrichedSampleDevice(String name, String deviceCategoryId, String deviceManufacturer, DeviceCategory deviceCategory) {
        super(name, deviceCategoryId, deviceManufacturer);
        this.deviceCategory = deviceCategory;
    }

    public EnrichedSampleDevice(String id, String name, String deviceCategoryId, String deviceManufacturer, DeviceCategory deviceCategory) {
        super(id, name, deviceCategoryId, deviceManufacturer);
        this.deviceCategory = deviceCategory;
    }

    public EnrichedSampleDevice(DeviceCategory deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public EnrichedSampleDevice(SampleDevice sampleDevice, DeviceCategory deviceCategory) {
        this.setId(sampleDevice.getId());
        this.setDeviceManufacturer(sampleDevice.getDeviceManufacturer());
        this.setName(sampleDevice.getName());
        this.setModelledElementDetail(sampleDevice.getModelledElementDetail());
        this.setDeviceCategoryId(sampleDevice.getDeviceCategoryId());
        this.deviceCategory = deviceCategory;
    }

    public DeviceCategory getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(DeviceCategory deviceCategory) {
        this.deviceCategory = deviceCategory;
    }
}

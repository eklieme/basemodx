package io.basemod.app.evaluation.experiment.dataset.sampling.device.device;

import io.basemod.app.domain.NamedModelledDomainElement;

import java.util.Objects;

public class SampleDevice extends NamedModelledDomainElement {

    private String deviceCategoryId;
    private String deviceManufacturer;

    public SampleDevice(String name, String deviceCategoryId, String deviceManufacturer) {
        super(name);
        this.deviceCategoryId = deviceCategoryId;
        this.deviceManufacturer = deviceManufacturer;
    }

    public SampleDevice(String id, String name, String deviceCategoryId, String deviceManufacturer) {
        super(id, name);
        this.deviceCategoryId = deviceCategoryId;
        this.deviceManufacturer = deviceManufacturer;
    }


    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public SampleDevice() {
    }

    public String getDeviceCategoryId() {
        return deviceCategoryId;
    }

    public void setDeviceCategoryId(String deviceCategoryId) {
        this.deviceCategoryId = deviceCategoryId;
    }

    @Override
    public String toString() {
        return "SampleDevice{" +
                "deviceCategoryId=" + deviceCategoryId +
                ", deviceManufacturer='" + deviceManufacturer + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        SampleDevice that = (SampleDevice) other;
        return Objects.equals(deviceCategoryId, that.deviceCategoryId) &&
                deviceManufacturer.equalsIgnoreCase(that.deviceManufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deviceCategoryId, deviceManufacturer);
    }
}

package io.basemod.app.evaluation.experiment.dataset.sampling;

import io.basemod.app.domain.DomainElement;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.SensorDimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SampledBiometric extends DomainElement {

    private List<String> sampleDeviceIds;
    private String sampledCharacteristicId;
    private List<String> samplingContextIds;
    private String supportCharacteristicId;


    public SampledBiometric(String id, List<String> sampleDeviceIds, String sampledCharacteristicId, List<String> samplingContextIds, String supportCharacteristicId) {
        super(id);
        this.sampleDeviceIds = sampleDeviceIds;
        this.sampledCharacteristicId = sampledCharacteristicId;
        this.samplingContextIds = samplingContextIds;
        this.supportCharacteristicId = supportCharacteristicId;
    }

    public SampledBiometric(List<String> sampleDeviceIds, String sampledCharacteristicId, List<String> samplingContextIds, String supportCharacteristicId) {
        this.sampleDeviceIds = sampleDeviceIds;
        this.sampledCharacteristicId = sampledCharacteristicId;
        this.samplingContextIds = samplingContextIds;
        this.supportCharacteristicId = supportCharacteristicId;
    }

    public SampledBiometric() {
    }


    public List<String> getSampleDeviceIds() {
        return sampleDeviceIds;
    }

    public void setSampleDeviceIds(List<String> sampleDeviceIds) {
        this.sampleDeviceIds = sampleDeviceIds;
    }

    public void addSampleDeviceId(String sampleDeviceId) {
        if(this.sampleDeviceIds==null) {
            this.sampleDeviceIds = new ArrayList<>();
        }
        this.sampleDeviceIds.add(sampleDeviceId);
    }

    public String getSampledCharacteristicId() {
        return sampledCharacteristicId;
    }

    public void setSampledCharacteristicId(String sampledCharacteristicId) {
        this.sampledCharacteristicId = sampledCharacteristicId;
    }

    public List<String> getSamplingContextIds() {
        return samplingContextIds;
    }

    public void setSamplingContextIds(List<String> samplingContextIds) {
        this.samplingContextIds = samplingContextIds;
    }

    public void addSamplingContextId(String samplingContextId) {
        if(this.samplingContextIds==null) {
            this.samplingContextIds = new ArrayList<>();
        }
        this.samplingContextIds.add(samplingContextId);
    }

    public String getSupportCharacteristicId() {
        return supportCharacteristicId;
    }

    public void setSupportCharacteristicId(String supportCharacteristicId) {
        this.supportCharacteristicId = supportCharacteristicId;
    }

    @Override
    public String toString() {
        return "SampledBiometric{" +
                "sampleDevices=" + sampleDeviceIds +
                ", characteristic=" + sampledCharacteristicId +
                ", samplingContexts=" + samplingContextIds +
                ", supportCharacteristic=" + supportCharacteristicId +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        SampledBiometric that = (SampledBiometric) other;
        return  Objects.equals(sampleDeviceIds, that.sampleDeviceIds) &&
                Objects.equals(sampledCharacteristicId, that.sampledCharacteristicId) &&
                Objects.equals(samplingContextIds, that.samplingContextIds) &&
                Objects.equals(supportCharacteristicId, that.supportCharacteristicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sampleDeviceIds, sampledCharacteristicId, samplingContextIds, supportCharacteristicId);
    }
}

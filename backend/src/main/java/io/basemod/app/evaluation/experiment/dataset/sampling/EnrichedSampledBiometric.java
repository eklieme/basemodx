package io.basemod.app.evaluation.experiment.dataset.sampling;

import io.basemod.app.characteristic.BehavioralBiometricCharacteristic;
import io.basemod.app.characteristic.BiometricCharacteristic;
import io.basemod.app.domain.DomainElement;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContext;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnrichedSampledBiometric extends DomainElement {

    private List<EnrichedSampleDevice> sampleDevices;
    private BiometricCharacteristic sampledCharacteristic;
    private List<SamplingContext> samplingContexts;
    private BehavioralBiometricCharacteristic supportCharacteristic;

    public EnrichedSampledBiometric() {
        this.sampleDevices = new ArrayList<>();
        this.samplingContexts = new ArrayList<>();
    }

    public EnrichedSampledBiometric(String id, List<EnrichedSampleDevice> sampleDevices, BiometricCharacteristic sampledCharacteristic, List<SamplingContext> samplingContexts, BehavioralBiometricCharacteristic supportCharacteristic) {
        super(id);
        this.sampleDevices = sampleDevices;
        this.sampledCharacteristic = sampledCharacteristic;
        this.samplingContexts = samplingContexts;
        this.supportCharacteristic = supportCharacteristic;
    }

    public EnrichedSampledBiometric(List<EnrichedSampleDevice> sampleDevices, BiometricCharacteristic sampledCharacteristic, List<SamplingContext> samplingContexts, BehavioralBiometricCharacteristic supportCharacteristic) {
        this.sampleDevices = sampleDevices;
        this.sampledCharacteristic = sampledCharacteristic;
        this.samplingContexts = samplingContexts;
        this.supportCharacteristic = supportCharacteristic;
    }

    public List<EnrichedSampleDevice> getSampleDevices() {
        return sampleDevices;
    }

    public void setSampleDevices(List<EnrichedSampleDevice> sampleDevices) {
        this.sampleDevices = sampleDevices;
    }

    public BiometricCharacteristic getSampledCharacteristic() {
        return sampledCharacteristic;
    }

    public void setSampledCharacteristic(BiometricCharacteristic sampledCharacteristic) {
        this.sampledCharacteristic = sampledCharacteristic;
    }

    public List<SamplingContext> getSamplingContexts() {
        return samplingContexts;
    }

    public void setSamplingContexts(List<SamplingContext> samplingContexts) {
        this.samplingContexts = samplingContexts;
    }

    public BehavioralBiometricCharacteristic getSupportCharacteristic() {
        return supportCharacteristic;
    }

    public void setSupportCharacteristic(BehavioralBiometricCharacteristic supportCharacteristic) {
        this.supportCharacteristic = supportCharacteristic;
    }

    @Override
    public String toString() {
        return "EnrichedSampledBiometric{" +
                "sampleDevices=" + sampleDevices +
                ", sampledCharacteristic=" + sampledCharacteristic +
                ", samplingContexts=" + samplingContexts +
                ", supportCharacteristic=" + supportCharacteristic +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EnrichedSampledBiometric that = (EnrichedSampledBiometric) o;
        return sampleDevices.equals(that.sampleDevices) && sampledCharacteristic.equals(that.sampledCharacteristic) && samplingContexts.equals(that.samplingContexts) && Objects.equals(supportCharacteristic, that.supportCharacteristic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sampleDevices);
    }
}

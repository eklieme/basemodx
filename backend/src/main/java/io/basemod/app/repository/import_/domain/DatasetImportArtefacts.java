package io.basemod.app.repository.import_.domain;

import io.basemod.app.characteristic.BiometricCharacteristic;
import io.basemod.app.evaluation.experiment.dataset.*;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContext;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDevice;

import java.util.ArrayList;
import java.util.List;

public class DatasetImportArtefacts {

    List<EnrichedDataSet> dataSetsToImport;
    List<EnrichedSampleDevice> sampleDevicesToImport;
    List<SamplingContext> samplingContextsToImport;

    List<BiometricCharacteristic> biometricCharacteristicsToImport;

    public DatasetImportArtefacts() {

        dataSetsToImport = new ArrayList<>();
        sampleDevicesToImport = new ArrayList<>();
        samplingContextsToImport = new ArrayList<>();
        biometricCharacteristicsToImport = new ArrayList<>();

    }

    public DatasetImportArtefacts(List<EnrichedDataSet> dataSetsToImport, List<EnrichedSampleDevice> sampleDevicesToImport, List<SamplingContext> samplingContextsToImport, List<BiometricCharacteristic> biometricCharacteristicsToImport) {
        this.dataSetsToImport = dataSetsToImport;
        this.sampleDevicesToImport = sampleDevicesToImport;
        this.samplingContextsToImport = samplingContextsToImport;
        this.biometricCharacteristicsToImport = biometricCharacteristicsToImport;
    }

    public List<SamplingContext> getSamplingContextsToImport() {
        return samplingContextsToImport;
    }

    public void setSamplingContextsToImport(List<SamplingContext> evaluationContextsToImport) {
        this.samplingContextsToImport = evaluationContextsToImport;
    }

    public void addSamplingContextsToImport(List<SamplingContext> evaluationContextsToImport) {
        this.samplingContextsToImport.addAll(evaluationContextsToImport);
    }

    public List<EnrichedDataSet> getDataSetsToImport() {
        return dataSetsToImport;
    }

    public void setDataSetsToImport(List<EnrichedDataSet> dataSetsToImport) {
        this.dataSetsToImport = dataSetsToImport;
    }

    public List<EnrichedSampleDevice> getSampleDevicesToImport() {
        return sampleDevicesToImport;
    }

    public void setSampleDevicesToImport(List<EnrichedSampleDevice> sampleDevicesToImport) {
        this.sampleDevicesToImport = sampleDevicesToImport;
    }

    public List<BiometricCharacteristic> getBiometricCharacteristicsToImport() {
        return biometricCharacteristicsToImport;
    }

    public void setBiometricCharacteristicsToImport(List<BiometricCharacteristic> biometricCharacteristicsToImport) {
        this.biometricCharacteristicsToImport = biometricCharacteristicsToImport;
    }

    @Override
    public String toString() {
        return "DatasetImportArtefacts{" +
                "dataSetsToImport=" + dataSetsToImport +
                ", sampleDevicesToImport=" + sampleDevicesToImport +
                ", samplingContextsToImport=" + samplingContextsToImport +
                ", biometricCharacteristicsToImport=" + biometricCharacteristicsToImport +
                '}';
    }

    public void addSampleDevicesToImport(List<EnrichedSampleDevice> sampleDevicesToImport) {
        if(this.sampleDevicesToImport==null) {
            this.sampleDevicesToImport = new ArrayList<>();
        }
        this.sampleDevicesToImport.addAll(sampleDevicesToImport);
    }
}

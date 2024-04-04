package io.basemod.app.repository.export_;

import io.basemod.app.base.Reference;
import io.basemod.app.characteristic.BiometricCharacteristic;
import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.dataset.participant.ParticipantInformation;
import io.basemod.app.evaluation.experiment.dataset.sampling.EnrichedSampledBiometric;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.EnrichedDeviceCategory;

import java.io.Serializable;
import java.util.List;

public class DatasetForExportArtefact implements Serializable {

    private List<EnrichedDeviceCategory> deviceCategoriesForExport;
    private List<EnrichedDataSet> enrichedDataSetsForExport;

    private List<BiometricCharacteristic> biometricCharacteristics;

    public DatasetForExportArtefact() {

    }

    public DatasetForExportArtefact(List<EnrichedDeviceCategory> deviceCategoriesForExport, List<EnrichedDataSet> enrichedDataSetsForExport, List<BiometricCharacteristic> biometricCharacteristics) {
        this.deviceCategoriesForExport = deviceCategoriesForExport;
        this.enrichedDataSetsForExport = enrichedDataSetsForExport;
        this.biometricCharacteristics = biometricCharacteristics;
    }

    public List<BiometricCharacteristic> getBiometricCharacteristics() {
        return biometricCharacteristics;
    }

    public void setBiometricCharacteristics(List<BiometricCharacteristic> biometricCharacteristics) {
        this.biometricCharacteristics = biometricCharacteristics;
    }

    public List<EnrichedDataSet> getEnrichedDataSetsForExport() {
        return enrichedDataSetsForExport;
    }

    public void setEnrichedDataSetsForExport(List<EnrichedDataSet> enrichedDataSetsForExport) {
        this.enrichedDataSetsForExport = enrichedDataSetsForExport;
    }

    public List<EnrichedDeviceCategory> getDeviceCategoriesForExport() {
        return deviceCategoriesForExport;
    }

    public void setDeviceCategoriesForExport(List<EnrichedDeviceCategory> deviceCategoriesForExport) {
        this.deviceCategoriesForExport = deviceCategoriesForExport;
    }
}

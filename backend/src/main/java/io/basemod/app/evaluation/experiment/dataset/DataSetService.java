package io.basemod.app.evaluation.experiment.dataset;

import io.basemod.app.characteristic.BehavioralBiometricCharacteristic;
import io.basemod.app.characteristic.BiometricCharacteristicCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.EnrichedSampledBiometric;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContextCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceCRUDRepository;
import io.basemod.app.repository.ModelledElementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSetService {

    Logger logger = LoggerFactory.getLogger(DataSetService.class);

    BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository;
    SampleDeviceCRUDRepository sampleDeviceCRUDRepository;

    SamplingContextCRUDRepository samplingContextCRUDRepository;

    DeviceCategoryCRUDRepository deviceCategoryCRUDRepository;

   @Autowired
    public DataSetService(BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository, SampleDeviceCRUDRepository sampleDeviceCRUDRepository, SamplingContextCRUDRepository samplingContextCRUDRepository, DeviceCategoryCRUDRepository deviceCategoryCRUDRepository) {
        this.biometricCharacteristicCRUDRepository = biometricCharacteristicCRUDRepository;
        this.sampleDeviceCRUDRepository = sampleDeviceCRUDRepository;
        this.samplingContextCRUDRepository = samplingContextCRUDRepository;
        this.deviceCategoryCRUDRepository = deviceCategoryCRUDRepository;
    }

    public EnrichedDataSet enrichDataset(DataSet datasetToEnrich) {

        EnrichedDataSet enrichedDataSet = new EnrichedDataSet();

        // set basic information
        enrichedDataSet.setId(datasetToEnrich.getId());
        enrichedDataSet.setName(datasetToEnrich.getName());
        enrichedDataSet.setReference(datasetToEnrich.getReference());
        enrichedDataSet.setParticipantInformation(datasetToEnrich.getParticipantInformation());
        enrichedDataSet.setModelledElementDetail(datasetToEnrich.getModelledElementDetail());

        datasetToEnrich.getSampledBiometrics().forEach(sampledBiometric -> {

            logger.debug("\t\t\t...enriching sampled biometric");
            EnrichedSampledBiometric enrichedSampledBiometric = new EnrichedSampledBiometric();

            enrichedSampledBiometric.setSampledCharacteristic(
                    biometricCharacteristicCRUDRepository.findById(sampledBiometric.getSampledCharacteristicId()).get());

            if(sampledBiometric.getSupportCharacteristicId()!=null) {
                enrichedSampledBiometric.setSupportCharacteristic(
                        (BehavioralBiometricCharacteristic) biometricCharacteristicCRUDRepository.findById(sampledBiometric.getSupportCharacteristicId()).get());
            }

            sampledBiometric.getSampleDeviceIds().forEach(sampleDeviceId -> {
                        SampleDevice fullSampleDevice = sampleDeviceCRUDRepository.findById(sampleDeviceId).get();
                enrichedSampledBiometric.getSampleDevices().add(new EnrichedSampleDevice(
                        fullSampleDevice,
                        deviceCategoryCRUDRepository.findById(fullSampleDevice.getDeviceCategoryId()).get()));
            });
            logger.debug("\t\t\t\t...resolved {} sample devices", enrichedSampledBiometric.getSampleDevices().size());

            if(sampledBiometric.getSamplingContextIds()!=null && sampledBiometric.getSamplingContextIds().size()>0) {
                sampledBiometric.getSamplingContextIds().forEach(samplingContextId -> {
                    enrichedSampledBiometric.getSamplingContexts().add(
                            samplingContextCRUDRepository.findById(samplingContextId).get());
                });
                logger.debug("\t\t\t\t...resolved {} sampling contexts", enrichedSampledBiometric.getSamplingContexts().size());
            }

            enrichedDataSet.getSampledBiometrics().add(enrichedSampledBiometric);
        });

        return enrichedDataSet;
    }

}

package io.basemod.app.evaluation.experiment.dataset.sampling.device.device;

import io.basemod.app.base.*;
import io.basemod.app.evaluation.experiment.dataset.DataSet;
import io.basemod.app.evaluation.experiment.dataset.DataSetCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.dataset.sampling.SampledBiometric;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryCRUDRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SampleDeviceService {

    Logger logger = LoggerFactory.getLogger(SampleDeviceService.class);

    private SampleDeviceCRUDRepository sampleDeviceCRUDRepository;
    private DataSetCRUDRepository dataSetCRUDRepository;
    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;

    private DeviceCategoryCRUDRepository deviceCategoryCRUDRepository;

    @Autowired
    public SampleDeviceService(SampleDeviceCRUDRepository sampleDeviceCRUDRepository, DataSetCRUDRepository dataSetCRUDRepository, BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository, DeviceCategoryCRUDRepository deviceCategoryCRUDRepository) {
        this.sampleDeviceCRUDRepository = sampleDeviceCRUDRepository;
        this.dataSetCRUDRepository = dataSetCRUDRepository;
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
        this.deviceCategoryCRUDRepository = deviceCategoryCRUDRepository;
    }




    public void deleteSampleDevice(String sampleDeviceIdToDelete,
                                   List<EnrichedBaseForExport> enrichedBASE,
                                   List<EnrichedDataSet> allUserEditableDatasets) {

        SampleDevice sampleDeviceToDelete = sampleDeviceCRUDRepository.findById(sampleDeviceIdToDelete).get();

        // we need to find all sampled biometrics where the device was used and modify or even remove them

        allUserEditableDatasets.forEach(enrichedDataSet -> {

            // has to be there
            DataSet dataSetToPotentiallyChangeOrRemove = dataSetCRUDRepository.findById(enrichedDataSet.getId()).get();

            List<SampledBiometric> sampledBiometricsToDelete = new ArrayList<>();

            dataSetToPotentiallyChangeOrRemove.getSampledBiometrics().forEach(sampledBiometric -> {
                // check whether sample device was used
                sampledBiometric.getSampleDeviceIds().forEach(sampleDeviceId -> {
                    if(sampleDeviceId.equals(sampleDeviceIdToDelete)) {
                        logger.debug("\t\t\t...sample device '{}' is part of sampled biometric in dataset '{}', remove from there",
                                sampleDeviceToDelete.getName(), enrichedDataSet.getName());
                        if(sampledBiometric.getSampleDeviceIds().size()==1) {
                            logger.debug("\t\t\t\t...sampled biometric has no other sample device and needs to be removed");
                            sampledBiometricsToDelete.add(sampledBiometric);
                        } else {
                            logger.debug("\t\t\t\t...sampled biometric has other sample devices and needs to be changed only");
                            sampledBiometric.getSampleDeviceIds().remove(sampleDeviceIdToDelete);
                        }
                    }
                });
            });


            if(sampledBiometricsToDelete.size() == dataSetToPotentiallyChangeOrRemove.getSampledBiometrics().size()) {
                logger.debug("\t\t\t..no sampled biometric remains after removing sample device, DELETE dataset '{}'",
                        dataSetToPotentiallyChangeOrRemove.getName());
                dataSetCRUDRepository.delete(dataSetToPotentiallyChangeOrRemove);

                logger.debug("\t\t\t..dataset '{}' was removed, check whether BASE using this dataset need modifications",
                        dataSetToPotentiallyChangeOrRemove.getName());

                enrichedBASE.forEach(enrichedBiometricAuthenticationSystemAndEvaluation -> {

                    // has to be there
                    BiometricAuthenticationSystemAndEvaluation biometricAuthenticationSystemAndEvaluationToChange
                            = biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBiometricAuthenticationSystemAndEvaluation.getId()).get();

                    boolean baseWasChanged = false;

                    if(biometricAuthenticationSystemAndEvaluationToChange.getBaseEvaluation()!=null
                            && biometricAuthenticationSystemAndEvaluationToChange.getBaseEvaluation().getExperimentSpecificEvaluation()!=null) {

                        // experimental evaluation result are modelled, check whether dataset is included
                        List<String> usedDatasetIds = biometricAuthenticationSystemAndEvaluationToChange
                                .getBaseEvaluation()
                                .getExperimentSpecificEvaluation()
                                .getUsedDatasetIds();

                        boolean resetExperimentalEvaluation = false;

                        if(usedDatasetIds.contains(dataSetToPotentiallyChangeOrRemove.getId())) {
                            baseWasChanged = true;
                            logger.debug("\t\t\t\t...dataset '{}' is part of experimental evaluation from BASE '{}', remove!",
                                    dataSetToPotentiallyChangeOrRemove.getName(),
                                    biometricAuthenticationSystemAndEvaluationToChange.getName());

                            usedDatasetIds.remove(dataSetToPotentiallyChangeOrRemove.getId());

                            if(usedDatasetIds.size() == 1) {
                                logger.debug("\t\t\t\t\t...dataset '{}' is only dataset in experimental evaluation from BASE '{}', reset experimental evaluation",
                                        dataSetToPotentiallyChangeOrRemove.getName(),
                                        biometricAuthenticationSystemAndEvaluationToChange.getName());
                                resetExperimentalEvaluation = true;
                            }
                        } else {
                            logger.debug("\t\t\t\t...dataset '{}' is NOT part of experimental evaluation from BASE '{}', do nothing ;)",
                                    dataSetToPotentiallyChangeOrRemove.getName(),
                                    biometricAuthenticationSystemAndEvaluationToChange.getName());
                        }

                        if(resetExperimentalEvaluation) {
                            logger.debug("\t\t\t\t\t...reset experimental evaluation of BASE '{}'",
                                    biometricAuthenticationSystemAndEvaluationToChange.getName());
                            biometricAuthenticationSystemAndEvaluationToChange.getBaseEvaluation().setExperimentSpecificEvaluation(null);

                            if(biometricAuthenticationSystemAndEvaluationToChange.getBaseEvaluation().getImplementationSpecificEvaluationResults()==null
                                    || biometricAuthenticationSystemAndEvaluationToChange.getBaseEvaluation().getImplementationSpecificEvaluationResults().size()==0) {

                                logger.debug("\t\t\t\t\t...neither experiment-based nor implementation-based BASED experiments of '{}' remain, " +
                                                "reset modelling progress to target architecture",
                                        biometricAuthenticationSystemAndEvaluationToChange.getName());

                                biometricAuthenticationSystemAndEvaluationToChange.setBaseEvaluation(null);
                                biometricAuthenticationSystemAndEvaluationToChange.setEvaluationCriteriaGrants(null);
                                biometricAuthenticationSystemAndEvaluationToChange.setModellingProgress(ModellingProgress.TARGETARCHITECTURE);
                            }
                        }

                        if(baseWasChanged) {
                            logger.debug("\t\t\t\t...update BASE '{}'", biometricAuthenticationSystemAndEvaluationToChange.getName());
                            biometricAuthenticationSystemAndEvaluationRepository.save(biometricAuthenticationSystemAndEvaluationToChange);
                        }

                    }

                });

            } else if(sampledBiometricsToDelete.size()>0) {
                logger.debug("\t\t\t..{} of {} sampled biometrics of dataset '{}' need to be removed",
                        sampledBiometricsToDelete.size(),
                        dataSetToPotentiallyChangeOrRemove.getSampledBiometrics().size(),
                        dataSetToPotentiallyChangeOrRemove.getName());

                sampledBiometricsToDelete.forEach(sampledBiometric -> {
                    dataSetToPotentiallyChangeOrRemove.getSampledBiometrics().remove(sampledBiometric);
                });
                logger.debug("\t\t\t\t..{} of {} sampled biometrics of dataset '{}' removed, update dataset",
                        sampledBiometricsToDelete.size(),
                        dataSetToPotentiallyChangeOrRemove.getSampledBiometrics().size(),
                        dataSetToPotentiallyChangeOrRemove.getName());
                dataSetCRUDRepository.save(dataSetToPotentiallyChangeOrRemove);
            } else {
                logger.debug("\t\t\t...dataset '{}' does not require any changes!", dataSetToPotentiallyChangeOrRemove.getName());
            }

        });

        // finally delete sample device
        logger.debug("\t\t...finally, delete sample device");
        sampleDeviceCRUDRepository.deleteById(sampleDeviceIdToDelete);
    }

    public EnrichedSampleDevice enrichSampleDevice(SampleDevice sampleDeviceToEnrich) {
        return new EnrichedSampleDevice(sampleDeviceToEnrich,
                deviceCategoryCRUDRepository.findById(sampleDeviceToEnrich.getDeviceCategoryId()).get());
    }

}

package io.basemod.app.base;

import io.basemod.app.architecture.EnrichedTargetArchitecture;
import io.basemod.app.architecture.ResourceToProtectCRUDRepository;
import io.basemod.app.characteristic.BehavioralBiometricCharacteristic;
import io.basemod.app.characteristic.BiometricCharacteristicCRUDRepository;
import io.basemod.app.characteristic.BiometricCharacteristicService;
import io.basemod.app.characteristic.SoftBiometricCharacteristic;
import io.basemod.app.evaluation.BaseEvaluationService;
import io.basemod.app.evaluation.EnrichedBaseEvaluation;
import io.basemod.app.evaluation.criteria.grant.EvaluationCriteriaGrantService;
import io.basemod.app.evaluation.experiment.biometricsystem.BiometricSystemService;
import io.basemod.app.evaluation.experiment.dataset.DataSetService;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryService;
import io.basemod.app.evaluation.experiment.evaluation.EnrichedExperimentSpecificEvaluation;
import io.basemod.app.evaluation.experiment.dataset.DataSetCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.result.EnrichedExperimentSpecificEvaluationSetup;
import io.basemod.app.evaluation.experiment.evaluation.result.SpecificResultService;
import io.basemod.app.evaluation.implementation.EnrichedImplementationSpecificEvaluationResult;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationResultService;
import io.basemod.app.repository.ModelledElementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrichedBiometricAuthenticationSystemAndEvaluationService {


    Logger logger = LoggerFactory.getLogger(EnrichedBiometricAuthenticationSystemAndEvaluationService.class);

    private ModelledElementService modelledElementService;
    private DataSetCRUDRepository dataSetCRUDRepository;

    private BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository;

    private BiometricCharacteristicService biometricCharacteristicService;

    private ResourceToProtectCRUDRepository resourceToProtectCRUDRepository;

    private SpecificResultService specificResultService;

    private DataSetService dataSetService;

    private EvaluationCriteriaGrantService evaluationCriteriaGrantService;

    private BiometricSystemService biometricSystemService;

    private DeviceCategoryService deviceCategoryService;

    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;

    private ImplementationSpecificEvaluationResultService implementationSpecificEvaluationResultService;

    private BaseEvaluationService baseEvaluationService;

    @Autowired
    public EnrichedBiometricAuthenticationSystemAndEvaluationService(ModelledElementService modelledElementService, DataSetCRUDRepository dataSetCRUDRepository, BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository, BiometricCharacteristicService biometricCharacteristicService, ResourceToProtectCRUDRepository resourceToProtectCRUDRepository, SpecificResultService specificResultService, DataSetService dataSetService, EvaluationCriteriaGrantService evaluationCriteriaGrantService, BiometricSystemService biometricSystemService, DeviceCategoryService deviceCategoryService, BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository, ImplementationSpecificEvaluationResultService implementationSpecificEvaluationResultService, BaseEvaluationService baseEvaluationService) {
        this.modelledElementService = modelledElementService;
        this.dataSetCRUDRepository = dataSetCRUDRepository;
        this.biometricCharacteristicCRUDRepository = biometricCharacteristicCRUDRepository;
        this.biometricCharacteristicService = biometricCharacteristicService;
        this.resourceToProtectCRUDRepository = resourceToProtectCRUDRepository;
        this.specificResultService = specificResultService;
        this.dataSetService = dataSetService;
        this.evaluationCriteriaGrantService = evaluationCriteriaGrantService;
        this.biometricSystemService = biometricSystemService;
        this.deviceCategoryService = deviceCategoryService;
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
        this.implementationSpecificEvaluationResultService = implementationSpecificEvaluationResultService;
        this.baseEvaluationService = baseEvaluationService;
    }

    public EnrichedBaseForExport getEnrichedBaseById(String baseId) {
        BiometricAuthenticationSystemAndEvaluation baseToEnrich =
                biometricAuthenticationSystemAndEvaluationRepository.findById(baseId).get();
        return transformBasesToReturn(baseToEnrich);
    }

    public EnrichedBaseForExport transformBasesToReturn(BiometricAuthenticationSystemAndEvaluation biometricAuthenticationSystemAndEvaluationToEnrich) {
        List<BiometricAuthenticationSystemAndEvaluation> toEnrich = new ArrayList<>();
        toEnrich.add(biometricAuthenticationSystemAndEvaluationToEnrich);

        return transformBasesToReturn(toEnrich).get(0);
    }

    public List<EnrichedBaseForExport> transformBasesToReturn(List<BiometricAuthenticationSystemAndEvaluation> biometricAuthenticationSystemAndEvaluations) {
        List<EnrichedBaseForExport> toReturn = new ArrayList<>();

        biometricAuthenticationSystemAndEvaluations.stream()
                .filter(base -> !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(base))
                .forEach(baseToEnrich -> {


                    logger.debug("transform Base {} to enriched one", baseToEnrich.getName());

                    EnrichedBaseForExport enrichedBase = new EnrichedBaseForExport(baseToEnrich);

                    logger.debug("\t...set base evaluation");
                    enrichedBase.setOriginalBaseEvaluation(baseToEnrich.getBaseEvaluation());

                    // resolve characteristics, we always have them no matter which modelling progress

                    logger.debug("\t...resolve {} biometric characteristics of base '{}'",
                            baseToEnrich.getBiometricCharacteristicIds().size(), baseToEnrich.getName());

                    enrichedBase.setBiometricCharacteristics(
                            baseToEnrich.getBiometricCharacteristicIds().stream().map(biometricCharacteristicId -> {
                                return biometricCharacteristicCRUDRepository.findById(biometricCharacteristicId).get();
                            }).map(biometricCharacteristic -> {
                                if(biometricCharacteristic instanceof SoftBiometricCharacteristic) {
                                    return biometricCharacteristicService.enrichSoftBiometric((SoftBiometricCharacteristic) biometricCharacteristic);
                                }
                               return biometricCharacteristic;
                            }).collect(Collectors.toList()));

                    // enrich target architecture based on modelling state
                    EnrichedTargetArchitecture enrichedTargetArchitecture = new EnrichedTargetArchitecture();

                    if(!baseToEnrich.getModellingProgress().equals(ModellingProgress.CHARACTERISTICS)) {
                        // target architecture exists
                        logger.debug("\t...resolve target architecture as we have modelling state '{}'", baseToEnrich.getModellingProgress());
                        enrichedTargetArchitecture.setDataCapturingDeviceCategories(baseToEnrich.getTargetArchitecture().getDataCapturingDeviceCategoryIds().stream().map(deviceCategoryId -> {
                            return deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(deviceCategoryId);
                        }).collect(Collectors.toList()));
                        enrichedTargetArchitecture.setSignalProcessingDeviceCategories(baseToEnrich.getTargetArchitecture().getSignalProcessingDeviceCategoryIds().stream().map(deviceCategoryId -> {
                            return deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(deviceCategoryId);
                        }).collect(Collectors.toList()));
                        enrichedTargetArchitecture.setSignatureCreationDeviceCategories(baseToEnrich.getTargetArchitecture().getSignatureCreationDeviceCategoryIds().stream().map(deviceCategoryId -> {
                            return deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(deviceCategoryId);
                        }).collect(Collectors.toList()));
                        enrichedTargetArchitecture.setDataStorageDeviceCategories(baseToEnrich.getTargetArchitecture().getDataStorageDeviceCategoryIds().stream().map(deviceCategoryId -> {
                            return deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(deviceCategoryId);
                        }).collect(Collectors.toList()));
                        enrichedTargetArchitecture.setMatchingDeviceCategories(baseToEnrich.getTargetArchitecture().getMatchingDeviceCategoryIds().stream().map(deviceCategoryId -> {
                            return deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(deviceCategoryId);
                        }).collect(Collectors.toList()));
                        enrichedTargetArchitecture.setDecisionDeviceCategories(baseToEnrich.getTargetArchitecture().getDecisionDeviceCategoryIds().stream().map(deviceCategoryId -> {
                            return deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(deviceCategoryId);
                        }).collect(Collectors.toList()));

                        if(baseToEnrich.getTargetArchitecture().getResourceToProtectId()!=null) {
                            enrichedTargetArchitecture.setResourceToProtect(
                                    resourceToProtectCRUDRepository.findById(baseToEnrich.getTargetArchitecture().getResourceToProtectId()).get().getName());
                        }

                    }
                    // set enriched target architecture
                    enrichedBase.setTargetArchitecture(enrichedTargetArchitecture);

                    logger.debug("\t..modelling state '{}' equals '{}' : {}", baseToEnrich.getModellingProgress(), ModellingProgress.EVALUATION,
                            baseToEnrich.getModellingProgress()==ModellingProgress.EVALUATION);
                    logger.debug("\t..modelling state '{}' equals '{}' : {}", baseToEnrich.getModellingProgress(), ModellingProgress.EVALUATION_CRITERIA_GRANTS,
                            baseToEnrich.getModellingProgress()==ModellingProgress.EVALUATION_CRITERIA_GRANTS);
                    logger.debug("\t..modelling state '{}' equals '{}' : {}", baseToEnrich.getModellingProgress(), ModellingProgress.REFERENCE,
                            baseToEnrich.getModellingProgress()==ModellingProgress.REFERENCE);

                    if(baseToEnrich.getModellingProgress()==ModellingProgress.EVALUATION
                            || baseToEnrich.getModellingProgress()==ModellingProgress.EVALUATION_CRITERIA_GRANTS
                            || baseToEnrich.getModellingProgress()==ModellingProgress.REFERENCE) {

                        EnrichedBaseEvaluation enrichedBaseEvaluation =
                                baseEvaluationService.enrichBaseEvaluation(baseToEnrich.getBaseEvaluation(), baseToEnrich.getName());

                        //create enriched base evaluation skeleton
                        enrichedBase.setBaseEvaluation(enrichedBaseEvaluation);

                    } else {
                        enrichedBase.setBaseEvaluation(new EnrichedBaseEvaluation());
                        enrichedBase.getBaseEvaluation().setExperimentSpecificEvaluation(new EnrichedExperimentSpecificEvaluation());
                        enrichedBase.getBaseEvaluation().setImplementationSpecificEvaluationResults(
                                new ArrayList<>());
                    }

                    // enrich evaluation criteria grants if existing
                    if(baseToEnrich.getModellingProgress()==ModellingProgress.EVALUATION_CRITERIA_GRANTS
                            || baseToEnrich.getModellingProgress()==ModellingProgress.REFERENCE) {

                        logger.debug("\t...enrich {} eval crit grants", baseToEnrich.getEvaluationCriteriaGrants().size());
                        enrichedBase.setEvaluationCriteriaGrants(
                                baseToEnrich.getEvaluationCriteriaGrants()
                                        .stream()
                                        .map(evaluationCriteriaGrant -> evaluationCriteriaGrantService.enrichEvaluationCriteriaGrant(evaluationCriteriaGrant))
                                        .collect(Collectors.toList()));

                    }

                    toReturn.add(enrichedBase);
                });

        return toReturn;
    }

}

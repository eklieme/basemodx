package io.basemod.app.repository;

import io.basemod.app.base.*;
import io.basemod.app.base.lifecycle.LifecycleState;
import io.basemod.app.characteristic.BehavioralBiometricCharacteristic;
import io.basemod.app.characteristic.BiometricCharacteristicCRUDRepository;
import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.evaluation.experiment.dataset.DataSet;
import io.basemod.app.evaluation.experiment.dataset.DataSetCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.DataSetService;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.dataset.sampling.EnrichedSampledBiometric;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContextCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.EnrichedDeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceService;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.SensorService;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationResult;
import io.basemod.app.security.authentication.domain.BaseUser;
import io.basemod.app.security.authentication.domain.BaseUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelledElementService {

    public static final String ANONYMOUS_USER_ID = "anonymousUser";
    Logger logger = LoggerFactory.getLogger(ModelledElementService.class);

    private DeviceCategoryCRUDRepository deviceCategoryCRUDRepository;
    private DataSetCRUDRepository dataSetCRUDRepository;
    private DataSetService dataSetService;

    private SampleDeviceService sampleDeviceService;

    private BaseService baseService;

    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;

    @Autowired
    public ModelledElementService(DeviceCategoryCRUDRepository deviceCategoryCRUDRepository, DataSetCRUDRepository dataSetCRUDRepository, DataSetService dataSetService, SampleDeviceService sampleDeviceService, BaseService baseService, BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository) {
        this.deviceCategoryCRUDRepository = deviceCategoryCRUDRepository;
        this.dataSetCRUDRepository = dataSetCRUDRepository;
        this.dataSetService = dataSetService;
        this.sampleDeviceService = sampleDeviceService;
        this.baseService = baseService;
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
    }

    private boolean isAuthenticated() {
        if(SecurityContextHolder.getContext().getAuthentication()!=null) {
            return SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                    && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
        }
        return false;
    }

    private String getUniqueUserId(boolean isAuthenticated) {
        String uniqueUserId = ANONYMOUS_USER_ID;
        if(isAuthenticated) {
            logger.trace("\t\t...current principal: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            uniqueUserId = ((BaseUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUniqueId();
        }
        logger.debug("\t...determined current userId '{}'", uniqueUserId);
        return uniqueUserId;
    }

    public boolean filterOutModelledElementBasedOnLifecycleAndUser(ModelledDomainElement modelledDomainElement) {

        boolean isAuthenticated = isAuthenticated();
        String uniqueUserId = getUniqueUserId(isAuthenticated);

        logger.debug("evaluating whether we can return modelled element {} with lifecycle state {} for user-id '{}' (authenticated: {})",
                modelledDomainElement,
                modelledDomainElement.getModelledElementDetail(),
                uniqueUserId,
                isAuthenticated);

        // edge cases since we are in dev ;)
        if(modelledDomainElement.getModelledElementDetail()==null) {
            logger.debug("\t...modelled element detail is null, return for now");
            // return for now
            return false;
        } else if(modelledDomainElement.getModelledElementDetail().getModelledInitiallyBy()!=null) {
            if (modelledDomainElement.getModelledElementDetail().getModelledInitiallyBy().equals(ModelledElementDetail.DEFAULT_MODELLER)) {
                logger.debug("\t...modelled element detail is default repository content...return!");
                return false;
            } else {

                // general check based
                if (isAuthenticated && !uniqueUserId.equals(ANONYMOUS_USER_ID)) {
                    logger.debug("\t...user '{}' is AUTHENTICATED, return reviewed items and self-created!", uniqueUserId);
                    BaseUser currentlyLoggedInUser = (BaseUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    if (modelledDomainElement.getModelledElementDetail().getElementLifecycle().getLifecycleState() == LifecycleState.CREATED) {
                        if (modelledDomainElement.getModelledElementDetail().getModelledInitiallyBy() != null) {
                            if (uniqueUserId.equals(modelledDomainElement.getModelledElementDetail().getModelledInitiallyBy())) {
                                logger.debug("\t...do return modelled element with id {} as it is just created and the creator logged in",
                                        modelledDomainElement.getId());
                                return false;
                            } else {
                                if (currentlyLoggedInUser.getBaseUserRoles().contains(BaseUserRole.REVIEWER)
                                        || currentlyLoggedInUser.getBaseUserRoles().contains(BaseUserRole.ADMIN)) {
                                    logger.debug("\t...do return modelled element with id {} although it is just created but the logged in user is reviewer or admin",
                                            modelledDomainElement.getId());
                                    return false;
                                } else {
                                    logger.debug("\t...do not return modelled element with id {} as it is just created and the creator is not logged in but someone else who is not reviewer",
                                            modelledDomainElement.getId());
                                    return true;
                                }
                            }
                        }
                    }
                } else {
                    logger.debug("\t...user is NOT AUTHENTICATED, return only reviewed items!");
                    if (modelledDomainElement.getModelledElementDetail().getElementLifecycle().getLifecycleState() == LifecycleState.CREATED) {
                        if (modelledDomainElement.getModelledElementDetail().getModelledInitiallyBy() == null) {
                            logger.debug("\t...do return modelled element with id {} for now as it is just created and no creator is set",
                                    modelledDomainElement.getId());
                            return false;
                        } else {
                            logger.debug("\t...do not return modelled element with id {} as it is just created and the creator is not logged in",
                                    modelledDomainElement.getId());
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


     public List<EnrichedDataSet> getAllEnrichedDatasets() {

        // we will transform the datasets for presentation, i.e. resolve all ids to full objects
        List<DataSet> allLcAwareDatasets = dataSetCRUDRepository.findAll().stream().filter(dataSet -> {
                    return !filterOutModelledElementBasedOnLifecycleAndUser(dataSet);
                }
        ).collect(Collectors.toList());

        logger.debug("\t\t...found {} datasets, enriching them", allLcAwareDatasets.size());

        return allLcAwareDatasets.stream()
                .map(dataSet -> dataSetService.enrichDataset(dataSet))
                .collect(Collectors.toList());
    }

    public void deleteDeviceCategoryById(String deviceCategoryIdToDelete,
                                         List<EnrichedBaseForExport> baseEditableByUser,
                                         List<EnrichedSampleDevice> allUserEditableSampleDevices,
                                         List<EnrichedDataSet> datasetsEditableByUser) {

        //has to be there, we can directly access by DB as only not yet reviewed categories can be deleted
        DeviceCategory deviceCategoryToDelete = deviceCategoryCRUDRepository.findById(deviceCategoryIdToDelete).get();

        /*
        we need to find all sample devices assigned to that category and delete them
        we need to find all base having device category as selected category for biometric system discipline and delete them
            - if no other category remains, we reset the modelling state of the base to characteristics
        we need to find all implementation-based evaluation results that refer to that device category and delete them
            - if no implementation-based result exists anymore and no experimental evaluation exists, we need to reset
            the modelling state of the base to target architecture

         */

        // get all devices we need to delete

        logger.debug("\t\t...delete all {} sample devices currently assigned to the device category",
                allUserEditableSampleDevices.size());

        allUserEditableSampleDevices.forEach(sampleDevice ->

        {
            if(sampleDevice.getDeviceCategoryId().equals(deviceCategoryIdToDelete)) {
                // we call the individual delete sample device method that takes care of all the rest
                sampleDeviceService.deleteSampleDevice(sampleDevice.getId(),
                        baseEditableByUser,
                        datasetsEditableByUser);
            }
        });

        // check for potentially affected target architectures
        baseEditableByUser.forEach(enrichedBaseEditableByUser ->

        {

            boolean removeModelledTargetArchitecture = false;

            // data capturing locations
            enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .setDataCapturingDeviceCategoryIds(filterDeviceCategory(enrichedBaseEditableByUser
                            .getTargetArchitecture()
                            .getDataCapturingDeviceCategoryIds(), deviceCategoryIdToDelete));

            if (enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .getDataCapturingDeviceCategoryIds().size() == 0) {
                removeModelledTargetArchitecture = true;
            }

            // preprocessing locations
            enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .setSignalProcessingDeviceCategoryIds(filterDeviceCategory(enrichedBaseEditableByUser
                            .getTargetArchitecture()
                            .getSignalProcessingDeviceCategoryIds(), deviceCategoryIdToDelete));

            if (enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .getDecisionDeviceCategoryIds().size() == 0) {
                removeModelledTargetArchitecture = true;
            }

            // template creation
            enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .setSignatureCreationDeviceCategoryIds(filterDeviceCategory(enrichedBaseEditableByUser
                            .getTargetArchitecture()
                            .getSignatureCreationDeviceCategoryIds(), deviceCategoryIdToDelete));

            if (enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .getSignatureCreationDeviceCategoryIds().size() == 0) {
                removeModelledTargetArchitecture = true;
            }

            // matching
            enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .setMatchingDeviceCategoryIds(filterDeviceCategory(enrichedBaseEditableByUser
                            .getTargetArchitecture()
                            .getMatchingDeviceCategoryIds(), deviceCategoryIdToDelete));

            if (enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .getMatchingDeviceCategoryIds().size() == 0) {
                removeModelledTargetArchitecture = true;
            }

            //decision
            enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .setDecisionDeviceCategoryIds(filterDeviceCategory(enrichedBaseEditableByUser
                            .getTargetArchitecture()
                            .getDecisionDeviceCategoryIds(), deviceCategoryIdToDelete));

            if (enrichedBaseEditableByUser
                    .getTargetArchitecture()
                    .getDecisionDeviceCategoryIds().size() == 0) {
                removeModelledTargetArchitecture = true;
            }

            if (removeModelledTargetArchitecture) {


                // first check whether base still exists
                if (biometricAuthenticationSystemAndEvaluationRepository
                        .findById(enrichedBaseEditableByUser.getId()).isPresent()) {
                    logger.debug("\t\t...target architecture of base '{}' needs to be set to skipped",
                            enrichedBaseEditableByUser.getName());

                    BiometricAuthenticationSystemAndEvaluation baseToChange =
                            biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBaseEditableByUser.getId()).get();

                    baseToChange.getTargetArchitecture().setSkipTargetArchitecture(true);
                    biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);
                } else {
                    logger.debug("\t\t...base '{}' was already deleted due to sample device deletion",
                            enrichedBaseEditableByUser.getName());
                }

            }

            // check for any implementation evaluation result related to the device category
            if (enrichedBaseEditableByUser.getModellingProgress()== ModellingProgress.EVALUATION
                    || enrichedBaseEditableByUser.getModellingProgress()==ModellingProgress.EVALUATION_CRITERIA_GRANTS
                    || enrichedBaseEditableByUser.getModellingProgress()==ModellingProgress.REFERENCE) {

                if (enrichedBaseEditableByUser.getBaseEvaluation().getImplementationSpecificEvaluationResults() != null
                        && enrichedBaseEditableByUser.getBaseEvaluation().getImplementationSpecificEvaluationResults().size() > 0) {

                    List<ImplementationSpecificEvaluationResult> remainingImplementationResults =
                            enrichedBaseEditableByUser.getBaseEvaluation().getImplementationSpecificEvaluationResults()
                                    .stream()
                                    .filter(implementationSpecificEvaluationResult -> !implementationSpecificEvaluationResult.getAffectedDeviceCategoryIds().contains(deviceCategoryIdToDelete))
                                    .collect(Collectors.toList());

                    if (remainingImplementationResults.size() == 0) {
                        logger.debug("\t\t...none of {} impl-spec eval results of base '{}' remain, reset impl-based results!",
                                enrichedBaseEditableByUser.getBaseEvaluation().getImplementationSpecificEvaluationResults().size(),
                                enrichedBaseEditableByUser.getName());
                        BiometricAuthenticationSystemAndEvaluation baseToChange =
                                biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBaseEditableByUser.getId()).get();

                        baseService.resetBaseImplementationBasedEvaluation(baseToChange);

                    } else if (remainingImplementationResults.size() < enrichedBaseEditableByUser.getBaseEvaluation().getImplementationSpecificEvaluationResults().size()) {
                        logger.debug("\t\t...only {} of {} impl-spec eval results of base '{}' remain, change base!",
                                remainingImplementationResults.size(),
                                enrichedBaseEditableByUser.getBaseEvaluation().getImplementationSpecificEvaluationResults().size(),
                                enrichedBaseEditableByUser.getName());

                        BiometricAuthenticationSystemAndEvaluation baseToChange =
                                biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBaseEditableByUser.getId()).get();

                        baseToChange.getBaseEvaluation().setImplementationSpecificEvaluationResults(remainingImplementationResults);
                        logger.debug("\t\t\t...update base '{}' in DB", enrichedBaseEditableByUser.getName());
                        biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);

                    } else {
                        logger.debug("\t\t...base '{}' needs no changes in implementation-based eval results",
                                enrichedBaseEditableByUser.getName());
                    }


                }

            } else {
                logger.debug("\t\t...base '{}' is not in relevant modelling progress to check for implementation-based results ('{}')",
                        enrichedBaseEditableByUser.getName(),
                        enrichedBaseEditableByUser.getModellingProgress());
            }
        });


        // finally delete device category
        logger.debug("\t\t..finally delete device category");
        deviceCategoryCRUDRepository.delete(deviceCategoryToDelete);
    }


    private List<String> filterDeviceCategory(List<String> listOfDeviceCategoryIdsToFilter, String deviceCategoryIdToFilter) {
        return listOfDeviceCategoryIdsToFilter.stream()
                .filter(deviceCategoryId -> !deviceCategoryId.equals(deviceCategoryIdToFilter))
                .collect(Collectors.toList());
    }
}

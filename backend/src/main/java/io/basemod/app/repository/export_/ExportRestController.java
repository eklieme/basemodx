package io.basemod.app.repository.export_;

import io.basemod.app.architecture.ResourceToProtect;
import io.basemod.app.base.EnrichedBaseForExport;
import io.basemod.app.base.EnrichedBiometricAuthenticationSystemAndEvaluationService;
import io.basemod.app.characteristic.BiometricCharacteristic;
import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContext;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryService;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.EnrichedDeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.criteria.EnrichedExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaService;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;
import io.basemod.app.repository.import_.ImportRestController;
import io.basemod.app.repository.review.ModelledElementReviewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.basemod.app.repository.review.ModelledElementReviewRestController.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}/repository/export")
public class ExportRestController {

    Logger logger = LoggerFactory.getLogger(ImportRestController.class);

    private DeviceCategoryService deviceCategoryService;

    private SampleDeviceCRUDRepository sampleDeviceCRUDRepository;

    private ModelledElementReviewController modelledElementReviewController;

    private ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository;
    private ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService;
    private EnrichedBiometricAuthenticationSystemAndEvaluationService enrichedBiometricAuthenticationSystemAndEvaluationService;


    @Autowired
    public ExportRestController(DeviceCategoryService deviceCategoryService, SampleDeviceCRUDRepository sampleDeviceCRUDRepository, ModelledElementReviewController modelledElementReviewController, ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository, ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService, EnrichedBiometricAuthenticationSystemAndEvaluationService enrichedBiometricAuthenticationSystemAndEvaluationService) {
        this.deviceCategoryService = deviceCategoryService;
        this.sampleDeviceCRUDRepository = sampleDeviceCRUDRepository;
        this.modelledElementReviewController = modelledElementReviewController;
        this.experimentSpecificEvaluationCriteriaCRUDRepository = experimentSpecificEvaluationCriteriaCRUDRepository;
        this.experimentSpecificEvaluationCriteriaService = experimentSpecificEvaluationCriteriaService;
        this.enrichedBiometricAuthenticationSystemAndEvaluationService = enrichedBiometricAuthenticationSystemAndEvaluationService;
    }

    @RequestMapping(method = RequestMethod.GET)
//    @RolesAllowed({"REVIEWER", "ADMIN"})
    public Map<String, List<? extends ModelledDomainElement>> exportReviewedArtifacts() {
        logger.debug("...got request to export all reviewed elements for import somewhere else");
        Map<String, List<? extends ModelledDomainElement>> domainElementsForExport =
                new HashMap<>();


        List<EnrichedDeviceCategory> reviewedDeviceCategoriesToExport = getReviewedEnrichedDeviceCategories();
        logger.debug("\t...got {} reviewed device categories for export", reviewedDeviceCategoriesToExport.size());
        // put enriched device categories
        domainElementsForExport.put(ARTIFACT_TYPE_DEVICE_CATEGORIES, reviewedDeviceCategoriesToExport);


        List<ResourceToProtect> reviewedResourcesToProtectToExport =
                getReviewedResourcesToProtect();
        logger.debug("\t...got {} reviewed resources to protect for export", reviewedResourcesToProtectToExport.size());
        // put resources to protect
        domainElementsForExport.put(ARTIFACT_TYPE_RESOURCES_TO_PROTECT, reviewedResourcesToProtectToExport);

        List<SamplingContext> reviewedSamplingContextsToExport =
                (List<SamplingContext>) modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_SAMPLING_CONTEXT);
        logger.debug("\t...got {} reviewed sampling contexts for export", reviewedSamplingContextsToExport.size());
        // put sampling contexts
        domainElementsForExport.put(ARTIFACT_TYPE_SAMPLING_CONTEXT, reviewedSamplingContextsToExport);

        List<EnrichedSampleDevice> reviewedSampleDevicesToExport =
                getReviewedEnrichedSampleDevicesToExport();
        logger.debug("\t...got {} reviewed sample devices for export", reviewedSampleDevicesToExport.size());
        // put enriched sample devices
        domainElementsForExport.put(ARTIFACT_TYPE_SAMPLE_DEVICES, reviewedSampleDevicesToExport);

        List<BiometricCharacteristic> reviewedBiometricCharacteristicsToExport =
                (List<BiometricCharacteristic>) modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_BIOMETRIC_CHARACTERISTICS);
        logger.debug("\t...got {} reviewed biometric characteristics for export", reviewedBiometricCharacteristicsToExport.size());
        // put biometric characteristics
        domainElementsForExport.put(ARTIFACT_TYPE_BIOMETRIC_CHARACTERISTICS, reviewedBiometricCharacteristicsToExport);

        List<EnrichedDataSet> reviewedDatasetsToExport =
                (List<EnrichedDataSet>) modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_DATASETS);
        logger.debug("\t...got {} reviewed datasets for export", reviewedDatasetsToExport.size());
        // put enriched datasets
        domainElementsForExport.put(ARTIFACT_TYPE_DATASETS, reviewedDatasetsToExport);

        List<EnrichedExperimentSpecificEvaluationCriteria> reviewedExperimentalCriteriaToExport
                = getReviewedEnrichedExperimentalEvaluationCriteria();
        logger.debug("\t...got {} reviewed experimental criteria for export", reviewedExperimentalCriteriaToExport.size());
        // put enriched experiment related evaluation criteria, include result metrics
        domainElementsForExport.put(ARTIFACT_TYPE_EVALUATION_CRITERIA_EXPERIMENT, reviewedExperimentalCriteriaToExport);

        List<ImplementationSpecificEvaluationCriteria> reviewedImplementationCriteriaToExport
                = (List<ImplementationSpecificEvaluationCriteria>) modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_EVALUATION_CRITERIA_IMPLEMENTATION);
        logger.debug("\t...got {} reviewed implementation criteria for export", reviewedImplementationCriteriaToExport.size());
        // put implementation criteria
        domainElementsForExport.put(ARTIFACT_TYPE_EVALUATION_CRITERIA_IMPLEMENTATION, reviewedImplementationCriteriaToExport);

        List<EnrichedBaseForExport> reviewedBaseToExport
                = getReviewedEnrichedBaseToExport();
        logger.debug("\t...got {} reviewed BASE for export", reviewedBaseToExport.size());
        // finally, put enriched base
        domainElementsForExport.put(ARTIFACT_TYPE_BASE, reviewedBaseToExport);

        return domainElementsForExport;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/"+ARTIFACT_TYPE_BASE)
//    @RolesAllowed({"REVIEWER", "ADMIN"})
    public List<EnrichedBaseForExport> exportReviewedBase() {
        logger.debug("...got request to export all reviewed BASE");
        List<EnrichedBaseForExport> reviewedBaseToExport
                = getReviewedEnrichedBaseToExport();
        logger.debug("\t...got {} reviewed BASE for export", reviewedBaseToExport.size());
        return reviewedBaseToExport;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/"+ARTIFACT_TYPE_DATASETS)
//    @RolesAllowed({"REVIEWER", "ADMIN"})
    public DatasetForExportArtefact exportReviewedDatasets() {
        logger.debug("...got request to export all reviewed Datasets");


        List<EnrichedDeviceCategory> reviewedDeviceCategoriesToExport = getReviewedEnrichedDeviceCategories();
        logger.debug("\t...got {} reviewed device categories for export", reviewedDeviceCategoriesToExport.size());

        List<EnrichedDataSet> reviewedDatasetsToExport =
                (List<EnrichedDataSet>) modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_DATASETS);
        logger.debug("\t...got {} reviewed datasets for export", reviewedDatasetsToExport.size());

        List<BiometricCharacteristic> reviewedBiometricCharacteristicsToExport =
                (List<BiometricCharacteristic>) modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_BIOMETRIC_CHARACTERISTICS);
        logger.debug("\t...got {} reviewed biometric characteristics for export", reviewedBiometricCharacteristicsToExport.size());

        return new DatasetForExportArtefact(reviewedDeviceCategoriesToExport, reviewedDatasetsToExport, reviewedBiometricCharacteristicsToExport);

    }

    private List<EnrichedBaseForExport> getReviewedEnrichedBaseToExport() {
        return modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_BASE)
                .stream().map(modelledBaseToEnrich -> {
                    return enrichedBiometricAuthenticationSystemAndEvaluationService.getEnrichedBaseById(modelledBaseToEnrich.getId());
                }).collect(Collectors.toList());
    }

    private List<EnrichedExperimentSpecificEvaluationCriteria> getReviewedEnrichedExperimentalEvaluationCriteria() {
        return modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_EVALUATION_CRITERIA_EXPERIMENT)
                .stream().map(experimentSpecificEvaluationCriteria -> {
                    ExperimentSpecificEvaluationCriteria criteriaToEnrich
                            = experimentSpecificEvaluationCriteriaCRUDRepository.findById(experimentSpecificEvaluationCriteria.getId()).get();
                    return experimentSpecificEvaluationCriteriaService.enrichExperimentSpecificEvaluationCriteria(criteriaToEnrich);
                }).collect(Collectors.toList());
    }

    private List<EnrichedSampleDevice> getReviewedEnrichedSampleDevicesToExport() {
        return modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_SAMPLE_DEVICES)
                .stream()
                .map(sampleDevice -> {
                    SampleDevice sampleDeviceToEnrich = sampleDeviceCRUDRepository.findById(sampleDevice.getId()).get();

                    EnrichedSampleDevice enrichedSampleDevice =
                            new EnrichedSampleDevice(
                                    sampleDeviceToEnrich,
                                    deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(
                                            sampleDeviceToEnrich.getDeviceCategoryId()
                                    )
                            );

                    return enrichedSampleDevice;
                }).collect(Collectors.toList());

    }


    private List<ResourceToProtect> getReviewedResourcesToProtect() {
        return (List<ResourceToProtect>) modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_RESOURCES_TO_PROTECT);
    }
    private List<EnrichedDeviceCategory> getReviewedEnrichedDeviceCategories() {
        return modelledElementReviewController.getAllReviewedArtifactsOfArtifactType(ARTIFACT_TYPE_DEVICE_CATEGORIES)
                .stream()
                .map(deviceCategory -> deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(deviceCategory.getId()))
                .collect(Collectors.toList());
    }
}

package io.basemod.app.repository.review;

import io.basemod.app.architecture.ResourceToProtect;
import io.basemod.app.architecture.ResourceToProtectCRUDRepository;
import io.basemod.app.base.*;
import io.basemod.app.base.lifecycle.LifecycleState;
import io.basemod.app.characteristic.BiometricCharacteristic;
import io.basemod.app.characteristic.BiometricCharacteristicCRUDRepository;
import io.basemod.app.characteristic.BiometricCharacteristicService;
import io.basemod.app.characteristic.SoftBiometricCharacteristic;
import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.evaluation.BaseEvaluation;
import io.basemod.app.evaluation.BaseEvaluationService;
import io.basemod.app.evaluation.criteria.grant.EvaluationCriteriaGrantService;
import io.basemod.app.evaluation.experiment.dataset.DataSetService;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryService;
import io.basemod.app.evaluation.experiment.evaluation.criteria.EnrichedExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaCRUDRepository;
import io.basemod.app.evaluation.experiment.biometricsystem.*;
import io.basemod.app.evaluation.experiment.biometricsystem.feature.Feature;
import io.basemod.app.evaluation.experiment.biometricsystem.feature.FeatureCRUDRepository;
import io.basemod.app.evaluation.experiment.biometricsystem.step.BiometricProcessingStepCRUDRepository;
import io.basemod.app.evaluation.experiment.biometricsystem.step.BiometricSystemProcessingStep;
import io.basemod.app.evaluation.experiment.dataset.DataSet;
import io.basemod.app.evaluation.experiment.dataset.DataSetCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContext;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContextCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.ReviewableSamplingContext;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.*;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaService;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.EnrichedResultMetric;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetric;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetricCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetricService;
import io.basemod.app.evaluation.extension.BaseEvaluationExtension;
import io.basemod.app.evaluation.extension.BaseEvaluationExtensionCRUDRepository;
import io.basemod.app.evaluation.extension.EnrichedBaseEvaluationExtension;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteriaCRUDRepository;
import io.basemod.app.security.authentication.domain.BaseUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.basemod.app.repository.review.ModelledElementReviewRestController.*;

@Service
public class ModelledElementReviewController {

    private Logger logger = LoggerFactory.getLogger(ModelledElementReviewController.class);

    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;
    private DataSetCRUDRepository dataSetCRUDRepository;
    private SampleDeviceCRUDRepository sampleDeviceCRUDRepository;
    private DeviceCategoryCRUDRepository deviceCategoryCRUDRepository;
    private SensorCRUDRepository sensorCRUDRepository;
    private SensorDimensionCRUDRepository sensorDimensionCRUDRepository;
    private BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository;
    private SamplingContextCRUDRepository evaluationContextCRUDRepository;
    private ResourceToProtectCRUDRepository resourceToProtectCRUDRepository;
    private BiometricSystemCRUDRepository biometricSystemCRUDRepository;
    private BiometricProcessingStepCRUDRepository biometricProcessingStepCRUDRepository;
    private FeatureCRUDRepository featureCRUDRepository;
    private ResultMetricCRUDRepository resultMetricCRUDRepository;
    private ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository;
    private ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository;
    private MongoTemplate mongoTemplate;
    private SensorService sensorService;
    private SensorDimensionService sensorDimensionService;
    private DataSetService dataSetService;
    private ResultMetricService resultMetricService;

    private DeviceCategoryService deviceCategoryService;

    private ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService;

    private EnrichedBiometricAuthenticationSystemAndEvaluationService enrichedBiometricAuthenticationSystemAndEvaluationService;
    private BiometricCharacteristicService biometricCharacteristicService;

    private BaseEvaluationService baseEvaluationService;

    private EvaluationCriteriaGrantService evaluationCriteriaGrantService;

    private BaseEvaluationExtensionCRUDRepository baseEvaluationExtensionCRUDRepository;

    @Autowired
    public ModelledElementReviewController(BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository, DataSetCRUDRepository dataSetCRUDRepository, SampleDeviceCRUDRepository sampleDeviceCRUDRepository, DeviceCategoryCRUDRepository deviceCategoryCRUDRepository, SensorCRUDRepository sensorCRUDRepository, SensorDimensionCRUDRepository sensorDimensionCRUDRepository, BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository, SamplingContextCRUDRepository evaluationContextCRUDRepository, ResourceToProtectCRUDRepository resourceToProtectCRUDRepository, BiometricSystemCRUDRepository biometricSystemCRUDRepository, BiometricProcessingStepCRUDRepository biometricProcessingStepCRUDRepository, FeatureCRUDRepository featureCRUDRepository, ResultMetricCRUDRepository resultMetricCRUDRepository, ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository, ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository, MongoTemplate mongoTemplate, SensorService sensorService, SensorDimensionService sensorDimensionService, DataSetService dataSetService, ResultMetricService resultMetricService, DeviceCategoryService deviceCategoryService, ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService, EnrichedBiometricAuthenticationSystemAndEvaluationService enrichedBiometricAuthenticationSystemAndEvaluationService, BiometricCharacteristicService biometricCharacteristicService, BaseEvaluationService baseEvaluationService, EvaluationCriteriaGrantService evaluationCriteriaGrantService, BaseEvaluationExtensionCRUDRepository baseEvaluationExtensionCRUDRepository) {
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
        this.dataSetCRUDRepository = dataSetCRUDRepository;
        this.sampleDeviceCRUDRepository = sampleDeviceCRUDRepository;
        this.deviceCategoryCRUDRepository = deviceCategoryCRUDRepository;
        this.sensorCRUDRepository = sensorCRUDRepository;
        this.sensorDimensionCRUDRepository = sensorDimensionCRUDRepository;
        this.biometricCharacteristicCRUDRepository = biometricCharacteristicCRUDRepository;
        this.evaluationContextCRUDRepository = evaluationContextCRUDRepository;
        this.resourceToProtectCRUDRepository = resourceToProtectCRUDRepository;
        this.biometricSystemCRUDRepository = biometricSystemCRUDRepository;
        this.biometricProcessingStepCRUDRepository = biometricProcessingStepCRUDRepository;
        this.featureCRUDRepository = featureCRUDRepository;
        this.resultMetricCRUDRepository = resultMetricCRUDRepository;
        this.implementationSpecificEvaluationCriteriaCRUDRepository = implementationSpecificEvaluationCriteriaCRUDRepository;
        this.experimentSpecificEvaluationCriteriaCRUDRepository = experimentSpecificEvaluationCriteriaCRUDRepository;
        this.mongoTemplate = mongoTemplate;
        this.sensorService = sensorService;
        this.sensorDimensionService = sensorDimensionService;
        this.dataSetService = dataSetService;
        this.resultMetricService = resultMetricService;
        this.deviceCategoryService = deviceCategoryService;
        this.experimentSpecificEvaluationCriteriaService = experimentSpecificEvaluationCriteriaService;
        this.enrichedBiometricAuthenticationSystemAndEvaluationService = enrichedBiometricAuthenticationSystemAndEvaluationService;
        this.biometricCharacteristicService = biometricCharacteristicService;
        this.baseEvaluationService = baseEvaluationService;
        this.evaluationCriteriaGrantService = evaluationCriteriaGrantService;
        this.baseEvaluationExtensionCRUDRepository = baseEvaluationExtensionCRUDRepository;
    }

    private BaseUser getCurrentUserUniqueId() {
        return (BaseUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Map<String, List<? extends ModelledDomainElement>> getAllArtifactsToReviewForReviewer() {
        Query modelledElementsToReviewQuery = new Query();

        logger.debug("Create query to get all elements to review");
        modelledElementsToReviewQuery.addCriteria(getElementCreatedCriteria());

        return getAllArtifactsForReviewBasedOnQuery(modelledElementsToReviewQuery);
    }

    public List<? extends ModelledDomainElement> getAllReviewedArtifactsOfArtifactType(String artifactType) {
        Query reviewedElementsOfArtifactTypeQuery = new Query();

        logger.debug("Create query to get all reviewed elements of type {}", artifactType);
        reviewedElementsOfArtifactTypeQuery.addCriteria(getElementOtherThanCreatedCriteria());

        switch(artifactType) {
            case ARTIFACT_TYPE_DEVICE_CATEGORIES:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, DeviceCategory.class);
            case ARTIFACT_TYPE_SAMPLE_DEVICES:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, SampleDevice.class);
            case ARTIFACT_TYPE_SAMPLING_CONTEXT:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, SamplingContext.class);
            case ARTIFACT_TYPE_RESOURCES_TO_PROTECT:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, ResourceToProtect.class);
            case ARTIFACT_TYPE_BIOMETRIC_CHARACTERISTICS:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, BiometricCharacteristic.class);
            case ARTIFACT_TYPE_BIOMETRIC_PROCESSING_STEPS:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, BiometricSystemProcessingStep.class);
            case ARTIFACT_TYPE_DATASETS:
                return getEnrichedDatasets(reviewedElementsOfArtifactTypeQuery);
            case ARTIFACT_TYPE_EVALUATION_CRITERIA_EXPERIMENT:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, ExperimentSpecificEvaluationCriteria.class);
            case ARTIFACT_TYPE_EVALUATION_CRITERIA_IMPLEMENTATION:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, ImplementationSpecificEvaluationCriteria.class);
            case ARTIFACT_TYPE_BASE:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, BiometricAuthenticationSystemAndEvaluation.class);
            case ARTIFACT_TYPE_BASE_EVALUATION_EXTENSION:
                return mongoTemplate.find(reviewedElementsOfArtifactTypeQuery, BaseEvaluationExtension.class);
        }

        return new ArrayList<>();
    }

    public List<? extends ModelledDomainElement> getAllArtifactsOfTypeToReviewForReviewer(String artifactType) {
        Query modelledElementsToReviewQuery = new Query();

        logger.debug("Create query to get all elements to review");
        modelledElementsToReviewQuery.addCriteria(getElementCreatedCriteria());

        return getArtifactsOfType(modelledElementsToReviewQuery, artifactType);
    }

    public Map<String, List<? extends ModelledDomainElement>> getAllArtifactsToReviewForAuthenticatedUser(String userUniqueId) {
        Query modelledElementsToReviewForUserQuery = new Query();

        logger.debug("Create query to get all created elements in review for user with uniqueID '{}'", userUniqueId);
        modelledElementsToReviewForUserQuery.addCriteria(getModelledByUserIdCriteria(userUniqueId));
        modelledElementsToReviewForUserQuery.addCriteria(getElementCreatedCriteria());

        return getAllArtifactsForReviewBasedOnQuery(modelledElementsToReviewForUserQuery);
    }



    public List<? extends ModelledDomainElement> getArtifactsOfTypeToReviewForAuthenticatedUser(String uniqueUserId, String artifactType) {
        logger.debug("Create query to get all elements of type {} in review for user with uniqueID '{}'", artifactType, uniqueUserId);
        Query createdElementsToReviewForAuthenticatedUserQuery = new Query();
        createdElementsToReviewForAuthenticatedUserQuery.addCriteria(getElementCreatedCriteria());
        createdElementsToReviewForAuthenticatedUserQuery.addCriteria(getModelledByUserIdCriteria(uniqueUserId));
        return getArtifactsOfType(createdElementsToReviewForAuthenticatedUserQuery,artifactType);
    }


    private Criteria getElementCreatedCriteria() {
        return Criteria.where("modelledElementDetail.elementModelledElementLifecycle.lifecycleState")
                .is(LifecycleState.CREATED);
    }

    private Criteria getElementOtherThanCreatedCriteria() {
        return Criteria.where("modelledElementDetail.elementModelledElementLifecycle.lifecycleState").ne(LifecycleState.CREATED);
    }

    private Criteria getModelledByUserIdCriteria(String uniqueUserId) {
        return Criteria.where("modelledElementDetail.modelledInitiallyBy")
                .is(uniqueUserId);
    }

    private List<? extends ModelledDomainElement> getArtifactsOfType(Query artifactCriteriaQuery, String artifactType) {


        switch(artifactType) {
            case ARTIFACT_TYPE_BIOMETRIC_CHARACTERISTICS:
                return mongoTemplate.find(artifactCriteriaQuery, BiometricCharacteristic.class).stream().map(biometricCharacteristic -> {
                    if(biometricCharacteristic instanceof SoftBiometricCharacteristic) {
                        return biometricCharacteristicService.enrichSoftBiometric((SoftBiometricCharacteristic) biometricCharacteristic);
                    }
                    return biometricCharacteristic;
                }).collect(Collectors.toList());
            case ARTIFACT_TYPE_DEVICE_CATEGORIES:
                return getDeviceCategoriesForReview(artifactCriteriaQuery);
            case ARTIFACT_TYPE_SENSORS:
                return getSensorsForReview(artifactCriteriaQuery);
            case ARTIFACT_TYPE_SENSOR_DIMENSIONS:
                return getSensorDimensionsForReview(artifactCriteriaQuery);
            case ARTIFACT_TYPE_RESOURCES_TO_PROTECT:
                return mongoTemplate.find(artifactCriteriaQuery, ResourceToProtect.class);
            case ARTIFACT_TYPE_SAMPLING_CONTEXT:
                return getReviewableSamplingContexts(artifactCriteriaQuery);
            case ARTIFACT_TYPE_SAMPLE_DEVICES:
                return getReviewableSampleDevices(artifactCriteriaQuery);
            case ARTIFACT_TYPE_DATASETS:
                return getEnrichedDatasets(artifactCriteriaQuery);
            case ARTIFACT_TYPE_FEATURES:
                return mongoTemplate.find(artifactCriteriaQuery, Feature.class);
            case ARTIFACT_TYPE_BIOMETRIC_PROCESSING_STEPS:
                return mongoTemplate.find(artifactCriteriaQuery, BiometricSystemProcessingStep.class);
            case ARTIFACT_TYPE_EVALUATION_CRITERIA_EXPERIMENT:
                return getEnrichedExperimentSpecificEvaluationCriteria(artifactCriteriaQuery);
            case ARTIFACT_TYPE_EVALUATION_CRITERIA_IMPLEMENTATION:
                return mongoTemplate.find(artifactCriteriaQuery, ImplementationSpecificEvaluationCriteria.class);
            case ARTIFACT_TYPE_RESULT_METRICS:
                return getEnrichedResultMetrics(artifactCriteriaQuery);
            case ARTIFACT_TYPE_BASE_EVALUATION_EXTENSION:
                return getEnrichedBaseEvaluationExtensions(artifactCriteriaQuery);
        }

        return new ArrayList<>();
    }
    private Map<String, List<? extends ModelledDomainElement>> getAllArtifactsForReviewBasedOnQuery(Query modelledElementForReviewQuery) {

        Map<String, List<? extends ModelledDomainElement>> resultElements = new HashMap<>();

        // base, first by state and then only the fully modelled ones
        List<EnrichedBiometricAuthenticationSystemAndEvaluation> resultBases
                = mongoTemplate.find(modelledElementForReviewQuery, BiometricAuthenticationSystemAndEvaluation.class)
                .stream()
                .map(baseToEnrich -> enrichedBiometricAuthenticationSystemAndEvaluationService.
                        transformBasesToReturn(baseToEnrich)).collect(Collectors.toList());

        resultBases = resultBases.stream().filter(enrichedBase -> {
            return enrichedBase.getModellingProgress()==ModellingProgress.REFERENCE;
        }).collect(Collectors.toList());

        logger.debug("\t...found {} base", resultBases.size());
        resultElements.put(ARTIFACT_TYPE_BASE, resultBases);

        // biometric characteristics
        List<BiometricCharacteristic> resultCharacteristics
                = mongoTemplate.find(modelledElementForReviewQuery, BiometricCharacteristic.class)
                    .stream()
                    .map(biometricCharacteristic -> {
                        if(biometricCharacteristic instanceof SoftBiometricCharacteristic) {
                            return biometricCharacteristicService.enrichSoftBiometric((SoftBiometricCharacteristic) biometricCharacteristic);
                        }
                        return biometricCharacteristic;
                    })
                    .collect(Collectors.toList());

        logger.debug("\t...found {} biometric characteristics", resultCharacteristics.size());
        resultElements.put(ARTIFACT_TYPE_BIOMETRIC_CHARACTERISTICS, resultCharacteristics);

        // device categories
        List<DeviceCategory> resultDeviceCategories = getDeviceCategoriesForReview(modelledElementForReviewQuery);
        logger.debug("\t...found {} device categories", resultDeviceCategories.size());
        resultElements.put(ARTIFACT_TYPE_DEVICE_CATEGORIES, resultDeviceCategories);

        // sensors, enriched
        List<EnrichedSensor> enrichedResultSensors = getSensorsForReview(modelledElementForReviewQuery);
        logger.debug("\t...found {} sensors", enrichedResultSensors.size());
        resultElements.put(ARTIFACT_TYPE_SENSORS, enrichedResultSensors);

        // sensor dimensions, enriched
        List<EnrichedSensorDimension> enrichedSensorDimensions = getSensorDimensionsForReview(modelledElementForReviewQuery);
        logger.debug("\t...found {} sensor dimensions", enrichedSensorDimensions.size());
        resultElements.put(ARTIFACT_TYPE_SENSOR_DIMENSIONS, enrichedSensorDimensions);

        // Resources to protect
        List<ResourceToProtect> resultResourcesToProtect
                = mongoTemplate.find(modelledElementForReviewQuery, ResourceToProtect.class);

        logger.debug("\t...found {} resources to protect", resultResourcesToProtect.size());
        resultElements.put(ARTIFACT_TYPE_RESOURCES_TO_PROTECT, resultResourcesToProtect);

        // datasets, enriched
        List<EnrichedDataSet> resultDatasets
                = getEnrichedDatasets(modelledElementForReviewQuery);

        logger.debug("\t...found {} datasets", resultDatasets.size());
        resultElements.put(ARTIFACT_TYPE_DATASETS, resultDatasets);

        // sampling contexts ... for review, we also want the datasets where they were used to better assess their quality
        List<ReviewableSamplingContext> resultSamplingContexts = getReviewableSamplingContexts(
                modelledElementForReviewQuery
        );
        logger.debug("\t...found {} sampling contexts", resultSamplingContexts.size());
        resultElements.put(ARTIFACT_TYPE_SAMPLING_CONTEXT, resultSamplingContexts);

        // Sample devices, enriched
        List<EnrichedSampleDevice> resultEnrichedSampleDevices = getReviewableSampleDevices(modelledElementForReviewQuery);

        logger.debug("\t...found {} sampleDevices", resultEnrichedSampleDevices.size());
        resultElements.put(ARTIFACT_TYPE_SAMPLE_DEVICES, resultEnrichedSampleDevices);


        // biometric processing steps

        List<BiometricSystemProcessingStep> resultBiometricProcessingSteps
                = mongoTemplate.find(modelledElementForReviewQuery, BiometricSystemProcessingStep.class);

        logger.debug("\t...found {} biometric processing steps", resultBiometricProcessingSteps.size());
        resultElements.put(ARTIFACT_TYPE_BIOMETRIC_PROCESSING_STEPS, resultBiometricProcessingSteps);

        // Features

        List<Feature> resultFeatures
                = mongoTemplate.find(modelledElementForReviewQuery, Feature.class);

        logger.debug("\t...found {} features", resultFeatures.size());
        resultElements.put(ARTIFACT_TYPE_FEATURES, resultFeatures);

        // Experiment Evaluation criteria, enriched
        List<EnrichedExperimentSpecificEvaluationCriteria> resultExperimentSpecificEvaluationCriteria
                = getEnrichedExperimentSpecificEvaluationCriteria(modelledElementForReviewQuery);
        logger.debug("\t...found {} experiment-specific evaluation criteria", resultExperimentSpecificEvaluationCriteria.size());
        resultElements.put(ARTIFACT_TYPE_EVALUATION_CRITERIA_EXPERIMENT, resultExperimentSpecificEvaluationCriteria);

        // Result metrics, enriched
        List<EnrichedResultMetric> resultResultMetrics
                = getEnrichedResultMetrics(modelledElementForReviewQuery);
        logger.debug("\t...found {} result metrics", resultResultMetrics.size());
        resultElements.put(ARTIFACT_TYPE_RESULT_METRICS, resultResultMetrics);

        // Implementation Evaluation criteria, enriched
        List<ImplementationSpecificEvaluationCriteria> resultImplementationSpecificEvaluationCriteria
                = mongoTemplate.find(modelledElementForReviewQuery, ImplementationSpecificEvaluationCriteria.class);
        logger.debug("\t...found {} implementation-specific evaluation criteria", resultImplementationSpecificEvaluationCriteria.size());
        resultElements.put(ARTIFACT_TYPE_EVALUATION_CRITERIA_IMPLEMENTATION, resultImplementationSpecificEvaluationCriteria);


        // Base Evaluation Extension
        List<EnrichedBaseEvaluationExtension> baseEvaluationExtensions
                = getEnrichedBaseEvaluationExtensions(modelledElementForReviewQuery);
        logger.debug("\t...found {} base evaluation extensions", baseEvaluationExtensions.size());
        resultElements.put(ARTIFACT_TYPE_BASE_EVALUATION_EXTENSION, baseEvaluationExtensions);

        return resultElements;
    }

    private List<EnrichedBaseEvaluationExtension> getEnrichedBaseEvaluationExtensions(Query modelledElementForReviewQuery) {
        return mongoTemplate.find(modelledElementForReviewQuery, BaseEvaluationExtension.class)
                .stream()
                .map(baseEvaluationExtension -> {
                    return baseEvaluationService.enrichBaseEvaluationExtension(baseEvaluationExtension);
                }).collect(Collectors.toList());
    }

    private List<EnrichedExperimentSpecificEvaluationCriteria> getEnrichedExperimentSpecificEvaluationCriteria(Query modelledElementForReviewQuery) {
        return mongoTemplate.find(modelledElementForReviewQuery, ExperimentSpecificEvaluationCriteria.class)
                .stream()
                .map(experimentSpecificEvaluationCriteria ->
                        experimentSpecificEvaluationCriteriaService.enrichExperimentSpecificEvaluationCriteria(experimentSpecificEvaluationCriteria))
                .collect(Collectors.toList());
    }

    private List<EnrichedResultMetric> getEnrichedResultMetrics(Query modelledElementForReviewQuery) {
        return mongoTemplate.find(modelledElementForReviewQuery, ResultMetric.class)
                .stream().map(resultMetric -> resultMetricService.enrichResultMetric(resultMetric))
                .collect(Collectors.toList());
    }

    private List<EnrichedDataSet> getEnrichedDatasets(Query modelledElementForReviewQuery) {
        return mongoTemplate.find(modelledElementForReviewQuery, DataSet.class).stream()
                .map(dataSet -> dataSetService.enrichDataset(dataSet))
                .collect(Collectors.toList());
    }

    private List<EnrichedSensor> getSensorsForReview(Query sensorQuery) {
        return mongoTemplate.find(sensorQuery, Sensor.class)
                .stream().map(sensor -> {
                    return sensorService.getEnrichedSensorFromSensor(sensor);
                }).collect(Collectors.toList());
    }

    private List<EnrichedSensorDimension> getSensorDimensionsForReview(Query sensorDimensionQuery) {
        return mongoTemplate.find(sensorDimensionQuery, SensorDimension.class)
                .stream().map(sensorDimension -> {
                    return sensorDimensionService.getEnrichedSensorDimensionBySensorDimensionId(sensorDimension.getId());
                }).collect(Collectors.toList());
    }

    private List<DeviceCategory> getDeviceCategoriesForReview(Query specificQuery) {
        // Device Categories => need enrichment for all sensor details (dimensions)
        List<DeviceCategory> resultDeviceCategories
                = mongoTemplate.find(specificQuery, DeviceCategory.class);

        return resultDeviceCategories.stream().map(deviceCategory -> {
            return deviceCategoryService.getDeviceCategoryByIdWithEnrichedSensorsUserSpecific(deviceCategory.getId(), getCurrentUserUniqueId());
        }).collect(Collectors.toList());
    }

    public void setBaseReviewed(String baseId) {

        /*
         we need to also set all included other entities reviewed in case they are not
         - Biometric Characteristics
         - Deployment Locations
         - Resources to Protect
         - Datasets
            - Sample Devices
            - Evaluation Contexts
         - Experimental/Implementation Criteria
            - Result Metrics
         - Biometric Processing Steps
         */

        logger.debug("\t..get base with id {} to set reviewed", baseId);
        BiometricAuthenticationSystemAndEvaluation baseToSetReviewed =
                biometricAuthenticationSystemAndEvaluationRepository.findById(baseId).get();

        // characteristics
        logger.debug("\t\t..set {} biometric characteristics of base to reviewed", baseToSetReviewed.getBiometricCharacteristicIds().size());
        baseToSetReviewed.getBiometricCharacteristicIds().forEach(biometricCharacteristic -> {
            setBiometricCharacteristicReviewed(biometricCharacteristic);
        });

        // resource to protect
        logger.debug("\t\t..set protected resource of base to reviewed");
        setProtectedResourceReviewed(baseToSetReviewed.getTargetArchitecture().getResourceToProtectId());

        // deployment locations
        Set<String> distinctDeviceCategoryIdsForBase = new HashSet<>();
        distinctDeviceCategoryIdsForBase.addAll(baseToSetReviewed.getTargetArchitecture().getDataCapturingDeviceCategoryIds());
        distinctDeviceCategoryIdsForBase.addAll(baseToSetReviewed.getTargetArchitecture().getSignalProcessingDeviceCategoryIds());
        distinctDeviceCategoryIdsForBase.addAll(baseToSetReviewed.getTargetArchitecture().getSignatureCreationDeviceCategoryIds());
        distinctDeviceCategoryIdsForBase.addAll(baseToSetReviewed.getTargetArchitecture().getDataStorageDeviceCategoryIds());
        distinctDeviceCategoryIdsForBase.addAll(baseToSetReviewed.getTargetArchitecture().getMatchingDeviceCategoryIds());
        distinctDeviceCategoryIdsForBase.addAll(baseToSetReviewed.getTargetArchitecture().getDecisionDeviceCategoryIds());
        logger.debug("\t\t..set {} target locations to reviewed", distinctDeviceCategoryIdsForBase.size());

        distinctDeviceCategoryIdsForBase.forEach(deviceCategoryId -> {
            setDeviceCategoryReviewed(deviceCategoryId);
        });

        setBaseEvaluationReviewed(baseToSetReviewed.getBaseEvaluation());

        // evaluation criteria grant
        logger.debug("\t...set any criteria as part of {} evaluation criteria grants as reviewed",
            baseToSetReviewed.getEvaluationCriteriaGrants().size());
        baseToSetReviewed.getEvaluationCriteriaGrants().forEach(evaluationCriteriaGrant -> {
            if(evaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId()!=null) {
                setExperimentSpecificCriteriaReviewed(evaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId());
            } else {
                setImplementationSpecificCriteriaReviewed(evaluationCriteriaGrant.getImplementationSpecificEvaluationCriteriaId());
            }
        });

        // finally, set base reviewed
        logger.debug("\t...finally, set base with id '{}' reviewed!", baseId);
        setModelledElementReviewStateReviewed(baseToSetReviewed, biometricAuthenticationSystemAndEvaluationRepository);
    }

    private void setBaseEvaluationReviewed(BaseEvaluation baseEvaluation) {

        // experimentspecific evaluation
        if(baseEvaluation.getExperimentSpecificEvaluation()!=null) {
            logger.debug("\t...set any modelled element related to experiment specific evaluation as reviewed");
            // datasets
            logger.debug("\t\t..set {} datasets to reviewed", baseEvaluation.getExperimentSpecificEvaluation().getUsedDatasetIds().size());

            baseEvaluation.getExperimentSpecificEvaluation().getUsedDatasetIds().forEach(dataSetId -> {
                setDatasetReviewed(dataSetId);
            });

            // biometric systems
            logger.debug("\t\t..set {} biometric systems to reviewed", baseEvaluation.getExperimentSpecificEvaluation().getBiometricSystemIds().size());

            baseEvaluation.getExperimentSpecificEvaluation().getBiometricSystemIds().forEach(biometricSystemId -> {
                setBiometricSystemReviewed(biometricSystemId);
            });

            // evaluation results
            logger.debug("\t\t..set any result metric in {} evaluation results to reviewed",
                    baseEvaluation.getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size());
            baseEvaluation
                    .getExperimentSpecificEvaluation()
                    .getExperimentSpecificEvaluationSetups().forEach(experimentSpecificEvaluationSetup -> {
                        experimentSpecificEvaluationSetup.getSpecificResults().forEach(specificResult -> {
                            logger.debug("\t\t\t..result metric: {}", specificResult.getResultMetricId());
                            setResultMetricReviewed(specificResult.getResultMetricId());
                        });
                    });
        }

        // implementationspecific evaluation
        if(baseEvaluation.getImplementationSpecificEvaluationResults()!=null) {
            logger.debug("\t...set any modelled element related to any of {} implementation specific evaluation results as reviewed",
                    baseEvaluation.getImplementationSpecificEvaluationResults().size());

            baseEvaluation.getImplementationSpecificEvaluationResults().forEach(implementationSpecificEvaluationResult -> {
                setImplementationSpecificCriteriaReviewed(implementationSpecificEvaluationResult.getCriteriaId());
            });
        }
    }

    public void setImplementationSpecificCriteriaReviewed(String implementationSpecificCriteriaId) {
        logger.debug("\t..get implementation specific evaluation criteria with id {} to set reviewed", implementationSpecificCriteriaId);
        ImplementationSpecificEvaluationCriteria implementationSpecificEvaluationCriteriaToSetReviewed =
                implementationSpecificEvaluationCriteriaCRUDRepository.findById(implementationSpecificCriteriaId).get();

        setModelledElementReviewStateReviewed(implementationSpecificEvaluationCriteriaToSetReviewed,
                implementationSpecificEvaluationCriteriaCRUDRepository);
    }

    public void setExperimentSpecificCriteriaReviewed(String experimentSpecificCriteriaId) {
        logger.debug("\t..get experiment-specific evaluation criteria with id {} to set reviewed", experimentSpecificCriteriaId);
        ExperimentSpecificEvaluationCriteria experimentSpecificEvaluationCriteriaToSetReviewed =
                experimentSpecificEvaluationCriteriaCRUDRepository.findById(experimentSpecificCriteriaId).get();

        setModelledElementReviewStateReviewed(experimentSpecificEvaluationCriteriaToSetReviewed,
                experimentSpecificEvaluationCriteriaCRUDRepository);
    }

    public void setResultMetricReviewed(String resultMetricId) {
        logger.debug("\t..get result metric with id {} to set reviewed", resultMetricId);
        ResultMetric resultMetricToSetReviewed = resultMetricCRUDRepository.findById(resultMetricId).get();
        setModelledElementReviewStateReviewed(resultMetricToSetReviewed, resultMetricCRUDRepository);
    }

    public void setBiometricSystemReviewed(String biometricSystemId) {
        /*
        we need to also set included entities to reviewed in case they are not yet
        - Processing Steps
        - Features
         */
        logger.debug("\t..get biometric system with id {} to set reviewed", biometricSystemId);
        BiometricSystem biometricSystemToSetReviewed = biometricSystemCRUDRepository.findById(biometricSystemId).get();

        Set<String> distinctProcessingStepIds = new HashSet<>();
        distinctProcessingStepIds.addAll(biometricSystemToSetReviewed.getSignalProcessingStepIds());
        distinctProcessingStepIds.addAll(biometricSystemToSetReviewed.getFurtherProcessingStepIds());

        logger.debug("\t\t..set {} biometric system processing steps to reviewed", distinctProcessingStepIds.size());

        distinctProcessingStepIds.forEach(biometricSystemProcessingStepId -> {
            setBiometricProcessingStepReviewed(biometricSystemProcessingStepId);
        });

        logger.debug("\t\t..set {} features to reviewed", biometricSystemToSetReviewed.getFeatureIds());
        biometricSystemToSetReviewed.getFeatureIds().forEach(feature -> {
            setFeatureReviewed(feature);
        });

        logger.debug("\t\t..finally, set biometric system with id {} to reviewed", biometricSystemId);
        setModelledElementReviewStateReviewed(biometricSystemToSetReviewed, biometricSystemCRUDRepository);

    }

    public void setFeatureReviewed(String featureID) {
        logger.debug("\t\t...get feature with id {} to set reviewed", featureID);
        Feature featureToSetReviewed = featureCRUDRepository.findById(featureID).get();
        setModelledElementReviewStateReviewed(featureToSetReviewed, featureCRUDRepository);
    }

    public void setBiometricProcessingStepReviewed(String biometricProcessingStepId) {
        logger.debug("\t...get processing step with id {} to set reviewed", biometricProcessingStepId);
        BiometricSystemProcessingStep biometricSystemProcessingStep = biometricProcessingStepCRUDRepository.findById(biometricProcessingStepId).get();
        setModelledElementReviewStateReviewed(biometricSystemProcessingStep, biometricProcessingStepCRUDRepository);
    }

    public void setProtectedResourceReviewed(String resourceToProtectId) {
        logger.debug("\t..get resource to protect with id {} to set reviewed", resourceToProtectId);
        ResourceToProtect resourceToProtect = resourceToProtectCRUDRepository.findById(resourceToProtectId).get();

        setModelledElementReviewStateReviewed(resourceToProtect, resourceToProtectCRUDRepository);
    }

    public void setDatasetReviewed(String datasetId) {
        logger.debug("\t..get dataset with id {} to set reviewed", datasetId);
        DataSet dataSetToSetReviewed = dataSetCRUDRepository.findById(datasetId).get();

        /*
        within a dataset we have further entities that should be set reviewed in case they are not.
        Specifically, this is the sampled behaviors and per behavior
         - Sample Devices with sensors
         - Biometric Characteristics sampled
         - Evaluation Contexts
         */

        logger.debug("\t\t..set sample behaviours to reviewed");
        dataSetToSetReviewed.getSampledBiometrics().forEach(sampledBiometric -> {
            logger.debug("\t\t..for sampled behaviour {}:", sampledBiometric);
            sampledBiometric.getSampleDeviceIds().forEach(sampleDeviceId -> {
                logger.debug("\t\t..set sample device id {} for behaviour {} to reviewed", sampleDeviceId, sampledBiometric);
                setSampleDeviceReviewed(sampleDeviceId);
            });

            logger.debug("\t\t..set sampled biometric {} to reviewed", sampledBiometric.getSampledCharacteristicId());
            setBiometricCharacteristicReviewed(sampledBiometric.getSampledCharacteristicId());

            if(sampledBiometric.getSupportCharacteristicId()!=null) {
                logger.debug("\t\t..set sampled support biometric {} to reviewed", sampledBiometric.getSupportCharacteristicId());
                setBiometricCharacteristicReviewed(sampledBiometric.getSupportCharacteristicId());
            } else {
                logger.debug("\t\t..no support biometric given!");
            }

            sampledBiometric.getSamplingContextIds().forEach(samplingContextId -> {
                logger.debug("\t\t..set evaluation context {} for behaviour {} to reviewed", samplingContextId, sampledBiometric);
                setSamplingContextReviewed(samplingContextId);
            });
        });

        logger.debug("\t\t..finally, set dataset with id {} to reviewed", datasetId);
        setModelledElementReviewStateReviewed(dataSetToSetReviewed, dataSetCRUDRepository);
    }

    public void setSamplingContextReviewed(String evaluationContextId) {
        logger.debug("\t..get evaluation context with id {} to set reviewed", evaluationContextId);
        SamplingContext evaluationContextToSetReviewed = evaluationContextCRUDRepository.findById(evaluationContextId).get();

        setModelledElementReviewStateReviewed(evaluationContextToSetReviewed, evaluationContextCRUDRepository);
    }

    public void setBiometricCharacteristicReviewed(String biometricCharacteristicId) {
        logger.debug("\t..get biometric characteristic with id {} to set reviewed", biometricCharacteristicId);
        BiometricCharacteristic biometricCharacteristicToSetReviewed =
                biometricCharacteristicCRUDRepository.findById(biometricCharacteristicId).get();

        setModelledElementReviewStateReviewed(biometricCharacteristicToSetReviewed, biometricCharacteristicCRUDRepository);
    }

    public void setSampleDeviceReviewed(String sampleDeviceId) {
        /*
        within a sample device we also have:
            - a device category and its sensors
        to review
         */

        logger.debug("\t..get sample device with id {} to set reviewed", sampleDeviceId);
        SampleDevice sampleDeviceToSetReviewed = sampleDeviceCRUDRepository.findById(sampleDeviceId).get();

        logger.debug("\t\t..set its device category to reviewed (including sensors)");
        setDeviceCategoryReviewed(sampleDeviceToSetReviewed.getDeviceCategoryId());


        logger.debug("\t\t..set sample device to reviewed");
        setModelledElementReviewStateReviewed(sampleDeviceToSetReviewed, sampleDeviceCRUDRepository);

    }

    public void setSensorReviewed(String sensorId) {
        logger.debug("\t..get sensor with id {} to set reviewed, also set dimensions to be reviewed", sensorId);

        EnrichedSensor enrichedSensor = sensorService.getEnrichedSensorFromSensorId(sensorId);
        logger.debug("\t\t...set sensor '{}' with {} dimensions approved", enrichedSensor.getName(), enrichedSensor.getSensorDimensions().size());
        enrichedSensor.getSensorDimensions().forEach(sensorDimension -> {
            logger.debug("\t\t\t...set dimension '{}' with id '{}' approved", sensorDimension.getName(), sensorDimension.getId());
            sensorDimension.getModelledElementDetail().getElementLifecycle().setLifecycleState(LifecycleState.REVIEWED);
            sensorDimensionCRUDRepository.save(sensorDimension);
        });
        Sensor sensorToSetReviewed = sensorCRUDRepository.findById(sensorId).get();
        setModelledElementReviewStateReviewed(sensorToSetReviewed, sensorCRUDRepository);
    }

    public List<ReviewableSamplingContext> getReviewableSamplingContexts() {

        Query evaluationContextsToReviewQuery = new Query();

        logger.debug("\t...create query to get all evaluation contexts to review");
        evaluationContextsToReviewQuery.addCriteria(
                Criteria.where("modelledElementDetail.elementModelledElementLifecycle.lifecycleState")
                        .is(LifecycleState.CREATED));

        return getReviewableSamplingContexts(evaluationContextsToReviewQuery);
    }

    public List<EnrichedSampleDevice> getReviewableSampleDevices(Query userSpecificElementQuery) {
        List<SampleDevice> resultSampleDevices
                = mongoTemplate.find(userSpecificElementQuery, SampleDevice.class);

        return resultSampleDevices.stream().map(sampleDevice -> new EnrichedSampleDevice(
                sampleDevice,
                deviceCategoryService.getDeviceCategoryByIdWithEnrichedSensorsUserSpecific(sampleDevice.getDeviceCategoryId(), getCurrentUserUniqueId())
        )).collect(Collectors.toList());
    }

    public List<ReviewableSamplingContext> getReviewableSamplingContexts(Query specificQuery) {

        List<ReviewableSamplingContext> reviewableSamplingContexts = new ArrayList<>();

        // first get all evaluation contexts to review
        List<SamplingContext> samplingContexts = mongoTemplate.find(specificQuery, SamplingContext.class);
        logger.debug("\t...get reviewable evaluation contexts based on {} available evaluation contexts", samplingContexts.size());

        // get all datasets and filter for datasets where sampled behaviours contains the respective
        List<DataSet> allDatasets = dataSetCRUDRepository.findAll();


        samplingContexts.forEach(samplingContext -> {
            List<String> usedInDatasetIds = allDatasets.stream().filter(dataSet -> {
                return dataSet.getSampledBiometrics().stream().filter(sampledBiometric -> {
                    if(sampledBiometric.getSamplingContextIds()!=null && sampledBiometric.getSamplingContextIds().size()>0) {
                        return sampledBiometric.getSamplingContextIds().stream().filter(samplingContextId -> {
                            return samplingContext.getId().equalsIgnoreCase(samplingContextId);
                        }).count() > 0;
                    }
                    return false;
                }).count()>0;
            }).map(DataSet::getId).collect(Collectors.toList());

            logger.debug("\t..evaluation context '{}' was used in {} datasets",
                    samplingContext.getDescription(), usedInDatasetIds.size());

            reviewableSamplingContexts.add(new ReviewableSamplingContext(samplingContext, usedInDatasetIds));

        });

        return reviewableSamplingContexts;
    }

    public void setDeviceCategoryReviewed(String deviceCategoryId) {

        // In case a device category is approved, we will approve its sensors and the sensors' dimensions.

        DeviceCategory deviceCategoryToApprove = deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(deviceCategoryId);

        logger.debug("\t...device category with id '{}' has {} sensors to approve", deviceCategoryId, deviceCategoryToApprove.getSensorIds().size());

        deviceCategoryToApprove.getSensorIds().forEach(sensorId -> {

            setSensorReviewed(sensorId);

        });

        logger.debug("\t...finally, set device category '{}' with id '{}' approved", deviceCategoryToApprove.getName(), deviceCategoryToApprove.getId());
        setModelledElementReviewStateReviewed(deviceCategoryToApprove, deviceCategoryCRUDRepository);
    }

    private void setModelledElementReviewStateReviewed(ModelledDomainElement modelledDomainElementToSetReviewed,
                                                       MongoRepository repositoryForPersistence) {

        if(modelledDomainElementToSetReviewed == null) {
            logger.warn("\t...Modelled Element of class {} is null and cant be set to reviewed", modelledDomainElementToSetReviewed.getClass().getName());
        } else if(!modelledDomainElementToSetReviewed.getModelledElementDetail().getElementLifecycle().getLifecycleState().equals(LifecycleState.REVIEWED)) {
            logger.debug("\t\t...persist element with id {} as REVIEWED", modelledDomainElementToSetReviewed.getId());
            modelledDomainElementToSetReviewed.getModelledElementDetail().getElementLifecycle().setLifecycleState(LifecycleState.REVIEWED);
            repositoryForPersistence.save(modelledDomainElementToSetReviewed);
        } else {
            logger.debug("\t\t...element with id {} is already REVIEWED ({}), do nothing!", modelledDomainElementToSetReviewed.getId(),
                    modelledDomainElementToSetReviewed.getModelledElementDetail().getElementLifecycle().getLifecycleState());
        }
    }

    public void setBaseEvaluationExtensionReviewed(String baseEvaluationExtensionIdToSetReviewed) {

        logger.debug("...got request to set base evaluation extension with id {} to reviewed",
                baseEvaluationExtensionIdToSetReviewed);

        BaseEvaluationExtension baseEvaluationExtensionToSetReviewed =
                baseEvaluationExtensionCRUDRepository.findById(baseEvaluationExtensionIdToSetReviewed).get();

        // first, set all included elements reviewed
        this.setBaseEvaluationReviewed(baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge());

        /*
         every potentially newly created artifact is reviewed now. We now need to add the respective setups or results
         to the existing ones and merge the evaluation criteria grants
         */

        // get BASE to extend
        BiometricAuthenticationSystemAndEvaluation baseToExtend =
                biometricAuthenticationSystemAndEvaluationRepository
                        .findByNameEqualsIgnoreCase(baseEvaluationExtensionToSetReviewed.getBaseNameForExtension());

        // prepare list of evaluation criteria that already have a grant
        List<String> evaluationCriteriaIdsWithGrant = baseToExtend.getEvaluationCriteriaGrants()
                .stream().map(evaluationCriteriaGrant -> evaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId())
                .collect(Collectors.toList());

        // start with experiment based evaluation
        if(baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getExperimentSpecificEvaluation()!=null
            && baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups()!=null) {

            logger.debug("\t...extending base '{}'s experimental evaluation with {} new setups",
                    baseToExtend.getName(),
                    baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size());

            if (baseToExtend.getBaseEvaluation().getExperimentSpecificEvaluation() != null) {
                logger.debug("\t\t...base '{}' to extend already has an experimental evaluation, we need to merge!",
                        baseEvaluationExtensionToSetReviewed.getBaseNameForExtension());
                if (baseToExtend.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups() != null) {
                    // at least the final setup of the extension needs to be taken over 100%, possibly datasets and biometric systems

                    // datasets
                    baseEvaluationExtensionToSetReviewed
                            .getBaseEvaluationToMerge()
                            .getExperimentSpecificEvaluation()
                            .getUsedDatasetIds().forEach(datasetId -> {

                                if(!baseToExtend
                                        .getBaseEvaluation()
                                        .getExperimentSpecificEvaluation()
                                        .getUsedDatasetIds()
                                        .contains(datasetId)) {

                                    logger.debug("\t\t\t...dataset id '{}' from extension is not yet in source BASE evaluation, add!",
                                            datasetId);

                                    baseToExtend
                                            .getBaseEvaluation()
                                            .getExperimentSpecificEvaluation()
                                            .getUsedDatasetIds().add(datasetId);

                                } else {
                                    logger.debug("\t\t\t...dataset id '{}' from extension is ALREADY in source BASE evaluation, ignore!",
                                            datasetId);
                                }
                    });

                    // experiment setups and biometric systems
                    baseEvaluationExtensionToSetReviewed
                            .getBaseEvaluationToMerge()
                            .getExperimentSpecificEvaluation()
                            .getExperimentSpecificEvaluationSetups().forEach(experimentSpecificEvaluationSetup -> {

                                // first, add setup itself
                                baseToExtend
                                        .getBaseEvaluation()
                                        .getExperimentSpecificEvaluation()
                                        .getExperimentSpecificEvaluationSetups().add(experimentSpecificEvaluationSetup);

                                // second, merge biometric systems used, e.g. add to overall if newly created
                                experimentSpecificEvaluationSetup.getBiometricSystemIds().forEach(biometricSystemId -> {
                                    if(!baseToExtend
                                            .getBaseEvaluation()
                                            .getExperimentSpecificEvaluation()
                                            .getBiometricSystemIds()
                                            .contains(biometricSystemId)) {

                                        logger.debug("\t\t\t...biometric system id '{}' from extension is not yet in source BASE evaluation, add!",
                                                biometricSystemId);

                                        baseToExtend
                                                .getBaseEvaluation()
                                                .getExperimentSpecificEvaluation()
                                                .getBiometricSystemIds().add(biometricSystemId);

                                    } else {
                                        logger.debug("\t\t\t...biometric system id '{}' from extension is ALREADY in source BASE evaluation, ignore!",
                                                biometricSystemId);
                                    }
                                });

                            });
                }
            } else {
                // no experimental evaluation exists yet, we can just take it over 100%
                logger.debug("\t\t...base '{}' to extend has no experimental evaluation yet, we can just take over 100%",
                        baseEvaluationExtensionToSetReviewed.getBaseNameForExtension());
                baseToExtend.getBaseEvaluation().setExperimentSpecificEvaluation(baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getExperimentSpecificEvaluation());
            }

        }

        // do for implementation based results, where we can just take over or added
        if(baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getImplementationSpecificEvaluationResults()!=null
            && baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getImplementationSpecificEvaluationResults().size()>0) {

            if (baseToExtend.getBaseEvaluation().getImplementationSpecificEvaluationResults() != null) {
                logger.debug("\t\t...base '{}' to extend already has {} implementation based evaluation results, lets add our {} results",
                        baseEvaluationExtensionToSetReviewed.getBaseNameForExtension(),
                        baseToExtend.getBaseEvaluation().getImplementationSpecificEvaluationResults().size(),
                        baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getImplementationSpecificEvaluationResults().size());
                baseToExtend.getBaseEvaluation().getImplementationSpecificEvaluationResults().addAll(
                    baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getImplementationSpecificEvaluationResults());

            } else {
                logger.debug("\t\t...base '{}' to extend has no implementation based evaluation yet, we can just take over our {} results to 100%",
                        baseEvaluationExtensionToSetReviewed.getBaseNameForExtension(),
                        baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getImplementationSpecificEvaluationResults().size());
                baseToExtend.getBaseEvaluation().setImplementationSpecificEvaluationResults(
                        baseEvaluationExtensionToSetReviewed.getBaseEvaluationToMerge().getImplementationSpecificEvaluationResults());
            }

        }

        // finally, merge evaluation criteria grants
        baseEvaluationExtensionToSetReviewed
                .getEvaluationCriteriaGrantsToMerge().forEach(evaluationCriteriaGrant ->  {


            if(evaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId()!=null) {
                logger.debug("\t\t..check whether criteria grant for exp criteria with id '{}' already exists",
                        evaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId());
                if(!evaluationCriteriaIdsWithGrant.contains(evaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId())) {
                    logger.debug("\t\t\t...base '{}' to extend has NOT YET an eval criteria grant for criteria with id '{}', add!",
                            baseEvaluationExtensionToSetReviewed.getBaseNameForExtension(),
                            evaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId());
                    baseToExtend.getEvaluationCriteriaGrants().add(evaluationCriteriaGrant);
                } else {
                    logger.debug("\t\t\t...base '{}' to extend ALREADY HAS an eval criteria grant for criteria with id '{}', ignore!",
                            baseEvaluationExtensionToSetReviewed.getBaseNameForExtension(),
                            evaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId());
                }
            }

            if(evaluationCriteriaGrant.getImplementationSpecificEvaluationCriteriaId()!=null) {
                logger.debug("\t\t..check whether criteria grant for impl criteria with id '{}' already exists",
                        evaluationCriteriaGrant.getImplementationSpecificEvaluationCriteriaId());
                if(!evaluationCriteriaIdsWithGrant.contains(evaluationCriteriaGrant.getImplementationSpecificEvaluationCriteriaId())) {
                    logger.debug("\t\t\t...base '{}' to extend has NOT YET an eval criteria grant for criteria with id '{}', add!",
                            baseEvaluationExtensionToSetReviewed.getBaseNameForExtension(),
                            evaluationCriteriaGrant.getImplementationSpecificEvaluationCriteriaId());
                    baseToExtend.getEvaluationCriteriaGrants().add(evaluationCriteriaGrant);
                } else {
                    logger.debug("\t\t\t...base '{}' to extend ALREADY HAS an eval criteria grant for criteria with id '{}', ignore!",
                            baseEvaluationExtensionToSetReviewed.getBaseNameForExtension(),
                            evaluationCriteriaGrant.getImplementationSpecificEvaluationCriteriaId());
                }
            }

        });

        // finally, save baseToExtend
        logger.debug("\t...finally, save extended base '{}'", baseToExtend.getName());
        biometricAuthenticationSystemAndEvaluationRepository.save(baseToExtend);

        logger.debug("...last but not least, set evaluation extension to be reviewed");
        setModelledElementReviewStateReviewed(baseEvaluationExtensionToSetReviewed,
                baseEvaluationExtensionCRUDRepository);


    }
}

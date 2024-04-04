package io.basemod.app.repository.review;

import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.evaluation.experiment.dataset.DataSetCRUDRepository;
import io.basemod.app.security.authentication.domain.BaseUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${spring.data.rest.base-path}/artifacts")
public class ModelledElementReviewRestController {

    private Logger logger = LoggerFactory.getLogger(ModelledElementReviewRestController.class);
    private MongoTemplate mongoTemplate;
    private ModelledElementReviewController modelledElementReviewController;
    private DataSetCRUDRepository dataSetCRUDRepository;

    public static final String ARTIFACT_TYPE_BASE = "base";
    public static final String ARTIFACT_TYPE_DATASETS = "datasets";

    public static final String ARTIFACT_TYPE_SAMPLING_CONTEXT = "samplingContexts";
    public static final String ARTIFACT_TYPE_BIOMETRIC_CHARACTERISTICS = "biometricCharacteristics";
    public static final String ARTIFACT_TYPE_DEVICE_CATEGORIES = "deviceCategories";

    public static final String ARTIFACT_TYPE_SENSORS = "sensors";

    public static final String ARTIFACT_TYPE_SENSOR_DIMENSIONS = "sensorDimensions";
    public static final String ARTIFACT_TYPE_RESOURCES_TO_PROTECT = "resourcesToProtect";
    public static final String ARTIFACT_TYPE_SAMPLE_DEVICES = "sampleDevices";
    public static final String ARTIFACT_TYPE_FEATURES = "features";
    public static final String ARTIFACT_TYPE_BIOMETRIC_PROCESSING_STEPS = "biometricProcessingSteps";
    public static final String ARTIFACT_TYPE_EVALUATION_CRITERIA_EXPERIMENT = "experimentCriteria";
    public static final String ARTIFACT_TYPE_RESULT_METRICS = "resultMetrics";
    public static final String ARTIFACT_TYPE_EVALUATION_CRITERIA_IMPLEMENTATION = "implementationCriteria";

    public static final String ARTIFACT_TYPE_BASE_EVALUATION_EXTENSION = "baseEvaluationExtension";

    @Autowired
    public ModelledElementReviewRestController(MongoTemplate mongoTemplate, ModelledElementReviewController modelledElementReviewController, DataSetCRUDRepository dataSetCRUDRepository) {
        this.mongoTemplate = mongoTemplate;
        this.modelledElementReviewController = modelledElementReviewController;
        this.dataSetCRUDRepository = dataSetCRUDRepository;
    }

    //provide all items to review
    @RequestMapping(value = "/review", method = RequestMethod.GET)
    @RolesAllowed("REVIEWER")
    public Map<String, List<? extends ModelledDomainElement>> getAllArtifactsToReviewForReviewer() {

        return modelledElementReviewController.getAllArtifactsToReviewForReviewer();

    }

    @RequestMapping(value = "/review/{artifactType}", method = RequestMethod.GET)
    @RolesAllowed("REVIEWER")
    public List<? extends ModelledDomainElement> getAllArtifactsOfTypeToReviewForReviewer(@PathVariable String artifactType) {

        return modelledElementReviewController.getAllArtifactsOfTypeToReviewForReviewer(artifactType);

    }

    // provide items of a modeller that are in review
    @RequestMapping(value = "/me/review", method = RequestMethod.GET)
    @RolesAllowed("MODELLER")
    public Map<String, List<? extends ModelledDomainElement>> getArtifactsToReviewForCurrentUser() {

        BaseUser authenticatedUser = (BaseUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return modelledElementReviewController.getAllArtifactsToReviewForAuthenticatedUser(authenticatedUser.getUniqueId());

    }

    @RequestMapping(value = "/me/review/{artifactType}", method = RequestMethod.GET)
    @RolesAllowed("MODELLER")
    public List<? extends ModelledDomainElement> restGetArtifactsOfTypeToReviewForAuthenticatedUser(@PathVariable String artifactType) {

        return getArtifactsOfTypeToReviewForAuthenticatedUser(artifactType);

    }

    public List<? extends ModelledDomainElement> getArtifactsOfTypeToReviewForAuthenticatedUser(String artifactType) {
        BaseUser authenticatedUser = (BaseUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return modelledElementReviewController.getArtifactsOfTypeToReviewForAuthenticatedUser(
                authenticatedUser.getUniqueId(),
                artifactType);
    }



    @RequestMapping(value = "/reviewed/{artifactType}/{modelledElementId}", method = RequestMethod.POST)
    @RolesAllowed({"REVIEWER", "ADMIN"})
    public Response setElementReviewed(@PathVariable String artifactType, @PathVariable String modelledElementId) {

        logger.info("Set modelledElement of type {} with id {} as reviewed", artifactType, modelledElementId);
        logger.debug("\t...get element of type {} by id {}", artifactType, modelledElementId);

        switch(artifactType) {
            case ARTIFACT_TYPE_BASE:
                modelledElementReviewController.setBaseReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_BASE_EVALUATION_EXTENSION:
                modelledElementReviewController.setBaseEvaluationExtensionReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_DATASETS:
                modelledElementReviewController.setDatasetReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_SAMPLING_CONTEXT:
                modelledElementReviewController.setSamplingContextReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_BIOMETRIC_PROCESSING_STEPS:
                modelledElementReviewController.setBiometricProcessingStepReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_BIOMETRIC_CHARACTERISTICS:
                modelledElementReviewController.setBiometricCharacteristicReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_DEVICE_CATEGORIES:
                modelledElementReviewController.setDeviceCategoryReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_SENSORS:
                modelledElementReviewController.setSensorReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_EVALUATION_CRITERIA_EXPERIMENT:
                modelledElementReviewController.setExperimentSpecificCriteriaReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_EVALUATION_CRITERIA_IMPLEMENTATION:
                modelledElementReviewController.setImplementationSpecificCriteriaReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_FEATURES:
                modelledElementReviewController.setFeatureReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_RESOURCES_TO_PROTECT:
                modelledElementReviewController.setProtectedResourceReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_RESULT_METRICS:
                modelledElementReviewController.setResultMetricReviewed(modelledElementId);
                break;
            case ARTIFACT_TYPE_SAMPLE_DEVICES:
                modelledElementReviewController.setSampleDeviceReviewed(modelledElementId);
                break;
            default:
                logger.warn("\t...DO NOT KNOW ANY ARTEFACT TYPE {}", artifactType);
                return Response.status(Response.Status.NOT_FOUND).build();

        }

        return Response.ok().build();

    }


}

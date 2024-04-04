package io.basemod.app.repository;

import io.basemod.app.architecture.ResourceToProtect;
import io.basemod.app.architecture.ResourceToProtectCRUDRepository;
import io.basemod.app.base.*;
import io.basemod.app.base.lifecycle.LifecycleState;
import io.basemod.app.characteristic.BiometricCharacteristic;
import io.basemod.app.characteristic.BiometricCharacteristicCRUDRepository;
import io.basemod.app.characteristic.BiometricCharacteristicService;
import io.basemod.app.characteristic.SoftBiometricCharacteristic;
import io.basemod.app.evaluation.BaseEvaluationService;
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
import io.basemod.app.evaluation.experiment.dataset.sampling.EnrichedSampledBiometric;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContext;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContextCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryService;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceService;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.*;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaService;
import io.basemod.app.evaluation.experiment.evaluation.result.ExperimentSpecificEvaluationSetup;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.EnrichedResultMetric;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetric;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetricCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetricService;
import io.basemod.app.evaluation.extension.BaseEvaluationExtension;
import io.basemod.app.evaluation.extension.BaseEvaluationExtensionCRUDRepository;
import io.basemod.app.evaluation.extension.EnrichedBaseEvaluationExtension;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteriaCRUDRepository;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationResult;
import io.basemod.app.security.authentication.domain.BaseUser;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "${spring.data.rest.base-path}/lcaware")
public class ModelledElementLifecycleAwareRestRepository {

    Logger logger = LoggerFactory.getLogger(ModelledElementLifecycleAwareRestRepository.class);

    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;
    private EnrichedBiometricAuthenticationSystemAndEvaluationService enrichedBiometricAuthenticationSystemAndEvaluationService;
    private DataSetCRUDRepository dataSetCRUDRepository;
    private SampleDeviceCRUDRepository sampleDeviceCRUDRepository;
    private SensorCRUDRepository sensorCRUDRepository;
    private SensorDimensionCRUDRepository sensorDimensionCRUDRepository;
    private SamplingContextCRUDRepository samplingContextCRUDRepository;
    private BiometricSystemCRUDRepository biometricSystemCRUDRepository;
    private BiometricProcessingStepCRUDRepository biometricProcessingStepCRUDRepository;
    private FeatureCRUDRepository featureCRUDRepository;
    private BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository;
    private DeviceCategoryCRUDRepository deviceCategoryCRUDRepository;
    private ResourceToProtectCRUDRepository resourceToProtectCRUDRepository;
    private ResultMetricCRUDRepository resultMetricCRUDRepository;
    private ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository;
    private ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository;
    private ModelledElementService modelledElementService;
    private BaseService baseService;
    private BiometricSystemService biometricSystemService;
    private ResultMetricService resultMetricService;
    private SampleDeviceService sampleDeviceService;
    private DeviceCategoryService deviceCategoryService;
    private SensorService sensorService;
    private ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService;

    private BiometricCharacteristicService biometricCharacteristicService;

    private BaseEvaluationExtensionCRUDRepository baseEvaluationExtensionCRUDRepository;

    private BaseEvaluationService baseEvaluationService;

    @Autowired
    public ModelledElementLifecycleAwareRestRepository(BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository, EnrichedBiometricAuthenticationSystemAndEvaluationService enrichedBiometricAuthenticationSystemAndEvaluationService, DataSetCRUDRepository dataSetCRUDRepository, SampleDeviceCRUDRepository sampleDeviceCRUDRepository, SensorCRUDRepository sensorCRUDRepository, SensorDimensionCRUDRepository sensorDimensionCRUDRepository, SamplingContextCRUDRepository samplingContextCRUDRepository, BiometricSystemCRUDRepository biometricSystemCRUDRepository, BiometricProcessingStepCRUDRepository biometricProcessingStepCRUDRepository, FeatureCRUDRepository featureCRUDRepository, BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository, DeviceCategoryCRUDRepository deviceCategoryCRUDRepository, ResourceToProtectCRUDRepository resourceToProtectCRUDRepository, ResultMetricCRUDRepository resultMetricCRUDRepository, ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository, ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository, ModelledElementService modelledElementService, BaseService baseService, BiometricSystemService biometricSystemService, ResultMetricService resultMetricService, SampleDeviceService sampleDeviceService, DeviceCategoryService deviceCategoryService, SensorService sensorService, ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService, BiometricCharacteristicService biometricCharacteristicService, BaseEvaluationExtensionCRUDRepository baseEvaluationExtensionCRUDRepository, BaseEvaluationService baseEvaluationService) {
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
        this.enrichedBiometricAuthenticationSystemAndEvaluationService = enrichedBiometricAuthenticationSystemAndEvaluationService;
        this.dataSetCRUDRepository = dataSetCRUDRepository;
        this.sampleDeviceCRUDRepository = sampleDeviceCRUDRepository;
        this.sensorCRUDRepository = sensorCRUDRepository;
        this.sensorDimensionCRUDRepository = sensorDimensionCRUDRepository;
        this.samplingContextCRUDRepository = samplingContextCRUDRepository;
        this.biometricSystemCRUDRepository = biometricSystemCRUDRepository;
        this.biometricProcessingStepCRUDRepository = biometricProcessingStepCRUDRepository;
        this.featureCRUDRepository = featureCRUDRepository;
        this.biometricCharacteristicCRUDRepository = biometricCharacteristicCRUDRepository;
        this.deviceCategoryCRUDRepository = deviceCategoryCRUDRepository;
        this.resourceToProtectCRUDRepository = resourceToProtectCRUDRepository;
        this.resultMetricCRUDRepository = resultMetricCRUDRepository;
        this.implementationSpecificEvaluationCriteriaCRUDRepository = implementationSpecificEvaluationCriteriaCRUDRepository;
        this.experimentSpecificEvaluationCriteriaCRUDRepository = experimentSpecificEvaluationCriteriaCRUDRepository;
        this.modelledElementService = modelledElementService;
        this.baseService = baseService;
        this.biometricSystemService = biometricSystemService;
        this.resultMetricService = resultMetricService;
        this.sampleDeviceService = sampleDeviceService;
        this.deviceCategoryService = deviceCategoryService;
        this.sensorService = sensorService;
        this.experimentSpecificEvaluationCriteriaService = experimentSpecificEvaluationCriteriaService;
        this.biometricCharacteristicService = biometricCharacteristicService;
        this.baseEvaluationExtensionCRUDRepository = baseEvaluationExtensionCRUDRepository;
        this.baseEvaluationService = baseEvaluationService;
    }

    private BaseUser getCurrentBaseUser() {
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof BaseUser) {
            return (BaseUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return new BaseUser(false);
    }

    @RequestMapping(value = "base", method = RequestMethod.GET)
    public List<EnrichedBaseForExport> getEnrichedBase() {
        return enrichedBiometricAuthenticationSystemAndEvaluationService.transformBasesToReturn(
                biometricAuthenticationSystemAndEvaluationRepository.findAll().stream().filter(biometricAuthenticationSystemAndEvaluation -> {
                            return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(biometricAuthenticationSystemAndEvaluation);
                        }
                ).collect(Collectors.toList()));
    }

    @RequestMapping(value = "baseEvaluationExtensions", method = RequestMethod.GET)
    public List<EnrichedBaseEvaluationExtension> getBaseEvaluationExtensions() {
        return baseEvaluationExtensionCRUDRepository
                .findAll()
                .stream()
                .filter(baseEvaluationExtension -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(baseEvaluationExtension);
                }).map(baseEvaluationExtension -> {
                    return baseEvaluationService.enrichBaseEvaluationExtension(baseEvaluationExtension);
                }).collect(Collectors.toList());
    }

    @RequestMapping(value = "baseEvaluationExtensions/{baseEvaluationExtensionToDeleteId}", method = RequestMethod.DELETE)
    public Response deleteBaseEvaluationExtension(@PathVariable String baseEvaluationExtensionToDeleteId) {

        logger.debug("\t...got request to delete base evaluation extension with id '{}'", baseEvaluationExtensionToDeleteId);

        if(baseEvaluationExtensionCRUDRepository.findById(baseEvaluationExtensionToDeleteId).isPresent()) {

            // before deleting the extension, we delete all new biometric systems
            BaseEvaluationExtension baseEvaluationExtensionToDelete =
                    baseEvaluationExtensionCRUDRepository.findById(baseEvaluationExtensionToDeleteId).get();

            // delete biometric systems just created for this extension, if existing
            if(baseEvaluationExtensionToDelete.getBaseEvaluationToMerge().getExperimentSpecificEvaluation()!=null
                && baseEvaluationExtensionToDelete.getBaseEvaluationToMerge().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups()!=null) {

                baseEvaluationExtensionToDelete
                        .getBaseEvaluationToMerge()
                        .getExperimentSpecificEvaluation()
                        .getExperimentSpecificEvaluationSetups().forEach(experimentSpecificEvaluationSetup -> {

                    experimentSpecificEvaluationSetup.getBiometricSystemIds().forEach(biometricSystemId -> {
                        logger.debug("\t...check review state of biometric system with id '{}'",
                                biometricSystemId);

                        BiometricSystem modelledSystem = biometricSystemCRUDRepository.findById(biometricSystemId).get();

                        logger.debug("\t...found biometric system, name: {}, lifecycle state: {}",
                                modelledSystem.getName(), modelledSystem.getModelledElementDetail().getElementLifecycle().getLifecycleState());
                        if(modelledSystem.getModelledElementDetail().getElementLifecycle().getLifecycleState().equals(LifecycleState.REVIEWED)) {
                            logger.debug("\t\t...biometric system '{}' with id '{}' is reviewed, so from other evaluations, DO NOT DELETE",
                                    modelledSystem.getName(),
                                    biometricSystemId);
                        } else {
                            logger.debug("\t\t...biometric system '{}' with id '{}' is NOT reviewed yet, DO DELETE",
                                    modelledSystem.getName(),
                                    biometricSystemId);
                            biometricSystemCRUDRepository.delete(modelledSystem);
                        }

                    });


                });


            }


            // finally, delete base evaluation extension
            logger.debug("\t...finally, deleting base evaluation extension");
            baseEvaluationExtensionCRUDRepository.delete(baseEvaluationExtensionToDelete);
            return Response.ok().build();
        }

        logger.debug("\t...did not find any base evaluation extension with id '{}' for deletion", baseEvaluationExtensionToDeleteId);
        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @RequestMapping(value = "dataSets", method = RequestMethod.GET)
    public List<EnrichedDataSet> getDatasets() {

        logger.debug("\t...got request to give back all lifecycle aware datasets");

        return modelledElementService.getAllEnrichedDatasets();
    }

    @RequestMapping(value = "samplingContexts", method = RequestMethod.GET)
    public List<SamplingContext> getSamplingContexts() {
        return samplingContextCRUDRepository.findAll().stream().filter(samplingContext -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(samplingContext);
                }
        ).collect(Collectors.toList());
    }

    @RequestMapping(value = "samplingContexts/{samplingContextToDeleteId}", method = RequestMethod.DELETE)
    //@Transactional
    public Response deleteSamplingContext(@PathVariable String samplingContextToDeleteId) {
        logger.debug("\t...do lifecycle aware deletion of sampling context with id '{}', i.e., removing it from any Dataset where it was reported", samplingContextToDeleteId);

        /*
        we need to find any Dataset where it was mentioned
         */

        if(samplingContextCRUDRepository.findById(samplingContextToDeleteId).isPresent()) {
            SamplingContext samplingContextToDelete = samplingContextCRUDRepository.findById(samplingContextToDeleteId).get();

            List<DataSet> allDatasets = dataSetCRUDRepository.findAll();
            allDatasets.forEach(dataSet -> {
                final boolean[] updateDatasetRequired = {false};
                dataSet.getSampledBiometrics().forEach(sampledBiometric -> {
                    int samplingContextsOfSampledBiometric = sampledBiometric.getSamplingContextIds().size();
                    sampledBiometric.setSamplingContextIds(sampledBiometric.getSamplingContextIds().stream().filter(samplingContextId -> {
                        return !samplingContextId.equals(samplingContextToDelete.getId());
                    }).collect(Collectors.toList()));
                    if(sampledBiometric.getSamplingContextIds().size()<samplingContextsOfSampledBiometric) {
                        updateDatasetRequired[0] = true;
                    }
                });
                if(updateDatasetRequired[0]) {
                    logger.debug("\t\t...need to update dataset '{}' as sampling context was used in at least one sampled biometric",
                            dataSet.getName());
                    dataSetCRUDRepository.save(dataSet);
                }
            });

            // delete sampling context itself
            logger.debug("\t\t...deleting sampling context '{}'", samplingContextToDelete.getDescription());
            samplingContextCRUDRepository.delete(samplingContextToDelete);
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @RequestMapping(value = "sampleDevices", method = RequestMethod.GET)
    public List<EnrichedSampleDevice> getSampleDevices() {

        List<SampleDevice> sampleDevices = sampleDeviceCRUDRepository.findAll().stream().filter(sampleDevice -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(sampleDevice);
                }
        ).collect(Collectors.toList());

        logger.debug("\t...enrich {} sample devices", sampleDevices.size());

        List<EnrichedSampleDevice> enrichedSampleDevices= sampleDevices
                .stream()
                .filter(sampleDevice -> {
                    return sampleDevice.getDeviceCategoryId()!=null;
                })
                .map(sampleDevice -> {

            EnrichedSampleDevice enrichedSampleDevice =
                    new EnrichedSampleDevice(
                            sampleDevice,
                            deviceCategoryService.getDeviceCategoryByIdWithEnrichedSensorsUserSpecific(
                                    sampleDevice.getDeviceCategoryId(), getCurrentBaseUser())
                    );


            return enrichedSampleDevice;
        }).collect(Collectors.toList());

        return enrichedSampleDevices;
    }

    @RequestMapping(value = "sampleDevices/{sampleDeviceIdToDelete}", method = RequestMethod.DELETE)
    //@Transactional
    public Response deleteSampleDevice(@PathVariable String sampleDeviceIdToDelete) {

        logger.debug("\t...got request to delete sample device with id '{}'", sampleDeviceIdToDelete);

        if(sampleDeviceCRUDRepository.findById(sampleDeviceIdToDelete).isPresent()) {

            sampleDeviceService.deleteSampleDevice(sampleDeviceIdToDelete,
                    getEnrichedBase(),
                    getDatasets());

            return Response.ok().build();
        }

        logger.debug("\t...did not find any sample device with id '{}' for deletion", sampleDeviceIdToDelete);
        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @RequestMapping(value = "sensors", method = RequestMethod.GET)
    public List<EnrichedSensor> getSensors() {
        List<Sensor> allSensors = sensorCRUDRepository.findAll().stream().filter(sensor -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(sensor);
                }
        ).collect(Collectors.toList());

        return allSensors.stream().map(sensor -> sensorService.getEnrichedSensorFromSensor(sensor)).collect(Collectors.toList());
    }

    @RequestMapping(value = "sensors/{sensorIdToDelete}", method = RequestMethod.DELETE)
    //@Transactional
    public Response deleteSensor(@PathVariable String sensorIdToDelete) {

        logger.debug("\t...do lifecycle aware deletion of sensor with id '{}', i.e., deleting defined dimensions and removing from device categories", sensorIdToDelete);

        /*
        we need to delete sensor dimensions attached to sensor as well as updating any device category that currently has this sensor assigned
         */

        if(sensorCRUDRepository.findById(sensorIdToDelete).isPresent()) {

            sensorService.deleteSensorBySensorId(
                    sensorIdToDelete,
                    getEnrichedBase(),
                    getSampleDevices(),
                    getDatasets()
            );
            return Response.ok().build();


        }
        logger.debug("\t... no sensor with id '{}' exists", sensorIdToDelete);
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @RequestMapping(value = "sensorDimensions", method = RequestMethod.GET)
    public List<SensorDimension> getSensorDimensions() {
        return sensorDimensionCRUDRepository.findAll().stream().filter(sensorDimension -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(sensorDimension);
                }
        ).collect(Collectors.toList());
    }

    @RequestMapping(value = "sensorDimensions/{sensorDimensionToDeleteId}", method = RequestMethod.DELETE)
    //@Transactional
    public Response deleteSensorDimension(@PathVariable String sensorDimensionToDeleteId) {

        logger.debug("\t...got request to delete sensor dimension with id '{}'", sensorDimensionToDeleteId);

        if(sensorDimensionCRUDRepository.findById(sensorDimensionToDeleteId).isPresent()) {

            SensorDimension sensorDimensionToDelete = sensorDimensionCRUDRepository.findById(sensorDimensionToDeleteId).get();

            /*
            we have to delete the dimension from the sensor it was assigned to
                - if the sensor has no dimensions left, the sensor has to be deleted as well
                    - see all implications of that in the respective method ;)
            finally, we have to delete the sensor dimension
             */

            List<EnrichedSensor> userEditableSensors = getSensors();

            final boolean[] deleteDimensionRequired = {true};
            userEditableSensors.forEach(enrichedSensor -> {

                List<String> remainingDimensionIds = enrichedSensor.getSensorDimensionIds().stream().filter(sensorDimensionId -> {
                    return !sensorDimensionId.equals(sensorDimensionToDeleteId);
                }).collect(Collectors.toList());

                if(remainingDimensionIds.size()==0) {

                    logger.debug("\t\t..no sensor dimensions remain for sensor '{}', delete sensor",
                            enrichedSensor.getName());

                    // will also delete dimension
                    sensorService.deleteSensorBySensorId(enrichedSensor.getId(),
                            getEnrichedBase(),
                            getSampleDevices(),
                            getDatasets());

                    deleteDimensionRequired[0] = false;
                } else if(remainingDimensionIds.size()<enrichedSensor.getSensorDimensionIds().size()) {

                    logger.debug("\t\t..only {} of {} sensor dimensions remain for sensor '{}', change sensor",
                            remainingDimensionIds.size(),
                            enrichedSensor.getSensorDimensionIds().size(),
                            enrichedSensor.getName());

                    Sensor sensorToChange = sensorCRUDRepository.findById(enrichedSensor.getId()).get();
                    sensorToChange.setSensorDimensionIds(remainingDimensionIds);
                    logger.debug("\t\t\t...update sensor {} in DB", enrichedSensor.getName());
                    sensorCRUDRepository.save(sensorToChange);

                } else {

                    logger.debug("\t\t..all sensor dimensions remain for sensor '{}', nothing todo",
                            enrichedSensor.getName());

                }

            });

            if(deleteDimensionRequired[0]) {
                //finally, delete sensor dimension
                logger.debug("\t\t...finally, delete sensor dimension '{}' by Id '{}'",
                        sensorDimensionToDelete.getName(),
                        sensorDimensionToDelete.getId()
                );
                sensorDimensionCRUDRepository.delete(sensorDimensionToDelete);
            }

            return Response.ok().build();
        }

        logger.debug("\t...did not find any sensor dimension with id '{}' for deletion", sensorDimensionToDeleteId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @RequestMapping(value = "deviceCategories", method = RequestMethod.GET)
    public List<DeviceCategory> getDeviceCategories() {
        return deviceCategoryCRUDRepository
                .findAll()
                .stream()
                .filter(deviceCategory -> !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(deviceCategory))
                    .map(deviceCategory -> deviceCategoryService.getDeviceCategoryByIdWithEnrichedSensorsUserSpecific(deviceCategory.getId(), getCurrentBaseUser()))
                    .collect(Collectors.toList());
    }

    @RequestMapping(value = "deviceCategories/{deviceCategoryId}", method = RequestMethod.GET)
    public DeviceCategory getDeviceCategoryById(@PathVariable String deviceCategoryId) {
        return deviceCategoryService.getEnrichedDeviceCategoryByIdWithEnrichedSensors(deviceCategoryId);
    }

    @RequestMapping(value = "deviceCategories/{deviceCategoryIdToDelete}", method = RequestMethod.DELETE)
    public Response deleteDeviceCategory(@PathVariable String deviceCategoryIdToDelete) {

        logger.debug("\t...request to delete device category by id '{}'", deviceCategoryIdToDelete);

        if(deviceCategoryCRUDRepository.findById(deviceCategoryIdToDelete).isPresent()) {
            modelledElementService.deleteDeviceCategoryById(deviceCategoryIdToDelete,
                    getEnrichedBase(),
                    getSampleDevices(),
                    getDatasets());
            return Response.ok().build();
        }

        logger.debug("\t...did not find any device category with id '{}' to delete", deviceCategoryIdToDelete);
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @RequestMapping(value = "biometricSystems", method = RequestMethod.GET)
    public List<EnrichedBiometricSystem> getBiometricSystems() {

        List<BiometricSystem> allBiometricSystemsEditableByUser = biometricSystemCRUDRepository.findAll().stream().filter(biometricSystem -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(biometricSystem);
                }
        ).collect(Collectors.toList());

        return allBiometricSystemsEditableByUser
                .stream()
                .map(biometricSystem -> biometricSystemService.enrichBiometricSystem(biometricSystem)).collect(Collectors.toList());
    }

    @RequestMapping(value = "biometricSystems/{biometricSystemId}", method = RequestMethod.GET)
    public EnrichedBiometricSystem getBiometricSystemById(@PathVariable String biometricSystemId) {

        BiometricSystem biometricSystemForUser = biometricSystemCRUDRepository.findById(biometricSystemId).get();

        if(!modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(biometricSystemForUser)) {
            return biometricSystemService.enrichBiometricSystem(biometricSystemForUser);
        }

        return null;

    }

    @RequestMapping(value = "biometricSystems/parentBaseName/{parentBaseName}", method = RequestMethod.GET)
    public List<EnrichedBiometricSystem> getBiometricSystemsByParentBaseName(@PathVariable String parentBaseName) {

        List<BiometricSystem> allBiometricSystemsEditableByUser = biometricSystemCRUDRepository.findAllByParentBaseName(parentBaseName).stream().filter(biometricSystem -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(biometricSystem);
                }
        ).collect(Collectors.toList());

        return allBiometricSystemsEditableByUser
                .stream()
                .map(biometricSystem -> biometricSystemService.enrichBiometricSystem(biometricSystem)).collect(Collectors.toList());

    }

    @RequestMapping(value = "biometricSystemProcessingSteps", method = RequestMethod.GET)
    public List<BiometricSystemProcessingStep> getBiometricSystemProcessingSteps() {
        return biometricProcessingStepCRUDRepository.findAll().stream().filter(biometricSystemProcessingStep -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(biometricSystemProcessingStep);
                }
        ).collect(Collectors.toList());
    }

    @RequestMapping(value = "biometricSystemProcessingSteps/{biometricSystemProcessingStepsToDeleteId}", method = RequestMethod.DELETE)
    //@Transactional
    public Response deleteBiometricProcessingStep(@PathVariable String biometricSystemProcessingStepsToDeleteId) {
        logger.debug("\t...got request to delete processing step by id '{}'", biometricSystemProcessingStepsToDeleteId);

        if(biometricProcessingStepCRUDRepository.findById(biometricSystemProcessingStepsToDeleteId).isPresent()) {

            BiometricSystemProcessingStep processingStepToDelete =
                    biometricProcessingStepCRUDRepository.findById(biometricSystemProcessingStepsToDeleteId).get();

            /*
            we need to find any biometric system where the step is included and delete it
                - if the step is the only one of the type (e.g. a matching or decision step), the whole biometric system needs
                to be deleted
                    - we have to deleted any experimental evaluation result basing on that biometric system
                        - if no result remains, experimental evaluation needs a reset
                            - if no impl-spec evaluation exists, base modelling state is reset
             */

            List<EnrichedBiometricSystem> biometricSystemsEditableByUser = getBiometricSystems();
            biometricSystemsEditableByUser.forEach(enrichedBiometricSystem -> {

                boolean biometricSystemRemains = true;

                List<BiometricSystemProcessingStep> remainingSignalProcessingSteps = enrichedBiometricSystem.getSignalProcessingSteps().stream().filter(biometricSystemProcessingStep -> {
                    return !biometricSystemProcessingStep.getId().equals(biometricSystemProcessingStepsToDeleteId);
                }).collect(Collectors.toList());

                if(remainingSignalProcessingSteps.size()==0) {
                    logger.debug("\t\t...no signal processing steps remain for biometric system '{}' of base '{}', delete system and check for related elements",
                            enrichedBiometricSystem.getName(), enrichedBiometricSystem.getParentBaseName());

                    biometricSystemService.deleteBiometricSystemById(enrichedBiometricSystem.getId());
                    biometricSystemRemains = false;

                } else if(remainingSignalProcessingSteps.size()<enrichedBiometricSystem.getSignalProcessingSteps().size()) {
                    logger.debug("\t\t...{} of {} signal processing steps remain for biometric system '{}' of base '{}', change biometric system",
                            remainingSignalProcessingSteps.size(),
                            enrichedBiometricSystem.getSignalProcessingSteps().size(),
                            enrichedBiometricSystem.getName(), enrichedBiometricSystem.getParentBaseName());

                    BiometricSystem biometricSystemToChange = biometricSystemCRUDRepository.findById(enrichedBiometricSystem.getId()).get();
                    biometricSystemToChange.setSignalProcessingStepIds(remainingSignalProcessingSteps
                            .stream().map(biometricSystemProcessingStep -> biometricSystemProcessingStep.getId()).collect(Collectors.toList()));
                    logger.debug("\t\t\t...update biometric system '{}'", biometricSystemToChange.getName());
                    biometricSystemCRUDRepository.save(biometricSystemToChange);
                } else {
                    logger.debug("\t\t...no change for biometric system '{}' of base '{}' required",
                            enrichedBiometricSystem.getName(), enrichedBiometricSystem.getParentBaseName());
                }

                if(biometricSystemRemains) {
                    List<BiometricSystemProcessingStep> remainingFurtherProcessingSteps =
                            enrichedBiometricSystem.getFurtherProcessingSteps().stream().filter(biometricSystemProcessingStep -> {
                                return !biometricSystemProcessingStep.getId().equals(biometricSystemProcessingStepsToDeleteId);
                            }).collect(Collectors.toList());

                    if (remainingFurtherProcessingSteps.size() == 0) {
                        logger.debug("\t...none of further processing steps of biometric system '{}' of base '{}' remain, delete system and check related elements",
                                enrichedBiometricSystem.getName(), enrichedBiometricSystem.getParentBaseName());

                        biometricSystemService.deleteBiometricSystemById(enrichedBiometricSystem.getId());
                    } else if (remainingFurtherProcessingSteps.size() < enrichedBiometricSystem.getFurtherProcessingSteps().size()) {
                        logger.debug("\t\t...{} of {} further processing steps remain for biometric system '{}' of base '{}', check whether will still have steps for every discipline",
                                remainingSignalProcessingSteps.size(),
                                enrichedBiometricSystem.getSignalProcessingSteps().size(),
                                enrichedBiometricSystem.getName(), enrichedBiometricSystem.getParentBaseName());

                        final boolean[] matchingStepExists = {false};
                        final boolean[] decisionStepExists = {false};
                        final boolean[] storageStepExists = {false};

                        remainingFurtherProcessingSteps.forEach(biometricSystemProcessingStep -> {
                            switch (biometricSystemProcessingStep.getProcessingType()) {
                                case STORAGE:
                                    storageStepExists[0] = true;
                                    break;
                                case DECISION:
                                    decisionStepExists[0] = true;
                                    break;
                                case MATCHING:
                                    matchingStepExists[0] = true;
                                    break;
                            }
                        });

                        if (matchingStepExists[0] && decisionStepExists[0] && storageStepExists[0]) {
                            logger.debug("\t\t\t..we still have at least one processing step for every discipline in system '{}'",
                                    enrichedBiometricSystem.getName());
                        } else {
                            logger.debug("\t\t\t..at least one system discipline misses a step, remove system '{}'",
                                    enrichedBiometricSystem.getName());
                            biometricSystemService.deleteBiometricSystemById(enrichedBiometricSystem.getId());
                        }

                    } else {
                        logger.debug("\t\t...no change for biometric system '{}' of base '{}' required",
                                enrichedBiometricSystem.getName(), enrichedBiometricSystem.getParentBaseName());
                    }
                }

            });

            //finally, delete processing step
            logger.debug("\t\t...finally, delete biometric processing step with id '{}'", biometricSystemProcessingStepsToDeleteId);
            biometricProcessingStepCRUDRepository.delete(processingStepToDelete);

            return Response.ok().build();
        }

        logger.debug("\t...did not find any biometric system processing step by id '{}' to delete", biometricSystemProcessingStepsToDeleteId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @RequestMapping(value = "features", method = RequestMethod.GET)
    public List<Feature> getFeatures() {
        return featureCRUDRepository.findAll().stream().filter(feature -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(feature);
                }
        ).collect(Collectors.toList());
    }

    @RequestMapping(value = "features/{featureToDeleteId}", method = RequestMethod.DELETE)
    public Response deleteFeatureById(@PathVariable String featureToDeleteId) {

        logger.debug("\t...got request to delete feature by id {}", featureToDeleteId);

        if(featureCRUDRepository.findById(featureToDeleteId).isPresent()) {

            // has to be there
            Feature featureToDelete = featureCRUDRepository.findById(featureToDeleteId).get();

            // if we delete a feature, we have to also delete it from any biometric system and see whether the biometric system
            // gets invalidated. If so, the experimental evaluation will be reset and, if no implementation-based evaluation exists
            // resets the overall base's modelling progress to "target architecture"

            List<EnrichedBaseForExport> baseEditableByUser =
                    getEnrichedBase();

            baseEditableByUser.forEach(enrichedBase -> {
                logger.debug("\t\t...see whether feature deletion affects base '{}'", enrichedBase.getName());

                if(enrichedBase.getModellingProgress().equals(ModellingProgress.EVALUATION)
                    || enrichedBase.getModellingProgress().equals(ModellingProgress.EVALUATION_CRITERIA_GRANTS)
                        || enrichedBase.getModellingProgress().equals(ModellingProgress.REFERENCE)) {

                    if(enrichedBase.getBaseEvaluation().getExperimentSpecificEvaluation()!=null
                        && enrichedBase.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size()>0) {

                        List<String> biometricSystemIdsToDelete = new ArrayList<>();

                        // check biometric systems to delete
                        enrichedBase.getBaseEvaluation().getExperimentSpecificEvaluation()
                                .getBiometricSystems().forEach(biometricSystem -> {
                                    List<String> newFeatureIds = biometricSystem.getFeatures()
                                            .stream()
                                            .filter(feature -> !feature.getId().equals(featureToDeleteId))
                                            .map(feature -> feature.getId())
                                            .collect(Collectors.toList());

                                    if(newFeatureIds.size()==0) {
                                        logger.debug("\t\t\t...all features of biometric system '{}' needs to be deleted, also remove biometric system",
                                                biometricSystem.getName());
                                        biometricSystemIdsToDelete.add(biometricSystem.getId());
                                    }
                                });


                        if(biometricSystemIdsToDelete.size()>0) {

                            // check whether any result remains in case we need to delete a biometric system
                            // if no results remain, we reset base's modelling progress to 'target architecture'

                            // has to be there
                            BiometricAuthenticationSystemAndEvaluation baseToChange =
                                    biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBase.getId()).get();

                            // first delete biometric system ids from evaluation
                            baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().setBiometricSystemIds(
                                    baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation()
                                            .getBiometricSystemIds()
                                            .stream()
                                            .filter(biometricSystemId -> !biometricSystemIdsToDelete.contains(biometricSystemId)).collect(Collectors.toList())
                            );

                            // check which results we further need to modify
                            biometricSystemIdsToDelete.forEach(biometricSystemIdToDelete -> {

                                List<ExperimentSpecificEvaluationSetup> remainingResults =
                                        baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation()
                                                .getExperimentSpecificEvaluationSetups().stream().filter(experimentSpecificEvaluationSetup -> {

                                            if(experimentSpecificEvaluationSetup.getBiometricSystemIds().contains(biometricSystemIdToDelete)) {
                                                return false;
                                            }

                                            return true;

                                        }).collect(Collectors.toList());

                                if(remainingResults.size()==0) {
                                    logger.debug("\t\t\t...all experimental evaluation results of base '{}' need deletion," +
                                            " since every result included biometric system to delete", enrichedBase.getName());
                                    logger.debug("\t\t\t\t...we will thus reset the experimental evaluation of base '{}'",
                                            enrichedBase.getName());

                                    baseService.resetBaseExperimentalEvaluation(baseToChange);

                                } else if(remainingResults.size()<baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size()) {
                                    logger.debug("\t\t\t..only {} of {} experimental evaluation results of base '{}' remain",
                                            remainingResults.size(),
                                            baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size(),
                                            enrichedBase.getName());

                                    baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().setExperimentSpecificEvaluationSetups(remainingResults);

                                } else {
                                    logger.debug("\t\t\t..no experimental evaluation result needs deletion of base '{}'",
                                            enrichedBase.getName());
                                }
                            });

                            logger.debug("\t\t\t...update '{}' base", enrichedBase.getName());
                            biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);

                        }

                    } else {
                        logger.debug("\t\t...base '{}' needs no modification as no experimental evaluation exists",
                                enrichedBase.getName());
                    }



                } else {
                    logger.debug("\t\t...base '{}' has modelling progress '{}' and thus no feature analysis needed",
                            enrichedBase.getName(), enrichedBase.getModellingProgress());
                }


            });

            //finally, delete feature with id
            logger.debug("\t\t..finally, delete feature with id '{}'", featureToDeleteId);
            featureCRUDRepository.delete(featureToDelete);

            return Response.ok().build();
        }

        logger.debug("\t...did not find feature by id {} to delete", featureToDeleteId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @RequestMapping(value = "biometricCharacteristics", method = RequestMethod.GET)
    public List<BiometricCharacteristic> getBiometricCharacteristics() {
        logger.debug("...got request to get all lifecycle aware biometric characteristics");
        return biometricCharacteristicCRUDRepository.findAll().stream().filter(biometricCharacteristic -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(biometricCharacteristic);
                }
        ).map(biometricCharacteristic -> {
           if(biometricCharacteristic instanceof SoftBiometricCharacteristic) {
               logger.debug("\t...enrich soft biometric with id {}", biometricCharacteristic.getId());
               return biometricCharacteristicService.enrichSoftBiometric((SoftBiometricCharacteristic) biometricCharacteristic);
           }
           return biometricCharacteristic;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "biometricCharacteristics/{biometricCharacteristicToDeleteId}", method = RequestMethod.DELETE)
    public Response deleteBiometricCharacteristic(@PathVariable String biometricCharacteristicToDeleteId) {
        logger.debug("\t...request to delete biometric characteristic by id '{}'", biometricCharacteristicToDeleteId);

        if(biometricCharacteristicCRUDRepository.findById(biometricCharacteristicToDeleteId).isPresent()) {
            BiometricCharacteristic biometricCharacteristicToDelete =
                    biometricCharacteristicCRUDRepository.findById(biometricCharacteristicToDeleteId).get();

            logger.debug("\t\t...find any BASE where characteristic '{}' is included", biometricCharacteristicToDelete.getName());

            List<EnrichedBaseForExport> allBaseEditableByUser = getEnrichedBase();

            allBaseEditableByUser.forEach(enrichedBiometricAuthenticationSystemAndEvaluation -> {

                boolean removeBASE = false;
                boolean analyzeDatasetsForCharacteristic = false;
                final List<BiometricCharacteristic> characteristicsToRemoveFromBASE = new ArrayList<>();

                // first, check for characteristics
                enrichedBiometricAuthenticationSystemAndEvaluation.getBiometricCharacteristics().forEach(biometricCharacteristic -> {
                    if(biometricCharacteristic.getId().equals(biometricCharacteristicToDeleteId)) {
                        logger.debug("\t\t\t...base '{}' has characteristic to delete '{}' within their {} reported biometric characteristics",
                                enrichedBiometricAuthenticationSystemAndEvaluation.getName(),
                                biometricCharacteristicToDelete.getName(),
                                enrichedBiometricAuthenticationSystemAndEvaluation.getBiometricCharacteristics().size());
                        characteristicsToRemoveFromBASE.add(biometricCharacteristic);
                    }
                });

                if(enrichedBiometricAuthenticationSystemAndEvaluation.getBiometricCharacteristics().size() == characteristicsToRemoveFromBASE.size()) {
                    logger.debug("\t\t...Any of the {} characteristics of base '{}' will be removed, remove BASE and look at datasets",
                            enrichedBiometricAuthenticationSystemAndEvaluation.getBiometricCharacteristics().size(),
                            enrichedBiometricAuthenticationSystemAndEvaluation.getName());

                    removeBASE = true;
                    analyzeDatasetsForCharacteristic = true;
                } else if(characteristicsToRemoveFromBASE.size()>0) {

                    logger.debug("\t\t...{} of the {} characteristics of base '{}' will be removed, keep BASE but also look at the Datasets used",
                            characteristicsToRemoveFromBASE.size(),
                            enrichedBiometricAuthenticationSystemAndEvaluation.getBiometricCharacteristics().size(),
                            enrichedBiometricAuthenticationSystemAndEvaluation.getName());

                    characteristicsToRemoveFromBASE.forEach(biometricCharacteristic -> {
                        logger.debug("\t\t\t...delete characteristic '{}'", biometricCharacteristic.getName());
                        biometricCharacteristicCRUDRepository.delete(biometricCharacteristic);
                        logger.debug("\t\t\t...remove {} deleted characteristic from BASE '{}'", biometricCharacteristic.getName(),
                                enrichedBiometricAuthenticationSystemAndEvaluation.getName());
                        BiometricAuthenticationSystemAndEvaluation biometricAuthenticationSystemAndEvaluationToChange =
                                biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBiometricAuthenticationSystemAndEvaluation.getId()).get();
                        biometricAuthenticationSystemAndEvaluationToChange.getBiometricCharacteristicIds().remove(biometricCharacteristic);
                        logger.debug("\t\t\t\t...update BASE '{}'", enrichedBiometricAuthenticationSystemAndEvaluation.getName());
                        biometricAuthenticationSystemAndEvaluationRepository.save(biometricAuthenticationSystemAndEvaluationToChange);
                    });

                    // now we have to look at the datasets
                    analyzeDatasetsForCharacteristic = true;

                } else {
                    logger.debug("\t\t...No characteristics in BASE '{}' needs to be deleted",
                            enrichedBiometricAuthenticationSystemAndEvaluation.getName());
                }

                if(removeBASE) {
                    logger.debug("\t\t...removing BASE '{}'!", enrichedBiometricAuthenticationSystemAndEvaluation.getName());
                    biometricAuthenticationSystemAndEvaluationRepository.deleteById(enrichedBiometricAuthenticationSystemAndEvaluation.getId());
                }

                if(analyzeDatasetsForCharacteristic) {
                    logger.debug("\t\t...analyze any dataset where biometric characteristic '{}' is included",
                            biometricCharacteristicToDelete.getName());

                    List<EnrichedDataSet> allDatasetsEditableByUser = getDatasets();

                    allDatasetsEditableByUser.forEach(enrichedDataSet -> {
                        final List<EnrichedSampledBiometric> sampledBiometricsToRemoveFromDataset = new ArrayList<>();
                        final List<EnrichedSampledBiometric> sampledBiometricsToChangeInDataset = new ArrayList<>();
                        enrichedDataSet.getSampledBiometrics().forEach(enrichedSampledBiometric -> {
                            if(enrichedSampledBiometric.getSampledCharacteristic().getId().equals(biometricCharacteristicToDeleteId)) {
                                logger.debug("\t\t\t...a sampled biometric needs to be deleted from dataset '{}'",
                                        enrichedDataSet.getName());
                                sampledBiometricsToRemoveFromDataset.add(enrichedSampledBiometric);
                            } else if(enrichedSampledBiometric.getSupportCharacteristic()!=null
                                && enrichedSampledBiometric.getSupportCharacteristic().getId().equals(biometricCharacteristicToDeleteId)) {
                                logger.debug("\t\t\t...only a support characteristic from a sampled biometric needs to be deleted from dataset '{}', keep sampled biometric",
                                        enrichedDataSet.getName());
                                enrichedSampledBiometric.setSupportCharacteristic(null);
                                sampledBiometricsToChangeInDataset.add(enrichedSampledBiometric);
                            }
                        });

                        if(sampledBiometricsToRemoveFromDataset.size() == enrichedDataSet.getSampledBiometrics().size()) {
                            logger.debug("\t\t\t...all sampled biometrics need to removed from dataset '{}', remove dataset and reset BASE to target architecture modelling progress",
                                    enrichedDataSet.getName());
                            logger.debug("\t\t\t\t...delete dataset '{}'", enrichedDataSet.getName());
                            dataSetCRUDRepository.deleteById(enrichedDataSet.getId());
                            logger.debug("\t\t\t\t...remove BASE '{}'", enrichedBiometricAuthenticationSystemAndEvaluation.getName());
                            BiometricAuthenticationSystemAndEvaluation biometricAuthenticationSystemAndEvaluationToChange =
                                    biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBiometricAuthenticationSystemAndEvaluation.getId()).get();

                            biometricAuthenticationSystemAndEvaluationToChange.setBaseEvaluation(null);
                            biometricAuthenticationSystemAndEvaluationToChange.setEvaluationCriteriaGrants(null);
                            biometricAuthenticationSystemAndEvaluationToChange.setModellingProgress(ModellingProgress.TARGETARCHITECTURE);
                            biometricAuthenticationSystemAndEvaluationRepository.save(biometricAuthenticationSystemAndEvaluationToChange);
                        } else if(sampledBiometricsToRemoveFromDataset.size()>0) {
                            logger.debug("\t\t\t...only remove {} of {} sampled biometrics from dataset '{}', keep dataset",
                                    sampledBiometricsToRemoveFromDataset.size(),
                                    enrichedDataSet.getSampledBiometrics().size(),
                                    enrichedDataSet.getName());

                            DataSet dataSetToChange = dataSetCRUDRepository.findById(enrichedDataSet.getId()).get();
                            sampledBiometricsToRemoveFromDataset.forEach(enrichedSampledBiometric -> {
                                dataSetToChange.getSampledBiometrics().remove(enrichedSampledBiometric);
                            });
                            logger.debug("\t\t\t\t...updating dataset '{}'", dataSetToChange.getName());
                            dataSetCRUDRepository.save(dataSetToChange);
                        } else {
                            logger.debug("\t\t\t...none of {} sampled biometrics from dataset '{}' need removal check, whether we need to change some",
                                    enrichedDataSet.getSampledBiometrics().size(), enrichedDataSet.getName());
                            if(sampledBiometricsToChangeInDataset.size()>0) {
                                logger.debug("\t\t\t\t...{} of {} sampled biometrics from dataset '{}' need change",
                                        sampledBiometricsToChangeInDataset.size(), enrichedDataSet.getSampledBiometrics().size(), enrichedDataSet.getName());
                                DataSet dataSetToChange = dataSetCRUDRepository.findById(enrichedDataSet.getId()).get();
                                sampledBiometricsToChangeInDataset.forEach(enrichedSampledBiometric -> {
                                    dataSetToChange.getSampledBiometrics().stream()
                                            .filter(sampledBiometric -> sampledBiometric.getSupportCharacteristicId().equals(enrichedSampledBiometric.getSupportCharacteristic().getId()))
                                            .collect(Collectors.toList()).get(0).setSupportCharacteristicId(null);
                                });
                                logger.debug("\t\t\t\t...updating dataset '{}'", dataSetToChange.getName());
                                dataSetCRUDRepository.save(dataSetToChange);

                            } else {
                                logger.debug("\t\t\t\t...none of {} sampled biometrics from dataset '{}' need change",
                                        enrichedDataSet.getSampledBiometrics().size(), enrichedDataSet.getName());
                            }
                        }
                    });

                }

            });

            // finally, delete characteristic
            logger.debug("\t...finally, delete biometric characteristic '{}'", biometricCharacteristicToDelete.getName());
            biometricCharacteristicCRUDRepository.delete(biometricCharacteristicToDelete);
            return Response.ok().build();
        }
        logger.debug("\t...did not find any characteristic with id '{}' to delete", biometricCharacteristicToDeleteId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @RequestMapping(value = "resourceToProtects", method = RequestMethod.GET)
    public List<ResourceToProtect> getResourcesToProtect() {
        return resourceToProtectCRUDRepository.findAll().stream().filter(resourceToProtect -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(resourceToProtect);
                }
        ).collect(Collectors.toList());
    }

    @RequestMapping(value = "resourceToProtects/{resourcesToProtectIdToDelete}", method = RequestMethod.DELETE)
    //@Transactional
    public Response deleteResourcesToProtect(@PathVariable String resourcesToProtectIdToDelete) {
        logger.debug("\t...do lifecycle aware deletion of resources to protect with id '{}', i.e., removing it from any BASE where it was given as resource to protect", resourcesToProtectIdToDelete);

        /*
        we need to find any BASE where it was mentioned within the target architecture and remove it
         */

        if(resourceToProtectCRUDRepository.findById(resourcesToProtectIdToDelete).isPresent()) {
            ResourceToProtect resourcesToProtectToDelete = resourceToProtectCRUDRepository.findById(resourcesToProtectIdToDelete).get();
            List<EnrichedBaseForExport> baseEditableByUser = getEnrichedBase();
            baseEditableByUser.forEach(enrichedBaseEditableByUser -> {

                if(!enrichedBaseEditableByUser.getModellingProgress().equals(ModellingProgress.CHARACTERISTICS)) {
                    // target architecture has to be modelled
                    BiometricAuthenticationSystemAndEvaluation baseToChange =
                            biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBaseEditableByUser.getId()).get();

                    if(baseToChange.getTargetArchitecture().getResourceToProtectId().equals(resourcesToProtectIdToDelete)) {
                        logger.debug("\t\t...resources to protect '{}' were reported in BASE '{}', remove from there",
                                resourcesToProtectToDelete.getName(), baseToChange.getName());
                        baseToChange.getTargetArchitecture().setResourceToProtectId(null);
                        logger.debug("\t\t\t...set architecture to skipped");
                        baseToChange.getTargetArchitecture().setSkipTargetArchitecture(true);
                        biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);
                    }
                }

            });

            // delete resources to protect itself
            logger.debug("\t\t...deleting resources to protect '{}'", resourcesToProtectToDelete.getName());
            resourceToProtectCRUDRepository.delete(resourcesToProtectToDelete);
            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @RequestMapping(value = "resultMetrics", method = RequestMethod.GET)
    public List<EnrichedResultMetric> getResultMetrics() {
        return resultMetricCRUDRepository.findAll().stream().filter(resultMetric -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(resultMetric);
                }
        ).map(resultMetric -> {
            return resultMetricService.enrichResultMetric(resultMetric);
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "resultMetrics/{resultMetricToDeleteId}", method = RequestMethod.DELETE)
    public Response deleteResultMetricById(@PathVariable String resultMetricToDeleteId) {

        logger.debug("...got request to delete result metric id '{}'", resultMetricToDeleteId);

        if(resultMetricCRUDRepository.findById(resultMetricToDeleteId).isPresent()) {

            resultMetricService.deleteResultMetricById(resultMetricToDeleteId,
                    getExperimentSpecificEvaluationCriteria(),
                    getEnrichedBase());
            return Response.ok().build();
        }


        logger.debug("\t...did not find any result metric to delete by id '{}'", resultMetricToDeleteId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }



    @RequestMapping(value = "implementationSpecificEvaluationCriterias", method = RequestMethod.GET)
    public List<ImplementationSpecificEvaluationCriteria> getImplementationSpecificEvaluationCriteria() {
        return implementationSpecificEvaluationCriteriaCRUDRepository.findAll().stream().filter(implementationSpecificEvaluationCriteria -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(implementationSpecificEvaluationCriteria);
                }
        ).collect(Collectors.toList());
    }

    @RequestMapping(value = "implementationSpecificEvaluationCriterias/{implementationSpecificEvaluationCriteriaToDeleteId}", method = RequestMethod.DELETE)
    //@Transactional
    public Response deleteImplementationSpecificEvaluationCriteria(@PathVariable String implementationSpecificEvaluationCriteriaToDeleteId) {
        logger.debug("\t...got request to delete implementation specific evaluation result criteria by id '{}'", implementationSpecificEvaluationCriteriaToDeleteId);

        if(implementationSpecificEvaluationCriteriaCRUDRepository.findById(implementationSpecificEvaluationCriteriaToDeleteId).isPresent()) {

            /*
            we need to delete any evaluation result related to this criteria
                - if no implementation based implementation criteria remains, remove
                    - if no experimental evaluation criteria exists, reset modelling state to target architecture
             we need to find any evaluation criteria grant with this implementation based criteria and remove it
                - if no other grant remains, reset base's modelling state to evalatuion
             */

            ImplementationSpecificEvaluationCriteria implementationSpecificEvaluationCriteriaToDelete =
                    implementationSpecificEvaluationCriteriaCRUDRepository.findById(implementationSpecificEvaluationCriteriaToDeleteId).get();

            List<EnrichedBaseForExport> enrichedBaseEditableByUser
                    = getEnrichedBase();

            enrichedBaseEditableByUser.forEach(enrichedBase -> {
                if(enrichedBase.getModellingProgress().equals(ModellingProgress.EVALUATION_CRITERIA_GRANTS)
                        || enrichedBase.getModellingProgress().equals(ModellingProgress.REFERENCE)) {

                    if(enrichedBase.getBaseEvaluation().getImplementationSpecificEvaluationResults()!=null
                        && enrichedBase.getBaseEvaluation().getImplementationSpecificEvaluationResults().size()>0) {

                        logger.debug("\t...base '{}' has {} implementation based evaluation results, check for criteria to delete",
                                enrichedBase.getName(),
                                enrichedBase.getBaseEvaluation().getImplementationSpecificEvaluationResults().size());

                        List<ImplementationSpecificEvaluationResult> remainingResults = enrichedBase.getBaseEvaluation()
                                .getImplementationSpecificEvaluationResults()
                                .stream().filter(implementationSpecificEvaluationResult -> {
                                    return !implementationSpecificEvaluationResult.getCriteriaId().equals(implementationSpecificEvaluationCriteriaToDeleteId);
                                }).collect(Collectors.toList());

                        if(remainingResults.size()==0) {
                            logger.debug("\t\t...no impl-spec eval results remain for base '{}', reset", enrichedBase.getName());
                            BiometricAuthenticationSystemAndEvaluation baseToChange =
                                    biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBase.getId()).get();

                            baseService.resetBaseImplementationBasedEvaluation(baseToChange);

                        } else if (remainingResults.size()<enrichedBase.getBaseEvaluation().getImplementationSpecificEvaluationResults().size()) {

                            logger.debug("\t\t...only {} of {} implementation results remain of base '{}', change",
                                    remainingResults.size(),
                                    enrichedBase.getBaseEvaluation().getImplementationSpecificEvaluationResults().size(),
                                    enrichedBase.getName());

                            BiometricAuthenticationSystemAndEvaluation baseToChange =
                                    biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBase.getId()).get();

                            baseToChange.getBaseEvaluation().setImplementationSpecificEvaluationResults(remainingResults);
                            biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);

                        }  else {
                            logger.debug("\t...implementation based evaluation results of base '{}' need no change",
                                    enrichedBase.getName());
                        }

                    } else {
                        logger.debug("\t...base '{}' has no implementation based evaluation results, check for criteria to delete",
                                enrichedBase.getName());

                    }

                } else {
                    logger.debug("\t...base '{}' is not in the required modelling state for analysis",
                            enrichedBase.getName());
                }
            });

            // finally, delete implementation based criteria
            logger.debug("\t...finally, delete implementation based criteria by id '{}'", implementationSpecificEvaluationCriteriaToDeleteId);
            implementationSpecificEvaluationCriteriaCRUDRepository.delete(implementationSpecificEvaluationCriteriaToDelete);

            return Response.ok().build();
        }
        logger.debug("\t...did not find any implementation specific evaluation criteria by id '{}' to delete", implementationSpecificEvaluationCriteriaToDeleteId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @RequestMapping(value = "implementationSpecificEvaluationCriterias/search/findByCategory", method = RequestMethod.GET)
    public List<ImplementationSpecificEvaluationCriteria> getImplementationSpecificEvaluationCriteriaByCategory(@Param("category") String category) {
        return implementationSpecificEvaluationCriteriaCRUDRepository.findByCategory(category).stream().filter(implementationSpecificEvaluationCriteria -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(implementationSpecificEvaluationCriteria);
                }
        ).collect(Collectors.toList());
    }


    @RequestMapping(value = "experimentSpecificEvaluationCriterias", method = RequestMethod.GET)
    public List<EnrichedExperimentSpecificEvaluationCriteria> getExperimentSpecificEvaluationCriteria() {
        return experimentSpecificEvaluationCriteriaCRUDRepository.findAll().stream().filter(experimentSpecificEvaluationCriteria -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(experimentSpecificEvaluationCriteria);
                }
        ).collect(Collectors.toList())
                .stream()
                .map(experimentSpecificEvaluationCriteria ->
                        experimentSpecificEvaluationCriteriaService.enrichExperimentSpecificEvaluationCriteria(experimentSpecificEvaluationCriteria))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "experimentSpecificEvaluationCriterias/{experimentSpecificEvaluationCriteriaToDeleteId}", method = RequestMethod.DELETE)
    //@Transactional
    public Response deleteExperimentSpecificEvaluationCriteria(@PathVariable String experimentSpecificEvaluationCriteriaToDeleteId) {
        logger.debug("\t...got request to delete experiment- specific evaluation result criteria by id '{}'", experimentSpecificEvaluationCriteriaToDeleteId);

        if(experimentSpecificEvaluationCriteriaCRUDRepository.findById(experimentSpecificEvaluationCriteriaToDeleteId).isPresent()) {

            ExperimentSpecificEvaluationCriteria experimentSpecificEvaluationCriteriaToDelete =
                    experimentSpecificEvaluationCriteriaCRUDRepository.findById(experimentSpecificEvaluationCriteriaToDeleteId).get();

            /*
            we have to delete every result metric referring to that criteria
                - we have to delete every evaluation result referring to any of the criteria's metric
                    - we have to reset the experimental evaluation if no result remains
                        - we have to reset the evaluation and the modelling state of the base if no implementation-based eval remains
            we have to delete every evaluation criteria grant related to that criteria
                - if no evaluation criteria grant remains, we have to reset the modelling state of the base
            finally, we have to delete the criteria
             */

            // for all of the tasks, we rely on the delete result metric operation which should care for everything
            if(experimentSpecificEvaluationCriteriaToDelete.getResultMetricIds()!=null) {
                experimentSpecificEvaluationCriteriaToDelete.getResultMetricIds().forEach(resultMetricToDeleteId -> {
                    resultMetricService.deleteResultMetricById(resultMetricToDeleteId,
                            getExperimentSpecificEvaluationCriteria(),
                            getEnrichedBase());
                });
            }

            logger.debug("\t...finally, delete criteria with id {} and name {}",
                    experimentSpecificEvaluationCriteriaToDelete.getId(), experimentSpecificEvaluationCriteriaToDelete.getName());

            experimentSpecificEvaluationCriteriaCRUDRepository.delete(experimentSpecificEvaluationCriteriaToDelete);

            return Response.ok().build();
        }
        logger.debug("\t...did not find any experiment-specific evaluation criteria by id '{}' to delete", experimentSpecificEvaluationCriteriaToDeleteId);
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @RequestMapping(value = "experimentSpecificEvaluationCriterias/search/findByCategory", method = RequestMethod.GET)
    public List<EnrichedExperimentSpecificEvaluationCriteria> getExperimentSpecificEvaluationCriteriaByCategory(@Param("category") String category) {
        return experimentSpecificEvaluationCriteriaCRUDRepository.findByCategory(category).stream().filter(experimentSpecificEvaluationCriteria -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(experimentSpecificEvaluationCriteria);
                }
        ).collect(Collectors.toList())
                .stream()
                .map(experimentSpecificEvaluationCriteria ->
                        experimentSpecificEvaluationCriteriaService.enrichExperimentSpecificEvaluationCriteria(experimentSpecificEvaluationCriteria))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "experimentSpecificEvaluationCriterias/search/findAllByIdIn", method = RequestMethod.GET)
    public List<EnrichedExperimentSpecificEvaluationCriteria> getExperimentSpecificEvaluationCriteriaByIdIn(@Param("ids") String[] ids) {
        return experimentSpecificEvaluationCriteriaCRUDRepository.findAllByIdIn(ids).stream().filter(experimentSpecificEvaluationCriteria -> {
                    return !modelledElementService.filterOutModelledElementBasedOnLifecycleAndUser(experimentSpecificEvaluationCriteria);
                }
        ).collect(Collectors.toList())
                .stream()
                .map(experimentSpecificEvaluationCriteria ->
                        experimentSpecificEvaluationCriteriaService.enrichExperimentSpecificEvaluationCriteria(experimentSpecificEvaluationCriteria))
                .collect(Collectors.toList());
    }






}

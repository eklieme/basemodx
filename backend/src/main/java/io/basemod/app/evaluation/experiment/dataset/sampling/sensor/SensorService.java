package io.basemod.app.evaluation.experiment.dataset.sampling.sensor;

import io.basemod.app.base.EnrichedBaseForExport;
import io.basemod.app.base.EnrichedBiometricAuthenticationSystemAndEvaluation;
import io.basemod.app.base.lifecycle.LifecycleState;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryService;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.repository.ModelledElementService;
import io.basemod.app.security.authentication.domain.BaseUser;
import io.basemod.app.security.authentication.domain.BaseUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensorService {

    Logger logger = LoggerFactory.getLogger(SensorService.class);

    private SensorCRUDRepository sensorCRUDRepository;
    private SensorDimensionCRUDRepository sensorDimensionCRUDRepository;
    private DeviceCategoryCRUDRepository deviceCategoryCRUDRepository;

    private ModelledElementService modelledElementService;

    @Autowired
    public SensorService(SensorCRUDRepository sensorCRUDRepository, SensorDimensionCRUDRepository sensorDimensionCRUDRepository, DeviceCategoryCRUDRepository deviceCategoryCRUDRepository, ModelledElementService modelledElementService) {
        this.sensorCRUDRepository = sensorCRUDRepository;
        this.sensorDimensionCRUDRepository = sensorDimensionCRUDRepository;
        this.deviceCategoryCRUDRepository = deviceCategoryCRUDRepository;
        this.modelledElementService = modelledElementService;
    }

    public void deleteSensorBySensorId(String sensorIdToDelete,
                                       List<EnrichedBaseForExport> baseEditableByUser,
                                       List<EnrichedSampleDevice> allUserEditableSampleDevices,
                                       List<EnrichedDataSet> datasetsEditableByUser) {
        Sensor sensorToDelete = sensorCRUDRepository.findById(sensorIdToDelete).get();
        EnrichedSensor enrichedSensorToDelete = getEnrichedSensorFromSensor(sensorToDelete);

        // get dimensions
        enrichedSensorToDelete.getSensorDimensions().forEach(sensorDimension -> {
            logger.debug("\t\t..deleting dimension '{}' of sensor '{}'", sensorDimension.getName(), enrichedSensorToDelete.getName());
            sensorDimensionCRUDRepository.delete(sensorDimension);
        });

        // find all categories having the sensor assigned to
        List<DeviceCategory> allDeviceCategories = deviceCategoryCRUDRepository.findAll();
        allDeviceCategories.forEach(deviceCategory -> {

            List<String> remainingSensorIds = deviceCategory.getSensorIds()
                    .stream().filter(sensorId -> !sensorId.equals(sensorIdToDelete)).collect(Collectors.toList());

            if(remainingSensorIds.size()==0) {
                logger.debug("\t\t...no sensor remains for device category '{}', delete category",
                        deviceCategory.getName());

                modelledElementService.deleteDeviceCategoryById(deviceCategory.getId(),
                        baseEditableByUser,
                        allUserEditableSampleDevices,
                        datasetsEditableByUser);
            } else if(remainingSensorIds.size()<deviceCategory.getSensorIds().size()) {
                logger.debug("\t\t\t...{} of {} sensors remain device category '{}', check whether it still needs review",
                        remainingSensorIds.size(),
                        deviceCategory.getSensorIds().size(),
                        deviceCategory.getName());

                deviceCategory.setSensorIds(remainingSensorIds);

                List<String> sensorIdsNeedingReview = deviceCategory.getSensorIds().stream().filter(sensorId -> {
                    Sensor sensor = sensorCRUDRepository.findById(sensorId).get();
                    return sensor.getModelledElementDetail().getElementLifecycle().getLifecycleState().equals(LifecycleState.CREATED);
                }).collect(Collectors.toList());

                if(sensorIdsNeedingReview.size()==0) {
                    logger.debug("\t\t\t\t...no sensor needs review anymore, change lifecycle status of device category back to '{}'", LifecycleState.REVIEWED);
                    deviceCategory.getModelledElementDetail().getElementLifecycle().setLifecycleState(LifecycleState.REVIEWED);
                }
                logger.debug("\t\t\t...updating device category '{}'", deviceCategory.getName());
                deviceCategoryCRUDRepository.save(deviceCategory);
            } else {
                logger.debug("\t\t..device category '{}' needs no change", deviceCategory.getName());
            }


        });

        // finally, delete sensor
        logger.debug("\t\t..finally, deleting sensor '{}' itself", sensorToDelete.getName());
        sensorCRUDRepository.delete(sensorToDelete);
    }

    public EnrichedSensor getEnrichedSensorFromSensor(Sensor sensor) {

        logger.debug("...got request to enrich sensor '{}' with id '{}'",
                sensor.getName(), sensor.getId());
        EnrichedSensor enrichedSensor = new EnrichedSensor();
        enrichedSensor.setModelledElementDetail(sensor.getModelledElementDetail());
        enrichedSensor.setName(sensor.getName());
        enrichedSensor.setContinuous(sensor.isContinuous());
        enrichedSensor.setId(sensor.getId());
        enrichedSensor.setSensorDimensionIds(sensor.getSensorDimensionIds());
        enrichedSensor.setSensorDimensions(new ArrayList<>());
        sensor.getSensorDimensionIds().forEach(sensorDimensionId -> {
            logger.debug("\t...get full sensor dimension data for dimension with id '{}' for sensor '{}' with id '{}'",
                    sensorDimensionId, sensor.getName(), sensor.getId());
            enrichedSensor.addSensorDimension(sensorDimensionCRUDRepository.findById(sensorDimensionId).get());
        });

        return enrichedSensor;
    }

    public EnrichedSensor getEnrichedSensorFromSensorId(String sensorId) {

        Sensor sensorToEnrich = sensorCRUDRepository.findById(sensorId).get();

        return getEnrichedSensorFromSensor(sensorToEnrich);
    }

    public EnrichedSensor getEnrichedSensorFromSensorIdUserSpecific(String sensorId, BaseUser baseUser) {

        Sensor sensorToEnrich = getSensorByIdAndUser(sensorId, baseUser);

        return getEnrichedSensorFromSensor(sensorToEnrich);
    }

    public Sensor getSensorByIdAndUser(String sensorId, BaseUser baseUser) {

        logger.debug("...got request to get sensor by id '{}' by user '{}'", sensorId, baseUser.getUniqueId());

        // depending on the user context we might have to filter dimensions that are not yet reviewed
        Sensor sensorToReturn = sensorCRUDRepository.findById(sensorId).get();

        List<String> remainingSensorDimensionIds = sensorToReturn.getSensorDimensionIds().stream().filter(sensorDimensionId -> {
            SensorDimension sensorDimension = sensorDimensionCRUDRepository.findById(sensorDimensionId).get();
            if(sensorDimension.getModelledElementDetail().getElementLifecycle().getLifecycleState()==LifecycleState.CREATED) {
                logger.debug("\t...sensor dimension '{}' of sensor '{}' is not yet reviewed, check current user context",
                        sensorDimension.getName(), sensorToReturn.getName());
                if(sensorDimension.getModelledElementDetail().getModelledInitiallyBy().equals(baseUser.getUniqueId())) {
                    logger.debug("\t...creator '{}' of element is requesting sensor '{}', return also dimension '{}' as user is creator",
                            baseUser.getUniqueId(), sensorToReturn.getName(), sensorDimension.getName());
                    return true;
                } else if(baseUser.getBaseUserRoles().contains(BaseUserRole.ADMIN) || baseUser.getBaseUserRoles().contains(BaseUserRole.REVIEWER)) {
                    logger.debug("\t...current user '{}' is admin or reviewer and is requesting sensor '{}', return also sensor dimension '{}'",
                            baseUser.getUniqueId(), sensorToReturn.getName(), sensorDimension.getName());
                    return true;
                } else {
                    logger.debug("\t...'{}' is requesting sensor '{}', WHO IS NOT THE CREATOR, leave out dimension '{}'",
                            baseUser.getUniqueId(), sensorToReturn.getName(), sensorDimension.getName());
                    return false;
                }
            } else {
                logger.debug("\t...sensor dimension '{}' of sensor '{}' is already reviewed, return!",
                        sensorDimension.getName(), sensorToReturn.getName());
                return true;
            }
        }).collect(Collectors.toList());

        logger.debug("\t...overall {} of {} sensor dimensions remain from sensor after user-context based filtering with user '{}'",
                remainingSensorDimensionIds.size(), sensorToReturn.getSensorDimensionIds().size(), baseUser.getUniqueId());

        sensorToReturn.setSensorDimensionIds(remainingSensorDimensionIds);

        return sensorToReturn;
    }
}

package io.basemod.app.evaluation.experiment.dataset.sampling.device.category;

import io.basemod.app.base.*;
import io.basemod.app.base.lifecycle.LifecycleState;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceService;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.Sensor;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.SensorService;
import io.basemod.app.security.authentication.domain.BaseUser;
import io.basemod.app.security.authentication.domain.BaseUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceCategoryService {

    Logger logger = LoggerFactory.getLogger(DeviceCategoryService.class);

    DeviceCategoryCRUDRepository deviceCategoryCRUDRepository;
    BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;
    SampleDeviceService sampleDeviceService;

    SensorService sensorService;

    BaseService baseService;

    @Autowired
    public DeviceCategoryService(DeviceCategoryCRUDRepository deviceCategoryCRUDRepository, BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository, SampleDeviceService sampleDeviceService, SensorService sensorService, BaseService baseService) {
        this.deviceCategoryCRUDRepository = deviceCategoryCRUDRepository;
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
        this.sampleDeviceService = sampleDeviceService;
        this.sensorService = sensorService;
        this.baseService = baseService;
    }



    public DeviceCategory getDeviceCategoryByIdAndUserContext(String deviceCategoryId, BaseUser baseUser) {

        logger.debug("...got request to get device category by id '{}' by user '{}'", deviceCategoryId, baseUser.getUniqueId());

        // depending on the user context we have to maybe filter out not yet reviewed parts of the device category, e.g. sensors
        DeviceCategory deviceCategoryToReturn = deviceCategoryCRUDRepository.findById(deviceCategoryId).get();

        List<String> remainingSensorIds = deviceCategoryToReturn.getSensorIds().stream().filter(sensorId -> {
            Sensor sensor = sensorService.getSensorByIdAndUser(sensorId, baseUser);
            if(sensor.getModelledElementDetail().getElementLifecycle().getLifecycleState().equals(LifecycleState.CREATED)) {
                logger.debug("\t...sensor '{}' of device category '{}' is not yet reviewed, check current user context",
                        sensor.getName(), deviceCategoryToReturn.getName());
                if(sensor.getModelledElementDetail().getModelledInitiallyBy().equals(baseUser.getUniqueId())) {
                    logger.debug("\t...creator '{}' of element is requesting device category '{}', return also sensor '{}' as user is creator",
                            baseUser, deviceCategoryToReturn.getName(), sensor.getName());
                    return true;
                } else if(baseUser.getBaseUserRoles().contains(BaseUserRole.ADMIN) || baseUser.getBaseUserRoles().contains(BaseUserRole.REVIEWER)) {
                    logger.debug("\t...current user '{}' is admin or reviewer and is requesting device category '{}', return also sensor '{}'",
                            baseUser.getUniqueId(), deviceCategoryToReturn.getName(), sensor.getName());
                    return true;
                } else {
                    logger.debug("\t...'{}' is requesting device category '{}', WHO IS NOT THE CREATOR AND NOT A REVIEWER OR ADMIN, leave out sensor '{}'",
                            baseUser.getUniqueId(), deviceCategoryToReturn.getName(), sensor.getName());
                    return false;
                }
            } else {
                logger.debug("\t...sensor '{}' of device category '{}' is already reviewed, return!",
                        sensor.getName(), deviceCategoryToReturn.getName());
                return true;
            }
        }).collect(Collectors.toList());

        logger.debug("\t...overall {} of {} sensors remain from device category after user-context based filtering with user '{}'",
                remainingSensorIds.size(), deviceCategoryToReturn.getSensorIds().size(), baseUser);

        deviceCategoryToReturn.setSensorIds(remainingSensorIds);



        return deviceCategoryToReturn;

    }

    public EnrichedDeviceCategory getEnrichedDeviceCategoryByIdWithEnrichedSensors(String deviceCategoryId) {

        logger.debug("...got request to get enriched device category based on category id '{}'", deviceCategoryId);
        DeviceCategory deviceCategoryToEnrich = deviceCategoryCRUDRepository.findById(deviceCategoryId).get();

        EnrichedDeviceCategory enrichedDeviceCategory = new EnrichedDeviceCategory();

        enrichedDeviceCategory.setName(deviceCategoryToEnrich.getName());
        enrichedDeviceCategory.setId(deviceCategoryId);
        enrichedDeviceCategory.setModelledElementDetail(deviceCategoryToEnrich.getModelledElementDetail());
        enrichedDeviceCategory.setSensorIds(deviceCategoryToEnrich.getSensorIds());
        enrichedDeviceCategory.setSensors(deviceCategoryToEnrich.getSensorIds()
                .stream()
                .map(sensorId -> {
                    return sensorService.getEnrichedSensorFromSensorId(sensorId);
                }).collect(Collectors.toList()));

        return enrichedDeviceCategory;

    }

    public EnrichedDeviceCategory getDeviceCategoryByIdWithEnrichedSensorsUserSpecific(String deviceCategoryId, BaseUser baseUser) {

        DeviceCategory deviceCategoryToEnrich = getDeviceCategoryByIdAndUserContext(deviceCategoryId, baseUser);

        EnrichedDeviceCategory enrichedDeviceCategory = new EnrichedDeviceCategory();

        enrichedDeviceCategory.setName(deviceCategoryToEnrich.getName());
        enrichedDeviceCategory.setId(deviceCategoryId);
        enrichedDeviceCategory.setModelledElementDetail(deviceCategoryToEnrich.getModelledElementDetail());
        enrichedDeviceCategory.setSensorIds(deviceCategoryToEnrich.getSensorIds());
        enrichedDeviceCategory.setSensors(deviceCategoryToEnrich.getSensorIds()
                .stream()
                .map(sensorId -> {
                    return sensorService.getEnrichedSensorFromSensorIdUserSpecific(sensorId, baseUser);
                }).collect(Collectors.toList()));

        return enrichedDeviceCategory;

    }


}

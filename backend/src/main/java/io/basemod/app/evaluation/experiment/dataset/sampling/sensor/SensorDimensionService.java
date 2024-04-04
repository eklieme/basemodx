package io.basemod.app.evaluation.experiment.dataset.sampling.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SensorDimensionService {

    Logger logger = LoggerFactory.getLogger(SensorDimensionService.class);

    private SensorDimensionCRUDRepository sensorDimensionCRUDRepository;
    private SensorCRUDRepository sensorCRUDRepository;

    @Autowired
    public SensorDimensionService(SensorDimensionCRUDRepository sensorDimensionCRUDRepository, SensorCRUDRepository sensorCRUDRepository) {
        this.sensorDimensionCRUDRepository = sensorDimensionCRUDRepository;
        this.sensorCRUDRepository = sensorCRUDRepository;
    }

    public SensorDimension getSensorDimensionById(String sensorDimensionId) {

        SensorDimension sensorDimensionToReturn = sensorDimensionCRUDRepository.findById(sensorDimensionId).get();

        return sensorDimensionToReturn;

    }

    public EnrichedSensorDimension getEnrichedSensorDimensionBySensorDimensionId(String sensorDimensionToEnrichId) {

        logger.debug("...got request to enrich sensor dimension with id '{}'", sensorDimensionToEnrichId);
        SensorDimension sensorDimensionToEnrich = getSensorDimensionById(sensorDimensionToEnrichId);
        Sensor parentSensor = sensorCRUDRepository.findBySensorDimensionIdsContains(sensorDimensionToEnrichId);
        if(parentSensor!=null) {
            logger.debug("\t..found parent sensor '{}'", parentSensor.getName());
        } else {
            logger.debug("\t..no parent sensor for dimension '{}', set orphaned", sensorDimensionToEnrich.getName());
        }

        EnrichedSensorDimension enrichedSensorDimension = new EnrichedSensorDimension(parentSensor);
        enrichedSensorDimension.setId(sensorDimensionToEnrich.getId());
        enrichedSensorDimension.setName(sensorDimensionToEnrich.getName());
        enrichedSensorDimension.setModelledElementDetail(sensorDimensionToEnrich.getModelledElementDetail());

        return enrichedSensorDimension;

    }
}

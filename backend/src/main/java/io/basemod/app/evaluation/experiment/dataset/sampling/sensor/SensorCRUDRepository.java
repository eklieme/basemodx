package io.basemod.app.evaluation.experiment.dataset.sampling.sensor;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorCRUDRepository extends ModelledElementCrudRepository<Sensor, String>, MongoRepository<Sensor, String> {

    public Sensor findBySensorDimensionIdsContains(String sensorDimensionId);
    public Sensor findFirstByNameEqualsIgnoreCase(String name);
}

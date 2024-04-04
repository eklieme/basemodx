package io.basemod.app.evaluation.experiment.dataset.sampling.sensor;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SensorDimensionCRUDRepository extends MongoRepository<SensorDimension, String> {

    public List<SensorDimension> findAllByName(String name);
}

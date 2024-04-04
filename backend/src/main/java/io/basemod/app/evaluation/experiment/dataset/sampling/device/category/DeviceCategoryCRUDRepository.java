package io.basemod.app.evaluation.experiment.dataset.sampling.device.category;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceCategoryCRUDRepository extends MongoRepository<DeviceCategory, String>, ModelledElementCrudRepository<DeviceCategory, String> {

    DeviceCategory findFirstByNameEqualsIgnoreCase(String name);

}

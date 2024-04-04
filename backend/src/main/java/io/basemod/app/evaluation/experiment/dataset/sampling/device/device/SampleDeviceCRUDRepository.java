package io.basemod.app.evaluation.experiment.dataset.sampling.device.device;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SampleDeviceCRUDRepository extends MongoRepository<SampleDevice, String>, ModelledElementCrudRepository<SampleDevice, String> {

    List<SampleDevice> findAllByDeviceCategoryId(String deviceCategoryId);


    SampleDevice findByNameAndDeviceManufacturer(String name, String deviceManufacturer);
}

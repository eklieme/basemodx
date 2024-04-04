package io.basemod.app.evaluation.experiment.biometricsystem.feature;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureCRUDRepository extends MongoRepository<Feature, String>,
        ModelledElementCrudRepository<Feature, String> {

    Feature findFirstByName(String name);
}

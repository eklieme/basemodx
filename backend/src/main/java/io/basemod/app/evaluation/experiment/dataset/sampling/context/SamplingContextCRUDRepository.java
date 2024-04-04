package io.basemod.app.evaluation.experiment.dataset.sampling.context;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SamplingContextCRUDRepository extends MongoRepository<SamplingContext, String>,
        ModelledElementCrudRepository<SamplingContext, String> {
    SamplingContext findFirstByDescriptionEqualsIgnoreCase(String description);
}

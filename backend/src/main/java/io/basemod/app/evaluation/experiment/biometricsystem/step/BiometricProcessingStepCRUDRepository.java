package io.basemod.app.evaluation.experiment.biometricsystem.step;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BiometricProcessingStepCRUDRepository extends MongoRepository<BiometricSystemProcessingStep, String>,
        ModelledElementCrudRepository<BiometricSystemProcessingStep, String> {

    BiometricSystemProcessingStep findFirstByNameAndProcessingType(String name, String processingType);
}

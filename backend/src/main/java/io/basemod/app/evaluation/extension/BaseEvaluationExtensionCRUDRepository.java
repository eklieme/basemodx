package io.basemod.app.evaluation.extension;

import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;
import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BaseEvaluationExtensionCRUDRepository extends MongoRepository<BaseEvaluationExtension, String>,
        ModelledElementCrudRepository<BaseEvaluationExtension, String> {


}

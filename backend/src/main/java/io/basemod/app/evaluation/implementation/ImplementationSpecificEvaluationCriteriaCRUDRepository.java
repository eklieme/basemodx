package io.basemod.app.evaluation.implementation;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImplementationSpecificEvaluationCriteriaCRUDRepository extends MongoRepository<ImplementationSpecificEvaluationCriteria, String>,
        ModelledElementCrudRepository<ImplementationSpecificEvaluationCriteria, String> {

    public List<ImplementationSpecificEvaluationCriteria> findByCategory(@Param("category") String category);
    List<ImplementationSpecificEvaluationCriteria> findAllByIdIn(@Param("ids") String[] ids);
    ImplementationSpecificEvaluationCriteria findByNameAndCategory(String name, String category);

}

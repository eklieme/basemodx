package io.basemod.app.evaluation.experiment.evaluation.criteria;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExperimentSpecificEvaluationCriteriaCRUDRepository extends MongoRepository<ExperimentSpecificEvaluationCriteria, String>,
        ModelledElementCrudRepository<ExperimentSpecificEvaluationCriteria, String> {

    public List<ExperimentSpecificEvaluationCriteria> findByCategory(@Param("category") String category);
    List<ExperimentSpecificEvaluationCriteria> findAllByIdIn(@Param("ids") String[] ids);
    ExperimentSpecificEvaluationCriteria findByNameAndCategory(String name, String category);

}

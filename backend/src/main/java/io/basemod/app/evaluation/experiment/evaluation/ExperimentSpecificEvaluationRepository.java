package io.basemod.app.evaluation.experiment.evaluation;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExperimentSpecificEvaluationRepository extends MongoRepository<ExperimentSpecificEvaluation, String> {


}

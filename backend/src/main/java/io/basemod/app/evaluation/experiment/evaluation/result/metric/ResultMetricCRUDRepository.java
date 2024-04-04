package io.basemod.app.evaluation.experiment.evaluation.result.metric;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResultMetricCRUDRepository extends MongoRepository<ResultMetric, String>,
        ModelledElementCrudRepository<ResultMetric, String> {

    ResultMetric findFirstByNameAndUnit(String name, String unit);

}

package io.basemod.app.evaluation.experiment.dataset;

import io.basemod.app.domain.NamedDomainSummary;
import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DataSetCRUDRepository extends MongoRepository<DataSet,String>, ModelledElementCrudRepository<DataSet, String> {

    public List<DataSet> findAllByIdIn(@Param("ids") List<String> ids);
    public DataSet findByName(String name);


}

package io.basemod.app.base;

import io.basemod.app.domain.NamedDomainSummary;
import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface BiometricAuthenticationSystemAndEvaluationRepository extends MongoRepository<BiometricAuthenticationSystemAndEvaluation, String>,
        ModelledElementCrudRepository<BiometricAuthenticationSystemAndEvaluation, String> {

    public List<BiometricAuthenticationSystemAndEvaluation> findByModellingProgress(@Param("modellingProgress") String modellingProgress);
    public BiometricAuthenticationSystemAndEvaluation findByNameEqualsIgnoreCase(@Param("name") String name);
    public List<NamedDomainSummary> findBy();

}

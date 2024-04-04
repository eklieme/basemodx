package io.basemod.app.evaluation.experiment.biometricsystem;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BiometricSystemCRUDRepository extends MongoRepository<BiometricSystem, String> {

    public BiometricSystem findByNameAndParentBaseName(String name, String parentBaseName);

    public List<BiometricSystem> findAllByParentBaseName(String parentBaseName);

    public List<BiometricSystem> findAllByIdIn(@Param("ids") List<String> ids);

}

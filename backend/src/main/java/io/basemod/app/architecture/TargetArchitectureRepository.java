package io.basemod.app.architecture;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TargetArchitectureRepository extends MongoRepository<TargetArchitecture, String> {

}

package io.basemod.app.architecture;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResourceToProtectCRUDRepository extends MongoRepository<ResourceToProtect, String>,
        ModelledElementCrudRepository<ResourceToProtect, String> {

    ResourceToProtect findFirstByName(String name);

}

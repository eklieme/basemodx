package io.basemod.app.characteristic;

import io.basemod.app.repository.review.ModelledElementCrudRepository;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface BiometricCharacteristicCRUDRepository extends MongoRepository<BiometricCharacteristic, String>, ModelledElementCrudRepository<BiometricCharacteristic, String> {

    BiometricCharacteristic findFirstByUniqueIdentifier(String uniqueIdentifier);
}

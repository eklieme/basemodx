package io.basemod.app.characteristic;

import io.basemod.app.repository.review.ModelledElementReviewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BiometricCharacteristicService {

    private BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository;

    private Logger logger = LoggerFactory.getLogger(BiometricCharacteristicService.class);

    @Autowired
    public BiometricCharacteristicService(BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository) {
        this.biometricCharacteristicCRUDRepository = biometricCharacteristicCRUDRepository;
    }

    public EnrichedSoftBiometricCharacteristic enrichSoftBiometric(SoftBiometricCharacteristic biometricCharacteristicToEnrich) {

        logger.debug("... got request to enrich soft biometric characteristic {}", biometricCharacteristicToEnrich.getName());
        EnrichedSoftBiometricCharacteristic enrichedSoftBiometricCharacteristic = new EnrichedSoftBiometricCharacteristic();
        enrichedSoftBiometricCharacteristic.setName(biometricCharacteristicToEnrich.getName());
        enrichedSoftBiometricCharacteristic.setUniqueIdentifier(biometricCharacteristicToEnrich.getUniqueIdentifier());
        enrichedSoftBiometricCharacteristic.setId(biometricCharacteristicToEnrich.getId());
        enrichedSoftBiometricCharacteristic.setModelledElementDetail(biometricCharacteristicToEnrich.getModelledElementDetail());
        enrichedSoftBiometricCharacteristic.setSourceBiometricCharacteristicId(biometricCharacteristicToEnrich.getSourceBiometricCharacteristicId());
        if(enrichedSoftBiometricCharacteristic.getSourceBiometricCharacteristicId()!=null) {
            logger.debug("... soft biometric characteristic {} has biometric set it was inferred from (id: {})", biometricCharacteristicToEnrich.getName(),
                    enrichedSoftBiometricCharacteristic.getSourceBiometricCharacteristicId());
            enrichedSoftBiometricCharacteristic.setSourceBiometricCharacteristic(
                    biometricCharacteristicCRUDRepository.findById(biometricCharacteristicToEnrich.getSourceBiometricCharacteristicId()).get()
            );
        }

        return enrichedSoftBiometricCharacteristic;
    }
}

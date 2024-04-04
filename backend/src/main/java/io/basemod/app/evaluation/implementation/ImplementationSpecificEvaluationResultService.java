package io.basemod.app.evaluation.implementation;

import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryCRUDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ImplementationSpecificEvaluationResultService {

    private ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository;
    private DeviceCategoryCRUDRepository deviceCategoryCRUDRepository;

    @Autowired
    public ImplementationSpecificEvaluationResultService(ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository, DeviceCategoryCRUDRepository deviceCategoryCRUDRepository) {
        this.implementationSpecificEvaluationCriteriaCRUDRepository = implementationSpecificEvaluationCriteriaCRUDRepository;
        this.deviceCategoryCRUDRepository = deviceCategoryCRUDRepository;
    }

    public EnrichedImplementationSpecificEvaluationResult enrichedImplementationSpecificEvaluationResult(
            ImplementationSpecificEvaluationResult implementationSpecificEvaluationResultToEnrich) {

        EnrichedImplementationSpecificEvaluationResult enrichedResult
                = new EnrichedImplementationSpecificEvaluationResult();

        enrichedResult.setDescription(implementationSpecificEvaluationResultToEnrich.getDescription());
        enrichedResult.setCriteriaId(implementationSpecificEvaluationResultToEnrich.getCriteriaId());
        enrichedResult.setAffectedDeviceCategoryIds(implementationSpecificEvaluationResultToEnrich.getAffectedDeviceCategoryIds());

        enrichedResult.setCriteria(implementationSpecificEvaluationCriteriaCRUDRepository.findById(
                implementationSpecificEvaluationResultToEnrich.getCriteriaId()).get());

        implementationSpecificEvaluationResultToEnrich.getAffectedDeviceCategoryIds().forEach(deviceCategoryId -> {
            enrichedResult.addDeviceCategory(
                    deviceCategoryCRUDRepository.findById(deviceCategoryId).get());
        });

        return enrichedResult;
    }
}

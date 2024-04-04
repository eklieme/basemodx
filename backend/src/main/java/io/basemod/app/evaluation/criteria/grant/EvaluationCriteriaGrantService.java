package io.basemod.app.evaluation.criteria.grant;

import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaService;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteriaCRUDRepository;
import io.basemod.app.repository.ModelledElementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluationCriteriaGrantService {

    Logger logger = LoggerFactory.getLogger(EvaluationCriteriaGrantService.class);

    private ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository;

    private ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository;

    private ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService;

    @Autowired
    public EvaluationCriteriaGrantService(ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository, ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository, ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService) {
        this.experimentSpecificEvaluationCriteriaCRUDRepository = experimentSpecificEvaluationCriteriaCRUDRepository;
        this.implementationSpecificEvaluationCriteriaCRUDRepository = implementationSpecificEvaluationCriteriaCRUDRepository;
        this.experimentSpecificEvaluationCriteriaService = experimentSpecificEvaluationCriteriaService;
    }

    public EnrichedEvaluationCriteriaGrant enrichEvaluationCriteriaGrant(EvaluationCriteriaGrant evaluationCriteriaGrantToEnrich) {

        logger.debug("\t...got request to enrich evaluation criteria grant");
        EnrichedEvaluationCriteriaGrant enrichedEvaluationCriteriaGrant =
                new EnrichedEvaluationCriteriaGrant();

        enrichedEvaluationCriteriaGrant
                .setGrantingLevel(evaluationCriteriaGrantToEnrich.getGrantingLevel());

        enrichedEvaluationCriteriaGrant
                .setId(evaluationCriteriaGrantToEnrich.getId());

        enrichedEvaluationCriteriaGrant
                .setModelledElementDetail(evaluationCriteriaGrantToEnrich.getModelledElementDetail());

        enrichedEvaluationCriteriaGrant
                .setImplementationSpecificEvaluationCriteriaId(evaluationCriteriaGrantToEnrich.getImplementationSpecificEvaluationCriteriaId());

        enrichedEvaluationCriteriaGrant
                .setExperimentSpecificEvaluationCriteriaId(evaluationCriteriaGrantToEnrich.getExperimentSpecificEvaluationCriteriaId());

        if(enrichedEvaluationCriteriaGrant.getImplementationSpecificEvaluationCriteriaId()!=null) {
            logger.debug("...get implementation based criteria for enriched eval crit grant based on id '{}'",
                    enrichedEvaluationCriteriaGrant.getImplementationSpecificEvaluationCriteriaId());

            enrichedEvaluationCriteriaGrant.setEvaluationCriteria(
                    implementationSpecificEvaluationCriteriaCRUDRepository.findById(
                            enrichedEvaluationCriteriaGrant.getImplementationSpecificEvaluationCriteriaId()
                    ).get()
            );
        }

        if(enrichedEvaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId()!=null) {
            logger.debug("...get experiment based criteria for enriched eval crit grant based on id '{}'",
                    enrichedEvaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId());

            enrichedEvaluationCriteriaGrant.setEvaluationCriteria(
                    experimentSpecificEvaluationCriteriaService.enrichExperimentSpecificEvaluationCriteria(
                            experimentSpecificEvaluationCriteriaCRUDRepository.findById(
                                enrichedEvaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId()
                            ).get()
                    )
            );

        }

        return enrichedEvaluationCriteriaGrant;
    }
}

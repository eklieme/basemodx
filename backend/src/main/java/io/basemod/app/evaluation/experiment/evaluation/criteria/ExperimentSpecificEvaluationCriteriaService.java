package io.basemod.app.evaluation.experiment.evaluation.criteria;

import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetricCRUDRepository;
import io.basemod.app.repository.ModelledElementLifecycleAwareRestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ExperimentSpecificEvaluationCriteriaService {

    Logger logger = LoggerFactory.getLogger(ExperimentSpecificEvaluationCriteriaService.class);

    private ResultMetricCRUDRepository resultMetricCRUDRepository;

    @Autowired
    public ExperimentSpecificEvaluationCriteriaService(ResultMetricCRUDRepository resultMetricCRUDRepository) {
        this.resultMetricCRUDRepository = resultMetricCRUDRepository;
    }

    public EnrichedExperimentSpecificEvaluationCriteria enrichExperimentSpecificEvaluationCriteria(ExperimentSpecificEvaluationCriteria experimentSpecificEvaluationCriteriaToEnrich) {

        logger.debug("\t...enrich experiment specific evaluation criteria '{}'", experimentSpecificEvaluationCriteriaToEnrich.getName());
        EnrichedExperimentSpecificEvaluationCriteria enrichedExpEvalCrit = new EnrichedExperimentSpecificEvaluationCriteria();

        enrichedExpEvalCrit.setName(experimentSpecificEvaluationCriteriaToEnrich.getName());
        enrichedExpEvalCrit.setDescription(experimentSpecificEvaluationCriteriaToEnrich.getDescription());
        enrichedExpEvalCrit.setCategory(experimentSpecificEvaluationCriteriaToEnrich.getCategory());
        enrichedExpEvalCrit.setGrantedRule(experimentSpecificEvaluationCriteriaToEnrich.getGrantedRule());
        enrichedExpEvalCrit.setNotGrantedRule(experimentSpecificEvaluationCriteriaToEnrich.getNotGrantedRule());
        enrichedExpEvalCrit.setQuasiGrantedRule(experimentSpecificEvaluationCriteriaToEnrich.getQuasiGrantedRule());
        enrichedExpEvalCrit.setModelledElementDetail(experimentSpecificEvaluationCriteriaToEnrich.getModelledElementDetail());
        enrichedExpEvalCrit.setReference(experimentSpecificEvaluationCriteriaToEnrich.getReference());
        enrichedExpEvalCrit.setId(experimentSpecificEvaluationCriteriaToEnrich.getId());
        enrichedExpEvalCrit.setResultMetricIds(experimentSpecificEvaluationCriteriaToEnrich.getResultMetricIds());

        if(enrichedExpEvalCrit.getResultMetricIds()!=null && enrichedExpEvalCrit.getResultMetricIds().size()>0) {
            enrichedExpEvalCrit.setResultMetrics(enrichedExpEvalCrit.getResultMetricIds()
                    .stream()
                    .filter(resultMetricId -> {
                        boolean existsById = resultMetricCRUDRepository.existsById(resultMetricId);
                        if(existsById) {
                            return true;
                        } else {
                            logger.debug("\t\t...result metric with id '{}' does not exist anymore but is still connected to criteria with " +
                                    "name '{}' (id '{}')", resultMetricId, enrichedExpEvalCrit.getName(), enrichedExpEvalCrit.getId());
                            return false;
                        }
                    })
                    .map(resultMetricId -> resultMetricCRUDRepository.findById(resultMetricId).get()).collect(Collectors.toList()));
        }


        return enrichedExpEvalCrit;

    }

    public ExperimentSpecificEvaluationCriteria strapDownEnrichedCriteria(EnrichedExperimentSpecificEvaluationCriteria enrichedExperimentSpecificEvaluationCriteria) {
        ExperimentSpecificEvaluationCriteria experimentSpecificEvaluationCriteria = new ExperimentSpecificEvaluationCriteria();

        experimentSpecificEvaluationCriteria.setId(enrichedExperimentSpecificEvaluationCriteria.getId());
        experimentSpecificEvaluationCriteria.setName(enrichedExperimentSpecificEvaluationCriteria.getName());
        experimentSpecificEvaluationCriteria.setResultMetricIds(enrichedExperimentSpecificEvaluationCriteria.getResultMetricIds());
        experimentSpecificEvaluationCriteria.setCategory(enrichedExperimentSpecificEvaluationCriteria.getCategory());
        experimentSpecificEvaluationCriteria.setDescription(enrichedExperimentSpecificEvaluationCriteria.getDescription());
        experimentSpecificEvaluationCriteria.setGrantedRule(enrichedExperimentSpecificEvaluationCriteria.getGrantedRule());
        experimentSpecificEvaluationCriteria.setQuasiGrantedRule(enrichedExperimentSpecificEvaluationCriteria.getQuasiGrantedRule());
        experimentSpecificEvaluationCriteria.setNotGrantedRule(enrichedExperimentSpecificEvaluationCriteria.getNotGrantedRule());
        experimentSpecificEvaluationCriteria.setModelledElementDetail(enrichedExperimentSpecificEvaluationCriteria.getModelledElementDetail());
        experimentSpecificEvaluationCriteria.setReference(enrichedExperimentSpecificEvaluationCriteria.getReference());
        return experimentSpecificEvaluationCriteria;

    }
}

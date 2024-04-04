package io.basemod.app.evaluation.experiment.evaluation.result.metric;

import io.basemod.app.base.*;
import io.basemod.app.evaluation.criteria.grant.EvaluationCriteriaGrant;
import io.basemod.app.evaluation.experiment.evaluation.criteria.EnrichedExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.result.SpecificResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultMetricService {

    Logger logger = LoggerFactory.getLogger(ResultMetricService.class);

    private ResultMetricCRUDRepository resultMetricCRUDRepository;
    private ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository;
    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;

    private BaseService baseService;

    @Autowired
    public ResultMetricService(ResultMetricCRUDRepository resultMetricCRUDRepository, ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository, BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository, BaseService baseService) {
        this.resultMetricCRUDRepository = resultMetricCRUDRepository;
        this.experimentSpecificEvaluationCriteriaCRUDRepository = experimentSpecificEvaluationCriteriaCRUDRepository;
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
        this.baseService = baseService;
    }

    public void deleteResultMetricById(String resultMetricToDeleteId,
                                        List<EnrichedExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteriaEditableByUser,
                                        List<EnrichedBaseForExport> enrichedBaseEditableByUser) {

        ResultMetric resultMetricToDelete = resultMetricCRUDRepository.findById(resultMetricToDeleteId).get();

            /* before deleting the result metric, we need to find:
             - any experimental criteria that is using the result metric
                - if the metric is the only result metric, the experimental criteria will be deleted as well
                    - any evaluation criteria grant will be deleted related to criteria
                        - if no grant remains, modelling state of base will be reset
             - in any case, any experimental evaluation result using the metric will be removed
                - if it is the only result, the experimental results will be reset
                    - if no implementation-based results exists, the base evaluation will be reset and the modelling progress
             */

        logger.debug("\t...check {} experimental evaluation criteria whether result metric is included",
                experimentSpecificEvaluationCriteriaEditableByUser.size());
        experimentSpecificEvaluationCriteriaEditableByUser.forEach(experimentSpecificEvaluationCriteria -> {

            List<String> resultMetricIdsToKeep = experimentSpecificEvaluationCriteria.getResultMetricIds()
                    .stream()
                    .filter(resultMetricId -> !resultMetricId.equals(resultMetricToDeleteId))
                    .collect(Collectors.toList());

            logger.debug("\t...{} of {} result metrics will remain from criteria '{}'",
                    resultMetricIdsToKeep.size(),
                    experimentSpecificEvaluationCriteria.getResultMetricIds().size(),
                    experimentSpecificEvaluationCriteria.getName());

            if(resultMetricIdsToKeep.size() == 0) {
                logger.debug("\t\t...no result metric remains for experiment criteria '{}', remove criteria", experimentSpecificEvaluationCriteria.getName());
                experimentSpecificEvaluationCriteriaCRUDRepository.delete(experimentSpecificEvaluationCriteria);

                // check which eval crit grants exist
                logger.debug("\t\t\t...check whether any grants were given based on the criteria");

                enrichedBaseEditableByUser.forEach(enrichedBase -> {
                    if(enrichedBase.getModellingProgress().equals(ModellingProgress.EVALUATION_CRITERIA_GRANTS)
                            || enrichedBase.getModellingProgress().equals(ModellingProgress.REFERENCE)) {
                        List<EvaluationCriteriaGrant> remainingGrants =
                                enrichedBase.getEvaluationCriteriaGrants().stream().filter(evaluationCriteriaGrant -> {
                                    return !evaluationCriteriaGrant.getExperimentSpecificEvaluationCriteriaId().equals(experimentSpecificEvaluationCriteria.getId());
                                }).collect(Collectors.toList());


                        if(remainingGrants.size()==0) {
                            logger.debug("\t\t\t\t...no evaluation criteria grant remains for base '{}', reset modelling state",
                                    enrichedBase.getName());
                            BiometricAuthenticationSystemAndEvaluation baseToChange =
                                    biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBase.getId()).get();

                            baseToChange.setEvaluationCriteriaGrants(null);
                            baseToChange.setModellingProgress(ModellingProgress.EVALUATION);
                            biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);

                        } else if(remainingGrants.size()<enrichedBase.getEvaluationCriteriaGrants().size()) {

                            logger.debug("\t\t\t\t...{} of {} evaluation criteria grants of base '{}' remain, change",
                                    remainingGrants.size(),
                                    enrichedBase.getEvaluationCriteriaGrants().size(),
                                    enrichedBase.getName());

                            BiometricAuthenticationSystemAndEvaluation baseToChange =
                                    biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBase.getId()).get();

                            baseToChange.setEvaluationCriteriaGrants(remainingGrants);
                            biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);
                        } else {
                            logger.debug("\t\t\t\t...evaluation criteria grants of base '{}' need no change",
                                    enrichedBase.getName());
                        }
                    } else {
                        logger.debug("\t\t\t\t..base '{}' editable by user is not in modelling progress to possibly delete grants",
                                enrichedBase.getName());
                    }
                });

            } else if(resultMetricIdsToKeep.size()<experimentSpecificEvaluationCriteria.getResultMetricIds().size()) {
                logger.debug("\t\t...only {} of {} result metrics of experiment criteria '{}' will remain, modify criteria",
                        resultMetricIdsToKeep.size(), experimentSpecificEvaluationCriteria.getResultMetricIds().size(),
                        experimentSpecificEvaluationCriteria.getName());

                experimentSpecificEvaluationCriteria.setResultMetricIds(resultMetricIdsToKeep);
                logger.debug("\t\t\t..update experiment specific criteria '{}' in DB", experimentSpecificEvaluationCriteria.getName());
                experimentSpecificEvaluationCriteriaCRUDRepository.save(experimentSpecificEvaluationCriteria);
            } else {
                logger.debug("\t\t...experiment criteria '{}' needs no change", experimentSpecificEvaluationCriteria.getName());
            }
        });

        // check for any evaluation result related to metric in base editable by user
        logger.debug("\t...check {} base editable by user whether result metric is included in eval results",
                enrichedBaseEditableByUser.size());

        enrichedBaseEditableByUser.forEach(enrichedBase -> {
            if(enrichedBase.getModellingProgress().equals(ModellingProgress.EVALUATION)
                    || enrichedBase.getModellingProgress().equals(ModellingProgress.EVALUATION_CRITERIA_GRANTS)
                    || enrichedBase.getModellingProgress().equals(ModellingProgress.REFERENCE)) {

                if(enrichedBase.getBaseEvaluation().getExperimentSpecificEvaluation()!=null
                        && enrichedBase.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups()!=null
                        && enrichedBase.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size()>0) {


                    BiometricAuthenticationSystemAndEvaluation baseToChange =
                            biometricAuthenticationSystemAndEvaluationRepository.findById(enrichedBase.getId()).get();

                    baseToChange.getBaseEvaluation()
                            .getExperimentSpecificEvaluation()
                            .getExperimentSpecificEvaluationSetups().forEach(experimentSpecificEvaluationSetup -> {

                                List<SpecificResult> remainingSpecificResults = experimentSpecificEvaluationSetup.getSpecificResults().stream().filter(specificResult -> {
                                    return !specificResult.getResultMetricId().equals(resultMetricToDeleteId);
                                }).collect(Collectors.toList());

                                if(remainingSpecificResults.size()==0) {
                                    logger.debug("\t\t...no specific results remain from experimental evaluation in base '{}', reset experimental evaluation",
                                            baseToChange.getName());

                                    baseService.resetBaseExperimentalEvaluation(baseToChange);

                                } else if(remainingSpecificResults.size()<experimentSpecificEvaluationSetup.getSpecificResults().size()) {
                                    logger.debug("\t\t...only {} of {} specific results remain from experimental evaluation in base '{}', reset modelling state",
                                            remainingSpecificResults.size(),
                                            experimentSpecificEvaluationSetup.getSpecificResults().size(),
                                            baseToChange.getName());

                                    baseToChange.getBaseEvaluation().setExperimentSpecificEvaluation(null);
                                    biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);
                                } else {
                                    logger.debug("\t\t...experimental results in base '{}' need no change",
                                            baseToChange.getName());
                                }


                            });

                } else {
                    logger.debug("\t\t...base '{}' is has no experimental evaluation results",
                            enrichedBase.getName());
                }
            } else {
                logger.debug("\t\t...base '{}' is not in respective modelling state for result metrics",
                        enrichedBase.getName());
            }
        });

        //finally delete result metric
        logger.debug("\t...finally, delete result metric with id '{}' itself", resultMetricToDeleteId);
        resultMetricCRUDRepository.delete(resultMetricToDelete);
    }

    public EnrichedResultMetric enrichResultMetric(ResultMetric resultMetricToEnrich) {

        EnrichedResultMetric enrichedResultMetric = new EnrichedResultMetric();

        logger.debug("...request to enrich result metric {}", resultMetricToEnrich.getName());
        logger.debug("\t...parent evaluation criteria id: {}", resultMetricToEnrich.getParentEvaluationCriteriaId());
        enrichedResultMetric.setId(resultMetricToEnrich.getId());
        enrichedResultMetric.setName(resultMetricToEnrich.getName());
        enrichedResultMetric.setDescription(resultMetricToEnrich.getDescription());
        enrichedResultMetric.setUnit(resultMetricToEnrich.getUnit());
        enrichedResultMetric.setParentEvaluationCriteriaId(resultMetricToEnrich.getParentEvaluationCriteriaId());
        enrichedResultMetric.setModelledElementDetail(resultMetricToEnrich.getModelledElementDetail());

        enrichedResultMetric.setParentCriteria(
                experimentSpecificEvaluationCriteriaCRUDRepository
                        .findById(resultMetricToEnrich.getParentEvaluationCriteriaId()).get()
        );


        return enrichedResultMetric;
    }
}

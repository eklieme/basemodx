package io.basemod.app.evaluation;

import io.basemod.app.evaluation.criteria.grant.EvaluationCriteriaGrantService;
import io.basemod.app.evaluation.experiment.biometricsystem.BiometricSystemService;
import io.basemod.app.evaluation.experiment.dataset.DataSetCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.DataSetService;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.evaluation.EnrichedExperimentSpecificEvaluation;
import io.basemod.app.evaluation.experiment.evaluation.result.EnrichedExperimentSpecificEvaluationSetup;
import io.basemod.app.evaluation.experiment.evaluation.result.SpecificResultService;
import io.basemod.app.evaluation.extension.BaseEvaluationExtension;
import io.basemod.app.evaluation.extension.EnrichedBaseEvaluationExtension;
import io.basemod.app.evaluation.implementation.EnrichedImplementationSpecificEvaluationResult;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BaseEvaluationService {

    Logger logger = LoggerFactory.getLogger(BaseEvaluationService.class);

    private ImplementationSpecificEvaluationResultService implementationSpecificEvaluationResultService;
    private SpecificResultService specificResultService;
    private BiometricSystemService biometricSystemService;
    private DataSetService dataSetService;
    private DataSetCRUDRepository dataSetCRUDRepository;

    private EvaluationCriteriaGrantService evaluationCriteriaGrantService;

    @Autowired
    public BaseEvaluationService(ImplementationSpecificEvaluationResultService implementationSpecificEvaluationResultService, SpecificResultService specificResultService, BiometricSystemService biometricSystemService, DataSetService dataSetService, DataSetCRUDRepository dataSetCRUDRepository, EvaluationCriteriaGrantService evaluationCriteriaGrantService) {
        this.implementationSpecificEvaluationResultService = implementationSpecificEvaluationResultService;
        this.specificResultService = specificResultService;
        this.biometricSystemService = biometricSystemService;
        this.dataSetService = dataSetService;
        this.dataSetCRUDRepository = dataSetCRUDRepository;
        this.evaluationCriteriaGrantService = evaluationCriteriaGrantService;
    }

    public EnrichedBaseEvaluation enrichBaseEvaluation(BaseEvaluation baseEvaluationToEnrich, String baseName) {

        //create enriched base evaluation skeleton
        EnrichedBaseEvaluation enrichedBaseEvaluation = new EnrichedBaseEvaluation();
        enrichedBaseEvaluation.setExperimentSpecificEvaluation(new EnrichedExperimentSpecificEvaluation());

        if(baseEvaluationToEnrich.getExperimentSpecificEvaluation()!=null
                && baseEvaluationToEnrich.getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups()!=null
                && baseEvaluationToEnrich.getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size()>0) {

            logger.debug("Biometric system with name {} has evaluation results and thus needs biometric systems and datasets replace", baseName);

            logger.debug("\t set full biometric systems ({})", baseEvaluationToEnrich.getExperimentSpecificEvaluation().getBiometricSystemIds().size());
            //replace biometric systems
            enrichedBaseEvaluation.getExperimentSpecificEvaluation()
                    .setBiometricSystems(baseEvaluationToEnrich
                            .getExperimentSpecificEvaluation()
                            .getBiometricSystemIds().stream()
                            .map(biometricSystemId -> biometricSystemService.enrichBiometricSystemById(biometricSystemId))
                            .collect(Collectors.toList()));

            //replace datasets
            logger.debug("\t set full datasets ({}), enrich", baseEvaluationToEnrich.getExperimentSpecificEvaluation().getUsedDatasetIds().size());
            List<EnrichedDataSet> enrichedDataSetsToSet =
                    dataSetCRUDRepository.findAllByIdIn(baseEvaluationToEnrich
                                    .getExperimentSpecificEvaluation()
                                    .getUsedDatasetIds())
                            .stream()
                            .map(dataSet -> dataSetService.enrichDataset(dataSet))
                            .collect(Collectors.toList());
            enrichedBaseEvaluation.getExperimentSpecificEvaluation()
                    .setUsedDatasets(enrichedDataSetsToSet);

            //set evaluation results
            logger.debug("\t set specific results ({})", baseEvaluationToEnrich.getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size());
            enrichedBaseEvaluation.getExperimentSpecificEvaluation().setExperimentSpecificEvaluationSetups(
                    new ArrayList<>()
            );

            baseEvaluationToEnrich.getExperimentSpecificEvaluation()
                    .getExperimentSpecificEvaluationSetups().forEach(specificEvaluationSetup -> {

                        // create enriched evaluation setup
                        EnrichedExperimentSpecificEvaluationSetup enrichedExperimentSpecificEvaluationSetup
                                = new EnrichedExperimentSpecificEvaluationSetup(specificEvaluationSetup);

                        // replace specific results with enriched ones
                        enrichedExperimentSpecificEvaluationSetup.setSpecificResults(
                                specificEvaluationSetup.getSpecificResults()
                                        .stream()
                                        .map(specificResult -> specificResultService.enrichSpecificResult(specificResult))
                                        .collect(Collectors.toList()));

                        // set biometric systems
                        enrichedExperimentSpecificEvaluationSetup.setBiometricSystems(
                                specificEvaluationSetup.getBiometricSystemIds()
                                        .stream()
                                        .map(biometricSystemId -> biometricSystemService.enrichBiometricSystemById(biometricSystemId))
                                        .collect(Collectors.toList()));

                        // finally add specific setup to enriched setups
                        enrichedBaseEvaluation
                                .getExperimentSpecificEvaluation()
                                .getExperimentSpecificEvaluationSetups()
                                .add(enrichedExperimentSpecificEvaluationSetup);
                    });

            logger.debug("\t created {} skeletons", enrichedBaseEvaluation
                    .getExperimentSpecificEvaluation()
                    .getBiometricSystems().size());

        }

        //replace implementation specific results if existent
        if(baseEvaluationToEnrich.getImplementationSpecificEvaluationResults()!=null
                && baseEvaluationToEnrich.getImplementationSpecificEvaluationResults().size()>0) {

            List<EnrichedImplementationSpecificEvaluationResult>
                    enrichedImplementationSpecificEvaluationResults = new ArrayList<>();

            baseEvaluationToEnrich.getImplementationSpecificEvaluationResults()
                    .forEach(implementationSpecificEvaluationResult -> {
                        enrichedImplementationSpecificEvaluationResults.add(
                                implementationSpecificEvaluationResultService.enrichedImplementationSpecificEvaluationResult(implementationSpecificEvaluationResult)
                        );
                    });

            enrichedBaseEvaluation.setImplementationSpecificEvaluationResults(enrichedImplementationSpecificEvaluationResults);

        } else {
            enrichedBaseEvaluation.setImplementationSpecificEvaluationResults(
                    new ArrayList<>());
        }

        return enrichedBaseEvaluation;
    }

    public EnrichedBaseEvaluationExtension enrichBaseEvaluationExtension(BaseEvaluationExtension baseEvaluationExtensionToEnrich) {

        logger.debug("\t...got request to enrich base evaluation extension with {} grants",
                baseEvaluationExtensionToEnrich.getEvaluationCriteriaGrantsToMerge().size());
        EnrichedBaseEvaluationExtension enrichedBaseEvaluationExtension = new EnrichedBaseEvaluationExtension();
        enrichedBaseEvaluationExtension.setId(baseEvaluationExtensionToEnrich.getId());
        enrichedBaseEvaluationExtension.setModelledElementDetail(baseEvaluationExtensionToEnrich.getModelledElementDetail());
        enrichedBaseEvaluationExtension.setBaseNameForExtension(baseEvaluationExtensionToEnrich.getBaseNameForExtension());

        enrichedBaseEvaluationExtension.setBaseEvaluationToMerge(
                enrichBaseEvaluation(baseEvaluationExtensionToEnrich.getBaseEvaluationToMerge(), baseEvaluationExtensionToEnrich.getBaseNameForExtension()));

        enrichedBaseEvaluationExtension.setEvaluationCriteriaGrantsToMerge(
                baseEvaluationExtensionToEnrich.getEvaluationCriteriaGrantsToMerge()
                        .stream()
                        .map(evaluationCriteriaGrant -> {
                            return evaluationCriteriaGrantService.enrichEvaluationCriteriaGrant(evaluationCriteriaGrant);
                        }).collect(Collectors.toList()));

        return enrichedBaseEvaluationExtension;
    }
}

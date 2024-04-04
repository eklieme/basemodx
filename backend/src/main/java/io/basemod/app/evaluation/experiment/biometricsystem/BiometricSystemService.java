package io.basemod.app.evaluation.experiment.biometricsystem;

import io.basemod.app.base.BaseService;
import io.basemod.app.base.BiometricAuthenticationSystemAndEvaluation;
import io.basemod.app.base.BiometricAuthenticationSystemAndEvaluationRepository;
import io.basemod.app.evaluation.experiment.biometricsystem.feature.FeatureCRUDRepository;
import io.basemod.app.evaluation.experiment.biometricsystem.step.BiometricProcessingStepCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.result.ExperimentSpecificEvaluationSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BiometricSystemService {

    Logger logger = LoggerFactory.getLogger(BiometricSystemService.class);

    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;
    private BaseService baseService;

    private BiometricSystemCRUDRepository biometricSystemCRUDRepository;

    private FeatureCRUDRepository featureCRUDRepository;

    private BiometricProcessingStepCRUDRepository biometricProcessingStepCRUDRepository;

    @Autowired
    public BiometricSystemService(BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository, BaseService baseService, BiometricSystemCRUDRepository biometricSystemCRUDRepository, FeatureCRUDRepository featureCRUDRepository, BiometricProcessingStepCRUDRepository biometricProcessingStepCRUDRepository) {
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
        this.baseService = baseService;
        this.biometricSystemCRUDRepository = biometricSystemCRUDRepository;
        this.featureCRUDRepository = featureCRUDRepository;
        this.biometricProcessingStepCRUDRepository = biometricProcessingStepCRUDRepository;
    }

    public void deleteBiometricSystemById(String biometricSystemIdToDelete) {

        logger.debug("\t..got request to delete biometric system by id '{}'", biometricSystemIdToDelete);
        // has to be there ;)
        BiometricSystem biometricSystemToDelete = biometricSystemCRUDRepository.findById(biometricSystemIdToDelete).get();

        BiometricAuthenticationSystemAndEvaluation baseToChange =
                biometricAuthenticationSystemAndEvaluationRepository.findByNameEqualsIgnoreCase(biometricSystemToDelete.getParentBaseName());

        List<String> remainingBiometricSystemIds = new ArrayList<>();

        if(baseToChange.getBaseEvaluation()!=null
            && baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation()!=null
            && baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystemIds()!=null) {

            remainingBiometricSystemIds = baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystemIds()
                    .stream()
                    .filter(biometricSystemId -> {
                        return !biometricSystemToDelete.getId().equals(biometricSystemId);
                    }).collect(Collectors.toList());
        } else {
            logger.debug("\t\t...step was created but biometric system with step was never created");
        }

        if(remainingBiometricSystemIds.size()==0) {

            logger.debug("\t\t\t..none of biometric systems of base '{}' remain, reset experimental evaluation",
                    baseToChange.getName());
            baseService.resetBaseExperimentalEvaluation(baseToChange);

        } else if(remainingBiometricSystemIds.size()<baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystemIds().size()) {
            logger.debug("\t\t\t..{} of {} biometric systems of base '{}' remain, check for results and modify base",
                    remainingBiometricSystemIds.size(),
                    baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystemIds().size(),
                    baseToChange.getName());

            List<ExperimentSpecificEvaluationSetup> remainingExperimentSpecificEvaluationSetups = baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().stream().filter(experimentSpecificEvaluationSetup -> {
                return !experimentSpecificEvaluationSetup.getBiometricSystemIds().contains(biometricSystemToDelete.getId());
            }).collect(Collectors.toList());

            if(remainingExperimentSpecificEvaluationSetups.size()==0) {
                logger.debug("\t\t\t\t...no experiment specific results remain, reset experimental evaluation of base '{}'",
                        baseToChange.getName());
                baseService.resetBaseExperimentalEvaluation(baseToChange);
            } else if(remainingExperimentSpecificEvaluationSetups.size()<baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size()) {
                logger.debug("\t\t\t\t...{} of {} specific results remain for base '{}', change!",
                        remainingExperimentSpecificEvaluationSetups.size(),
                        baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().size(),
                        baseToChange.getName());

                baseToChange.getBaseEvaluation().getExperimentSpecificEvaluation().setExperimentSpecificEvaluationSetups(remainingExperimentSpecificEvaluationSetups);
                logger.debug("\t\t\t\t\t...update base '{}' in DB", baseToChange.getName());
                biometricAuthenticationSystemAndEvaluationRepository.save(baseToChange);

            } else {
                logger.debug("\t\t\t\t...base '{}' needs no changes in specific results", baseToChange.getName());
            }
        } else {
            logger.debug("\t\t\t..no changes in biometric systems of base '{}' required", baseToChange.getName());
        }

        logger.debug("\t\t..finally, delete biometric system '{}' of base '{}'",
                biometricSystemToDelete.getName(), biometricSystemToDelete.getParentBaseName());

        biometricSystemCRUDRepository.deleteById(biometricSystemToDelete.getId());
    }

    public EnrichedBiometricSystem enrichBiometricSystem(BiometricSystem biometricSystemToEnrich) {

        logger.debug("\t..enrich biometric system with name '{}' and parent base '{}'", biometricSystemToEnrich.getName(),
                biometricSystemToEnrich.getParentBaseName());

        EnrichedBiometricSystem enrichedBiometricSystem = new EnrichedBiometricSystem(biometricSystemToEnrich.getName());

        //copy simple things
        enrichedBiometricSystem.setId(biometricSystemToEnrich.getId());
        enrichedBiometricSystem.setDataInputIds(biometricSystemToEnrich.getDataInputIds());
        enrichedBiometricSystem.setFusedSystems(biometricSystemToEnrich.getFusedSystemIds());
        enrichedBiometricSystem.setFusionType(biometricSystemToEnrich.getFusionType());
        enrichedBiometricSystem.setModelledElementDetail(biometricSystemToEnrich.getModelledElementDetail());
        enrichedBiometricSystem.setParentBaseName(biometricSystemToEnrich.getParentBaseName());

        //resolve ids
        if(biometricSystemToEnrich.getFeatureIds()!=null && biometricSystemToEnrich.getFeatureIds().size()>0) {
            logger.debug("\t..biometric system {} has features, enrich!", biometricSystemToEnrich.getName());
            enrichedBiometricSystem.setFeatures(biometricSystemToEnrich.getFeatureIds()
                    .stream().map(featureId -> featureCRUDRepository.findById(featureId).get()).collect(Collectors.toList()));
        } else {
            logger.debug("\t..biometric system {} has no features", biometricSystemToEnrich.getName());
        }

        if(biometricSystemToEnrich.getFurtherProcessingStepIds()!=null && biometricSystemToEnrich.getFurtherProcessingStepIds().size()>0) {
            logger.debug("\t..biometric system {} has further processing steps, enrich!", biometricSystemToEnrich.getName());
            enrichedBiometricSystem.setFurtherProcessingSteps(biometricSystemToEnrich.getFurtherProcessingStepIds()
                    .stream().map(furtherProcessingStepId -> biometricProcessingStepCRUDRepository.findById(furtherProcessingStepId).get()).collect(Collectors.toList()));
        } else {
            logger.debug("\t..biometric system {} has no further processing steps", biometricSystemToEnrich.getName());
        }

        if(biometricSystemToEnrich.getSignalProcessingStepIds()!=null && biometricSystemToEnrich.getSignalProcessingStepIds().size()>0) {
            logger.debug("\t..biometric system {} has signal processing steps, enrich!", biometricSystemToEnrich.getName());
            enrichedBiometricSystem.setSignalProcessingSteps(biometricSystemToEnrich.getSignalProcessingStepIds()
                    .stream().map(signalProcessingStepId -> biometricProcessingStepCRUDRepository.findById(signalProcessingStepId).get()).collect(Collectors.toList()));
        } else {
            logger.debug("\t..biometric system {} has no signal processing steps", biometricSystemToEnrich.getName());
        }

        return enrichedBiometricSystem;
    }

    public EnrichedBiometricSystem enrichBiometricSystemById(String biometricSystemToEnrichId) {
        logger.debug("...request to get enriched biometric system based on id {}", biometricSystemToEnrichId);
        return enrichBiometricSystem(biometricSystemCRUDRepository.findById(biometricSystemToEnrichId).get());
    }
}

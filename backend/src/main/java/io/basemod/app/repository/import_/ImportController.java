package io.basemod.app.repository.import_;

import io.basemod.app.architecture.*;
import io.basemod.app.base.BiometricAuthenticationSystemAndEvaluation;
import io.basemod.app.base.BiometricAuthenticationSystemAndEvaluationRepository;
import io.basemod.app.base.EnrichedBaseForExport;
import io.basemod.app.base.EnrichedBiometricAuthenticationSystemAndEvaluation;
import io.basemod.app.characteristic.PhysiologicalBiometricCharacteristic;
import io.basemod.app.characteristic.SoftBiometricCharacteristic;
import io.basemod.app.domain.DomainElement;
import io.basemod.app.domain.NamedDomainSummary;
import io.basemod.app.characteristic.BiometricCharacteristic;
import io.basemod.app.characteristic.BiometricCharacteristicCRUDRepository;
import io.basemod.app.evaluation.criteria.EvaluationCriteria;
import io.basemod.app.evaluation.criteria.grant.EnrichedEvaluationCriteriaGrant;
import io.basemod.app.evaluation.criteria.grant.EvaluationCriteriaGrant;
import io.basemod.app.evaluation.experiment.biometricsystem.step.*;
import io.basemod.app.evaluation.experiment.dataset.sampling.EnrichedSampledBiometric;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.EnrichedDeviceCategory;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceService;
import io.basemod.app.evaluation.experiment.dataset.sampling.sensor.*;
import io.basemod.app.evaluation.experiment.evaluation.criteria.EnrichedExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaCRUDRepository;
import io.basemod.app.evaluation.experiment.biometricsystem.*;
import io.basemod.app.evaluation.experiment.biometricsystem.feature.Feature;
import io.basemod.app.evaluation.experiment.biometricsystem.feature.FeatureCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.*;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContext;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContextCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.SampledBiometric;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategoryCRUDRepository;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDevice;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.SampleDeviceCRUDRepository;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteriaService;
import io.basemod.app.evaluation.experiment.evaluation.result.ExperimentSpecificEvaluationSetup;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetric;
import io.basemod.app.evaluation.experiment.evaluation.result.metric.ResultMetricCRUDRepository;
import io.basemod.app.evaluation.implementation.EnrichedImplementationSpecificEvaluationResult;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteriaCRUDRepository;
import io.basemod.app.repository.export_.DatasetForExportArtefact;
import io.basemod.app.repository.import_.domain.*;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
public class ImportController {

    private Logger logger = LoggerFactory.getLogger(ImportController.class);

    private BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository;
    private DataSetCRUDRepository dataSetCRUDRepository;
    private BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository;
    private ResourceToProtectCRUDRepository resourceToProtectCRUDRepository;
    private BiometricProcessingStepCRUDRepository biometricProcessingStepCRUDRepository;
    private BiometricSystemCRUDRepository biometricSystemCRUDRepository;
    private DeviceCategoryCRUDRepository deviceCategoryCRUDRepository;
    private SampleDeviceCRUDRepository sampleDeviceCRUDRepository;
    private SensorCRUDRepository sensorCRUDRepository;
    private SensorDimensionCRUDRepository sensorDimensionCRUDRepository;
    private SamplingContextCRUDRepository samplingContextCRUDRepository;
    private ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository;
    private ResultMetricCRUDRepository resultMetricCRUDRepository;
    private ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository;
    private FeatureCRUDRepository featureCRUDRepository;

    private SensorService sensorService;

    private DataSetService dataSetService;

    private SampleDeviceService sampleDeviceService;

    private ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService;

    @Autowired
    public ImportController(BiometricAuthenticationSystemAndEvaluationRepository biometricAuthenticationSystemAndEvaluationRepository, DataSetCRUDRepository dataSetCRUDRepository, BiometricCharacteristicCRUDRepository biometricCharacteristicCRUDRepository, ResourceToProtectCRUDRepository resourceToProtectCRUDRepository, BiometricProcessingStepCRUDRepository biometricProcessingStepCRUDRepository, BiometricSystemCRUDRepository biometricSystemCRUDRepository, DeviceCategoryCRUDRepository deviceCategoryCRUDRepository, SampleDeviceCRUDRepository sampleDeviceCRUDRepository, SensorCRUDRepository sensorCRUDRepository, SensorDimensionCRUDRepository sensorDimensionCRUDRepository, SamplingContextCRUDRepository samplingContextCRUDRepository, ExperimentSpecificEvaluationCriteriaCRUDRepository experimentSpecificEvaluationCriteriaCRUDRepository, ResultMetricCRUDRepository resultMetricCRUDRepository, ImplementationSpecificEvaluationCriteriaCRUDRepository implementationSpecificEvaluationCriteriaCRUDRepository, FeatureCRUDRepository featureCRUDRepository, SensorService sensorService, DataSetService dataSetService, SampleDeviceService sampleDeviceService, ExperimentSpecificEvaluationCriteriaService experimentSpecificEvaluationCriteriaService) {
        this.biometricAuthenticationSystemAndEvaluationRepository = biometricAuthenticationSystemAndEvaluationRepository;
        this.dataSetCRUDRepository = dataSetCRUDRepository;
        this.biometricCharacteristicCRUDRepository = biometricCharacteristicCRUDRepository;
        this.resourceToProtectCRUDRepository = resourceToProtectCRUDRepository;
        this.biometricProcessingStepCRUDRepository = biometricProcessingStepCRUDRepository;
        this.biometricSystemCRUDRepository = biometricSystemCRUDRepository;
        this.deviceCategoryCRUDRepository = deviceCategoryCRUDRepository;
        this.sampleDeviceCRUDRepository = sampleDeviceCRUDRepository;
        this.sensorCRUDRepository = sensorCRUDRepository;
        this.sensorDimensionCRUDRepository = sensorDimensionCRUDRepository;
        this.samplingContextCRUDRepository = samplingContextCRUDRepository;
        this.experimentSpecificEvaluationCriteriaCRUDRepository = experimentSpecificEvaluationCriteriaCRUDRepository;
        this.resultMetricCRUDRepository = resultMetricCRUDRepository;
        this.implementationSpecificEvaluationCriteriaCRUDRepository = implementationSpecificEvaluationCriteriaCRUDRepository;
        this.featureCRUDRepository = featureCRUDRepository;
        this.sensorService = sensorService;
        this.dataSetService = dataSetService;
        this.sampleDeviceService = sampleDeviceService;
        this.experimentSpecificEvaluationCriteriaService = experimentSpecificEvaluationCriteriaService;
    }
/*
         for each base to import we need to do check several general things:
         1. Is the name of the BASE unique?
            if not, we do not import
         2. Check on characteristics (by user activity name + transitionalState)
            if new => OK
            if existing with same name exist => check for similarity
                if not similar => ask user?
                if similar => skip
         3. Check on deployment locations (by location name)
            Only add new ones
         4. Check for resources to protect (by resource name)
            Only add new ones
         5. Check Experimental Evaluations
            Check on datasets (based on name)
                if fully similar apart from name => skip, but continue import
                if partially different (same name, but different content) => skip, but also skip full base import?
                if different => consider adding dataset
                    per dataset
                        => sampled behaviour, => check main / support characteristics
                        => context => add only new, skip existing ones
                        => sample devices
                            => add new (by manuf+model+category)
                            => if exiting, add missing dimensions / sensors respectively
            If not stopped:
                Check on biometric systems based on included processing steps
                for feature calculation step => add new features, skip existing
                any other step: add if new (full comparison based on name and processing type)

                Check Evaluation setups based on metrics
                => create metric if not existing (name + measuring unit), warning for different granting rules
                => create parent criteria if not existing, otherwise add to respective criteria
        6. Check Implementation Based Results
            - just add results, create criteria if not existing

         */

    public ArtifactImportResult processImportBaseRequest(List<EnrichedBaseForExport> baseToImport) {

        //Import result skeleton
        ArtifactImportResult artifactImportResult = new ArtifactImportResult(ImportResultStatus.SUCCESS);

        for (EnrichedBaseForExport enrichedBaseToImport : baseToImport) {

            logger.trace("Start importing Base with name {}", enrichedBaseToImport.getName());

            // 1. => Check base name
            logger.trace("\t check if BASE with name already exists");

            List<NamedDomainSummary> existingBaseNames = biometricAuthenticationSystemAndEvaluationRepository.findBy();

            if (existingBaseNames.stream().map(NamedDomainSummary::getName).collect(Collectors.toList())
                    .contains(enrichedBaseToImport.getName())) {
                logger.trace("Base with name {} already exists in repository, deny import", enrichedBaseToImport.getName());
                artifactImportResult.addImportRemark(
                        new ImportRemark("Base with name " + enrichedBaseToImport.getName() + " already exists and will be skipped",
                                ArtefactType.BASE,
                                ImportRemarkLevel.WARNING));

                // continue to next BASE
                continue;
            }


            /*
             2. Check for characteristics
             Therefor, we take main characteristics and any characteristic that may be modelled as support characteristic within any
             dataset from the experimental evaluation, if existing
             */

            List<BiometricCharacteristic> characteristicsToImport = new ArrayList<>();

            if(enrichedBaseToImport.getBaseEvaluation()!=null
                && enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation()!=null
                && enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets().size()>0) {

                logger.trace("Check characteristics to import for base with experiments with name '{}' ({} characteristics, {} datasets)",
                        enrichedBaseToImport.getName(),
                        enrichedBaseToImport.getBiometricCharacteristics().size(),
                        enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets().size());

                characteristicsToImport = checkCharacteristicsForImport(
                        artifactImportResult,
                        enrichedBaseToImport.getBiometricCharacteristics(),
                        enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets());

                if (artifactImportResult.containsCriticalRemark()) {
                    logger.trace("skipping import of base {} as we have a critical import remark after checking base names",
                            enrichedBaseToImport.getName());
                    continue;
                }
            } else {
                logger.debug("Check {} biometric characteristics for import, no experiments were done in base '{}', so datasets need no analysis",
                        enrichedBaseToImport.getBiometricCharacteristics().size(),
                        enrichedBaseToImport.getName());

                characteristicsToImport = checkCharacteristicsForImport(
                        artifactImportResult,
                        enrichedBaseToImport.getBiometricCharacteristics(),
                        new ArrayList<>());
            }

            // 3. Device categories and Resources To Protect

            logger.trace("Check device category related artefacts to import for base {}", enrichedBaseToImport.getName());

            DeviceCategoryImportArtifacts deviceCategoryImportArtifacts = determineDeviceCategoryRelatedArtefactsToImport(
                    artifactImportResult,
                    enrichedBaseToImport.getTargetArchitecture());

            logger.trace("Check resource to protect '{}' to import for base {}",
                    enrichedBaseToImport.getTargetArchitecture().getResourceToProtect(),
                    enrichedBaseToImport.getName());

            boolean importResourceToProtect = checkResourceToProtectForImport(
                    artifactImportResult,
                    enrichedBaseToImport.getTargetArchitecture());


            DatasetImportArtefacts datasetImportArtefacts = new DatasetImportArtefacts();
            List<BiometricSystemProcessingStep> processingStepsToImport = new ArrayList<>();
            List<Feature> featuresToImport = new ArrayList<>();

            ExperimentSpecificEvaluationCriteriaImportArtefact experimentSpecificEvaluationCriteriaImportArtefact =
                    new ExperimentSpecificEvaluationCriteriaImportArtefact();

            List<EnrichedExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteriaToCheckForImportForBase =
                    new ArrayList<>();

            // 4. Check for experimental evaluation artefacts to import
            if (enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets()!= null
                    && enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets().size() > 0) {

                // we have experimental evaluations, check datasets for existence and processing steps used in the biometric systems
                logger.trace("Check data sets to import for base {}", enrichedBaseToImport.getName());

                datasetImportArtefacts = checkDatasetsForImport(
                        artifactImportResult,
                        enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets(),
                        false
                );

                if (artifactImportResult.containsCriticalRemark()) {
                    logger.trace("skipping import of base {} as we have a critical import remark after datasets",
                            enrichedBaseToImport.getName());
                    continue;
                }

                logger.trace("Check processing steps for base {}", enrichedBaseToImport.getName());

                processingStepsToImport = checkProcessingStepsForImport(
                        artifactImportResult,
                        enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystems()
                );

                if (artifactImportResult.containsCriticalRemark()) {
                    logger.trace("skipping import of base {} as we have a critical import remark after processing steps",
                            enrichedBaseToImport.getName());
                    continue;
                }

                // check features for import
                featuresToImport = checkFeaturesToImport(artifactImportResult,
                        enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystems());


                // check criteria
                logger.trace("Check experiment specific evaluation criteria for base {}", enrichedBaseToImport.getName());
                experimentSpecificEvaluationCriteriaToCheckForImportForBase
                        = enrichedBaseToImport.getEvaluationCriteriaGrants()
                            .stream()
                            .map(enrichedEvaluationCriteriaGrant -> enrichedEvaluationCriteriaGrant.getEvaluationCriteria())
                        .filter(evaluationCriteria -> evaluationCriteria.getType().equals(EvaluationCriteria.ENRICHED_EXPERIMENT_SPECIFIC_TYPE))
                        .map(EnrichedExperimentSpecificEvaluationCriteria.class::cast)
                        .collect(Collectors.toList());

                experimentSpecificEvaluationCriteriaImportArtefact = checkExperimentSpecificCriteriaToImport(
                        artifactImportResult,
                        experimentSpecificEvaluationCriteriaToCheckForImportForBase
                );

                if (artifactImportResult.containsCriticalRemark()) {
                    artifactImportResult.addImportRemark(new ImportRemark(
                            "A critical import remark after the import of experimental evaluation criteria exist, skip base import",
                            ArtefactType.EVALUATION_CRITERIA,
                            ImportRemarkLevel.CRITICAL
                    ));
                    continue;
                }

                // now, every experiment specific evaluation result could be persisted

            }

            // persisting implementation-based evaluation happens at the end without specific check as no critical situation can happen initially
            // persisting evaluaion criteria grants happens at end without specific checks since nothing critical can happen neither

            // we checked all components for import and potential duplicates and conflicts and had no problems, now the actual import can happen

            BiometricAuthenticationSystemAndEvaluation baseToFinallyImport = new BiometricAuthenticationSystemAndEvaluation();
            baseToFinallyImport.setName(enrichedBaseToImport.getName());
            baseToFinallyImport.setModellingProgress(enrichedBaseToImport.getModellingProgress());
            baseToFinallyImport.setModellingFinished(enrichedBaseToImport.isModellingFinished());
            baseToFinallyImport.setModelledElementDetail(enrichedBaseToImport.getModelledElementDetail());
            baseToFinallyImport.setReference(enrichedBaseToImport.getReference());

            // 1. => we can just save all characteristics
            importNewBiometricCharacteristics(characteristicsToImport, artifactImportResult);
            // ...and update the ids
            updateBiometricCharacteristicIdMapping(enrichedBaseToImport, baseToFinallyImport,
                    artifactImportResult);

            // 2. => we can just save all deployment locations and resources to protect
            importDeviceCategoryRelatedArtifacts(deviceCategoryImportArtifacts, artifactImportResult);


            if (importResourceToProtect) {
                ResourceToProtect resourceToProtectToImport = new ResourceToProtect();
                resourceToProtectToImport.setName(enrichedBaseToImport.getTargetArchitecture().getResourceToProtect());
                resourceToProtectToImport.setModelledElementDetail(enrichedBaseToImport.getModelledElementDetail());
                resourceToProtectCRUDRepository.save(resourceToProtectToImport);
                artifactImportResult.anotherNArtefactsImported(1,
                        ArtefactType.RESOURCE_TO_PROTECT,
                        ArtefactType.RESOURCE_TO_PROTECT.getNamePlural());
            }

            // ... and update the ids
            updateTargetArchitectureIdMappings(enrichedBaseToImport, baseToFinallyImport);

            // set base evaluation for base to finally import as it was originally
            baseToFinallyImport.setBaseEvaluation(enrichedBaseToImport.getOriginalBaseEvaluation());


            if (enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets() != null
                    && enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets().size() > 0) {

                /*
                 we have experiment specific evaluations
                 3. => data sets, after saving the new it has to be made sure that the id references in the base are correct
                    1. => save all new data sets
                        1. save all included artefacts such as sensors, devices, evaluation contexts etc.
                        2. save all datasets
                    2. => update all ids in base to import by getting the ids of all datasets referenced in base to import and replacing respectively
                    3. => do the same for the evaluation setups
                 */

                Map<String, String> biometricSystemIdMappings = importExperimentalEvaluationRelatedArtifacts(
                        enrichedBaseToImport,
                        datasetImportArtefacts,
                        processingStepsToImport,
                        featuresToImport,
                        artifactImportResult,
                        baseToFinallyImport
                );

                // update id mappings of datasets based on just imported datasets since we imported them in the context
                // of experimental evaluation together with biometric systems

                updateDatasetIdMappings(enrichedBaseToImport, baseToFinallyImport, artifactImportResult);

                if (artifactImportResult.containsCriticalRemark()) {
                    logger.trace("skipping import of base {} as we have a critical import remark after remapping dataset references",
                            enrichedBaseToImport.getName());
                }

                // update biometric system ids based on just imported biometric systems

                updateBiometricSystemIdMappings(enrichedBaseToImport,
                        baseToFinallyImport,
                        biometricSystemIdMappings);

                if (artifactImportResult.containsCriticalRemark()) {
                    artifactImportResult.addImportRemark(new ImportRemark(
                            "A critical import remark after the import of experimental evaluation artefacts exist, skip base import",
                            ArtefactType.EXPERIMENTAL_EVALUATION,
                            ImportRemarkLevel.CRITICAL
                    ));
                    continue;
                }

                // import criteria and metrics
                importCriteriaAndResultMetrics(experimentSpecificEvaluationCriteriaImportArtefact,
                        artifactImportResult,
                        experimentSpecificEvaluationCriteriaToCheckForImportForBase);

                // update mappings in experimental results
                updateExperimentalResultMetricIds(enrichedBaseToImport, baseToFinallyImport, artifactImportResult);

                if(artifactImportResult.containsCriticalRemark()) {
                    artifactImportResult.addImportRemark(new ImportRemark(
                            "A critical import remark after updating the result metric id mapping in experimental results, skip base import",
                            ArtefactType.EXPERIMENTAL_EVALUATION,
                            ImportRemarkLevel.CRITICAL
                    ));
                    continue;
                }

            }

            if (enrichedBaseToImport.getBaseEvaluation().getImplementationSpecificEvaluationResults().size() > 0) {
                // we have implementation specific results

                // lets first find all implementation specific result criteria to import

                List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteriaToCheckForImport
                        = enrichedBaseToImport.getEvaluationCriteriaGrants()
                            .stream().map(EnrichedEvaluationCriteriaGrant::getEvaluationCriteria).filter(evaluationCriteria -> {
                            return evaluationCriteria.getType().equals(ExperimentSpecificEvaluationCriteria.IMPLEMENTATION_SPECIFIC_TYPE);
                        }).map(ImplementationSpecificEvaluationCriteria.class::cast)
                        .collect(Collectors.toList());

                List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteriaToImport = checkImplementationSpecificCriteriaToImport(
                        artifactImportResult,
                        implementationSpecificEvaluationCriteriaToCheckForImport
                );

                // persist these criteria
                implementationSpecificEvaluationCriteriaCRUDRepository.saveAll(implementationSpecificEvaluationCriteriaToImport);
                artifactImportResult.anotherNArtefactsImported(implementationSpecificEvaluationCriteriaToImport.size(),
                        ArtefactType.EVALUATION_CRITERIA,
                        ArtefactType.EVALUATION_CRITERIA.getNamePlural());

                updateImplementationSpecificResultsCriteriaMapping(enrichedBaseToImport, baseToFinallyImport);


                // all implementationspecific results now reference the right criteria
            }

            // import evaluation criteria grants if available
            if(enrichedBaseToImport.getEvaluationCriteriaGrants()!=null) {
                // for each grant, we have to set the most current criteria (they are already imported)
                for (EnrichedEvaluationCriteriaGrant evaluationCriteriaGrantToImport : enrichedBaseToImport.getEvaluationCriteriaGrants()) {

                    EvaluationCriteriaGrant evaluationCriteriaGrantReadyForImport = new EvaluationCriteriaGrant();
                    evaluationCriteriaGrantReadyForImport.setGrantingLevel(evaluationCriteriaGrantToImport.getGrantingLevel());

                    if (evaluationCriteriaGrantToImport.getEvaluationCriteria().getType().equals(EvaluationCriteria.ENRICHED_EXPERIMENT_SPECIFIC_TYPE)) {
                        // experiment specific criteria grant
                        ExperimentSpecificEvaluationCriteria mostCurrentVersionOfCriteria = experimentSpecificEvaluationCriteriaCRUDRepository.findByNameAndCategory(
                                evaluationCriteriaGrantToImport.getEvaluationCriteria().getName(), evaluationCriteriaGrantToImport.getEvaluationCriteria().getCategory().name()
                        );

                        evaluationCriteriaGrantReadyForImport.setExperimentSpecificEvaluationCriteriaId(mostCurrentVersionOfCriteria.getId());
                    } else {
                        // implementation specific criteria grant
                        ImplementationSpecificEvaluationCriteria mostCurrentVersionOfCriteria = implementationSpecificEvaluationCriteriaCRUDRepository.findByNameAndCategory(
                                evaluationCriteriaGrantToImport.getEvaluationCriteria().getName(), evaluationCriteriaGrantToImport.getEvaluationCriteria().getCategory().name()
                        );

                        evaluationCriteriaGrantReadyForImport.setImplementationSpecificEvaluationCriteriaId(mostCurrentVersionOfCriteria.getId());

                    }

                    evaluationCriteriaGrantReadyForImport.setModelledElementDetail(evaluationCriteriaGrantToImport.getModelledElementDetail());

                    baseToFinallyImport.addEvaluationCriteriaGrant(evaluationCriteriaGrantReadyForImport);
                }
            }

            // since no critical situation occured until now, we can finally import the base that was updated during
            // the import with all required ids of imported artifacts

            logger.trace("finally, import overall base {}", enrichedBaseToImport.getName());
            biometricAuthenticationSystemAndEvaluationRepository.save(baseToFinallyImport);
            artifactImportResult.anotherNArtefactsImported(1,
                    ArtefactType.BASE,
                    ArtefactType.BASE.getNamePlural());

        }


        artifactImportResult.updateResultStatus();

        return artifactImportResult;

    }

    private void updateImplementationSpecificResultsCriteriaMapping(EnrichedBaseForExport enrichedBaseToImport, BiometricAuthenticationSystemAndEvaluation baseToFinallyImport) {


        logger.debug("...update implementation specific criteria ids of base '{}'", enrichedBaseToImport.getName());
        Map<String, String> implementationSpecificCriteriaMappings = new HashMap<>();
        Map<String, String> deviceCategoryIdMappings = new HashMap<>();
        for (EnrichedImplementationSpecificEvaluationResult implementationSpecificEvaluationResultToImport : enrichedBaseToImport.getBaseEvaluation().getImplementationSpecificEvaluationResults()) {


            // all we need to care for is the respective Criteria used
            ImplementationSpecificEvaluationCriteria mostCurrentVersionOfCriteria = implementationSpecificEvaluationCriteriaCRUDRepository.findByNameAndCategory(
                    implementationSpecificEvaluationResultToImport.getCriteria().getName(), implementationSpecificEvaluationResultToImport.getCriteria().getCategory().name()
            );

            implementationSpecificCriteriaMappings.put(implementationSpecificEvaluationResultToImport.getCriteriaId(),
                    mostCurrentVersionOfCriteria.getId());

            implementationSpecificEvaluationResultToImport.getAffectedDeviceCategories().forEach(deviceCategoryToImport -> {
                DeviceCategory deviceCategoryInTargetRepository =
                        deviceCategoryCRUDRepository.findFirstByNameEqualsIgnoreCase(deviceCategoryToImport.getName());

                deviceCategoryIdMappings.put(deviceCategoryToImport.getId(), deviceCategoryInTargetRepository.getId());
            });

        }

        logger.debug("\t...create mappings for {} device categories that may be affected by implementation specific results",
                deviceCategoryIdMappings.keySet().size());

        baseToFinallyImport.getBaseEvaluation().getImplementationSpecificEvaluationResults().forEach(implementationSpecificEvaluationResult -> {
            implementationSpecificEvaluationResult.setCriteriaId(implementationSpecificCriteriaMappings.get(implementationSpecificEvaluationResult.getCriteriaId()));
            implementationSpecificEvaluationResult.setAffectedDeviceCategoryIds(
                    implementationSpecificEvaluationResult.getAffectedDeviceCategoryIds().stream().map(deviceCategoryIdMappings::get).collect(Collectors.toList()));
        });

        logger.debug("\t...updated {} implementation specific criteria ids and replaced affected device category ids " +
                "based on {} mappings", baseToFinallyImport.getBaseEvaluation().getImplementationSpecificEvaluationResults().size(),
                deviceCategoryIdMappings.keySet().size());

    }

    private void updateTargetArchitectureIdMappings(EnrichedBiometricAuthenticationSystemAndEvaluation enrichedBaseToImport, BiometricAuthenticationSystemAndEvaluation baseToFinallyImport) {

        logger.debug("...update target architecture mappings of base to import '{}'", enrichedBaseToImport.getName());

        baseToFinallyImport.setTargetArchitecture(enrichedBaseToImport.getTargetArchitecture());
        if(!enrichedBaseToImport.getTargetArchitecture().isSkipTargetArchitecture()) {
            logger.debug("\t...target architecture of base to import '{}' is not skipped!", enrichedBaseToImport.getName());
            // replace all Ids
            ResourceToProtect resourceToProtectInTargetRepository = resourceToProtectCRUDRepository.findFirstByName(enrichedBaseToImport.getTargetArchitecture().getResourceToProtect());
            baseToFinallyImport.getTargetArchitecture().setResourceToProtectId(resourceToProtectInTargetRepository.getId());

            baseToFinallyImport.getTargetArchitecture().setDataCapturingDeviceCategoryIds(
                    replaceDeviceCategoriesWithIds(enrichedBaseToImport.getTargetArchitecture().getDataCapturingDeviceCategories()));

            baseToFinallyImport.getTargetArchitecture().setDataStorageDeviceCategoryIds(
                    replaceDeviceCategoriesWithIds(enrichedBaseToImport.getTargetArchitecture().getDataStorageDeviceCategories()));

            baseToFinallyImport.getTargetArchitecture().setDecisionDeviceCategoryIds(
                    replaceDeviceCategoriesWithIds(enrichedBaseToImport.getTargetArchitecture().getDecisionDeviceCategories()));

            baseToFinallyImport.getTargetArchitecture().setMatchingDeviceCategoryIds(
                    replaceDeviceCategoriesWithIds(enrichedBaseToImport.getTargetArchitecture().getMatchingDeviceCategories()));

            baseToFinallyImport.getTargetArchitecture().setSignatureCreationDeviceCategoryIds(
                    replaceDeviceCategoriesWithIds(enrichedBaseToImport.getTargetArchitecture().getSignatureCreationDeviceCategories()));

            baseToFinallyImport.getTargetArchitecture().setSignalProcessingDeviceCategoryIds(
                    replaceDeviceCategoriesWithIds(enrichedBaseToImport.getTargetArchitecture().getSignalProcessingDeviceCategories()));
        } else {
            logger.debug("\t...target architecture of base to import '{}' is skipped!", enrichedBaseToImport.getName());
        }

    }

    private List<String> replaceDeviceCategoriesWithIds(List<EnrichedDeviceCategory> deviceCategoriesToReplace) {
        List<String> resultIds = new ArrayList<>();
        deviceCategoriesToReplace.forEach(deviceCategory -> {
            DeviceCategory deviceCategoryInTargetRepository = deviceCategoryCRUDRepository.findFirstByNameEqualsIgnoreCase(deviceCategory.getName());
            resultIds.add(deviceCategoryInTargetRepository.getId());
        });

        return resultIds;
    }

    private void updateBiometricCharacteristicIdMapping(EnrichedBiometricAuthenticationSystemAndEvaluation enrichedBaseToImport,
                                                        BiometricAuthenticationSystemAndEvaluation baseToFinallyImport,
                                                        ArtifactImportResult artifactImportResult) {

        logger.debug("..update biometric characteristic id mapping of base to import '{}'", baseToFinallyImport);
        enrichedBaseToImport.getBiometricCharacteristics().forEach(biometricCharacteristic -> {
            BiometricCharacteristic characteristicInTargetRepository = biometricCharacteristicCRUDRepository.findFirstByUniqueIdentifier(biometricCharacteristic.getUniqueIdentifier());
            baseToFinallyImport.addBiometricCharacteristicId(characteristicInTargetRepository.getId());
        });

    }

    private void importDeviceCategoryRelatedArtifacts(DeviceCategoryImportArtifacts deviceCategoryImportArtifacts, ArtifactImportResult artifactImportResult) {

        logger.debug("...importing device category related artifacts");
        logger.debug("\t...{} sensor dimensions to existing sensors", deviceCategoryImportArtifacts.getSensorDimensionsToFinallyImport().size());
        logger.debug("\t...{} sensors to {} existing device categories",
                deviceCategoryImportArtifacts.getSensorsToFinallyImportToExistingDeviceCategory().values().size(),
                deviceCategoryImportArtifacts.getSensorsToFinallyImportToExistingDeviceCategory().keySet().size());
        logger.debug("\t...{} completely new device categories", deviceCategoryImportArtifacts.getDeviceCategoriesToNewlyImport().size());

        //1. we attach the sensor dimensions to the existing sensors
        deviceCategoryImportArtifacts.getSensorDimensionsToFinallyImport().forEach(enrichedSensorDimension -> {
            logger.debug("\t...attaching sensor dimension '{}' to existing sensor '{}'",
                    enrichedSensorDimension.getName(), enrichedSensorDimension.getSensor().getName());
            SensorDimension importedDimension = importSensorDimension(enrichedSensorDimension);
            Sensor parentSensorToChange = sensorCRUDRepository.findFirstByNameEqualsIgnoreCase(enrichedSensorDimension.getSensor().getName());
            parentSensorToChange.addSensorDimensionId(importedDimension.getId());
            sensorCRUDRepository.save(parentSensorToChange);
        });

        artifactImportResult.anotherNArtefactsImported(deviceCategoryImportArtifacts.getSensorDimensionsToFinallyImport().size(),
                ArtefactType.SENSOR_DIMENSION,
                ArtefactType.SENSOR_DIMENSION.getNamePlural());


        //2. we attach the sensors to the existing device categories
        deviceCategoryImportArtifacts.getSensorsToFinallyImportToExistingDeviceCategory()
                .entrySet().forEach(deviceCategoryNameNewSensorsList -> {

                    DeviceCategory deviceCategoryToChange =
                            deviceCategoryCRUDRepository.findFirstByNameEqualsIgnoreCase(deviceCategoryNameNewSensorsList.getKey());

                    logger.debug("\t...attaching {} sensors to existing device category '{}'",
                            deviceCategoryNameNewSensorsList.getValue().size(),
                            deviceCategoryToChange.getName());

                    List<String> sensorIds = new ArrayList<>();
                    deviceCategoryNameNewSensorsList.getValue().forEach(enrichedSensor -> {
                        Sensor savedSensor = importSensor(enrichedSensor);
                        sensorIds.add(savedSensor.getId());
                    });

                    artifactImportResult.anotherNArtefactsImported(sensorIds.size(),
                            ArtefactType.SENSOR,
                            ArtefactType.SENSOR.getNamePlural());

                    logger.debug("\t\t..attaching {} new sensor ids to existing device category '{}'",
                            sensorIds.size(),
                            deviceCategoryToChange.getName());
                    deviceCategoryToChange.addSensorIds(sensorIds);
                    deviceCategoryCRUDRepository.save(deviceCategoryToChange);
                });


        if(deviceCategoryImportArtifacts.getDeviceCategoriesToNewlyImport().size()>0) {
            // finally, we add the completely new device categories
            logger.debug("\t..persisting {} new device categories", deviceCategoryImportArtifacts.getDeviceCategoriesToNewlyImport().size());
            deviceCategoryImportArtifacts.getDeviceCategoriesToNewlyImport().forEach(enrichedDeviceCategory -> {
                logger.debug("\t..persisting device category '{}' with {} sensors",
                        enrichedDeviceCategory.getName(),
                        enrichedDeviceCategory.getSensors().size());

                List<String> sensorIds = new ArrayList<>();
                enrichedDeviceCategory.getSensors().forEach(enrichedSensor -> {
                    Sensor importedSensor = importSensor(enrichedSensor);
                    sensorIds.add(importedSensor.getId());
                });

                DeviceCategory deviceCategoryToSave = new DeviceCategory();
                deviceCategoryToSave.setName(enrichedDeviceCategory.getName());
                deviceCategoryToSave.setModelledElementDetail(enrichedDeviceCategory.getModelledElementDetail());
                deviceCategoryToSave.setSensorIds(sensorIds);
                logger.debug("\t\t...finally persisting device category");
                deviceCategoryCRUDRepository.save(deviceCategoryToSave);
            });

            artifactImportResult.anotherNArtefactsImported(deviceCategoryImportArtifacts.getDeviceCategoriesToNewlyImport().size(),
                    ArtefactType.DEVICE_CATEGORY,
                    ArtefactType.DEVICE_CATEGORY.getNamePlural());
        }

    }

    private Sensor importSensor(EnrichedSensor notImportedSensor) {

        logger.debug("\t\t..persisting {} dimensions of sensor to import '{}'",
                notImportedSensor.getSensorDimensions().size(), notImportedSensor.getName());
        List<String> sensorDimensionIds = new ArrayList<>();
        notImportedSensor.getSensorDimensions().forEach(sensorDimension -> {
            SensorDimension importedDimension = importSensorDimension(sensorDimension);
            sensorDimensionIds.add(importedDimension.getId());
        });



        logger.debug("\t\t..persisting sensor to import '{}'",notImportedSensor.getName());
        Sensor sensorToSave = new Sensor();
        sensorToSave.setName(notImportedSensor.getName());
        sensorToSave.setSensorDimensionIds(sensorDimensionIds);
        sensorToSave.setContinuous(notImportedSensor.isContinuous());
        sensorToSave.setModelledElementDetail(notImportedSensor.getModelledElementDetail());
        Sensor importedSensor = sensorCRUDRepository.save(sensorToSave);

        return importedSensor;
    }

    private SensorDimension importSensorDimension(SensorDimension notImportedSensorDimension) {
        SensorDimension sensorDimensionToImport = new SensorDimension();
        sensorDimensionToImport.setName(notImportedSensorDimension.getName());
        sensorDimensionToImport.setModelledElementDetail(notImportedSensorDimension.getModelledElementDetail());
        SensorDimension importedDimension = sensorDimensionCRUDRepository.save(sensorDimensionToImport);
        return importedDimension;
    }

    private void importNewBiometricCharacteristics(List<BiometricCharacteristic> characteristicsToImport, ArtifactImportResult artifactImportResult) {
        biometricCharacteristicCRUDRepository.saveAll(characteristicsToImport);
        artifactImportResult.anotherNArtefactsImported(characteristicsToImport.size(),
                ArtefactType.BEHAVIOURAL_BIOMETRIC_CHARACTERISTIC,
                ArtefactType.BEHAVIOURAL_BIOMETRIC_CHARACTERISTIC.getNamePlural());
    }

    private List<Feature> checkFeaturesToImport(ArtifactImportResult artifactImportResult, List<EnrichedBiometricSystem> biometricSystems) {

        // get all available features
        List<Feature> allExistingFeatures = featureCRUDRepository.findAll();
        List<Feature> featuresToCheckForImport = new ArrayList<>();
        List<Feature> featuresToImport = new ArrayList<>();


        for (EnrichedBiometricSystem biometricSystemToImport : biometricSystems) {
            if(biometricSystemToImport.getFeatures()!=null) {
                featuresToCheckForImport.addAll(biometricSystemToImport.getFeatures());
            }
        }

        if(featuresToCheckForImport.size()>0) {
            // filter duplicates
            featuresToCheckForImport = featuresToCheckForImport.stream().filter(distinctByKey(feature -> feature.getName()))
                    .collect(Collectors.toList());

            // check for any feature if existing

            for (Feature featureToCheckForImport : featuresToCheckForImport) {
                boolean featureCanBeImported = true;

                for (Feature existingFeature : allExistingFeatures) {
                    if (existingFeature.getName().equalsIgnoreCase(featureToCheckForImport.getName())) {

                        logger.trace("Feature with name '{}' will not be imported as its exists by name",
                                featureToCheckForImport.getName());

                        artifactImportResult.addImportRemark(new ImportRemark(
                                featureToCheckForImport.getName() + " will not be imported as it already exists",
                                ArtefactType.FEATURE,
                                ImportRemarkLevel.INFO
                        ));

                        featureCanBeImported = false;
                        break;
                    }
                }

                if (featureCanBeImported) {
                    featuresToImport.add(featureToCheckForImport);
                }

            }

            logger.trace("\t...After intersection with existing by name, {} features will be ready to get imported", featuresToImport.size());

        } else {
            logger.debug("\t...no feature to import");
        }
        return featuresToImport;
    }

    private void updateExperimentalResultMetricIds(EnrichedBiometricAuthenticationSystemAndEvaluation enrichedBase,
                                                   BiometricAuthenticationSystemAndEvaluation baseToFinallyImport,
                                                   ArtifactImportResult artifactImportResult) {

        // we need to map the result metric id to the new one from the repository

        // prepare result metric mapping
        Map<String, String> resultMetricIdMappings = new HashMap<>();

        enrichedBase.getBaseEvaluation()
                .getExperimentSpecificEvaluation()
                .getExperimentSpecificEvaluationSetups().forEach(experimentSpecificEvaluationSetup -> {

            experimentSpecificEvaluationSetup.getSpecificResults().forEach(specificResult -> {
                ResultMetric resultMetricInTargetRepository = resultMetricCRUDRepository.findFirstByNameAndUnit(
                        specificResult.getResultMetric().getName(), specificResult.getResultMetric().getUnit().name());

                resultMetricIdMappings.put(
                        specificResult.getResultMetricId(), resultMetricInTargetRepository.getId());
            });

        });

        // replace result metric ids in evaluation

        baseToFinallyImport.getBaseEvaluation()
                .getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups().forEach(experimentSpecificEvaluationSetup -> {


            experimentSpecificEvaluationSetup.getSpecificResults().forEach(specificResult -> {
                // set new parent criteria ID
                logger.trace("\tupdate result metric id {} used in result {}",
                        specificResult.getResultMetricId(),
                        experimentSpecificEvaluationSetup.getDescription());

                if(resultMetricIdMappings.get(specificResult.getResultMetricId())!=null) {
                    logger.trace("\t...update result metric id {} of specific result in {} ", specificResult.getResultMetricId(),
                            experimentSpecificEvaluationSetup.getDescription());
                    specificResult.setResultMetricId(resultMetricIdMappings.get(
                            specificResult.getResultMetricId()));
                } else {
                    logger.debug("\t...result metric id {} in result {} can not be updated," +
                                    " please inform the developers", specificResult.getResultMetricId(),
                            experimentSpecificEvaluationSetup.getDescription());

                    artifactImportResult.addImportRemark(new ImportRemark(
                            "Result metric id "+specificResult.getResultMetricId()+" of specific result in "+experimentSpecificEvaluationSetup.getDescription()+" can not be updated",
                            ArtefactType.EXPERIMENTAL_EVALUATION,
                            ImportRemarkLevel.CRITICAL
                    ));
                }

            });
        });
    }

    public ArtifactImportResult processImportDatasetRequest(DatasetForExportArtefact artifactsForDataSetsImport) {

        ArtifactImportResult artifactImportResult = new ArtifactImportResult(ImportResultStatus.SUCCESS);

        List<BiometricCharacteristic> biometricCharacteristicsToImport
                = checkCharacteristicsForImport(artifactImportResult, artifactsForDataSetsImport.getBiometricCharacteristics(), artifactsForDataSetsImport.getEnrichedDataSetsForExport());

        importNewBiometricCharacteristics(biometricCharacteristicsToImport, artifactImportResult);

        EnrichedTargetArchitecture enrichedTargetArchitectureForImport = new EnrichedTargetArchitecture();
        enrichedTargetArchitectureForImport.setDataCapturingDeviceCategories(artifactsForDataSetsImport.getDeviceCategoriesForExport());
        DeviceCategoryImportArtifacts deviceCategoryImportArtifacts = determineDeviceCategoryRelatedArtefactsToImport(artifactImportResult, enrichedTargetArchitectureForImport);

        importDeviceCategoryRelatedArtifacts(deviceCategoryImportArtifacts, artifactImportResult);

        DatasetImportArtefacts datasetImportArtefacts = checkDatasetsForImport(artifactImportResult, artifactsForDataSetsImport.getEnrichedDataSetsForExport(), true);

        importDatasetArtefacts(datasetImportArtefacts,
                artifactImportResult,
                true);


        artifactImportResult.updateResultStatus();
        return artifactImportResult;

    }

    public ArtifactImportResult processImportSampleDeviceRequest(List<EnrichedSampleDevice> sampleDevicesToImport) {

        ArtifactImportResult artifactImportResult = new ArtifactImportResult(ImportResultStatus.SUCCESS);

        DatasetImportArtefacts datasetImportArtefacts =
                checkSampleDevicesForImport(artifactImportResult, sampleDevicesToImport, new ArrayList<>());

        importDatasetArtefacts(datasetImportArtefacts,
                artifactImportResult,
                true);

        artifactImportResult.updateResultStatus();
        return artifactImportResult;

    }

    public ArtifactImportResult processImportImplementationSpecificEvaluationCriteriaRequest(List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteria) {

        ArtifactImportResult artifactImportResult = new ArtifactImportResult(ImportResultStatus.SUCCESS);

        List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteriaToImport = checkImplementationSpecificCriteriaToImport(artifactImportResult, implementationSpecificEvaluationCriteria);

        implementationSpecificEvaluationCriteriaCRUDRepository.saveAll(implementationSpecificEvaluationCriteriaToImport);
        artifactImportResult.anotherNArtefactsImported(implementationSpecificEvaluationCriteriaToImport.size(),
                ArtefactType.EVALUATION_CRITERIA,
                ArtefactType.EVALUATION_CRITERIA.getNamePlural());


        artifactImportResult.updateResultStatus();
        return artifactImportResult;

    }

    public ArtifactImportResult processImportExperimentSpecificEvaluationCriteriaRequest(List<EnrichedExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteria) {

        ArtifactImportResult artifactImportResult = new ArtifactImportResult(ImportResultStatus.SUCCESS);

        ExperimentSpecificEvaluationCriteriaImportArtefact experimentSpecificEvaluationCriteriaImportArtefact =
                checkExperimentSpecificCriteriaToImport(artifactImportResult, experimentSpecificEvaluationCriteria);

        importCriteriaAndResultMetrics(experimentSpecificEvaluationCriteriaImportArtefact,
                artifactImportResult, experimentSpecificEvaluationCriteria);

        artifactImportResult.updateResultStatus();
        return artifactImportResult;

    }


    private Map<String, String> importExperimentalEvaluationRelatedArtifacts(EnrichedBiometricAuthenticationSystemAndEvaluation enrichedBase,
                                                                             DatasetImportArtefacts datasetImportArtefacts,
                                                                             List<BiometricSystemProcessingStep> processingStepsToImport,
                                                                             List<Feature> featuresToImport,
                                                                             ArtifactImportResult artifactImportResult,
                                                                             BiometricAuthenticationSystemAndEvaluation baseToFinallyImport) {

        logger.debug("...import experimental evaluation result related artifacts");
        // 1. import dataset artefacts
        importDatasetArtefacts(datasetImportArtefacts,
                artifactImportResult,
                false);

        /*
         then: we need to replace the biometric system references in the same manner
         */

        /*
         persist all biometric systems:
            ==> we need to update the data input mappings previous to persistence (ids of sensor dims used for input)
            therefor: iterate through sensor dimensions of sensors of device categories that were reported
             in the data sampling category of the target architecture we imported and create a mapping
            of these imported dimension ids that we will find in the biometric systems and their new ids.
            The new ids we will find out via name of parent sensor
         */

        logger.debug("\t...create sensor dimension mapping for biometric system input");
        Map<String, String> sensorDimensionIdMapping = new HashMap<>();

        logger.debug("\t\t...{} device categories were reported for data capturing, create mapping based on these",
                enrichedBase.getTargetArchitecture().getDataCapturingDeviceCategories().size());
        for (EnrichedDeviceCategory enrichedDeviceCategory:enrichedBase.getTargetArchitecture().getDataCapturingDeviceCategories()) {

            logger.debug("\t\t\t..create mapping for device category '{}' named for capturing",
                    enrichedDeviceCategory.getName());

            enrichedDeviceCategory.getSensors().forEach(enrichedSensor -> {
                Sensor sensorInTargetRepository = sensorCRUDRepository.findFirstByNameEqualsIgnoreCase(enrichedSensor.getName());
                enrichedSensor.getSensorDimensions().forEach(sensorDimensionFromImportArtifact -> {
                    // lets find id for dimension in target repository were we already imported any device category
                    // related artifact earlier
                    List<SensorDimension> allDimensionsInNewRepositoryByName = sensorDimensionCRUDRepository.findAllByName(sensorDimensionFromImportArtifact.getName());
                    SensorDimension targetDimensionTargetRepository = allDimensionsInNewRepositoryByName.stream().filter(
                            sensorDimensionToFilter -> {
                                return sensorInTargetRepository.getSensorDimensionIds().contains(sensorDimensionToFilter.getId());
                            }).collect(Collectors.toList()).get(0);
                    logger.trace("\t\t\t\t create mapping of '{}' (import artifact) <-> '{}' (target repository)",
                            sensorDimensionFromImportArtifact.getId(), targetDimensionTargetRepository.getId());
                    sensorDimensionIdMapping.put(sensorDimensionFromImportArtifact.getId(), targetDimensionTargetRepository.getId());
                });
            });

            logger.debug("\t\t\t\t..in total, {} dimension mappings exist after creating mappings for device category '{}'",
                    sensorDimensionIdMapping.keySet().size(), enrichedDeviceCategory.getName());

        }

        logger.debug("Prepared a map of {} sensorDimensionId mappings for updating {} biometric systems to import",
                sensorDimensionIdMapping.size(), enrichedBase.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystems().size());


        if (artifactImportResult.containsCriticalRemark()) {
            logger.trace("skipping import of base {} as we have a critical import remark after remapping biometric system data input references",
                    enrichedBase.getName());
            return null;
        }

        logger.trace("import {} new processing steps", processingStepsToImport.size());
        processingStepsToImport.forEach(biometricSystemProcessingStep -> {
            logger.trace("\t...name: '{}', type: '{}'", biometricSystemProcessingStep.getName(), biometricSystemProcessingStep.getProcessingType());
            if(biometricSystemProcessingStep.getProcessingType().equals(ProcessingType.DECISION)) {
                DecisionProcessingStep decisionProcessingStep = (DecisionProcessingStep) biometricSystemProcessingStep;
                biometricProcessingStepCRUDRepository.save(decisionProcessingStep);
            } else if(biometricSystemProcessingStep.getProcessingType().equals(ProcessingType.MATCHING)) {
                MachineLearningProcessingStep mlProcessingStep = (MachineLearningProcessingStep) biometricSystemProcessingStep;
                biometricProcessingStepCRUDRepository.save(mlProcessingStep);
            } else {
                biometricProcessingStepCRUDRepository.save(biometricSystemProcessingStep);
            }
        });
        artifactImportResult.anotherNArtefactsImported(processingStepsToImport.size(),
                ArtefactType.PROCESSING_STEP,
                ArtefactType.PROCESSING_STEP.getNamePlural());

        logger.trace("import {} new features", featuresToImport.size());
        featureCRUDRepository.saveAll(featuresToImport);
        artifactImportResult.anotherNArtefactsImported(featuresToImport.size(),
                ArtefactType.FEATURE,
                ArtefactType.FEATURE.getNamePlural());


        // before actually persisting biometric systems, we need to update data input ids, feature ids and processing step ids

        List<BiometricSystem> biometricSystemsToImport = prepareBiometricSystemsForImport(
                enrichedBase,
                sensorDimensionIdMapping,
                artifactImportResult);

        logger.trace("persisting {} biometric systems", enrichedBase.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystems().size());

        biometricSystemCRUDRepository.saveAll(biometricSystemsToImport);
        artifactImportResult.anotherNArtefactsImported(biometricSystemsToImport.size(),
                ArtefactType.BIOMETRIC_SYSTEM,
                ArtefactType.BIOMETRIC_SYSTEM.getNamePlural());

        Map<String, String> biometricSystemIdMapping = updateFusedSystemIds(biometricSystemsToImport, artifactImportResult);

        if (artifactImportResult.containsCriticalRemark()) {
            logger.trace("skipping import of base {} as we have a critical import remark after remapping biometric system references",
                    enrichedBase.getName());
            return null;
        }

        return biometricSystemIdMapping;


    }

    private Map<String, String> updateFusedSystemIds(List<BiometricSystem> biometricSystemsToImport,
                                                     ArtifactImportResult artifactImportResult) {

        logger.debug("...for each biometric system imported, we check whether we now need and can update the ids of the fused systems");

        // create id mapping

        Map<String, String> biometricSystemReferencesToReplace = new HashMap<>();

        for (BiometricSystem biometricSystemImportedOrExistingBefore : biometricSystemsToImport) {
            String idOfBiometricSystemReferenceThatNeedToBeReplaced = biometricSystemImportedOrExistingBefore.getId();

            // look for new Id
            String idToReplaceWith = biometricSystemCRUDRepository
                    .findByNameAndParentBaseName(biometricSystemImportedOrExistingBefore.getName(),
                            biometricSystemImportedOrExistingBefore.getParentBaseName()).getId();

            logger.debug("\t..found new id {} for biometric to import/existing before based on biometric system name {} with parent base {}, original id {}",
                    idToReplaceWith, biometricSystemImportedOrExistingBefore.getName(), biometricSystemImportedOrExistingBefore.getParentBaseName(),
                    biometricSystemImportedOrExistingBefore.getId());
            biometricSystemReferencesToReplace.put(idOfBiometricSystemReferenceThatNeedToBeReplaced,
                    idToReplaceWith);
        }

        // crosscheck -> we should have found mappings for all biometric systems given for this base

        if (biometricSystemsToImport.size() != biometricSystemReferencesToReplace.size()) {
            logger.debug("\t..did NOT find biometric system mappings for all biometric systems to import");
            artifactImportResult.addImportRemark(new ImportRemark(
                    "Did not find dataset references for all biometric systems provided",
                    ArtefactType.DATASET,
                    ImportRemarkLevel.CRITICAL
            ));

            return null;
        } else {
            logger.trace("\t...found id mappings for all biometric systems to import");
        }


         /*
         replace all references in any base that might fuse other systems
         => they already have been persisted!
         */


        List<BiometricSystem> fusionSystemsToUpdate = new ArrayList<>();

        for(BiometricSystem biometricSystem:biometricSystemsToImport) {
            //check if systems are fused
            if(biometricSystem.getFusedSystemIds()!=null && biometricSystem.getFusedSystemIds().size()>0) {

                logger.debug("\t...need to replace fused system ids ({}) for biometric system {} with id {}",
                        String.join(",",biometricSystem.getFusedSystemIds()),biometricSystem.getName(), biometricSystem.getId());
                biometricSystem.setFusedSystemIds(biometricSystem.getFusedSystemIds()
                        .stream()
                        .map(fusedSystemID -> biometricSystemReferencesToReplace.get(fusedSystemID))
                        .collect(Collectors.toList()));

                logger.debug("\t...replaced fused system ids for biometric system {} with id {} to {}",
                        biometricSystem.getName(), biometricSystem.getId(), String.join(",",biometricSystem.getFusedSystemIds()));
                fusionSystemsToUpdate.add(biometricSystem);
            }
        }

        biometricSystemCRUDRepository.saveAll(fusionSystemsToUpdate);
        logger.trace("\t...replaced fusedSystemIds of {} fusion systems", fusionSystemsToUpdate.size());

        return biometricSystemReferencesToReplace;
    }

    private List<BiometricSystem> prepareBiometricSystemsForImport(EnrichedBiometricAuthenticationSystemAndEvaluation baseToImport,
                                                                   Map<String, String> sensorDimensionIdMapping,
                                                                   ArtifactImportResult artifactImportResult) {

        List<BiometricSystem> biometricSystemsReadyForImport = new ArrayList<>();

        logger.debug("...got request to prepare {} biometric systems for import",
                baseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystems());

        for (EnrichedBiometricSystem enrichedBiometricSystemForImport : baseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getBiometricSystems()) {

            logger.debug("\t...prepare biometric system '{}' of base '{}' for import",
                    enrichedBiometricSystemForImport.getName(), enrichedBiometricSystemForImport.getParentBaseName());

            BiometricSystem biometricSystemReadyForImport = new BiometricSystem();
            biometricSystemReadyForImport.setName(enrichedBiometricSystemForImport.getName());
            biometricSystemReadyForImport.setId(enrichedBiometricSystemForImport.getId());
            biometricSystemReadyForImport.setModelledElementDetail(enrichedBiometricSystemForImport.getModelledElementDetail());
            biometricSystemReadyForImport.setParentBaseName(enrichedBiometricSystemForImport.getParentBaseName());
            biometricSystemReadyForImport.setFusionType(enrichedBiometricSystemForImport.getFusionType());
            // we have to keep the fused system ids which we can update after the final import
            biometricSystemReadyForImport.setFusedSystemIds(enrichedBiometricSystemForImport.getFusedSystems());

            // update data input ids
            List<String> updatedMappingDataInputIds = new ArrayList<>();

            logger.debug("\t\t...update {} data inputs ids for biometric system '{}'",
                    enrichedBiometricSystemForImport.getDataInputIds().size(), enrichedBiometricSystemForImport.getName());

            for (String dataInputToUpdate : enrichedBiometricSystemForImport.getDataInputIds()) {

                /*
                Each data input is the ID of a sensor dimension of a dataset defined / referred to from this base
                => look for dataInput in all datasets defined sample devices
                 */

                logger.debug("\t\t...update data input '{}' ids for biometric system '{}'",
                        dataInputToUpdate, enrichedBiometricSystemForImport.getName());

                if (sensorDimensionIdMapping.containsKey(dataInputToUpdate.split("_")[0])) {
                    // if an id is found here, the respective dataset has just been imported

                    String oldDeviceCategoryId = dataInputToUpdate.split("_")[1];
                    // we already have the new dimension id, lets now look for the new device category id
                    final String[] newDeviceCategoryId = {null};

                    baseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets().forEach(enrichedDataSet -> {
                        enrichedDataSet.getSampledBiometrics().forEach(enrichedSampledBiometric -> {
                            List<DeviceCategory> oldEnrichedDeviceCategoriesInferredFromSampleDevicesUSed =
                                    enrichedSampledBiometric
                                            .getSampleDevices()
                                            .stream().
                                            filter(enrichedSampleDevice -> enrichedSampleDevice.getDeviceCategory().getId().equals(oldDeviceCategoryId))
                                            .map(enrichedSampleDevice -> enrichedSampleDevice.getDeviceCategory()).collect(Collectors.toList());


                            logger.debug("\t\t...found {} sample devices assigned to old device category matching to id {}, name {}",
                                    oldEnrichedDeviceCategoriesInferredFromSampleDevicesUSed.size(), oldDeviceCategoryId, oldEnrichedDeviceCategoriesInferredFromSampleDevicesUSed.get(0).getName());


                            if(oldEnrichedDeviceCategoriesInferredFromSampleDevicesUSed.size()==0) {
                                logger.warn("DID NOT FIND ANY SAMPLE DEVICE THAT WAS ASSIGNED TO THE ORIGINAL DEVICE CATEGORY TO GET ITS NAME");
                                artifactImportResult.addImportRemark(new ImportRemark(
                                        "DID NOT FIND FORMER DEVICE CATEGORY FOR MAPPING of DataInput " + dataInputToUpdate + " of biometricSystem " + enrichedBiometricSystemForImport.getName(),
                                        ArtefactType.BIOMETRIC_SYSTEM,
                                        ImportRemarkLevel.CRITICAL
                                ));
                            } else {
                                DeviceCategory deviceCategoryTargetRepository = deviceCategoryCRUDRepository.findFirstByNameEqualsIgnoreCase(
                                        oldEnrichedDeviceCategoriesInferredFromSampleDevicesUSed.get(0).getName());

                                newDeviceCategoryId[0] = deviceCategoryTargetRepository.getId();
                            }

                        });
                    });

                    if(newDeviceCategoryId[0]!=null) {
                        // lets find the full
                        String newDataInput = sensorDimensionIdMapping.get(dataInputToUpdate.split("_")[0])+"_"+newDeviceCategoryId[0];
                        logger.debug("\t replace old data input id {} with {}", dataInputToUpdate, newDataInput);
                        updatedMappingDataInputIds.add(newDataInput);
                    } else {
                        logger.warn("DID NOT FIND FORMER DEVICE CATEGORY FOR MAPPING");
                        artifactImportResult.addImportRemark(new ImportRemark(
                                "DID NOT FIND FORMER DEVICE CATEGORY FOR MAPPING of DataInput " + dataInputToUpdate + " of biometricSystem " + enrichedBiometricSystemForImport.getName(),
                                ArtefactType.BIOMETRIC_SYSTEM,
                                ImportRemarkLevel.CRITICAL
                        ));
                    }


                } else {
                    logger.debug("DataInput {} of biometricSystem {} can not be replaced, sth. went wrong during creation" +
                            "of id mappings, stop import - please inform the developers", dataInputToUpdate, enrichedBiometricSystemForImport.getName());

                    artifactImportResult.addImportRemark(new ImportRemark(
                            "DataInput " + dataInputToUpdate + " of biometricSystem " + enrichedBiometricSystemForImport.getName() + " can not be replaced, sth. went wrong during update of datasets",
                            ArtefactType.BIOMETRIC_SYSTEM,
                            ImportRemarkLevel.WARNING
                    ));

                }
            }

            // set actual data input ids
            biometricSystemReadyForImport.setDataInputIds(updatedMappingDataInputIds);

            if(enrichedBiometricSystemForImport.getFeatures()!=null) {
                // update feature ids
                enrichedBiometricSystemForImport.getFeatures().forEach(featureToImport -> {
                    // find all features in target repository, they have to be there
                    Feature featureInTargetRepository = featureCRUDRepository.findFirstByName(featureToImport.getName());
                    biometricSystemReadyForImport.addFeatureId(featureInTargetRepository.getId());
                });

                logger.debug("\t..update {} feature ids prior to import of biometric system '{}' of base '{}'",
                        enrichedBiometricSystemForImport.getFeatures().size(), enrichedBiometricSystemForImport.getName(),
                        enrichedBiometricSystemForImport.getParentBaseName());
            } else {
                logger.debug("\t...no features to import for biometric system {} of base {}", enrichedBiometricSystemForImport.getName(), enrichedBiometricSystemForImport.getParentBaseName());
            }

            if(enrichedBiometricSystemForImport.getSignalProcessingSteps()!=null) {
                // update signal processing step ids
                enrichedBiometricSystemForImport.getSignalProcessingSteps().forEach(biometricSystemProcessingStepToImport -> {
                    BiometricSystemProcessingStep signalProcessingStepInTargetRepository =
                            biometricProcessingStepCRUDRepository.findFirstByNameAndProcessingType(biometricSystemProcessingStepToImport.getName(),
                                    biometricSystemProcessingStepToImport.getProcessingType().name());
                    biometricSystemReadyForImport.addSignalProcessingStepId(signalProcessingStepInTargetRepository.getId());
                });

                logger.debug("\t..update {} signal processing step ids prior to import of biometric system '{}' of base '{}'",
                        enrichedBiometricSystemForImport.getSignalProcessingSteps().size(), enrichedBiometricSystemForImport.getName(),
                        enrichedBiometricSystemForImport.getParentBaseName());
            } else {
                logger.debug("\t...no signal processing steps to import for biometric system {} of base {}", enrichedBiometricSystemForImport.getName(), enrichedBiometricSystemForImport.getParentBaseName());
            }

            if(enrichedBiometricSystemForImport.getFurtherProcessingSteps()!=null) {
            // update further processing step ids
            enrichedBiometricSystemForImport.getFurtherProcessingSteps().forEach(biometricSystemProcessingStepToImport -> {
                BiometricSystemProcessingStep furtherProcessingStepInTargetRepository =
                        biometricProcessingStepCRUDRepository.findFirstByNameAndProcessingType(biometricSystemProcessingStepToImport.getName(),
                                biometricSystemProcessingStepToImport.getProcessingType().name());
                biometricSystemReadyForImport.addFurtherProcessingStepId(furtherProcessingStepInTargetRepository.getId());
            });


            logger.debug("\t..update {} further processing step ids prior to import of biometric system '{}' of base '{}'",
                    enrichedBiometricSystemForImport.getFurtherProcessingSteps().size(), enrichedBiometricSystemForImport.getName(),
                    enrichedBiometricSystemForImport.getParentBaseName());

            } else {
                logger.debug("\t...no further processing steps to import for biometric system {} of base {}", enrichedBiometricSystemForImport.getName(), enrichedBiometricSystemForImport.getParentBaseName());
            }

            biometricSystemsReadyForImport.add(biometricSystemReadyForImport);
        }

        return biometricSystemsReadyForImport;

    }

    private void importDatasetArtefacts(DatasetImportArtefacts datasetImportArtefacts,
                                                       ArtifactImportResult artifactImportResult,
                                                       boolean onlyDatasets) {

        // save all datasets

        /*
         1. save all devices
         */

        // save all 100% new devices
        importNewSampleDevices(datasetImportArtefacts.getSampleDevicesToImport(), artifactImportResult);


        if(datasetImportArtefacts.getSamplingContextsToImport().size()>0) {
            // import any sampling context
            logger.trace("Import {} sampling contexts", datasetImportArtefacts.getSamplingContextsToImport().size());
            samplingContextCRUDRepository.saveAll(datasetImportArtefacts.getSamplingContextsToImport());
            artifactImportResult.anotherNArtefactsImported(datasetImportArtefacts.getSamplingContextsToImport().size(),
                    ArtefactType.SAMPLING_CONTEXT,
                    ArtefactType.SAMPLING_CONTEXT.getNamePlural());

        }
        /*


        we now have to update the ids within every dataset to update,
        so we need mappings for:
         - biometric characteristics used
         - sampling contexts
         - sample devices
         */

        List<DataSet> dataSetsReadyForImport = new ArrayList<>();

        if (datasetImportArtefacts.getDataSetsToImport().size() > 0) {

            for (EnrichedDataSet dataSetToImport : datasetImportArtefacts.getDataSetsToImport()) {

                DataSet dataSetReadyForImport = new DataSet();
                dataSetReadyForImport.setName(dataSetToImport.getName());
                dataSetReadyForImport.setModelledElementDetail(dataSetToImport.getModelledElementDetail());
                dataSetReadyForImport.setParticipantInformation(dataSetToImport.getParticipantInformation());
                dataSetReadyForImport.setReference(dataSetToImport.getReference());

                logger.debug("\t...update id mappings of sampled biometric for dataset to import {}", dataSetReadyForImport.getName());
                dataSetToImport.getSampledBiometrics().forEach(enrichedSampledBiometricToImport -> {


                    SampledBiometric sampledBiometricReadyForImport = new SampledBiometric();

                    // sampling context
                   enrichedSampledBiometricToImport.getSamplingContexts().forEach(samplingContextToImport -> {
                       SamplingContext samplingContextTargetRepository =
                               samplingContextCRUDRepository.findFirstByDescriptionEqualsIgnoreCase(samplingContextToImport.getDescription());
                       logger.debug("\t\t...add sampling context id {} for sampling context {}", samplingContextTargetRepository.getId(), samplingContextTargetRepository.getDescription());
                       sampledBiometricReadyForImport.addSamplingContextId(samplingContextTargetRepository.getId());
                   });

                   // sampled Biometric
                    BiometricCharacteristic characteristicTargetRepository =
                            biometricCharacteristicCRUDRepository.findFirstByUniqueIdentifier(enrichedSampledBiometricToImport.getSampledCharacteristic().getUniqueIdentifier());

                    logger.debug("\t\t...set sampled biometric characteristic id {} for sampled characteristic unique ID {}",
                            characteristicTargetRepository.getId(),
                            enrichedSampledBiometricToImport.getSampledCharacteristic().getUniqueIdentifier());
                    sampledBiometricReadyForImport.setSampledCharacteristicId(characteristicTargetRepository.getId());

                    // support biometric
                    if(enrichedSampledBiometricToImport.getSupportCharacteristic()!=null) {

                        BiometricCharacteristic supportCharacteristicTargetRepository =
                                biometricCharacteristicCRUDRepository.findFirstByUniqueIdentifier(enrichedSampledBiometricToImport.getSupportCharacteristic().getUniqueIdentifier());

                        logger.debug("\t\t...set support biometric characteristic id {} for support characteristic unique ID {}",
                                supportCharacteristicTargetRepository.getId(),
                                enrichedSampledBiometricToImport.getSupportCharacteristic().getUniqueIdentifier());

                        sampledBiometricReadyForImport.setSupportCharacteristicId(supportCharacteristicTargetRepository.getId());
                    }

                    // sample devices
                    enrichedSampledBiometricToImport.getSampleDevices().forEach(enrichedSampleDevice -> {
                        SampleDevice sampleDeviceTargetRepository =
                                sampleDeviceCRUDRepository.findByNameAndDeviceManufacturer(enrichedSampleDevice.getName(), enrichedSampleDevice.getDeviceManufacturer());
                        sampledBiometricReadyForImport.addSampleDeviceId(sampleDeviceTargetRepository.getId());
                    });

                    dataSetReadyForImport.addSampledBiometric(sampledBiometricReadyForImport);

                });

                dataSetsReadyForImport.add(dataSetReadyForImport);

            }


            // finally, save all Datasets
            dataSetCRUDRepository.saveAll(dataSetsReadyForImport);

            artifactImportResult.anotherNArtefactsImported(datasetImportArtefacts.getDataSetsToImport().size(),
                    ArtefactType.DATASET,
                    ArtefactType.DATASET.getNamePlural());

            if (onlyDatasets) {
                if(datasetImportArtefacts.getBiometricCharacteristicsToImport()!=null) {
                    // we imported only datasets, because of that we also have to import any new behavioral characteristics if they were introduced
                    logger.debug("\t...we imported only datasets, lets import yet unknown {} characteristics from the dataset",
                            datasetImportArtefacts.getBiometricCharacteristicsToImport().size());
                    biometricCharacteristicCRUDRepository.saveAll(datasetImportArtefacts.getBiometricCharacteristicsToImport());
                }
            }
        } else {
            logger.trace("No datasets are imported");
        }

    }

    private void importCriteriaAndResultMetrics(ExperimentSpecificEvaluationCriteriaImportArtefact experimentSpecificEvaluationCriteriaImportArtefact,
                                                ArtifactImportResult artifactImportResult,
                                                List<EnrichedExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteriaToCheckForImportForBase) {


        // first, we persist the evaluation criteria if existing
        if(experimentSpecificEvaluationCriteriaImportArtefact.getExperimentSpecificEvaluationCriteriaToImport().size()>0) {
            logger.debug("\tImport {} new experiment specific evaluation criteria",
                    experimentSpecificEvaluationCriteriaImportArtefact.getExperimentSpecificEvaluationCriteriaToImport().size());
            experimentSpecificEvaluationCriteriaCRUDRepository.saveAll(experimentSpecificEvaluationCriteriaImportArtefact.getExperimentSpecificEvaluationCriteriaToImport());

            artifactImportResult.anotherNArtefactsImported(experimentSpecificEvaluationCriteriaImportArtefact.getExperimentSpecificEvaluationCriteriaToImport().size(),
                    ArtefactType.EVALUATION_CRITERIA,
                    ArtefactType.EVALUATION_CRITERIA.getNamePlural());
        } else {
            logger.debug("\tNO new experiment specific evaluation criteria need an import");
        }

        // we then get all referenced criteria from the result metrics in the imported data to create a mapping for their new id, if they're new
        if(experimentSpecificEvaluationCriteriaImportArtefact.getResultMetricsToImport().size()>0) {

            logger.debug("\tImport {} new result metrics",
                    experimentSpecificEvaluationCriteriaImportArtefact.getResultMetricsToImport().size());

            List<ExperimentSpecificEvaluationCriteria> criteriaToFindNewIdsFor = new ArrayList<>();

            for (ResultMetric resultMetricToImport : experimentSpecificEvaluationCriteriaImportArtefact.getResultMetricsToImport()) {
                for (ExperimentSpecificEvaluationCriteria experimentSpecificEvaluationCriteriaToImport : experimentSpecificEvaluationCriteriaImportArtefact.getExperimentSpecificEvaluationCriteriaToImport()) {
                    if (resultMetricToImport.getParentEvaluationCriteriaId().equalsIgnoreCase(experimentSpecificEvaluationCriteriaToImport.getId())) {
                        logger.trace("Adding criteria {} to list for required ID mappings based on child metric '{}'", experimentSpecificEvaluationCriteriaToImport.getName(), resultMetricToImport.getName());
                        criteriaToFindNewIdsFor.add(experimentSpecificEvaluationCriteriaToImport);
                    }
                }
            }

            Map<String, String> idMappingsExperimentSpecificEvaluationCriteria = new HashMap<>();

            // find new Ids
            for (ExperimentSpecificEvaluationCriteria experimentSpecificEvaluationCriteria : criteriaToFindNewIdsFor) {
                ExperimentSpecificEvaluationCriteria importedCriteria = experimentSpecificEvaluationCriteriaCRUDRepository.findByNameAndCategory(experimentSpecificEvaluationCriteria.getName(),
                        experimentSpecificEvaluationCriteria.getCategory().name());

                logger.trace("\tAdd new Id {} for criteria {} (oldID: {}) to mapping",
                        importedCriteria.getId(),
                        experimentSpecificEvaluationCriteria.getName(),
                        experimentSpecificEvaluationCriteria.getId());
                idMappingsExperimentSpecificEvaluationCriteria.put(experimentSpecificEvaluationCriteria.getId(), importedCriteria.getId());

            }

            List<ResultMetric> resultMetricsToImportWithCorrectParentCriteriaId =
                    experimentSpecificEvaluationCriteriaImportArtefact.getResultMetricsToImport().stream().map(resultMetric -> {

                        // set new parent criteria ID
                        logger.trace("\tupdate parent evaluation criteria id for result metric to import '{}'", resultMetric.getName());

                        // therefor, first look in criteria from this specific base
                        String newParentEvaluationCriteriaId = idMappingsExperimentSpecificEvaluationCriteria.get(resultMetric.getParentEvaluationCriteriaId());
                        if(newParentEvaluationCriteriaId==null) {
                            logger.trace("\t\t...parent criteria for metric '{}' already existed before, find id {} within {} criteria given in import artifacts",
                                    resultMetric.getName(),
                                    resultMetric.getParentEvaluationCriteriaId(),
                                    experimentSpecificEvaluationCriteriaToCheckForImportForBase.size());
                            ExperimentSpecificEvaluationCriteria criteriaInImportArtifact =
                                    experimentSpecificEvaluationCriteriaToCheckForImportForBase.stream().filter(enrichedExperimentSpecificEvaluationCriteria -> {
                                        return enrichedExperimentSpecificEvaluationCriteria.getId().equals(resultMetric.getParentEvaluationCriteriaId());
                                    }).collect(Collectors.toList()).get(0);

                            if(criteriaInImportArtifact!=null) {
                                logger.trace("\t\t\t..found parent criteria {} in import artifacts, look for id in target repository",
                                        criteriaInImportArtifact.getName());

                                ExperimentSpecificEvaluationCriteria criteriaInTargetRepository
                                        = experimentSpecificEvaluationCriteriaCRUDRepository.findByNameAndCategory(
                                                criteriaInImportArtifact.getName(),
                                            criteriaInImportArtifact.getCategory().name());

                                logger.trace("\t\t\t..determined id {} for parent criteria {} in target repository",
                                        criteriaInTargetRepository.getId(), criteriaInTargetRepository.getName());

                                resultMetric.setParentEvaluationCriteriaId(criteriaInTargetRepository.getId());
                            } else {
                                logger.error("DID NOT FIND PARENT CRITERIA, CAN NOT MAP!!");
                                artifactImportResult.addImportRemark(new ImportRemark(
                                        "Did not find parent criteria for result metric "+resultMetric.getName(),
                                        ArtefactType.RESULT_METRIC,
                                        ImportRemarkLevel.CRITICAL
                                ));
                            }
                        } else {
                            resultMetric.setParentEvaluationCriteriaId(newParentEvaluationCriteriaId);
                        }


                        return resultMetric;

                    }).collect(Collectors.toList());

            resultMetricsToImportWithCorrectParentCriteriaId.forEach(resultMetric -> {
                logger.trace("\t\t persisting metric '{}' with parent criteria id '{}'", resultMetric.getName(), resultMetric.getParentEvaluationCriteriaId());
            });

            logger.debug("\t\t\t..result metrics to import: {}, result metrics with available id mapping: {}",
                    experimentSpecificEvaluationCriteriaImportArtefact.getResultMetricsToImport().size(),
                    resultMetricsToImportWithCorrectParentCriteriaId.size());

            // persist result metrics with updated referenced Ids
            resultMetricCRUDRepository.saveAll(resultMetricsToImportWithCorrectParentCriteriaId);
            artifactImportResult.anotherNArtefactsImported(resultMetricsToImportWithCorrectParentCriteriaId.size(),
                    ArtefactType.RESULT_METRIC,
                    ArtefactType.RESULT_METRIC.getNamePlural());

        } else {
            logger.debug("\tNO new result metrics need an import");
        }

    }

    private void updateBiometricSystemIdMappings(EnrichedBiometricAuthenticationSystemAndEvaluation enrichedBase,
                                                 BiometricAuthenticationSystemAndEvaluation baseToFinallyImport,
                                                 Map<String, String> biometricSystemReferencesToReplace) {

        /*
         in a base several references will be saved by id:
         -> any system defined in experimental evaluations
         -> any system used for specific result
         */



        // now: replace all references in base to import, generally defined biometric systems

        logger.debug("...replace biometric system ids for base {}...", baseToFinallyImport.getName());
        baseToFinallyImport
                .getBaseEvaluation()
                .getExperimentSpecificEvaluation().setBiometricSystemIds(
                        enrichedBase
                        .getBaseEvaluation()
                        .getExperimentSpecificEvaluation().getBiometricSystems()
                        .stream()
                        .map(enrichedBiometricSystem -> biometricSystemReferencesToReplace.get(enrichedBiometricSystem.getId()))
                        .collect(Collectors.toList())
        );
        logger.debug("\t...replaced biometric system ids from base {} experimental evaluation...to {}",
                baseToFinallyImport.getName(), String.join(",", baseToFinallyImport
                        .getBaseEvaluation()
                        .getExperimentSpecificEvaluation().getBiometricSystemIds()));

        // replace all references in base to import, per experiment specific result
        for (ExperimentSpecificEvaluationSetup experimentSpecificEvaluationSetup
                : baseToFinallyImport.getBaseEvaluation().getExperimentSpecificEvaluation().getExperimentSpecificEvaluationSetups()) {

            experimentSpecificEvaluationSetup.setBiometricSystemIds(
                    experimentSpecificEvaluationSetup.getBiometricSystemIds()
                            .stream()
                            .peek(enrichedBiometricSystemId -> biometricSystemReferencesToReplace.get(enrichedBiometricSystemId))
                            .collect(Collectors.toList())
            );

            logger.debug("\t...replaced biometric system ids from base {} experimental evaluation result...to {}",
                    baseToFinallyImport.getName(), String.join(",",experimentSpecificEvaluationSetup.getBiometricSystemIds()));


        }




    }

    private void updateDatasetIdMappings(EnrichedBiometricAuthenticationSystemAndEvaluation enrichedBaseToImport,
                                         BiometricAuthenticationSystemAndEvaluation baseToFinallyImport,
                                         ArtifactImportResult artifactImportResult) {

        // prepare remapping of dataset references
        Map<String, String> datasetReferencesToReplace = new HashMap<>();

        for (EnrichedDataSet datasetImportedOrExistingBefore : enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets()) {

            String idOfDatasetReferenceThatNeedToBeReplacedInBase = datasetImportedOrExistingBefore.getId();
            // look for new Id
            String idToReplaceWith = dataSetCRUDRepository.findByName(datasetImportedOrExistingBefore.getName()).getId();
            datasetReferencesToReplace.put(idOfDatasetReferenceThatNeedToBeReplacedInBase,
                    idToReplaceWith);
        }

        // crosscheck -> we should have found mappings for all datasets given for this base

        if (enrichedBaseToImport.getBaseEvaluation().getExperimentSpecificEvaluation().getUsedDatasets().size() != datasetReferencesToReplace.size()) {
            logger.trace("did NOT find dataset mappings for all datasets to import");
            artifactImportResult.addImportRemark(new ImportRemark(
                    "Did not find dataset references for all datasets provided",
                    ArtefactType.DATASET,
                    ImportRemarkLevel.CRITICAL
            ));
            return;
        } else {
            logger.trace("Found dataset mappings for all datasets to import");
        }

        // replace dataset references, first for list of all used datasets

        baseToFinallyImport
                .getBaseEvaluation()
                .getExperimentSpecificEvaluation().setUsedDatasetIds(
                    baseToFinallyImport
                        .getBaseEvaluation()
                        .getExperimentSpecificEvaluation().getUsedDatasetIds()
                        .stream()
                        .map(datasetReferencesToReplace::get)
                        .collect(Collectors.toList())
        );

    }


    List<ImplementationSpecificEvaluationCriteria> checkImplementationSpecificCriteriaToImport(ArtifactImportResult artifactImportResult, List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteriasToCheckForImport) {

        // check all criteria to import if they already exist by name and category

        List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteriaToImport = new ArrayList<>();

        List<ImplementationSpecificEvaluationCriteria> existingImplementationSpecificEvaluationCriterias =
                implementationSpecificEvaluationCriteriaCRUDRepository.findAll();

        if(implementationSpecificEvaluationCriteriasToCheckForImport!=null) {
            for (ImplementationSpecificEvaluationCriteria implementationSpecificEvaluationCriteriaToCheckForImport : implementationSpecificEvaluationCriteriasToCheckForImport) {

                boolean implementationSpecificCriteriaExists = false;

                for (ImplementationSpecificEvaluationCriteria existingImplementationSpecificEvaluationCriteria : existingImplementationSpecificEvaluationCriterias) {
                    if (existingImplementationSpecificEvaluationCriteria.getName().equalsIgnoreCase(implementationSpecificEvaluationCriteriaToCheckForImport.getName())
                            && existingImplementationSpecificEvaluationCriteria.getCategory().name().equalsIgnoreCase(implementationSpecificEvaluationCriteriaToCheckForImport.getCategory().name())) {

                        // they have the same name and category, check for deep match

                        logger.trace("Criteria to import '{}' already exists by its name and category '{}'",
                                existingImplementationSpecificEvaluationCriteria.getName(),
                                existingImplementationSpecificEvaluationCriteria.getCategory().name());

                        implementationSpecificCriteriaExists = true;

                        if (existingImplementationSpecificEvaluationCriteria.equals(implementationSpecificEvaluationCriteriaToCheckForImport)) {
                            // deep match, criteria can be skipped
                            logger.trace("Criteria {} already exists for 100% and is thus skipped", implementationSpecificEvaluationCriteriaToCheckForImport.getName());
                            artifactImportResult.addImportRemark(new ImportRemark(
                                    "Criteria " + implementationSpecificEvaluationCriteriaToCheckForImport.getName() + " already exists and is thus skipped",
                                    ArtefactType.EVALUATION_CRITERIA,
                                    ImportRemarkLevel.INFO
                            ));
                        } else {

                            //check description
                            if (!implementationSpecificEvaluationCriteriaToCheckForImport.getDescription().equalsIgnoreCase(
                                    existingImplementationSpecificEvaluationCriteria.getDescription())) {
                                artifactImportResult.addImportRemark(new ImportRemark(
                                        "Description for existing implementation specific evaluation criteria " + existingImplementationSpecificEvaluationCriteria.getName() + " is different compared to the imported ones",
                                        ArtefactType.EVALUATION_CRITERIA,
                                        ImportRemarkLevel.WARNING
                                ));
                            }

                            //granting rules
                            if (!implementationSpecificEvaluationCriteriaToCheckForImport.getGrantedRule().getRule().equalsIgnoreCase(
                                    existingImplementationSpecificEvaluationCriteria.getGrantedRule().getRule())
                                || !implementationSpecificEvaluationCriteriaToCheckForImport.getQuasiGrantedRule().getRule().equalsIgnoreCase(
                                    existingImplementationSpecificEvaluationCriteria.getQuasiGrantedRule().getRule())
                                || !implementationSpecificEvaluationCriteriaToCheckForImport.getNotGrantedRule().getRule().equalsIgnoreCase(
                                            existingImplementationSpecificEvaluationCriteria.getNotGrantedRule().getRule())) {
                                artifactImportResult.addImportRemark(new ImportRemark(
                                        "Granting rules for existing implementation specific evaluation criteria " + existingImplementationSpecificEvaluationCriteria.getName() + " is different compared to the imported ones",
                                        ArtefactType.EVALUATION_CRITERIA,
                                        ImportRemarkLevel.WARNING
                                ));
                            }
                        }

                        //in any way, we can break the loop here
                        break;

                    }

                }


                if (!implementationSpecificCriteriaExists) {
                    logger.trace("Marking criteria {} for import", implementationSpecificEvaluationCriteriaToCheckForImport.getName());
                    artifactImportResult.addImportRemark(new ImportRemark(
                            "Marking " + implementationSpecificEvaluationCriteriaToCheckForImport.getName() + " for import",
                            ArtefactType.EVALUATION_CRITERIA,
                            ImportRemarkLevel.INFO
                    ));

                    implementationSpecificEvaluationCriteriaToImport.add(implementationSpecificEvaluationCriteriaToCheckForImport);

                }

            }
        }
        return implementationSpecificEvaluationCriteriaToImport;
    }



    ExperimentSpecificEvaluationCriteriaImportArtefact checkExperimentSpecificCriteriaToImport(ArtifactImportResult artifactImportResult, List<EnrichedExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteriasToCheckForImport) {

        List<ExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteriaToImport = new ArrayList<>();
        List<ResultMetric> resultMetricsToImport = new ArrayList<>();

        List<EnrichedExperimentSpecificEvaluationCriteria> existingExperimentSpecificCriteria =
                experimentSpecificEvaluationCriteriaCRUDRepository.findAll()
                        .stream()
                        .map(experimentSpecificEvaluationCriteria ->
                                experimentSpecificEvaluationCriteriaService
                                        .enrichExperimentSpecificEvaluationCriteria(experimentSpecificEvaluationCriteria))
                        .collect(Collectors.toList());

        logger.debug("...check {} experiment specific criteria for import based on {} existing",
                experimentSpecificEvaluationCriteriasToCheckForImport.size(), existingExperimentSpecificCriteria.size());

        for (EnrichedExperimentSpecificEvaluationCriteria enrichedExperimentSpecificEvaluationCriteriaToCheckForImport : experimentSpecificEvaluationCriteriasToCheckForImport) {

            boolean experimentSpecificCriteriaExistsByNameAndCategory = false;
            for (EnrichedExperimentSpecificEvaluationCriteria existingExperimentSpecificEvaluationCriteria : existingExperimentSpecificCriteria) {

                logger.debug("...\tcomparing existing criteria '{}' (cat: '{}') with potential new criteria '{}' (cat: '{}')",
                        existingExperimentSpecificEvaluationCriteria.getName(),
                        existingExperimentSpecificEvaluationCriteria.getCategory().name(),
                        enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getName(),
                        enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getCategory().name());

                if (existingExperimentSpecificEvaluationCriteria.getName().equalsIgnoreCase(enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getName())
                        && existingExperimentSpecificEvaluationCriteria.getCategory().name().equalsIgnoreCase(enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getCategory().name())) {
                    // they have the same name and category, check for deep match

                    experimentSpecificCriteriaExistsByNameAndCategory = true;

                    boolean descriptionIsSimilar = true;
                    boolean grantingRuleIsSimilar = true;
                    boolean notGrantingRuleIsSimilar = true;
                    boolean quasiGrantingRuleIsSimilar = true;
                    boolean resultMetricsAreSimilar = true;

                    if(!existingExperimentSpecificEvaluationCriteria.getDescription().equalsIgnoreCase(
                            enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getDescription())) {
                        descriptionIsSimilar = false;
                    }

                    if(!existingExperimentSpecificEvaluationCriteria.getGrantedRule().getRule().equalsIgnoreCase(
                            enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getGrantedRule().getRule())) {
                        grantingRuleIsSimilar = false;
                    }
                    if(!existingExperimentSpecificEvaluationCriteria.getNotGrantedRule().getRule().equalsIgnoreCase(
                            enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getNotGrantedRule().getRule())) {
                        notGrantingRuleIsSimilar = false;
                    }
                    if(!existingExperimentSpecificEvaluationCriteria.getQuasiGrantedRule().getRule().equalsIgnoreCase(
                            enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getQuasiGrantedRule().getRule())) {
                        quasiGrantingRuleIsSimilar = false;
                    }


                    for (int i = 0; i < existingExperimentSpecificEvaluationCriteria.getResultMetrics().size(); i++) {
                        if(!existingExperimentSpecificEvaluationCriteria.getResultMetrics().get(i)
                                .equals(enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getResultMetrics().get(i))) {
                            resultMetricsAreSimilar = false;
                        }
                    }

                    if (descriptionIsSimilar
                        && grantingRuleIsSimilar
                        && notGrantingRuleIsSimilar
                        && quasiGrantingRuleIsSimilar
                        && resultMetricsAreSimilar) {
                        // deep match, criteria can be skipped
                        logger.debug("\t\t...criteria {} already exists for 100% and is thus skipped", enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getName());
                        artifactImportResult.addImportRemark(new ImportRemark(
                                "Criteria " + enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getName() + " already exists and is thus skipped",
                                ArtefactType.EVALUATION_CRITERIA,
                                ImportRemarkLevel.INFO
                        ));
                        logger.trace("set respective id for criteria so all refer to the same");
                    } else {
                        /*
                         sth. is different: description, granting rules or result metrics
                         lets first check for result metrics:
                            no problem until we find a metric with same name but different unit
                            we will only give warnings for different descriptions / granting rules */

                        logger.trace("\t\t...criteria {} exists by name and same category but has differences, checking result metrics", enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getName());

                        artifactImportResult.addImportRemark(new ImportRemark(
                                "Criteria " + enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getName() + " already exists by name and category and is thus skipped",
                                ArtefactType.EVALUATION_CRITERIA,
                                ImportRemarkLevel.INFO
                        ));

                        if(enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getResultMetrics()!=null) {
                            for (ResultMetric resultMetricToCheckForImport : enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getResultMetrics()) {
                                boolean resultMetricAlreadyExists = false;

                                for (ResultMetric existingResultMetric : existingExperimentSpecificEvaluationCriteria.getResultMetrics()) {
                                    if (resultMetricToCheckForImport.getName().equalsIgnoreCase(existingResultMetric.getName())) {

                                        resultMetricAlreadyExists = true;

                                        if (resultMetricToCheckForImport.getUnit().name().equalsIgnoreCase(existingResultMetric.getUnit().name())) {

                                            logger.trace("Result metric {} (unit: {}) already exists and is thus skipped", resultMetricToCheckForImport.getName(), resultMetricToCheckForImport.getUnit());
                                            artifactImportResult.addImportRemark(new ImportRemark(
                                                    "Result metric " + resultMetricToCheckForImport.getName() + " (unit: " + resultMetricToCheckForImport.getUnit() + ") already exists and is thus skipped",
                                                    ArtefactType.RESULT_METRIC,
                                                    ImportRemarkLevel.INFO
                                            ));

                                            // check for description and add potential warning

                                            if (!resultMetricToCheckForImport.getDescription().equalsIgnoreCase(existingResultMetric.getDescription())) {
                                                artifactImportResult.addImportRemark(new ImportRemark(
                                                        "Description for existing result metric " + resultMetricToCheckForImport.getName() + " is different compared to the imported ones",
                                                        ArtefactType.RESULT_METRIC,
                                                        ImportRemarkLevel.WARNING
                                                ));
                                            }
                                        } else {
                                            // result metric has different unit, we will skip import here
                                            artifactImportResult.addImportRemark(new ImportRemark(
                                                    "Existing result metric " + existingResultMetric.getName()
                                                            + " has different unit '"
                                                            + existingResultMetric.getUnit().name()
                                                            + "' compared to the imported one (" + resultMetricToCheckForImport.getUnit().name() + ")",
                                                    ArtefactType.RESULT_METRIC,
                                                    ImportRemarkLevel.CRITICAL
                                            ));
                                        }

                                        // in any case, we can break this loop here
                                        break;
                                    }
                                }

                                if (!resultMetricAlreadyExists) {
                                    logger.trace("\t result metric {} is marked for import", resultMetricToCheckForImport.getName());
                                    artifactImportResult.addImportRemark(new ImportRemark(
                                            "Result metric " + resultMetricToCheckForImport.getName() + " is marked for import",
                                            ArtefactType.RESULT_METRIC,
                                            ImportRemarkLevel.INFO
                                    ));

                                    // only result metric is new, but not parent criteria. Update parent criteria id
                                    logger.trace("\t\t..refresh parent criteria id from '{}' to '{}' for result metric '{}' as parent criteria '{}' already exists",
                                            resultMetricToCheckForImport.getParentEvaluationCriteriaId(), existingExperimentSpecificEvaluationCriteria.getId(),
                                            resultMetricToCheckForImport.getName(), existingExperimentSpecificEvaluationCriteria.getName());
                                    resultMetricToCheckForImport.setParentEvaluationCriteriaId(existingExperimentSpecificEvaluationCriteria.getId());
                                    resultMetricsToImport.add(resultMetricToCheckForImport);
                                }

                            }
                        }

                        if (artifactImportResult.containsCriticalRemark()) {
                            artifactImportResult.addImportRemark(new ImportRemark(
                                    "A result metric to import has a different unit compared to an existing one for the same criteria and category",
                                    ArtefactType.RESULT_METRIC,
                                    ImportRemarkLevel.INFO
                            ));
                        }

                        //check description / granting rules
                        if (!enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getDescription().equalsIgnoreCase(
                                existingExperimentSpecificEvaluationCriteria.getDescription())) {
                            artifactImportResult.addImportRemark(new ImportRemark(
                                    "Description for existing experiment specific evaluation criteria " + existingExperimentSpecificEvaluationCriteria.getName() + " is different compared to the imported ones",
                                    ArtefactType.EVALUATION_CRITERIA,
                                    ImportRemarkLevel.WARNING
                            ));
                        }

                        //check description / granting rules
                        if (!enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getGrantedRule().getRule().equalsIgnoreCase(
                                existingExperimentSpecificEvaluationCriteria.getGrantedRule().getRule())
                            || !enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getQuasiGrantedRule().getRule().equalsIgnoreCase(
                                existingExperimentSpecificEvaluationCriteria.getQuasiGrantedRule().getRule())
                            || !enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getNotGrantedRule().getRule().equalsIgnoreCase(
                                existingExperimentSpecificEvaluationCriteria.getNotGrantedRule().getRule())) {
                            artifactImportResult.addImportRemark(new ImportRemark(
                                    "Granting rules for existing experiment specific evaluation criteria " + existingExperimentSpecificEvaluationCriteria.getName() + " is different compared to the imported ones",
                                    ArtefactType.EVALUATION_CRITERIA,
                                    ImportRemarkLevel.WARNING
                            ));
                        }

                    }

                    // in any case, we can break the loop here
                    break;
                }

            }

            if (!experimentSpecificCriteriaExistsByNameAndCategory) {
                logger.trace("\t criteria {} is marked for import as it is new", enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getName());
                artifactImportResult.addImportRemark(new ImportRemark(
                        "criteria " + enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getName() + " is marked for import",
                        ArtefactType.EVALUATION_CRITERIA,
                        ImportRemarkLevel.INFO
                ));
                // set type
                ExperimentSpecificEvaluationCriteria experimentSpecificEvaluationCriteriaToActuallyImport
                        = experimentSpecificEvaluationCriteriaService.strapDownEnrichedCriteria(enrichedExperimentSpecificEvaluationCriteriaToCheckForImport);
                experimentSpecificEvaluationCriteriaToActuallyImport.setId(null);
                experimentSpecificEvaluationCriteriaToImport.add(experimentSpecificEvaluationCriteriaToActuallyImport);

                if(enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getResultMetricIds()!=null) {
                    logger.trace("\t\t {} result metrics of criteria are marked for import, too since new",
                            enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getResultMetricIds().size());
                    for (ResultMetric resultMetricOfNewCriteria : enrichedExperimentSpecificEvaluationCriteriaToCheckForImport.getResultMetrics()) {
                        artifactImportResult.addImportRemark(new ImportRemark(
                                "result metric " + resultMetricOfNewCriteria.getName() + " is marked for import",
                                ArtefactType.RESULT_METRIC,
                                ImportRemarkLevel.INFO
                        ));
                        resultMetricsToImport.add(resultMetricOfNewCriteria);
                    }
                }


            }

        }

        return new ExperimentSpecificEvaluationCriteriaImportArtefact(
                experimentSpecificEvaluationCriteriaToImport,
                resultMetricsToImport
        );

    }


    public void importNewSampleDevices(List<EnrichedSampleDevice> sampleDevicesToImport,
                                                      ArtifactImportResult artifactImportResult) {

        logger.debug("...importing {} sample devices", sampleDevicesToImport.size());

        sampleDevicesToImport.forEach(sampleDeviceToImport -> {

            logger.trace("\t...importing new sample device {} {} (category {}) to DB ", sampleDeviceToImport.getDeviceManufacturer(), sampleDeviceToImport.getName(),
                    sampleDeviceToImport.getDeviceCategory().getName());

            SampleDevice sampleDeviceReadyForImport = new SampleDevice();
            sampleDeviceReadyForImport.setDeviceManufacturer(sampleDeviceToImport.getDeviceManufacturer());
            sampleDeviceReadyForImport.setName(sampleDeviceToImport.getName());
            sampleDeviceReadyForImport.setModelledElementDetail(sampleDeviceToImport.getModelledElementDetail());

            // replace device category id
            DeviceCategory sampleDeviceCategoryTargetRepository =
                    deviceCategoryCRUDRepository.findFirstByNameEqualsIgnoreCase(sampleDeviceToImport.getDeviceCategory().getName());

            sampleDeviceReadyForImport.setDeviceCategoryId(sampleDeviceCategoryTargetRepository.getId());
            SampleDevice savedSampleDevice = sampleDeviceCRUDRepository.save(sampleDeviceReadyForImport);
            logger.trace("\t\t..imported sample device: {}", savedSampleDevice);

        });


        artifactImportResult.anotherNArtefactsImported(sampleDevicesToImport.size(),
                ArtefactType.SAMPLE_DEVICE,
                ArtefactType.SAMPLE_DEVICE.getNamePlural());

    }

    List<BiometricSystemProcessingStep> checkProcessingStepsForImport(ArtifactImportResult artifactImportResult, List<EnrichedBiometricSystem> biometricSystems) {

        logger.debug("...check {} biometric systems for processing steps to import", biometricSystems.size());
        List<BiometricSystemProcessingStep> definedProcessingStepsToCheck = new ArrayList<>();
        List<BiometricSystemProcessingStep> processingStepsToImport = new ArrayList<>();

        for (EnrichedBiometricSystem biometricSystemToImport : biometricSystems) {
            if(biometricSystemToImport.getSignalProcessingSteps()!=null) {
                logger.debug("\t...check {} signal processing steps from biometric system {}", biometricSystemToImport.getSignalProcessingSteps().size(), biometricSystemToImport.getName());
                definedProcessingStepsToCheck.addAll(biometricSystemToImport.getSignalProcessingSteps());
            }
            if(biometricSystemToImport.getFurtherProcessingSteps()!=null) {
                logger.debug("\t...check {} further processing steps from biometric system {}", biometricSystemToImport.getFurtherProcessingSteps().size(), biometricSystemToImport.getName());
                definedProcessingStepsToCheck.addAll(biometricSystemToImport.getFurtherProcessingSteps());
            }
        }

        // filter duplicates

        definedProcessingStepsToCheck =
                definedProcessingStepsToCheck
                        .stream()
                        .filter(distinctByKey(processingStep -> processingStep.getName()+"_"+processingStep.getProcessingType()))
                        .collect(Collectors.toList());

        logger.debug("\t...overall, {} unique processing steps were defined in the base to import", definedProcessingStepsToCheck.size());
        for (BiometricSystemProcessingStep stepToCheckForImport : definedProcessingStepsToCheck) {
            logger.trace("\t\t... name: {}, type: {}", stepToCheckForImport.getName(), stepToCheckForImport.getProcessingType().name());
        }


        List<BiometricSystemProcessingStep> existingProcessingSteps = biometricProcessingStepCRUDRepository.findAll();

        for (BiometricSystemProcessingStep biometricSystemProcessingStepToPossiblyImport : definedProcessingStepsToCheck) {

            boolean biometricSystemProcessingStepCanBeImported = true;

            for (BiometricSystemProcessingStep existingBiometricSystemProcessingStep : existingProcessingSteps) {

                if (biometricSystemProcessingStepToPossiblyImport.getName()
                        .equalsIgnoreCase(existingBiometricSystemProcessingStep.getName())) {

                    // same name, check for processing type


                    if (biometricSystemProcessingStepToPossiblyImport.getProcessingType().name()
                            .equals(existingBiometricSystemProcessingStep.getProcessingType().name())) {

                        // deeply similar from name and type, do specific casting
                        boolean processingStepsAreDeeplyEqual = true;
                        String specialDifferenceReason = "";

                        if(biometricSystemProcessingStepToPossiblyImport.getProcessingType().equals(ProcessingType.DECISION)) {

                            DecisionProcessingStep existingDecisionStep = (DecisionProcessingStep) existingBiometricSystemProcessingStep;
                            DecisionProcessingStep stepToPotentiallyImport = (DecisionProcessingStep) biometricSystemProcessingStepToPossiblyImport;

                            if(existingDecisionStep.isDeepLearning() != stepToPotentiallyImport.isDeepLearning()
                                || !existingDecisionStep.getDecisionMode().equals(stepToPotentiallyImport.getDecisionMode())) {
                                logger.debug("\t\t...decision step '{}' does not fully equal existing decision step with that name",
                                        stepToPotentiallyImport.getName());
                                processingStepsAreDeeplyEqual = false;
                                specialDifferenceReason = "decision mode";
                            }

                        }

                        if(biometricSystemProcessingStepToPossiblyImport.getProcessingType().equals(ProcessingType.MATCHING)) {

                            MachineLearningProcessingStep existingMlStep = (MachineLearningProcessingStep) existingBiometricSystemProcessingStep;
                            MachineLearningProcessingStep stepToPotentiallyImport = (MachineLearningProcessingStep) biometricSystemProcessingStepToPossiblyImport;

                            if(existingMlStep.isDeepLearning() != stepToPotentiallyImport.isDeepLearning()) {
                                logger.debug("\t\t...ML step '{}' does not fully equal existing decision step with that name",
                                        stepToPotentiallyImport.getName());
                                processingStepsAreDeeplyEqual = false;
                                specialDifferenceReason = "Deep learning vs. shallow machine learning";
                            }

                        }

                        if(processingStepsAreDeeplyEqual) {
                            // we can skip this processing step, as it already exists in the repository
                            logger.trace("Step with name '{}' will not be imported as its exists by name and type '{}'",
                                    biometricSystemProcessingStepToPossiblyImport.getName(),
                                    biometricSystemProcessingStepToPossiblyImport.getProcessingType().name());
                            artifactImportResult.addImportRemark(new ImportRemark(
                                    biometricSystemProcessingStepToPossiblyImport.getName() + " will not be imported as it already exists",
                                    ArtefactType.PROCESSING_STEP,
                                    ImportRemarkLevel.INFO
                            ));

                            biometricSystemProcessingStepCanBeImported = false;
                        } else {
                            logger.trace("Step with name '{}' and type '{}' will be imported although it exists by name and type but has different specialities (DL / decision mode)",
                                    biometricSystemProcessingStepToPossiblyImport.getName(),
                                    biometricSystemProcessingStepToPossiblyImport.getProcessingType().name());

                            artifactImportResult.addImportRemark(new ImportRemark(
                                    biometricSystemProcessingStepToPossiblyImport.getName() + " will be imported as its name and type " +
                                            biometricSystemProcessingStepToPossiblyImport.getProcessingType().name() + " already exists " +
                                            " but speciality ("
                                            + specialDifferenceReason + ") is different",
                                    ArtefactType.PROCESSING_STEP,
                                    ImportRemarkLevel.WARNING
                            ));
                        }



                    } else {
                        // processing Type is different, set warning
                        logger.trace("Step with name '{}' will be imported although it exists by name but has a different type '{}' vs '{}'",
                                biometricSystemProcessingStepToPossiblyImport.getName(),
                                biometricSystemProcessingStepToPossiblyImport.getProcessingType().name(),
                                existingBiometricSystemProcessingStep.getProcessingType().name());

                        artifactImportResult.addImportRemark(new ImportRemark(
                                existingBiometricSystemProcessingStep.getName() + " will be imported as its name already exists," +
                                        "but type (" + biometricSystemProcessingStepToPossiblyImport.getProcessingType().name() + " vs. "
                                        + existingBiometricSystemProcessingStep.getProcessingType().name() + ") is different",
                                ArtefactType.PROCESSING_STEP,
                                ImportRemarkLevel.WARNING
                        ));
                    }


                    // in any way, we can break the for loop now
                    break;
                }


            }

            if (biometricSystemProcessingStepCanBeImported) {
                processingStepsToImport.add(biometricSystemProcessingStepToPossiblyImport);
            }
        }

        logger.trace("After intersection with existing by name, {} processing steps will be imported", processingStepsToImport.size());

        return processingStepsToImport;

    }

    DatasetImportArtefacts checkDatasetsForImport(ArtifactImportResult artifactImportResult,
                                                  List<EnrichedDataSet> datasetsToCheckForImport,
                                                  boolean analyzeSupportCharacteristics) {


        // behavioral biometric characteristics of sampled behaviour will be done initially with main characteristics

        logger.debug("...determine dataset import artifacts based on {} datasets", datasetsToCheckForImport);

        DatasetImportArtefacts datasetImportArtefacts = new DatasetImportArtefacts();

        List<EnrichedDataSet> datasetsToImport = new ArrayList<>();
        List<EnrichedDataSet> enrichedExistingDataSets = dataSetCRUDRepository.findAll()
                .stream()
                .map(dataSet ->
                        dataSetService.enrichDataset(dataSet))
                .collect(Collectors.toList());

        List<BiometricCharacteristic> biometricCharacteristicsToImport = new ArrayList<>();

        for (EnrichedDataSet dataSetToCheckForImport : datasetsToCheckForImport) {

            boolean dataSetAlreadyExists = false;

            logger.debug("\t...check if dataset {} already exists", dataSetToCheckForImport.getName());

            for (EnrichedDataSet enrichedExistingDataset : enrichedExistingDataSets) {

                if (enrichedExistingDataset.getName().
                        equalsIgnoreCase(dataSetToCheckForImport.getName())) {

                    logger.debug("\t\t...there exists already a dataset with name {}", enrichedExistingDataset.getName());
                    dataSetAlreadyExists = true;

                    boolean participantInformationSimilar = true;
                    final boolean[] sampledBiometricsSimilar = {true};

                    if(!enrichedExistingDataset.getParticipantInformation().equals(dataSetToCheckForImport.getParticipantInformation())) {
                        participantInformationSimilar = false;
                        logger.debug("\t\t\t...participant information are not similar");
                    }

                    final int[] sampledBiometricIndex = {0};

                    enrichedExistingDataset.getSampledBiometrics().forEach(enrichedSampledBiometric -> {

                        // check sampling contexts
                        boolean samplingContextsSimilar = true;
                        Set<String> samplingContextsExisting = enrichedSampledBiometric.getSamplingContexts().stream().map(
                                samplingContext -> samplingContext.getDescription()
                        ).collect(Collectors.toSet());
                        Set<String> samplingContextsToImport =
                                dataSetToCheckForImport.getSampledBiometrics().get(sampledBiometricIndex[0]).getSamplingContexts().stream().map(
                                samplingContext -> samplingContext.getDescription()
                        ).collect(Collectors.toSet());
                        if(!samplingContextsExisting.equals(samplingContextsToImport)) {
                            logger.debug("\t\t\t...sampling contexts of sampled biometric {} are not similar", sampledBiometricIndex[0]);
                            samplingContextsSimilar = false;
                        }

                        if(!samplingContextsSimilar) {
                            sampledBiometricsSimilar[0] = false;
                        }

                        // check support and main characteristic
                        if(!enrichedSampledBiometric.getSampledCharacteristic().equals(
                                dataSetToCheckForImport.getSampledBiometrics().get(sampledBiometricIndex[0]).getSampledCharacteristic())
                            || (enrichedSampledBiometric.getSupportCharacteristic()!=null && enrichedSampledBiometric.getSupportCharacteristic().equals(
                                dataSetToCheckForImport.getSampledBiometrics().get(sampledBiometricIndex[0]).getSupportCharacteristic()))) {
                            logger.debug("\t\t\t...main or support characteristic of sampled biometric {} are not similar", sampledBiometricIndex[0]);
                            sampledBiometricsSimilar[0] = false;
                        }

                        // check sample devices
                        final boolean[] samplingDevicesSimilar = {true};
                        final int[] sampleDeviceIndex = {0};

                        enrichedSampledBiometric.getSampleDevices().forEach(sampleDevice -> {
                            if(!sampleDevice.getDeviceManufacturer().equalsIgnoreCase(
                                    dataSetToCheckForImport.getSampledBiometrics().get(sampledBiometricIndex[0]).getSampleDevices().get(sampleDeviceIndex[0]).getDeviceManufacturer())
                                    || !sampleDevice.getName().equalsIgnoreCase(
                                    dataSetToCheckForImport.getSampledBiometrics().get(sampledBiometricIndex[0]).getSampleDevices().get(sampleDeviceIndex[0]).getName())
                                    || !sampleDevice.getDeviceCategory().getName().equalsIgnoreCase(
                                    dataSetToCheckForImport.getSampledBiometrics().get(sampledBiometricIndex[0]).getSampleDevices().get(sampleDeviceIndex[0]).getDeviceCategory().getName())
                            ) {
                                logger.debug("\t\t\t...sampling device at index {} of sampled biometric {} are not similar",
                                        sampleDeviceIndex[0],
                                        sampledBiometricIndex[0]);
                                samplingDevicesSimilar[0] = false;
                            }

                            sampleDeviceIndex[0]++;
                        });

                        if(!samplingDevicesSimilar[0]) {
                            sampledBiometricsSimilar[0] = false;
                        }

                        sampledBiometricIndex[0]++;

                    });

                    if (!participantInformationSimilar
                        || !sampledBiometricsSimilar[0]) {

                        // same name, but partial differences, skip import of parent base
                        logger.trace("\t ... but it does not equal the new one to 100%, skip overall base import");
                        artifactImportResult.addImportRemark(new ImportRemark(
                                "Dataset " + dataSetToCheckForImport.getName() + " already exists under that name but is partially different",
                                ArtefactType.DATASET,
                                ImportRemarkLevel.CRITICAL
                        ));

                    } else {
                        // totally similar, just skip this dataset as it already exists
                        logger.trace("\t ... and it is 100% equal so we skip the import but continue base import");
                        artifactImportResult.addImportRemark(new ImportRemark(
                                "Dataset " + dataSetToCheckForImport.getName() + " already exists and is thus skipped for import",
                                ArtefactType.DATASET,
                                ImportRemarkLevel.INFO
                        ));
                    }

                    // we can break this for loop for now as we found a respective similar dataset
                    break;

                }
            }

            // if dataset has not been found on the server side and components are ok as well => add for import
            if (!dataSetAlreadyExists) {

                // mark sample devices and sampling contexts for import

                List<SamplingContext> evaluationContextsToImport = new ArrayList<>();

                for (EnrichedSampledBiometric sampledBiometric : dataSetToCheckForImport.getSampledBiometrics()) {

                    // check regarding sample devices

                    DatasetImportArtefacts sampleDeviceImportArtefacts = checkSampleDevicesForImport(
                            artifactImportResult,
                            sampledBiometric.getSampleDevices(),
                            datasetImportArtefacts.getSampleDevicesToImport()
                    );


                    if (artifactImportResult.containsCriticalRemark()) {
                        //break loop for sampled behaviours and stop import of overall base
                        break;
                    }

                    // no critical remark exists, we can continue import

                    datasetImportArtefacts.addSampleDevicesToImport(sampleDeviceImportArtefacts.getSampleDevicesToImport());

                    // check regarding evaluation Contexts (no critical remarks can come here)
                    checkSamplingContextsForImport(
                            evaluationContextsToImport,
                            artifactImportResult,
                            sampledBiometric.getSamplingContexts()
                    );

                }

                datasetImportArtefacts.addSamplingContextsToImport(evaluationContextsToImport);

                // if a dataset is imported without its encapsulating BASE, the support characteristics need a check themselves
                if(analyzeSupportCharacteristics) {
                    // add characteristics to import
                    List<EnrichedDataSet> datasetsToImportFilledWithThisDataset = new ArrayList<>();
                    datasetsToImportFilledWithThisDataset.add(dataSetToCheckForImport);
                    List<BiometricCharacteristic> biometricCharacteristicsToImportForDataset =
                            checkCharacteristicsForImport(artifactImportResult, new ArrayList<>(), datasetsToImportFilledWithThisDataset);

                    biometricCharacteristicsToImport.addAll(biometricCharacteristicsToImportForDataset);

                    logger.debug("\t..to import dataset '{}', we need to import {} biometric characteristics",
                            dataSetToCheckForImport.getName(), biometricCharacteristicsToImportForDataset.size());

                }

                // finally, add dataset to import as a full copy to not change initial dataset (later needed)
                EnrichedDataSet dataSetToReallyImport = new EnrichedDataSet();

                dataSetToReallyImport = SerializationUtils.clone(dataSetToCheckForImport);

                datasetsToImport.add(dataSetToReallyImport);

            }

        }

        datasetImportArtefacts.setDataSetsToImport(datasetsToImport);
        datasetImportArtefacts.setBiometricCharacteristicsToImport(biometricCharacteristicsToImport);

        return datasetImportArtefacts;

    }

    List<SamplingContext> checkSamplingContextsForImport(List<SamplingContext> allSamplingContextsAlreadyMarkedForImport,
                                                             ArtifactImportResult artifactImportResult,
                                                             List<SamplingContext> samplingContextsToCheckForImport) {



        List<SamplingContext> existingSamplingContexts = samplingContextCRUDRepository.findAll();

        logger.debug("...check {} sampling context for imports, based on {} existing and {} sampling contexts already marked for import",
                samplingContextsToCheckForImport.size(),
                existingSamplingContexts.size(),
                allSamplingContextsAlreadyMarkedForImport.size());

        existingSamplingContexts.addAll(allSamplingContextsAlreadyMarkedForImport);

        for (SamplingContext samplingContextToCheckForImport : samplingContextsToCheckForImport) {

            boolean samplingContextAlreadyExists = false;

            for (SamplingContext existingSamplingContext : existingSamplingContexts) {
                if (samplingContextToCheckForImport.getDescription().equalsIgnoreCase(existingSamplingContext.getDescription())) {
                    samplingContextAlreadyExists = true;

                    logger.trace("Do NOT mark evaluation context {} for import as it already exists", samplingContextToCheckForImport.getDescription());
                    artifactImportResult.addImportRemark(new ImportRemark(
                            "Evaluation context " + samplingContextToCheckForImport.getDescription() + " is not imported as it already exists",
                            ArtefactType.SAMPLING_CONTEXT,
                            ImportRemarkLevel.INFO
                    ));

                    //we can break the loop here
                    break;
                }
            }

            if (!samplingContextAlreadyExists) {

                logger.trace("Mark evaluation context {} for import", samplingContextToCheckForImport.getDescription());
                artifactImportResult.addImportRemark(new ImportRemark(
                        "Evaluation context " + samplingContextToCheckForImport.getDescription() + " is marked for import ",
                        ArtefactType.SAMPLING_CONTEXT,
                        ImportRemarkLevel.INFO
                ));

                allSamplingContextsAlreadyMarkedForImport.add(samplingContextToCheckForImport);

            }
        }

        return allSamplingContextsAlreadyMarkedForImport;
    }

    DatasetImportArtefacts checkSampleDevicesForImport(ArtifactImportResult artifactImportResult,
                                                       List<EnrichedSampleDevice> sampleDevicesToCheckForImport,
                                                       List<EnrichedSampleDevice> sampleDevicesAlreadyMarkedForImport) {

        List<EnrichedSampleDevice> sampleDevicesToImport = new ArrayList<>();

        List<EnrichedSampleDevice> existingSampleDevices = sampleDeviceCRUDRepository.findAll().stream().map(sampleDevice -> {
            return sampleDeviceService.enrichSampleDevice(sampleDevice);
        }).collect(Collectors.toList());

        logger.debug("...check {} sample devices to import based on {} existing sample devices and {} sample devices already marked" +
                " for import", existingSampleDevices.size(),
                sampleDevicesToCheckForImport.size(),
                sampleDevicesAlreadyMarkedForImport.size());

        existingSampleDevices.addAll(sampleDevicesAlreadyMarkedForImport);

        /*
        1. any device that is yet not known in terms of manufacturer - model - category is added
        2. any device that already exists in terms of manu - model - category will be analyzed according its sensors
            if 100% equal => skip, but continue import of overall base
            if additional sensors => add
            if equal sensors
                check sensor type
                    if mismatch discrete/continuous => skip device and overall base import
                check sensor dimensions
                    if additional dimensions => add dimensions
         */

        for (EnrichedSampleDevice sampleDeviceToCheckForImport : sampleDevicesToCheckForImport) {

            boolean sampleDeviceAlreadyExists = false;
            boolean sampleDeviceHasDifferentDeviceCategory = false;

            for (EnrichedSampleDevice existingSampleDevice : existingSampleDevices) {


                if (sampleDeviceToCheckForImport.getDeviceCategory().getName().equalsIgnoreCase(existingSampleDevice.getDeviceCategory().getName())
                        && sampleDeviceToCheckForImport.getDeviceManufacturer().equalsIgnoreCase(existingSampleDevice.getDeviceManufacturer())
                        && sampleDeviceToCheckForImport.getName().equalsIgnoreCase(existingSampleDevice.getName())) {

                    // device already exists

                    sampleDeviceAlreadyExists = true;

                    logger.debug("\t...sample device {} to import does already exist for 100%, it can be skipped", sampleDeviceToCheckForImport.getName());
                    artifactImportResult.addImportRemark(new ImportRemark(
                            "Sample device " + sampleDeviceToCheckForImport.getName() + " already exists exactly and is thus skipped",
                            ArtefactType.SAMPLE_DEVICE,
                            ImportRemarkLevel.INFO
                    ));

                    // we can break this for loop
                    break;
                }

                if (!sampleDeviceToCheckForImport.getDeviceCategory().getName().equalsIgnoreCase(existingSampleDevice.getDeviceCategory().getName())
                        && sampleDeviceToCheckForImport.getDeviceManufacturer().equalsIgnoreCase(existingSampleDevice.getDeviceManufacturer())
                        && sampleDeviceToCheckForImport.getName().equalsIgnoreCase(existingSampleDevice.getName())) {

                    logger.debug("\t...sample device {} to import exists by name and manufacturer {}, but is assigned to a " +
                            "different device category, skip import!",
                            sampleDeviceToCheckForImport.getName(),
                            sampleDeviceToCheckForImport.getDeviceManufacturer());

                    sampleDeviceHasDifferentDeviceCategory = true;
                    // we can break this for loop
                    break;
                }
            }

            if (sampleDeviceHasDifferentDeviceCategory) {


                // add sample device to complete extent as it does not exist yet
                artifactImportResult.addImportRemark(new ImportRemark(
                        "Sample Device " + sampleDeviceToCheckForImport.getName() + " from manufacturer " +
                                sampleDeviceToCheckForImport.getDeviceManufacturer() +
                                " exists but is assigned to a different category, STOP IMPORT! " +
                                "including its dimensions",
                        ArtefactType.SAMPLE_DEVICE,
                        ImportRemarkLevel.CRITICAL
                ));

            } else {

                if (!sampleDeviceAlreadyExists) {

                    logger.debug("\t...sample device {} to import does NOT already exist, check whether it can be imported",
                            sampleDeviceToCheckForImport.getName());


                    // add sample device to complete extent as it does not exist yet
                    artifactImportResult.addImportRemark(new ImportRemark(
                            "Sample Device " + sampleDeviceToCheckForImport.getName() + " is totally new and thus imported" +
                                    " including sensors and " +
                                    "including its dimensions",
                            ArtefactType.SAMPLE_DEVICE,
                            ImportRemarkLevel.INFO
                    ));

                    logger.trace("Add all specs of sample device {} {}({}) for import", sampleDeviceToCheckForImport.getName(),
                            sampleDeviceToCheckForImport.getDeviceManufacturer(), sampleDeviceToCheckForImport.getDeviceCategoryId());

                    // dimensions and sensors will be inferred from device
                    sampleDevicesToImport.add(sampleDeviceToCheckForImport);

                }
            }

        }

        DatasetImportArtefacts importArtefacts = new DatasetImportArtefacts();
        importArtefacts.setSampleDevicesToImport(sampleDevicesToImport);

        return importArtefacts;

    }

    boolean checkResourceToProtectForImport(ArtifactImportResult artifactImportResult,
                                            EnrichedTargetArchitecture targetArchitecture) {

        List<ResourceToProtect> existingResourcesToProtect = resourceToProtectCRUDRepository.findAll();

        // check if new resource is already in list

        if (existingResourcesToProtect.contains(targetArchitecture.getResourceToProtect())) {
            artifactImportResult.addImportRemark(new ImportRemark(
                    "resource to protect "+targetArchitecture.getResourceToProtect()+" already exists, no need for import",
                    ArtefactType.TARGET_ARCHITECTURE,
                    ImportRemarkLevel.INFO));

            return false;

        } else {
            artifactImportResult.addImportRemark(new ImportRemark(
                    "resource to protect "+targetArchitecture.getResourceToProtect()+" does not exist and will be imported",
                    ArtefactType.TARGET_ARCHITECTURE,
                    ImportRemarkLevel.INFO));

            return true;
        }
    }

    DeviceCategoryImportArtifacts determineDeviceCategoryRelatedArtefactsToImport(ArtifactImportResult artifactImportResult,
                                                                                  EnrichedTargetArchitecture targetArchitecture) {

        List<EnrichedDeviceCategory> distinctDeviceCategoriesToImport = new ArrayList<>();

        // add all data capturing deployment locations
        distinctDeviceCategoriesToImport.addAll(
                targetArchitecture.getDataCapturingDeviceCategories());

        // add all data storage deployment locations
        distinctDeviceCategoriesToImport.addAll(targetArchitecture.getDataStorageDeviceCategories());

        // add all data storage deployment locations
        distinctDeviceCategoriesToImport.addAll(targetArchitecture.getDataStorageDeviceCategories());
        // add all decision deployment locations
        distinctDeviceCategoriesToImport.addAll(targetArchitecture.getDecisionDeviceCategories());
        // add all matching deployment locations
        distinctDeviceCategoriesToImport.addAll(targetArchitecture.getMatchingDeviceCategories());
        // add all signal processing deployment locations
        distinctDeviceCategoriesToImport.addAll(targetArchitecture.getSignalProcessingDeviceCategories());
        // add all signature creation deployment locations
        distinctDeviceCategoriesToImport.addAll(targetArchitecture.getSignatureCreationDeviceCategories());

        // filter duplicates
        distinctDeviceCategoriesToImport = distinctDeviceCategoriesToImport.stream().filter(distinctByKey(deviceCategory -> deviceCategory.getName()))
                .collect(Collectors.toList());

        logger.debug("\t...overall, {} device categories should be imported", distinctDeviceCategoriesToImport.size());

        // for each device category, we need to check whether we could import all attached sensors and dimensions

        List<EnrichedDeviceCategory> deviceCategoriesToNewlyImport = new ArrayList<>();
        Map<String,List<EnrichedSensor>> sensorsToFinallyImportToExistingDeviceCategory = new HashMap<>();
        List<EnrichedSensorDimension> sensorDimensionsToFinallyImport = new ArrayList<>();

        for(EnrichedDeviceCategory deviceCategoryToImport : distinctDeviceCategoriesToImport) {
            boolean deviceCategoryNameAlreadyExists =
                    deviceCategoryCRUDRepository.findFirstByNameEqualsIgnoreCase(deviceCategoryToImport.getName()) != null;

            if(deviceCategoryNameAlreadyExists) {
                logger.debug("\t\t...category '{}' already exists, check for sensors to add", deviceCategoryToImport.getName());

                final boolean[] atLeastOneSensorIsNew = {false};

                deviceCategoryToImport.getSensors().forEach(enrichedSensorToImport -> {

                    boolean sensorAlreadyExists = sensorCRUDRepository.findFirstByNameEqualsIgnoreCase(enrichedSensorToImport.getName())!=null;

                    if(sensorAlreadyExists) {
                        logger.debug("\t\t\t...sensor '{}' of category '{}' already exists, check for dimensions", enrichedSensorToImport.getName(), deviceCategoryToImport.getName());

                        final boolean[] atLeastOneDimensionIsNew = {false};
                        enrichedSensorToImport.getSensorDimensions().forEach(sensorDimensionToImport -> {
                            Sensor existingParentSensor = sensorCRUDRepository.findFirstByNameEqualsIgnoreCase(enrichedSensorToImport.getName());
                            EnrichedSensor enrichedParentSensor = sensorService.getEnrichedSensorFromSensor(existingParentSensor);

                            final boolean[] dimensionAlreadyExists = {false};
                            enrichedParentSensor.getSensorDimensions().forEach(dimension -> {
                                if(dimension.getName().equalsIgnoreCase(sensorDimensionToImport.getName())) {
                                    dimensionAlreadyExists[0] = true;
                                }
                            });

                            if(dimensionAlreadyExists[0]) {
                                logger.debug("\t\t\t\t...sensor dimension '{}' of sensor '{}' of category '{}' already exists, skip",
                                        sensorDimensionToImport.getName(), enrichedSensorToImport.getName(), deviceCategoryToImport.getName());
                            } else {
                                atLeastOneDimensionIsNew[0] = true;
                                EnrichedSensorDimension enrichedSensorDimensionToImport =
                                        new EnrichedSensorDimension();

                                enrichedSensorDimensionToImport.setName(sensorDimensionToImport.getName());
                                enrichedSensorDimensionToImport.setSensor(existingParentSensor);
                                sensorDimensionsToFinallyImport.add(enrichedSensorDimensionToImport);

                                artifactImportResult.addImportRemark(new ImportRemark(
                                        "Added dimension "+enrichedSensorDimensionToImport.getName()
                                                +" to sensor "+existingParentSensor.getName()
                                                + " to existing device category "
                                                +deviceCategoryToImport.getName()+" that gets newly imported",

                                        ArtefactType.TARGET_ARCHITECTURE,
                                        ImportRemarkLevel.INFO));

                            }

                        });
                    } else {
                        logger.debug("\t\t\t...sensor '{}' of existing category '{}' does NOT YET exist, IMPORT!", enrichedSensorToImport.getName(), deviceCategoryToImport.getName());
                        List<EnrichedSensor> sensorsToImportForDeviceCategory = new ArrayList<>();
                        if(sensorsToFinallyImportToExistingDeviceCategory.containsKey(deviceCategoryToImport.getName())) {
                            sensorsToImportForDeviceCategory = sensorsToFinallyImportToExistingDeviceCategory.get(deviceCategoryToImport.getName());
                        }
                        sensorsToImportForDeviceCategory.add(enrichedSensorToImport);
                        sensorsToFinallyImportToExistingDeviceCategory.put(deviceCategoryToImport.getName(), sensorsToImportForDeviceCategory);

                        artifactImportResult.addImportRemark(new ImportRemark(
                                "Added sensor "+enrichedSensorToImport.getName()+ " to existing device category "+deviceCategoryToImport.getName()+" that gets newly imported",
                                ArtefactType.TARGET_ARCHITECTURE,
                                ImportRemarkLevel.INFO));

                        atLeastOneSensorIsNew[0] = true;
                    }
                });
            } else {
                logger.debug("\t\t...category '{}' does not yet exists, full import possible", deviceCategoryToImport.getName());
                deviceCategoriesToNewlyImport.add(deviceCategoryToImport);
                artifactImportResult.addImportRemark(new ImportRemark(
                        "Added device category "+ deviceCategoryToImport.getName() +" that gets newly imported",
                        ArtefactType.TARGET_ARCHITECTURE,
                        ImportRemarkLevel.INFO));
            }

        }

        DeviceCategoryImportArtifacts deviceCategoryImportArtifactsToReturn =
                new DeviceCategoryImportArtifacts(deviceCategoriesToNewlyImport,
                        sensorsToFinallyImportToExistingDeviceCategory,
                        sensorDimensionsToFinallyImport);

        return deviceCategoryImportArtifactsToReturn;

    }


    List<BiometricCharacteristic> checkCharacteristicsForImport(ArtifactImportResult artifactImportResult,
                                                                List<BiometricCharacteristic> baseCharacteristicsToCheckForImport,
                                                                List<EnrichedDataSet> dataSetsToImport) {


        logger.debug("...check {} new biometric characteristics for import", baseCharacteristicsToCheckForImport.size());

        List<BiometricCharacteristic> existingCharacteristics = biometricCharacteristicCRUDRepository.findAll();
        logger.debug("\t...found {} existing biometric characteristics", existingCharacteristics.size());

        List<BiometricCharacteristic> characteristicsToImport = new ArrayList();

        // create deep copy to not mess with original base's characteristics
        List<BiometricCharacteristic> characteristicsToCheckForImport = new ArrayList<>();
        for(BiometricCharacteristic baseMainCharacteristic: baseCharacteristicsToCheckForImport) {
            characteristicsToCheckForImport.add(SerializationUtils.clone(baseMainCharacteristic));
        }

        if(dataSetsToImport.size()>0) {
            logger.debug("\t...{} further datasets for import need to be checked to also include the given support characteristics",
                    dataSetsToImport.size());

            // merge main characteristics with possible support characteristics from sampled behaviour
            List<BiometricCharacteristic> supportCharacteristics = new ArrayList<>();

            List<BiometricCharacteristic> finalSupportCharacteristics = supportCharacteristics;

            dataSetsToImport.forEach(dataSet -> {
                dataSet.getSampledBiometrics().forEach(sampledBiometric -> {
                    finalSupportCharacteristics.add(sampledBiometric.getSampledCharacteristic());
                    if(sampledBiometric.getSupportCharacteristic()!=null) {
                        finalSupportCharacteristics.add(sampledBiometric.getSupportCharacteristic());
                    }
                });
            });
            logger.debug("\t\t...overall, {} main and support characteristics were given over all datasets", finalSupportCharacteristics.size());

            logger.debug("\t\t...going to remove duplicates from list of {} main and {} support characteristics of sampled behaviour",
                    baseCharacteristicsToCheckForImport.size(),
                    supportCharacteristics.size());

            // remove duplicates by id that was exported from other baseModX
            supportCharacteristics = supportCharacteristics.stream()
                    .filter(distinctByKey(DomainElement::getId))
                    .collect(Collectors.toList());

            logger.debug("\t {} support characteristics remain to be merged with main characteristics", supportCharacteristics.size());

            // add all support characteristics
            characteristicsToCheckForImport.addAll(supportCharacteristics);

            logger.debug("\t\t...going to remove duplicates from list of {} all characteristics for potential import", characteristicsToCheckForImport.size());
            // remove duplicates, use id provided by export from other baseModX
            characteristicsToCheckForImport = characteristicsToCheckForImport.stream()
                    .filter(distinctByKey(DomainElement::getId))
                    .collect(Collectors.toList());

        }

        logger.debug("\tcheck overall {} behavioral biometric characteristics for import", characteristicsToCheckForImport.size());

        for (BiometricCharacteristic characteristicToCheckForImport : characteristicsToCheckForImport) {

            boolean characteristicToImportAlreadyExists = false;

            for (BiometricCharacteristic existingCharacteristic : existingCharacteristics) {

                if (characteristicToCheckForImport.getUniqueIdentifier().equalsIgnoreCase(existingCharacteristic.getUniqueIdentifier())) {
                    logger.debug("\t\t..characteristic to import with unique id {} already exists in repository", characteristicToCheckForImport.getUniqueIdentifier());


                    // deep match => just do nothing but continue

                    artifactImportResult.addImportRemark(
                            new ImportRemark("Characteristic " + characteristicToCheckForImport.getUniqueIdentifier() + " already existed, skipped",
                                    ArtefactType.BEHAVIOURAL_BIOMETRIC_CHARACTERISTIC,
                                    ImportRemarkLevel.INFO));
                    characteristicToImportAlreadyExists = true;

                    break;


                }

            }

            if (!characteristicToImportAlreadyExists) {


                logger.debug("Biometric Characteristic with name {} does not exist yet, add it", characteristicToCheckForImport.getUniqueIdentifier());
                // completely new characteristic
                characteristicsToImport.add(characteristicToCheckForImport);

                artifactImportResult.addImportRemark(
                        new ImportRemark("Characteristic " + characteristicToCheckForImport.getUniqueIdentifier() + " imported as new characteristic",
                                ArtefactType.BEHAVIOURAL_BIOMETRIC_CHARACTERISTIC,
                                ImportRemarkLevel.INFO));

            }

        }

        return characteristicsToImport;

    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}

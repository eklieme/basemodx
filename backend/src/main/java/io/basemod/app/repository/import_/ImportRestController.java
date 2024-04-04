package io.basemod.app.repository.import_;

import io.basemod.app.base.EnrichedBaseForExport;
import io.basemod.app.base.EnrichedBiometricAuthenticationSystemAndEvaluation;
import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.evaluation.experiment.dataset.sampling.device.device.EnrichedSampleDevice;
import io.basemod.app.evaluation.experiment.evaluation.criteria.EnrichedExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;
import io.basemod.app.repository.export_.DatasetForExportArtefact;
import io.basemod.app.repository.import_.domain.ArtifactImportResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static io.basemod.app.repository.review.ModelledElementReviewRestController.ARTIFACT_TYPE_DATASETS;

@RestController
@RequestMapping("${spring.data.rest.base-path}/repository/import")
public class ImportRestController {

    Logger logger = LoggerFactory.getLogger(ImportRestController.class);

    private ImportController importController;
    private RepositoryInitializationController repositoryInitializationController;

    @Autowired
    public ImportRestController(ImportController importController, RepositoryInitializationController repositoryInitializationController) {
        this.importController = importController;
        this.repositoryInitializationController = repositoryInitializationController;
    }

    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    public ArtifactImportResult resetAndInitWithDemoArtefacts() {

        logger.debug("reset modeled artefacts and import demo set of artefacts");
        return repositoryInitializationController.resetAndInitWithDemoData();

    }

    @RequestMapping(value = "/base", method = RequestMethod.POST)
    public ArtifactImportResult importExistingBase(@RequestBody  List<EnrichedBaseForExport> enrichedBaseToImport) {

        logger.debug("Start importing {} base", enrichedBaseToImport.size());
        return importController.processImportBaseRequest(enrichedBaseToImport);

    }

    @RequestMapping(value = "/datasets", method = RequestMethod.POST)
    public ArtifactImportResult importExistingDatasets(@RequestBody DatasetForExportArtefact artifactsForDataSetsImport) {

        logger.debug("Start importing {} datasets", artifactsForDataSetsImport.getEnrichedDataSetsForExport().size());
        return importController.processImportDatasetRequest(artifactsForDataSetsImport);

    }

    @RequestMapping(value = "/sampleDevices", method = RequestMethod.POST)
    public ArtifactImportResult importExistingSampleDevices(@RequestBody List<EnrichedSampleDevice> sampleDevicesToImport) {

        logger.debug("Start importing {} sample devices", sampleDevicesToImport.size());
        return importController.processImportSampleDeviceRequest(sampleDevicesToImport);

    }

    @RequestMapping(value = "/criteria/implementation", method = RequestMethod.POST)
    public ArtifactImportResult importExistingImplementationSpecificCriteria(
            @RequestBody List<ImplementationSpecificEvaluationCriteria> implementationSpecificEvaluationCriteriaToImport) {

        logger.debug("Start importing {} implementation specific criteria", implementationSpecificEvaluationCriteriaToImport.size());
        return importController.processImportImplementationSpecificEvaluationCriteriaRequest(
                implementationSpecificEvaluationCriteriaToImport);

    }

    @RequestMapping(value = "/criteria/experiment", method = RequestMethod.POST)
    public ArtifactImportResult importExistingExperimentSpecificCriteria(
            @RequestBody List<EnrichedExperimentSpecificEvaluationCriteria> experimentSpecificEvaluationCriteriaToImport) {

        logger.debug("Start importing {} experiment specific criteria", experimentSpecificEvaluationCriteriaToImport.size());
        return importController.processImportExperimentSpecificEvaluationCriteriaRequest(
                experimentSpecificEvaluationCriteriaToImport);

    }



}

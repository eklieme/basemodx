package io.basemod.app.repository.import_;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mongodb.client.MongoClient;
import io.basemod.app.base.EnrichedBaseForExport;
import io.basemod.app.base.EnrichedBiometricAuthenticationSystemAndEvaluation;
import io.basemod.app.domain.ModelledDomainElement;
import io.basemod.app.evaluation.experiment.dataset.EnrichedDataSet;
import io.basemod.app.repository.export_.DatasetForExportArtefact;
import io.basemod.app.repository.import_.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class RepositoryInitializationController {

    Logger logger = LoggerFactory.getLogger(RepositoryInitializationController.class);

    private final String[] demoArtefactsPaths = new String[]{};
            /*"artefacts/1_tips_with_khan.json",
            "artefacts/2_touchalytics_modelled.json",
            "artefacts/3_implauth_modelled.json",
            "artefacts/4_silentsense_modeled.json",
            "artefacts/5_datasets_smombies_netsense.json",
            "artefacts/6_castra_modelled.json"};*/

    private MongoClient mongoClient;
    private ImportController importController;
    private ResourceLoader resourceLoader;

    @Value("${spring.data.mongodb.database}")
    private String mongoDbName;

    @Value("${deployment.environment}")
    private String deploymentEnvironment;

    @Autowired
    public RepositoryInitializationController(MongoClient mongoClient, ImportController importController, ResourceLoader resourceLoader) {
        this.mongoClient = mongoClient;
        this.importController = importController;
        this.resourceLoader = resourceLoader;
    }

    public ArtifactImportResult resetAndInitWithDemoData() {


        ArtifactImportResult artifactImportResult = new ArtifactImportResult();

        logger.debug("Dropping database {}", mongoDbName);
        mongoClient.getDatabase(mongoDbName).drop();

        try {
            for(String demoArtefactPath: demoArtefactsPaths) {

                Resource jsonExportResource = resourceLoader.getResource("classpath:"+demoArtefactPath);
                logger.debug("\tparsing json file {} ...", demoArtefactPath);

                //custom deserializer
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules();

                // general configuration
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


                JsonNode exportJsonArtefact = objectMapper
                        .readTree(jsonExportResource.getInputStream());


                logger.debug("\tFile contains {} exports!", exportJsonArtefact.get("type").asText());

                switch(exportJsonArtefact.get("type").asText()) {
                    case "base":
                        ObjectReader objectReaderBaseImportArtefact = objectMapper.readerFor(new TypeReference<List<EnrichedBaseForExport>>(){});
                        List<EnrichedBaseForExport> baseImportArtefacts = objectReaderBaseImportArtefact.readValue(exportJsonArtefact.get("data"));
                        logger.debug("Importing {} base...", baseImportArtefacts.size());
                        ArtifactImportResult baseImportResultArtefact = importController.processImportBaseRequest(baseImportArtefacts);
                        logger.debug("DONE!");
                        artifactImportResult.addImportRemarks(baseImportResultArtefact.getImportRemarks());
                        break;
                    case "datasets":
                        ObjectReader objectReaderDataset = objectMapper.readerFor(new TypeReference<DatasetForExportArtefact>(){});
                        DatasetForExportArtefact datasetArtefacts = objectReaderDataset.readValue(exportJsonArtefact.get("data"));
                        logger.debug("Importing {} datasets...", datasetArtefacts.getEnrichedDataSetsForExport().size());
                        ArtifactImportResult datasetImportResultArtefact = importController.processImportDatasetRequest(datasetArtefacts);
                        logger.debug("DONE!");
                        artifactImportResult.addImportRemarks(datasetImportResultArtefact.getImportRemarks());
                        break;
                    default:
                        logger.debug("Type {} not considered for demo", exportJsonArtefact.get("type").asText());
                        artifactImportResult.addImportRemark(new ImportRemark("Did not add entities of type "+
                                exportJsonArtefact.get("type").asText()+" contained in export", ArtefactType.UNKNOWN, ImportRemarkLevel.WARNING));
                        break;
                }

            }
        } catch (IOException e) {
            artifactImportResult.addImportRemark(new ImportRemark("Something went wrong, see server log", ArtefactType.UNKNOWN, ImportRemarkLevel.CRITICAL));
            e.printStackTrace();
        }

        return artifactImportResult;

    }
}

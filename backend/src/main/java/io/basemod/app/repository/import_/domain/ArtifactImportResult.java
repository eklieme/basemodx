package io.basemod.app.repository.import_.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArtifactImportResult {

    private ImportResultStatus importResultStatus;
    private List<ImportRemark> importRemarks;
    private int artefactsImported;


    public ArtifactImportResult(ImportResultStatus importResultStatus) {
        this();
        this.importResultStatus = importResultStatus;
    }

    public ArtifactImportResult() {
        this.importRemarks = new ArrayList<>();
    }

    public ImportResultStatus getImportResultStatus() {
        return importResultStatus;
    }

    public void setImportResultStatus(ImportResultStatus importResultStatus) {
        this.importResultStatus = importResultStatus;
    }

    public List<ImportRemark> getImportRemarks() {
        return importRemarks;
    }

    public void setImportRemarks(List<ImportRemark> importRemarks) {
        this.importRemarks = importRemarks;
    }

    public void addImportRemark(ImportRemark importRemarkToAdd) {
        this.importRemarks.add(importRemarkToAdd);
    }

    public void addImportRemarks(List<ImportRemark> importRemarksToAdd) {
        this.importRemarks.addAll(importRemarksToAdd);
    }

    public boolean containsCriticalRemark() {
        return containsImportRemarkOfLevel(ImportRemarkLevel.CRITICAL);
    }

    public boolean containsWarningRemark() {
        return containsImportRemarkOfLevel(ImportRemarkLevel.WARNING);
    }

    private boolean containsImportRemarkOfLevel(ImportRemarkLevel importRemarkLevel) {
        return this.importRemarks.stream().filter(importRemark -> importRemark.getImportRemarkLevel().equals(importRemarkLevel))
                .collect(Collectors.toList()).size()>0;
    }

    public int getArtefactsImported() {
        return artefactsImported;
    }

    public void setArtefactsImported(int artefactsImported) {
        this.artefactsImported = artefactsImported;
    }

    public void anotherNArtefactsImported(int numberOfArtefacts,
                                          ArtefactType artefactType,
                                          String artefactName) {
        if(numberOfArtefacts>0) {
            this.artefactsImported += numberOfArtefacts;
            addImportRemark(new ImportRemark(
                    "Imported "+numberOfArtefacts
                            +" "+artefactName+" successfully",
                    artefactType,
                    ImportRemarkLevel.INFO
            ));
        }
    }


    public void updateResultStatus() {
        if(this.containsCriticalRemark()) {
            this.importResultStatus = ImportResultStatus.PARTIAL_SUCCESS;
        }
        if(this.artefactsImported == 0) {
            this.importResultStatus = ImportResultStatus.NO_CHANGES_IN_REPOSITORY;
        }
        if(this.artefactsImported == 0 && this.containsCriticalRemark()) {
            this.importResultStatus = ImportResultStatus.NOT_SUCCESSFUL;
        }
    }
}

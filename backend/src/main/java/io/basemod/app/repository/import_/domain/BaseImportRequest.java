package io.basemod.app.repository.import_.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public class BaseImportRequest {

    @JsonFormat(shape=JsonFormat.Shape.ARRAY)
    List<BaseImportArtefact> baseImportArtefacts;

    public BaseImportRequest(List<BaseImportArtefact> baseImportArtefacts) {
        this.baseImportArtefacts = baseImportArtefacts;
    }

    public BaseImportRequest() {
    }

    public List<BaseImportArtefact> getBaseImportArtefacts() {
        return baseImportArtefacts;
    }

    public void setBaseImportArtefacts(List<BaseImportArtefact> baseImportArtefacts) {
        this.baseImportArtefacts = baseImportArtefacts;
    }

    @Override
    public String toString() {
        return "BaseImportRequest{" +
                "baseImportArtefacts=" + baseImportArtefacts +
                '}';
    }
}

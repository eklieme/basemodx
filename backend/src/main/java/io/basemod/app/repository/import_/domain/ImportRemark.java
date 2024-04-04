package io.basemod.app.repository.import_.domain;

public class ImportRemark {

    private String remarkMessage;
    private ArtefactType importArtefactType;
    private ImportRemarkLevel importRemarkLevel;

    public ImportRemark(String remarkMessage, ArtefactType importArtefactType, ImportRemarkLevel importRemarkLevel) {
        this.remarkMessage = remarkMessage;
        this.importArtefactType = importArtefactType;
        this.importRemarkLevel = importRemarkLevel;
    }

    public String getRemarkMessage() {
        return remarkMessage;
    }

    public void setRemarkMessage(String remarkMessage) {
        this.remarkMessage = remarkMessage;
    }

    public ImportRemarkLevel getImportRemarkLevel() {
        return importRemarkLevel;
    }

    public void setImportRemarkLevel(ImportRemarkLevel importRemarkLevel) {
        this.importRemarkLevel = importRemarkLevel;
    }

    public ArtefactType getImportArtefactType() {
        return importArtefactType;
    }

    public void setImportArtefactType(ArtefactType importArtefactType) {
        this.importArtefactType = importArtefactType;
    }

    @Override
    public String toString() {
        return "ImportRemark{" +
                "remarkMessage='" + remarkMessage + '\'' +
                ", importArtefactType=" + importArtefactType +
                ", importRemarkLevel=" + importRemarkLevel +
                '}';
    }
}

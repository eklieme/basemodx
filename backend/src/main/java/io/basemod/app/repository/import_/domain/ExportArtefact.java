package io.basemod.app.repository.import_.domain;

import java.util.List;

public class ExportArtefact {

    private String type;
    private List<? extends Object> data;

    public ExportArtefact() {
    }

    public ExportArtefact(String type, List<? extends Object> data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<? extends Object> getData() {
        return data;
    }

    public void setData(List<? extends Object> data) {
        this.data = data;
    }

}

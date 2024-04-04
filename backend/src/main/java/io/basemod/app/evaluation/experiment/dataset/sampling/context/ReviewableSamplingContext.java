package io.basemod.app.evaluation.experiment.dataset.sampling.context;

import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.evaluation.experiment.dataset.sampling.context.SamplingContext;

import java.util.List;

public class ReviewableSamplingContext extends SamplingContext {

    private List<String> datasetIdsUsedIn;

    public ReviewableSamplingContext(String description) {
        super(description);
    }

    public ReviewableSamplingContext(String description, List<String> datasetIdsUsedIn) {
        super(description);
        this.datasetIdsUsedIn = datasetIdsUsedIn;
    }

    public ReviewableSamplingContext(SamplingContext samplingContext, List<String> datasetIdsUsedIn) {
        super(samplingContext.getId(), samplingContext.getModelledElementDetail(), samplingContext.getDescription());
        this.datasetIdsUsedIn = datasetIdsUsedIn;
    }

    public ReviewableSamplingContext(List<String> datasetIdsUsedIn) {
        this.datasetIdsUsedIn = datasetIdsUsedIn;
    }

    public ReviewableSamplingContext(String id, ModelledElementDetail modelledElementDetail, String description, List<String> datasetIdsUsedIn) {
        super(id, modelledElementDetail, description);
        this.datasetIdsUsedIn = datasetIdsUsedIn;
    }

    public ReviewableSamplingContext(ModelledElementDetail modelledElementDetail, String description, List<String> datasetIdsUsedIn) {
        super(modelledElementDetail, description);
        this.datasetIdsUsedIn = datasetIdsUsedIn;
    }

    public ReviewableSamplingContext(String id, String description, List<String> datasetIdsUsedIn) {
        super(id, description);
        this.datasetIdsUsedIn = datasetIdsUsedIn;
    }

    public List<String> getDatasetIdsUsedIn() {
        return datasetIdsUsedIn;
    }

    public void setDatasetIdsUsedIn(List<String> datasetIdsUsedIn) {
        this.datasetIdsUsedIn = datasetIdsUsedIn;
    }
}

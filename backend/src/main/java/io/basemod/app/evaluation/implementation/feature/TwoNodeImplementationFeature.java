package io.basemod.app.evaluation.implementation.feature;

import io.basemod.app.evaluation.experiment.dataset.sampling.device.category.DeviceCategory;

public class TwoNodeImplementationFeature extends ImplementationFeature {

    private DeviceCategory nodeOne;
    private DeviceCategory nodeTwo;

    public TwoNodeImplementationFeature(String featureDescription, DeviceCategory nodeOne, DeviceCategory nodeTwo) {
        super(featureDescription);
        this.nodeOne = nodeOne;
        this.nodeTwo = nodeTwo;
    }

    public DeviceCategory getNodeOne() {
        return nodeOne;
    }

    public void setNodeOne(DeviceCategory nodeOne) {
        this.nodeOne = nodeOne;
    }

    public DeviceCategory getNodeTwo() {
        return nodeTwo;
    }

    public void setNodeTwo(DeviceCategory nodeTwo) {
        this.nodeTwo = nodeTwo;
    }

    @Override
    public String toString() {
        return "TwoNodeImplementationFeature{" +
                "nodeOne=" + nodeOne +
                ", nodeTwo=" + nodeTwo +
                "} " + super.toString();
    }
}

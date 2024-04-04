package io.basemod.app.domain;

import io.basemod.app.base.lifecycle.ModelledElementLifecycle;
import org.springframework.data.annotation.CreatedBy;

import java.io.Serializable;

public class ModelledElementDetail implements Serializable {

    public static final String DEFAULT_MODELLER = "REPOSITORY";
    @CreatedBy
    private String modelledInitiallyBy;

    private ModelledElementLifecycle elementModelledElementLifecycle;

    public ModelledElementDetail(String modelledInitiallyBy, ModelledElementLifecycle elementModelledElementLifecycle) {
        this.modelledInitiallyBy = modelledInitiallyBy;
        this.elementModelledElementLifecycle = elementModelledElementLifecycle;
    }

    public ModelledElementDetail() {
            }

    public String getModelledInitiallyBy() {
        return modelledInitiallyBy;
    }

    public void setModelledInitiallyBy(String modelledInitiallyBy) {
        this.modelledInitiallyBy = modelledInitiallyBy;
    }

    public ModelledElementLifecycle getElementLifecycle() {
        return elementModelledElementLifecycle;
    }

    public void setElementLifecycle(ModelledElementLifecycle elementModelledElementLifecycle) {
        this.elementModelledElementLifecycle = elementModelledElementLifecycle;
    }

    @Override
    public String toString() {
        return "ModelledElementDetail{" +
                "modelledInitiallyBy='" + modelledInitiallyBy + '\'' +
                ", elementLifecycle=" + elementModelledElementLifecycle +
                '}';
    }
}

package io.basemod.app.domain;

import io.basemod.app.base.lifecycle.ModelledElementLifecycle;

public abstract class ModelledDomainElement extends DomainElement{

    private ModelledElementDetail modelledElementDetail;

    public ModelledDomainElement(String id, ModelledElementDetail modelledElementDetail) {
        super(id);
        this.modelledElementDetail = modelledElementDetail;
    }

    public ModelledDomainElement(ModelledElementDetail modelledElementDetail) {
        this.modelledElementDetail = modelledElementDetail;
    }

    public ModelledDomainElement(String id) {

        super(id);
        this.modelledElementDetail = new ModelledElementDetail();
        this.modelledElementDetail.setElementLifecycle(new ModelledElementLifecycle());
    }

    public ModelledDomainElement() {
        this.modelledElementDetail = new ModelledElementDetail();
        this.modelledElementDetail.setElementLifecycle(new ModelledElementLifecycle());
    }

    public ModelledElementDetail getModelledElementDetail() {
        return modelledElementDetail;
    }

    public void setModelledElementDetail(ModelledElementDetail modelledElementDetail) {
        this.modelledElementDetail = modelledElementDetail;
    }


    @Override
    public String toString() {
        return "ModelledDomainElement{" +
                "modelledElementDetail=" + modelledElementDetail +
                "} " + super.toString();
    }
}

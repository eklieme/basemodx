package io.basemod.app.evaluation.experiment.biometricsystem.feature;

import io.basemod.app.domain.NamedModelledDomainElement;

public class Feature extends NamedModelledDomainElement {

    public Feature(String name) {
        super(name);
    }

    public Feature() {
    }

    @Override
    public String toString() {
        return "Feature{} " + super.toString();
    }
}

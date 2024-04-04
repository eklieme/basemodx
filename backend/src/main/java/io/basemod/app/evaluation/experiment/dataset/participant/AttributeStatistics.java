package io.basemod.app.evaluation.experiment.dataset.participant;

import io.basemod.app.domain.NamedModelledDomainElement;

public class AttributeStatistics extends NamedModelledDomainElement {

    private Statistics statistics;

    public AttributeStatistics(String id, String name, Statistics statistics) {
        super(id, name);
        this.statistics = statistics;
    }

    public AttributeStatistics() {
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}

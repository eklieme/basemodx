package io.basemod.app.evaluation.experiment.dataset.sampling.sensor;

import io.basemod.app.domain.NamedModelledDomainElement;

import java.util.Objects;

public class SensorDimension extends NamedModelledDomainElement {

    public SensorDimension(String name) {
        super(name);
    }


    public SensorDimension() {
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return true;
    }

}

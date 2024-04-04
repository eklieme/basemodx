package io.basemod.app.evaluation.experiment.dataset.sampling.device.category;

import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.domain.NamedModelledDomainElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeviceCategory extends NamedModelledDomainElement {

    private List<String> sensorIds;

    public DeviceCategory(String name, List<String> sensorIds) {
        super(name);
        this.sensorIds = sensorIds;
    }

    public DeviceCategory(String id, String name, List<String> sensorIds) {
        super(id, name);
        this.sensorIds = sensorIds;
    }

    public DeviceCategory(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    public DeviceCategory(String id, ModelledElementDetail modelledElementDetail, String name, List<String> sensorIds) {
        super(id, modelledElementDetail, name);
        this.sensorIds = sensorIds;
    }

    public DeviceCategory(ModelledElementDetail modelledElementDetail, String name, List<String> sensorIds) {
        super(modelledElementDetail, name);
        this.sensorIds = sensorIds;
    }

    public DeviceCategory(String name) {
        super(name);
    }

    public DeviceCategory() {
    }


    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    public void addSensorId(String sensorId) {
        if(this.sensorIds==null) {
            this.sensorIds = new ArrayList<>();
        }

        this.sensorIds.add(sensorId);
    }

    public void addSensorIds(List<String> sensorIds) {
        if(this.sensorIds==null) {
            this.sensorIds = new ArrayList<>();
        }

        this.sensorIds.addAll(sensorIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeviceCategory that = (DeviceCategory) o;
        return Objects.equals(sensorIds, that.sensorIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sensorIds);
    }
}

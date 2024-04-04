package io.basemod.app.domain;

import java.util.Objects;

public abstract class NamedModelledDomainElement extends ModelledDomainElement {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NamedModelledDomainElement(String name) {
        super();
        this.name = name;
    }

    public NamedModelledDomainElement(String id, String name) {
        super(id);
        this.name = name;
    }

    public NamedModelledDomainElement() {
        super();
    }

    public NamedModelledDomainElement(String id, ModelledElementDetail modelledElementDetail, String name) {
        super(id, modelledElementDetail);
        this.name = name;
    }

    public NamedModelledDomainElement(ModelledElementDetail modelledElementDetail, String name) {
        super(modelledElementDetail);
        this.name = name;
    }

    @Override
    public String toString() {
        return "NamedModelledDomainElement{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        NamedModelledDomainElement that = (NamedModelledDomainElement) other;
        return name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

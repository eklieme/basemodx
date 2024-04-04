package io.basemod.app.domain;


import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Objects;

public abstract class DomainElement implements Serializable {

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DomainElement(String id) {
        this.id = id;
    }

    public DomainElement() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
        // ids do not have to equal
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DomainElement{" +
                "id='" + id + '\'' +
                '}';
    }
}

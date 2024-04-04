package io.basemod.app.evaluation.experiment.dataset.participant;

import io.basemod.app.domain.DomainElement;

import java.util.Objects;

public class ParticipantDetails extends DomainElement {

    private int age;
    private String gender;

    public ParticipantDetails() {
    }

    public ParticipantDetails(int age, String gender) {
        this.age = age;
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "ParticipantInformation{" +
                "age=" + age +
                ", gender='" + gender + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ParticipantDetails that = (ParticipantDetails) o;
        return age == that.age && gender.equals(that.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), age, gender);
    }
}

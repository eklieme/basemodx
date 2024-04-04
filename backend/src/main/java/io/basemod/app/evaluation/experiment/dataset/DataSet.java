package io.basemod.app.evaluation.experiment.dataset;

import io.basemod.app.domain.ReferencedNamedModelledDomainElement;
import io.basemod.app.base.Reference;
import io.basemod.app.evaluation.experiment.dataset.participant.ParticipantInformation;
import io.basemod.app.evaluation.experiment.dataset.sampling.SampledBiometric;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataSet extends ReferencedNamedModelledDomainElement {

    private List<SampledBiometric> sampledBiometrics;
    private ParticipantInformation participantInformation;


    public DataSet(String name, Reference reference, List<SampledBiometric> sampledBiometrics, ParticipantInformation participantInformation) {
        super(name, reference);
        this.sampledBiometrics = sampledBiometrics;
        this.participantInformation = participantInformation;
    }

    public DataSet(String id, String name, Reference reference, List<SampledBiometric> sampledBiometrics, ParticipantInformation participantInformation) {
        super(id, name, reference);
        this.sampledBiometrics = sampledBiometrics;
        this.participantInformation = participantInformation;
    }

    public List<SampledBiometric> getSampledBiometrics() {
        return sampledBiometrics;
    }

    public void setSampledBiometrics(List<SampledBiometric> sampledBiometrics) {
        this.sampledBiometrics = sampledBiometrics;
    }

    public void addSampledBiometric(SampledBiometric sampledBiometric) {
        if(this.sampledBiometrics==null) {
            this.sampledBiometrics = new ArrayList<>();
        }
        this.sampledBiometrics.add(sampledBiometric);
    }

    public ParticipantInformation getParticipantInformation() {
        return participantInformation;
    }

    public void setParticipantInformation(ParticipantInformation participantInformation) {
        this.participantInformation = participantInformation;
    }

    public DataSet() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSet dataSet = (DataSet) o;
        return Objects.equals(sampledBiometrics, dataSet.sampledBiometrics) &&
                Objects.equals(participantInformation, dataSet.participantInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sampledBiometrics, participantInformation);
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "sampledBiometrics=" + sampledBiometrics +
                ", participantInformation=" + participantInformation +
                "} " + super.toString();
    }
}

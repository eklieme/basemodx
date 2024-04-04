package io.basemod.app.evaluation.experiment.dataset;

import io.basemod.app.base.Reference;
import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.domain.ReferencedNamedModelledDomainElement;
import io.basemod.app.evaluation.experiment.dataset.participant.ParticipantInformation;
import io.basemod.app.evaluation.experiment.dataset.sampling.EnrichedSampledBiometric;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnrichedDataSet extends ReferencedNamedModelledDomainElement {

    private ParticipantInformation participantInformation;
    private List<EnrichedSampledBiometric> sampledBiometrics;

    public EnrichedDataSet() {
        sampledBiometrics = new ArrayList<>();
    }

    public EnrichedDataSet(Reference reference, ParticipantInformation participantInformation, List<EnrichedSampledBiometric> sampledBiometrics) {
        super(reference);
        this.participantInformation = participantInformation;
        this.sampledBiometrics = sampledBiometrics;
    }

    public EnrichedDataSet(String id, ModelledElementDetail modelledElementDetail, String name, Reference reference, ParticipantInformation participantInformation, List<EnrichedSampledBiometric> sampledBiometrics) {
        super(id, modelledElementDetail, name, reference);
        this.participantInformation = participantInformation;
        this.sampledBiometrics = sampledBiometrics;
    }

    public EnrichedDataSet(ModelledElementDetail modelledElementDetail, String name, Reference reference, ParticipantInformation participantInformation, List<EnrichedSampledBiometric> sampledBiometrics) {
        super(modelledElementDetail, name, reference);
        this.participantInformation = participantInformation;
        this.sampledBiometrics = sampledBiometrics;
    }

    public EnrichedDataSet(String name, Reference reference, ParticipantInformation participantInformation, List<EnrichedSampledBiometric> sampledBiometrics) {
        super(name, reference);
        this.participantInformation = participantInformation;
        this.sampledBiometrics = sampledBiometrics;
    }

    public EnrichedDataSet(String id, String name, Reference reference, ParticipantInformation participantInformation, List<EnrichedSampledBiometric> sampledBiometrics) {
        super(id, name, reference);
        this.participantInformation = participantInformation;
        this.sampledBiometrics = sampledBiometrics;
    }

    public EnrichedDataSet(ParticipantInformation participantInformation, List<EnrichedSampledBiometric> sampledBiometrics) {
        this.participantInformation = participantInformation;
        this.sampledBiometrics = sampledBiometrics;
    }

    public ParticipantInformation getParticipantInformation() {
        return participantInformation;
    }

    public void setParticipantInformation(ParticipantInformation participantInformation) {
        this.participantInformation = participantInformation;
    }

    public List<EnrichedSampledBiometric> getSampledBiometrics() {
        return sampledBiometrics;
    }

    public void setSampledBiometrics(List<EnrichedSampledBiometric> sampledBiometrics) {
        this.sampledBiometrics = sampledBiometrics;
    }

    @Override
    public String toString() {
        return "EnrichedDataSet{" +
                "participantInformation=" + participantInformation +
                ", sampledBiometrics=" + sampledBiometrics +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EnrichedDataSet that = (EnrichedDataSet) o;
        return Objects.equals(participantInformation, that.participantInformation) && Objects.equals(sampledBiometrics, that.sampledBiometrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), participantInformation, sampledBiometrics);
    }
}

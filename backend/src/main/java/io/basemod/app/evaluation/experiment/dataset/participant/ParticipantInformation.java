package io.basemod.app.evaluation.experiment.dataset.participant;

import io.basemod.app.domain.DomainElement;

import java.util.List;
import java.util.Objects;

public class ParticipantInformation extends DomainElement {

    private List<ParticipantDetails> participantDetails;
    private boolean participantDetailsUnknown;
    private int numberOfParticipants;

    public ParticipantInformation() {
    }

    public ParticipantInformation(List<ParticipantDetails> participantDetails, boolean participantDetailsUnknown) {
        this.participantDetails = participantDetails;
        this.participantDetailsUnknown = participantDetailsUnknown;
    }

    public ParticipantInformation(List<ParticipantDetails> participantDetails, boolean participantDetailsUnknown, int numberOfParticipants) {
        this.participantDetails = participantDetails;
        this.participantDetailsUnknown = participantDetailsUnknown;
        this.numberOfParticipants = numberOfParticipants;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public List<ParticipantDetails> getParticipantDetails() {
        return participantDetails;
    }

    public void setParticipantDetails(List<ParticipantDetails> participantDetails) {
        this.participantDetails = participantDetails;
    }

    public boolean isParticipantDetailsUnknown() {
        return participantDetailsUnknown;
    }

    public void setParticipantDetailsUnknown(boolean participantDetailsUnknown) {
        this.participantDetailsUnknown = participantDetailsUnknown;
    }

    @Override
    public String toString() {
        return "ParticipantInformation{" +
                "participantDetails=" + participantDetails +
                ", participantDetailsUnknown=" + participantDetailsUnknown +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ParticipantInformation that = (ParticipantInformation) o;
        return participantDetailsUnknown == that.participantDetailsUnknown && numberOfParticipants == that.numberOfParticipants && participantDetails.equals(that.participantDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), participantDetails, participantDetailsUnknown, numberOfParticipants);
    }
}

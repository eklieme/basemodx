package io.basemod.app.base;

import io.basemod.app.architecture.EnrichedTargetArchitecture;
import io.basemod.app.characteristic.BiometricCharacteristic;
import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.domain.ReferencedNamedModelledDomainElement;
import io.basemod.app.evaluation.EnrichedBaseEvaluation;
import io.basemod.app.evaluation.criteria.grant.EnrichedEvaluationCriteriaGrant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EnrichedBiometricAuthenticationSystemAndEvaluation extends ReferencedNamedModelledDomainElement {

    private List<BiometricCharacteristic> biometricCharacteristics;
    private EnrichedTargetArchitecture targetArchitecture;
    private EnrichedBaseEvaluation baseEvaluation;
    private List<EnrichedEvaluationCriteriaGrant> evaluationCriteriaGrants;
    private ModellingProgress modellingProgress;
    private boolean modellingFinished;
    private Instant createdDate;
    private Instant lastModifiedDate;

    public EnrichedBiometricAuthenticationSystemAndEvaluation() {

    }

    public EnrichedBiometricAuthenticationSystemAndEvaluation(String name) {
        this.setName(name);
        this.biometricCharacteristics = new ArrayList<>();
        this.baseEvaluation = new EnrichedBaseEvaluation();
        this.targetArchitecture = new EnrichedTargetArchitecture();
        this.evaluationCriteriaGrants = new ArrayList<>();
        this.modellingFinished = false;
        this.setModelledElementDetail(new ModelledElementDetail());
    }

    public EnrichedBiometricAuthenticationSystemAndEvaluation(BiometricAuthenticationSystemAndEvaluation biometricAuthenticationSystemAndEvaluation) {
        this.setId(biometricAuthenticationSystemAndEvaluation.getId());
        this.setName(biometricAuthenticationSystemAndEvaluation.getName());
        super.setReference(biometricAuthenticationSystemAndEvaluation.getReference());
        this.setId(biometricAuthenticationSystemAndEvaluation.getId());
        this.setCreatedDate(biometricAuthenticationSystemAndEvaluation.getCreatedDate());
        this.setLastModifiedDate(biometricAuthenticationSystemAndEvaluation.getLastModifiedDate());
        this.modellingProgress = biometricAuthenticationSystemAndEvaluation.getModellingProgress();
        this.modellingFinished = biometricAuthenticationSystemAndEvaluation.isModellingFinished();
        this.setModelledElementDetail(biometricAuthenticationSystemAndEvaluation.getModelledElementDetail());
    }


    public boolean isModellingFinished() {
        return modellingFinished;
    }

    public void setModellingFinished(boolean modellingFinished) {
        this.modellingFinished = modellingFinished;
    }

    public EnrichedBaseEvaluation getBaseEvaluation() {
        return baseEvaluation;
    }

    public void setBaseEvaluation(EnrichedBaseEvaluation baseEvaluation) {
        this.baseEvaluation = baseEvaluation;
    }

    public List<BiometricCharacteristic> getBiometricCharacteristics() {
        return biometricCharacteristics;
    }

    public void setBiometricCharacteristics(List<BiometricCharacteristic> biometricCharacteristics) {
        this.biometricCharacteristics = biometricCharacteristics;
    }

    public EnrichedTargetArchitecture getTargetArchitecture() {
        return targetArchitecture;
    }

    public void setTargetArchitecture(EnrichedTargetArchitecture targetArchitecture) {
        this.targetArchitecture = targetArchitecture;
    }

    public ModellingProgress getModellingProgress() {
        return modellingProgress;
    }

    public void setModellingProgress(ModellingProgress modellingProgress) {
        this.modellingProgress = modellingProgress;
    }

    public List<EnrichedEvaluationCriteriaGrant> getEvaluationCriteriaGrants() {
        return evaluationCriteriaGrants;
    }

    public void setEvaluationCriteriaGrants(List<EnrichedEvaluationCriteriaGrant> evaluationCriteriaGrants) {
        this.evaluationCriteriaGrants = evaluationCriteriaGrants;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "EnrichedBiometricAuthenticationSystemAndEvaluation{" +
                "characteristics=" + biometricCharacteristics +
                ", targetArchitecture=" + targetArchitecture +
                ", baseEvaluation=" + baseEvaluation +
                ", evaluationCriteriaGrants=" + evaluationCriteriaGrants +
                ", modellingProgress=" + modellingProgress +
                ", modellingFinished=" + modellingFinished +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                "} " + super.toString();
    }
}

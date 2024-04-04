package io.basemod.app.base;

import io.basemod.app.architecture.TargetArchitecture;
import io.basemod.app.domain.ModelledElementDetail;
import io.basemod.app.domain.ReferencedNamedModelledDomainElement;
import io.basemod.app.evaluation.BaseEvaluation;
import io.basemod.app.evaluation.criteria.grant.EvaluationCriteriaGrant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BiometricAuthenticationSystemAndEvaluation extends ReferencedNamedModelledDomainElement {

    private List<String> biometricCharacteristicIds;
    private TargetArchitecture targetArchitecture;
    private BaseEvaluation baseEvaluation;
    private List<EvaluationCriteriaGrant> evaluationCriteriaGrants;
    private ModellingProgress modellingProgress;
    private boolean modellingFinished;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;


    public BiometricAuthenticationSystemAndEvaluation() {
        biometricCharacteristicIds = new ArrayList<>();
        targetArchitecture = new TargetArchitecture();
        baseEvaluation = new BaseEvaluation();
        evaluationCriteriaGrants = new ArrayList<>();
    }

    public BiometricAuthenticationSystemAndEvaluation(Reference reference, List<String> biometricCharacteristicIds, TargetArchitecture targetArchitecture, BaseEvaluation baseEvaluation, List<EvaluationCriteriaGrant> evaluationCriteriaGrants, ModellingProgress modellingProgress, boolean modellingFinished, Instant createdDate, Instant lastModifiedDate) {
        super(reference);
        this.biometricCharacteristicIds = biometricCharacteristicIds;
        this.targetArchitecture = targetArchitecture;
        this.baseEvaluation = baseEvaluation;
        this.evaluationCriteriaGrants = evaluationCriteriaGrants;
        this.modellingProgress = modellingProgress;
        this.modellingFinished = modellingFinished;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public BiometricAuthenticationSystemAndEvaluation(String id, ModelledElementDetail modelledElementDetail, String name, Reference reference, List<String> biometricCharacteristicIds, TargetArchitecture targetArchitecture, BaseEvaluation baseEvaluation, List<EvaluationCriteriaGrant> evaluationCriteriaGrants, ModellingProgress modellingProgress, boolean modellingFinished, Instant createdDate, Instant lastModifiedDate) {
        super(id, modelledElementDetail, name, reference);
        this.biometricCharacteristicIds = biometricCharacteristicIds;
        this.targetArchitecture = targetArchitecture;
        this.baseEvaluation = baseEvaluation;
        this.evaluationCriteriaGrants = evaluationCriteriaGrants;
        this.modellingProgress = modellingProgress;
        this.modellingFinished = modellingFinished;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public BiometricAuthenticationSystemAndEvaluation(ModelledElementDetail modelledElementDetail, String name, Reference reference, List<String> biometricCharacteristicIds, TargetArchitecture targetArchitecture, BaseEvaluation baseEvaluation, List<EvaluationCriteriaGrant> evaluationCriteriaGrants, ModellingProgress modellingProgress, boolean modellingFinished, Instant createdDate, Instant lastModifiedDate) {
        super(modelledElementDetail, name, reference);
        this.biometricCharacteristicIds = biometricCharacteristicIds;
        this.targetArchitecture = targetArchitecture;
        this.baseEvaluation = baseEvaluation;
        this.evaluationCriteriaGrants = evaluationCriteriaGrants;
        this.modellingProgress = modellingProgress;
        this.modellingFinished = modellingFinished;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public BiometricAuthenticationSystemAndEvaluation(String name, Reference reference, List<String> biometricCharacteristicIds, TargetArchitecture targetArchitecture, BaseEvaluation baseEvaluation, List<EvaluationCriteriaGrant> evaluationCriteriaGrants, ModellingProgress modellingProgress, boolean modellingFinished, Instant createdDate, Instant lastModifiedDate) {
        super(name, reference);
        this.biometricCharacteristicIds = biometricCharacteristicIds;
        this.targetArchitecture = targetArchitecture;
        this.baseEvaluation = baseEvaluation;
        this.evaluationCriteriaGrants = evaluationCriteriaGrants;
        this.modellingProgress = modellingProgress;
        this.modellingFinished = modellingFinished;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public BiometricAuthenticationSystemAndEvaluation(String id, String name, Reference reference, List<String> biometricCharacteristicIds, TargetArchitecture targetArchitecture, BaseEvaluation baseEvaluation, List<EvaluationCriteriaGrant> evaluationCriteriaGrants, ModellingProgress modellingProgress, boolean modellingFinished, Instant createdDate, Instant lastModifiedDate) {
        super(id, name, reference);
        this.biometricCharacteristicIds = biometricCharacteristicIds;
        this.targetArchitecture = targetArchitecture;
        this.baseEvaluation = baseEvaluation;
        this.evaluationCriteriaGrants = evaluationCriteriaGrants;
        this.modellingProgress = modellingProgress;
        this.modellingFinished = modellingFinished;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public BiometricAuthenticationSystemAndEvaluation(List<String> biometricCharacteristicIds, TargetArchitecture targetArchitecture, BaseEvaluation baseEvaluation, List<EvaluationCriteriaGrant> evaluationCriteriaGrants, ModellingProgress modellingProgress, boolean modellingFinished, Instant createdDate, Instant lastModifiedDate) {
        this.biometricCharacteristicIds = biometricCharacteristicIds;
        this.targetArchitecture = targetArchitecture;
        this.baseEvaluation = baseEvaluation;
        this.evaluationCriteriaGrants = evaluationCriteriaGrants;
        this.modellingProgress = modellingProgress;
        this.modellingFinished = modellingFinished;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }



    public boolean isModellingFinished() {
        return modellingFinished;
    }

    public void setModellingFinished(boolean modellingFinished) {
        this.modellingFinished = modellingFinished;
    }

    public BaseEvaluation getBaseEvaluation() {
        return baseEvaluation;
    }

    public void setBaseEvaluation(BaseEvaluation baseEvaluation) {
        this.baseEvaluation = baseEvaluation;
    }

    public List<String> getBiometricCharacteristicIds() {
        return biometricCharacteristicIds;
    }

    public void setBiometricCharacteristicIds(List<String> biometricCharacteristicIds) {
        this.biometricCharacteristicIds = biometricCharacteristicIds;
    }

    public void addBiometricCharacteristicId(String biometricCharacteristicId) {
        if(this.biometricCharacteristicIds==null) {
            this.biometricCharacteristicIds = new ArrayList<>();
        }
        this.biometricCharacteristicIds.add(biometricCharacteristicId);
    }

    public TargetArchitecture getTargetArchitecture() {
        return targetArchitecture;
    }

    public void setTargetArchitecture(TargetArchitecture targetArchitecture) {
        this.targetArchitecture = targetArchitecture;
    }

    public ModellingProgress getModellingProgress() {
        return modellingProgress;
    }

    public void setModellingProgress(ModellingProgress modellingProgress) {
        this.modellingProgress = modellingProgress;
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

    public List<EvaluationCriteriaGrant> getEvaluationCriteriaGrants() {
        return evaluationCriteriaGrants;
    }

    public void setEvaluationCriteriaGrants(List<EvaluationCriteriaGrant> evaluationCriteriaGrants) {
        this.evaluationCriteriaGrants = evaluationCriteriaGrants;
    }

    public void addEvaluationCriteriaGrant(EvaluationCriteriaGrant evaluationCriteriaGrantToAdd) {
        if(this.evaluationCriteriaGrants==null) {
            this.evaluationCriteriaGrants = new ArrayList<>();
        }
        this.evaluationCriteriaGrants.add(evaluationCriteriaGrantToAdd);
    }

    @Override
    public String toString() {
        return "BiometricAuthenticationSystemAndEvaluation{" +
                "characteristics=" + biometricCharacteristicIds +
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

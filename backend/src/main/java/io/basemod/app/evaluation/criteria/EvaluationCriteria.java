package io.basemod.app.evaluation.criteria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.basemod.app.base.*;
import io.basemod.app.domain.ReferencedNamedModelledDomainElement;
import io.basemod.app.evaluation.criteria.grant.GrantingRule;
import io.basemod.app.evaluation.experiment.evaluation.criteria.ExperimentSpecificEvaluationCriteria;
import io.basemod.app.evaluation.implementation.ImplementationSpecificEvaluationCriteria;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExperimentSpecificEvaluationCriteria.class, name = EvaluationCriteria.EXPERIMENT_SPECIFIC_TYPE),
        @JsonSubTypes.Type(value = ImplementationSpecificEvaluationCriteria.class, name = EvaluationCriteria.IMPLEMENTATION_SPECIFIC_TYPE) }
)
public abstract class EvaluationCriteria extends ReferencedNamedModelledDomainElement {

    public static final String EXPERIMENT_SPECIFIC_TYPE = "experiment_specific";
    public static final String ENRICHED_EXPERIMENT_SPECIFIC_TYPE = "enriched_experiment_specific";
    public static final String IMPLEMENTATION_SPECIFIC_TYPE = "implementation_specific";
    private String description;
    private GrantingRule grantedRule;
    private GrantingRule quasiGrantedRule;
    private GrantingRule notGrantedRule;
    private EvaluationCriteriaCategory category;

    public EvaluationCriteria() {
        super();
    }

    public EvaluationCriteria(String name, Reference reference, String description, GrantingRule grantedRule, GrantingRule quasiGrantedRule, GrantingRule notGrantedRule, EvaluationCriteriaCategory category) {
        super(name, reference);
        this.description = description;
        this.grantedRule = grantedRule;
        this.quasiGrantedRule = quasiGrantedRule;
        this.notGrantedRule = notGrantedRule;
        this.category = category;
    }

    public GrantingRule getGrantedRule() {
        return grantedRule;
    }

    public void setGrantedRule(GrantingRule grantedRule) {
        this.grantedRule = grantedRule;
    }

    public GrantingRule getQuasiGrantedRule() {
        return quasiGrantedRule;
    }

    public void setQuasiGrantedRule(GrantingRule quasiGrantedRule) {
        this.quasiGrantedRule = quasiGrantedRule;
    }

    public GrantingRule getNotGrantedRule() {
        return notGrantedRule;
    }

    public void setNotGrantedRule(GrantingRule notGrantedRule) {
        this.notGrantedRule = notGrantedRule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EvaluationCriteriaCategory getCategory() {
        return category;
    }

    public void setCategory(EvaluationCriteriaCategory category) {
        this.category = category;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return "EvaluationCriteria{" +
                "description='" + description + '\'' +
                ", grantedRule=" + grantedRule +
                ", quasiGrantedRule=" + quasiGrantedRule +
                ", notGrantedRule=" + notGrantedRule +
                ", category=" + category +
                "} " + super.toString();
    }
}

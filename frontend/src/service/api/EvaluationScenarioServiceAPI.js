import api from './api';
import helper from "./apiHelperService";
import constants from "../../helpers/constants";
import transformers from "@/service/transformers";
import Vue from "vue";
const API_URL_EXPERIMENT_SPECIFIC_EVALUATION_CRITERIA_LIFECYCLE_AWARE = '/api/lcaware/experimentSpecificEvaluationCriterias';
const API_URL_EXPERIMENT_SPECIFIC_EVALUATION_CRITERIA = '/api/experimentSpecificEvaluationCriterias';
const API_URL_IMPLEMENTATION_SPECIFIC_EVALUATION_CRITERIA_LIFECYCLE_AWARE = '/api/lcaware/implementationSpecificEvaluationCriterias';
const API_URL_IMPLEMENTATION_SPECIFIC_EVALUATION_CRITERIA = '/api/implementationSpecificEvaluationCriterias';
const API_URL_RESULT_METRIC_LIFECYCLE_AWARE = '/api/lcaware/resultMetrics';
const API_URL_RESULT_METRIC = '/api/resultMetrics';
const API_URL_BASE_EVALUATION_EXTENSION_LIFECYCLE_AWARE = '/api/lcaware/baseEvaluationExtensions';
const API_URL_BASE_EVALUATION_EXTENSION = '/api/baseEvaluationExtensions';

export class EvaluationScenarioServiceAPI {

    constructor(){
    }

    static getResultMetrics() {
        return api.get(API_URL_RESULT_METRIC_LIFECYCLE_AWARE);
    }

    static getExperimentSpecificEvaluationCriterias() {
        return api.get(API_URL_EXPERIMENT_SPECIFIC_EVALUATION_CRITERIA_LIFECYCLE_AWARE);
    }

    static getImplementationSpecificEvaluationCriterias() {
        return api.get(API_URL_IMPLEMENTATION_SPECIFIC_EVALUATION_CRITERIA_LIFECYCLE_AWARE);
    }

    static getExperimentSpecificEvaluationCriteriasByCategory(categoryName) {
        return api.get(API_URL_EXPERIMENT_SPECIFIC_EVALUATION_CRITERIA_LIFECYCLE_AWARE+"/search/findByCategory",
            {params: {"category": categoryName.toUpperCase()}});
    }

    static getExperimentSpecificEvaluationCriteriasById(criteriaIds) {
        return api.get(API_URL_EXPERIMENT_SPECIFIC_EVALUATION_CRITERIA_LIFECYCLE_AWARE+"/search/findAllByIdIn", {params: {"ids": criteriaIds.join(",")}});
    }

    static getImplementationSpecificEvaluationCriteriasByCategory(categoryName) {
        return api.get(API_URL_IMPLEMENTATION_SPECIFIC_EVALUATION_CRITERIA_LIFECYCLE_AWARE+"/search/findByCategory",
            {params: {"category": categoryName.toUpperCase()}});
    }

    static createUpdateEvaluationCriteria(newUpdatedCriteria) {
        if(newUpdatedCriteria.type===constants.evaluationTypes.experimentSpecific) {
            return helper.createUpdateDomainObject(API_URL_EXPERIMENT_SPECIFIC_EVALUATION_CRITERIA,
                transformers.transformExperimentSpecificEvaluationCriteriaForPersistence(newUpdatedCriteria));
        } else {
            return helper.createUpdateDomainObject(API_URL_IMPLEMENTATION_SPECIFIC_EVALUATION_CRITERIA, newUpdatedCriteria);
        }

    }

    static deleteImplementationEvaluationCriteria(implementationEvaluationCriteria) {
        return api.delete(API_URL_IMPLEMENTATION_SPECIFIC_EVALUATION_CRITERIA_LIFECYCLE_AWARE+"/"+implementationEvaluationCriteria.id);
    }

    static deleteExperimentalEvaluationCriteria(experimentalEvaluationCriteria) {
        return api.delete(API_URL_EXPERIMENT_SPECIFIC_EVALUATION_CRITERIA_LIFECYCLE_AWARE+"/"+experimentalEvaluationCriteria.id);
    }

    static deleteResultMetric(resultMetric) {
        return api.delete(API_URL_RESULT_METRIC_LIFECYCLE_AWARE+"/"+resultMetric.id);
    }

    static createUpdateResultMetric(newUpdatedMetric) {
        return helper.createUpdateDomainObject(API_URL_RESULT_METRIC, newUpdatedMetric);
    }

    static createUpdateBaseEvaluationExtension(baseEvaluationExtension) {
        // transform before persistence
        baseEvaluationExtension.baseEvaluationToMerge =
            transformers.transformBaseEvaluationForPersistence(baseEvaluationExtension.baseEvaluationToMerge);
        baseEvaluationExtension.evaluationCriteriaGrantsToMerge =
            transformers.transformEvaluationCriteriaGrantForPersistence(baseEvaluationExtension.evaluationCriteriaGrantsToMerge);

        Vue.$log.debug("...persisting base evaluation extension", baseEvaluationExtension);
        return helper.createUpdateDomainObject(API_URL_BASE_EVALUATION_EXTENSION, baseEvaluationExtension);
    }

    static summarizeEvaluationCriteriaGrants(evaluationCriteriaGrants) {

        Vue.$log.debug("\t...summarizing evaluation criteria grants", evaluationCriteriaGrants);
        if(evaluationCriteriaGrants && evaluationCriteriaGrants.length > 0) {
            let notDecidedCounter = 0, notGrantedCounter = 0, quasiGrantedCounter = 0, grantedCounter = 0;
            evaluationCriteriaGrants.forEach((criteriaGrant) => {
                if(criteriaGrant.grantingLevel) {
                    if(criteriaGrant.grantingLevel.value) {
                        switch (criteriaGrant.grantingLevel.value) {
                            case constants.grantingLevels.notDecidedYet.value:
                                notDecidedCounter++;
                                break;
                            case constants.grantingLevels.notGranted.value:
                                notGrantedCounter++;
                                break;
                            case constants.grantingLevels.quasiGranted.value:
                                quasiGrantedCounter++;
                                break;
                            case constants.grantingLevels.granted.value:
                                grantedCounter++;
                                break;
                        }
                    } else {
                        switch (criteriaGrant.grantingLevel) {
                            case constants.grantingLevels.notDecidedYet.value:
                                notDecidedCounter++;
                                break;
                            case constants.grantingLevels.notGranted.value:
                                notGrantedCounter++;
                                break;
                            case constants.grantingLevels.quasiGranted.value:
                                quasiGrantedCounter++;
                                break;
                            case constants.grantingLevels.granted.value:
                                grantedCounter++;
                                break;
                        }
                    }
                }
            });

            let toReturn = [];
            if(notDecidedCounter>0) {
                toReturn.push(notDecidedCounter+" not decided yet");
            }
            if(notGrantedCounter>0) {
                toReturn.push(notGrantedCounter+" not granted");
            }
            if(quasiGrantedCounter>0) {
                toReturn.push(quasiGrantedCounter+" quasi-granted");
            }
            if(grantedCounter>0) {
                toReturn.push(grantedCounter+" granted");
            }

            return toReturn.join(", ");
        }

        return "-";
    }

    static deleteBaseEvaluationExtension(baseEvaluationExtension) {
        return api.delete(API_URL_BASE_EVALUATION_EXTENSION_LIFECYCLE_AWARE+"/"+baseEvaluationExtension.id);
    }

    static mergeWithExistingGrants(usedEvaluationCriteria, evaluationCriteriaGrants) {
        /*
        rules:
            1. grants of criteria that already existed and are still there need to be untouched
            2. grants of criteria that were removed will be removed as well
            3. new criteria will create new grants initialized with not decided
         */

        Vue.$log.debug("evaluate merge of evaluation criteria grants with all criteria used in evaluations:", usedEvaluationCriteria);
        Vue.$log.debug("\t...evaluation criteria grants:", evaluationCriteriaGrants);

        // filter
        evaluationCriteriaGrants = evaluationCriteriaGrants.filter(grant => {
            return evaluationCriteriaGrants.filter(criteria => {
                return grant.evaluationCriteria.id === criteria.id
            }).length > 0
        });

        const criteriaToAddToGrants = usedEvaluationCriteria.filter(criteria => {
            return evaluationCriteriaGrants.filter(grant => {
                return grant.evaluationCriteria.id === criteria.id
            }).length === 0
        });

        Vue.$log.debug("Overall, "+criteriaToAddToGrants.length+
            " grants need to be created based on criteria", criteriaToAddToGrants);

        let newEvaluationCriteriaGrants = [];
        criteriaToAddToGrants.forEach(criteria => {
            newEvaluationCriteriaGrants.push({
                evaluationCriteria: criteria,
                grantingLevel: constants.grantingLevels.notDecidedYet,
            })
        });

        return evaluationCriteriaGrants.concat(newEvaluationCriteriaGrants);

    }

}

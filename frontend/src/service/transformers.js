import constants from "../helpers/constants";
import {Util as Utils, Util} from "../helpers/util";
import Vue from "vue";

let transformers = {

    extractAttributeFromResponse: function(response, args) {
        if(!response) {
            return response;
        } else {
            let resultArray = [];

            for(const arg of args) {
                if(JSON.parse(response)._embedded[arg]) {
                    resultArray = resultArray.concat(JSON.parse(response)._embedded[arg]);
                }
            }

            return resultArray;
        }
    },

    getExtractDomainObjectFromResponseTransformer : function(...nameDomainObjects) {
        const context = this;
        return [function (data) {
            return context.extractAttributeFromResponse(data, nameDomainObjects);
        }];
    },

    furtherEnrichBaseObjectsFromResponseTransformer : function() {

        return [function (data) {

            let remoteBaseRepresentation = JSON.parse(data);

            console.debug("Got "+remoteBaseRepresentation.length+"  BASE from repository, do enrich for presentation ", remoteBaseRepresentation);

            // do manipulation for gui in case of data model differences of frontend and backend
            remoteBaseRepresentation.forEach(function (base) {

                transformers.transformSingleBaseForPresentation(base);

            });

            Vue.$log.debug('base representations after transformation');
            Vue.$log.debug(remoteBaseRepresentation);

            return remoteBaseRepresentation;

        }];
    },

    furtherEnrichBaseObjectFromResponseTransformer : function() {

        return [function (data) {

            let remoteBaseRepresentation = JSON.parse(data);

            Vue.$log.debug("Got Base from repository, do enrich for presentation ", remoteBaseRepresentation);

            // do manipulation for gui in case of data model differences of frontend and backend
            if(remoteBaseRepresentation.entity) {
                transformers.transformSingleBaseForPresentation(remoteBaseRepresentation.entity);

                Vue.$log.debug('full base by name request after transformation');
                Vue.$log.debug(remoteBaseRepresentation);


            } else {
                remoteBaseRepresentation["entity"] = {
                    id: null,
                    name: "",
                    modellingProgress: "",
                    characteristics:[],
                    targetArchitecture:{},
                    baseEvaluation: {
                        implementationSpecificEvaluationResults: [],
                        experimentSpecificEvaluation: {},
                    },
                    evaluationCriteriaGrants: [],
                    reference: {},
                    modelledElementDetail: {},
                };
            }

            return remoteBaseRepresentation;

        }];
    },

    transformSingleBaseForPresentation: function(base) {
        //modify protected resource
        if(base.targetArchitecture
            && base.targetArchitecture.resourceToProtect) {
            base.targetArchitecture["resourceToProtectName"] = base.targetArchitecture.resourceToProtect;
        }

        if (base.modellingProgress === constants.modellingProgress.reference
            || base.modellingProgress === constants.modellingProgress.evaluation
            || base.modellingProgress === constants.modellingProgress.evaluationCriteriaGrants) {


            // evaluation criteria grants
            base.evaluationCriteriaGrants = this.transformEvaluationCriteriaGrantsForPresentation(base.evaluationCriteriaGrants);
        }
    },

    transformEvaluationCriteriaGrantsForPresentation: function(evaluationCriteriaGrants) {
        if(evaluationCriteriaGrants && evaluationCriteriaGrants.length>0) {
            console.debug("Transform evaluation criteria grants for presentation");
            evaluationCriteriaGrants.forEach((evaluationCriteriaGrant) => {
                evaluationCriteriaGrant.grantingLevel = {
                    name: constants.grantingLevels.getGrantingLevelName(evaluationCriteriaGrant.grantingLevel),
                    value: evaluationCriteriaGrant.grantingLevel,
                };
            })
        }

        return evaluationCriteriaGrants;
    },

    transformBaseForPersistence: function(behavioralAuthenticationSystemToCreateOrUpdate, externalExport = false) {

        // do deep copy to not destroy visualisation
        let baseModifiedForPersistence = Util.deepCopyObject(behavioralAuthenticationSystemToCreateOrUpdate);

        Vue.$log.debug("base before transformation before persistence", baseModifiedForPersistence);
        // transform grantingLevel, used datasets, biometric systems representation from any criteria

        let usedDatasets=[], definedBiometricSystems=[], implementationSpecificEvaluationCriteria=[], experimentSpecificEvaluationCriteriaIds = [];

        // in any case, we will have characteristics we need to transform so that the id is transmitted
        baseModifiedForPersistence["biometricCharacteristicIds"] = baseModifiedForPersistence.biometricCharacteristics.map(characteristic => {
            return characteristic.id;
        });
        delete baseModifiedForPersistence.biometricCharacteristics;

        // if target architecture is set, modify for persistence
        if(behavioralAuthenticationSystemToCreateOrUpdate.targetArchitecture
            && behavioralAuthenticationSystemToCreateOrUpdate.targetArchitecture.decisionDeviceCategories
            && behavioralAuthenticationSystemToCreateOrUpdate.targetArchitecture.decisionDeviceCategories.length>0
            && !behavioralAuthenticationSystemToCreateOrUpdate.targetArchitecture.skipTargetArchitecture) {

            Vue.$log.debug("\t...transform target architecture for persistence", baseModifiedForPersistence.targetArchitecture);

            baseModifiedForPersistence.targetArchitecture["dataCapturingDeviceCategoryIds"] =
                baseModifiedForPersistence.targetArchitecture.dataCapturingDeviceCategories.map(category => {
                    return category.id;
                });
            delete baseModifiedForPersistence.targetArchitecture.dataCapturingDeviceCategories;

            baseModifiedForPersistence.targetArchitecture["signalProcessingDeviceCategoryIds"] =
                baseModifiedForPersistence.targetArchitecture.signalProcessingDeviceCategories.map(category => {
                    return category.id;
                });
            delete baseModifiedForPersistence.targetArchitecture.signalProcessingDeviceCategories;

            baseModifiedForPersistence.targetArchitecture["signatureCreationDeviceCategoryIds"] =
                baseModifiedForPersistence.targetArchitecture.signatureCreationDeviceCategories.map(category => {
                    return category.id;
                });
            delete baseModifiedForPersistence.targetArchitecture.signatureCreationDeviceCategories;

            baseModifiedForPersistence.targetArchitecture["dataStorageDeviceCategoryIds"] =
                baseModifiedForPersistence.targetArchitecture.dataStorageDeviceCategories.map(category => {
                    return category.id;
                });
            delete baseModifiedForPersistence.targetArchitecture.dataStorageDeviceCategories;

            baseModifiedForPersistence.targetArchitecture["matchingDeviceCategoryIds"] =
                baseModifiedForPersistence.targetArchitecture.matchingDeviceCategories.map(category => {
                    return category.id;
                });
            delete baseModifiedForPersistence.targetArchitecture.matchingDeviceCategories;

            baseModifiedForPersistence.targetArchitecture["decisionDeviceCategoryIds"] =
                baseModifiedForPersistence.targetArchitecture.decisionDeviceCategories.map(category => {
                    return category.id;
                });
            delete baseModifiedForPersistence.targetArchitecture.decisionDeviceCategories;

            baseModifiedForPersistence.targetArchitecture["resourceToProtectId"] =
                baseModifiedForPersistence.targetArchitecture.resourceToProtect.id;
            delete baseModifiedForPersistence.targetArchitecture.resourceToProtect;
        }

        // do further modifications based on respective modelling progress
        if (baseModifiedForPersistence.modellingProgress !== constants.modellingProgress.characteristics) {

            baseModifiedForPersistence.baseEvaluation =
                this.transformBaseEvaluationForPersistence(baseModifiedForPersistence.baseEvaluation);

        }

        //evaluation criteria grant
        if(baseModifiedForPersistence.evaluationCriteriaGrants
            && baseModifiedForPersistence.evaluationCriteriaGrants.length>0) {

            baseModifiedForPersistence.evaluationCriteriaGrants =
                this.transformEvaluationCriteriaGrantForPersistence(baseModifiedForPersistence.evaluationCriteriaGrants);

        }

        if(externalExport && definedBiometricSystems.length>0) {
            // add type specifics for processing steps
            definedBiometricSystems.forEach(biometricSystem => {

                biometricSystem.signalProcessingSteps.forEach(step => {
                   step["type"] = constants.processingsteptypes.simple;
                });

                biometricSystem.furtherProcessingSteps.forEach(step => {
                    step["type"] = constants.processingsteptypes.simple;
                    if(step.processingType === constants.processingsteptypes.decision) {
                        step["type"] = constants.processingsteptypes.decision;
                    }
                })
            });
        }


        Vue.$log.debug("base after transformation before persistence", baseModifiedForPersistence);

        return [baseModifiedForPersistence,
            usedDatasets,
            definedBiometricSystems,
            implementationSpecificEvaluationCriteria,
            experimentSpecificEvaluationCriteriaIds];

    },

    transformEvaluationCriteriaGrantForPersistence: function(evaluationCriteriaGrants) {

        Vue.$log.debug("normalizing grant level and evaluation criteria of evaluation criteria grants", evaluationCriteriaGrants);

        //normalize grant level
        evaluationCriteriaGrants.forEach(evaluationCriteriaGrant => {
            if(evaluationCriteriaGrant.evaluationCriteria.type === constants.evaluationTypes.implementationSpecific) {
                evaluationCriteriaGrant["implementationSpecificEvaluationCriteriaId"] =
                    evaluationCriteriaGrant.evaluationCriteria.id;
            } else {
                evaluationCriteriaGrant["experimentSpecificEvaluationCriteriaId"] =
                    evaluationCriteriaGrant.evaluationCriteria.id;
            }
            delete evaluationCriteriaGrant.evaluationCriteria;

            if(evaluationCriteriaGrant.grantingLevel.value) {
                evaluationCriteriaGrant.grantingLevel = evaluationCriteriaGrant.grantingLevel.value;
            }
        });

        return evaluationCriteriaGrants;

    },

    transformBaseEvaluationForPersistence: function(baseEvaluation) {

        // 2.2 Experiment-Based Experiments


        if (baseEvaluation.experimentSpecificEvaluation.experimentSpecificEvaluationSetups
            && baseEvaluation.experimentSpecificEvaluation.experimentSpecificEvaluationSetups.length > 0) {

            let newExperimentSpecificEvaluation = {};

            newExperimentSpecificEvaluation = Utils.deepCopyObject(baseEvaluation.experimentSpecificEvaluation);

            // modify to only store ids for used datasets
            Vue.$log.debug("Keeping only Ids of usedDatasets");

            newExperimentSpecificEvaluation["usedDatasetIds"] =
                baseEvaluation.experimentSpecificEvaluation.usedDatasets.map(usedDataset => usedDataset.id);
            delete newExperimentSpecificEvaluation["usedDatasets"]

            Vue.$log.debug("Keeping only Ids of all biometric systems created for experiments");

            newExperimentSpecificEvaluation["biometricSystemIds"] =
                baseEvaluation.experimentSpecificEvaluation.biometricSystems.map(biometricSystem => biometricSystem.id);
            delete newExperimentSpecificEvaluation["biometricSystems"]

            Vue.$log.debug("Keeping only ids of biometric systems per specific evaluation result");
            newExperimentSpecificEvaluation.experimentSpecificEvaluationSetups.forEach(function (evaluationResult) {

                evaluationResult["biometricSystemIds"] = evaluationResult.biometricSystems.map(biometricSystem => biometricSystem.id);
                delete evaluationResult.biometricSystems;

                // replace metric with result metric id for backend
                evaluationResult.specificResults.forEach(function(specificResult) {
                    specificResult["resultMetricId"] = specificResult.resultMetric.id;
                    delete specificResult.resultMetric;
                });

            });

            Vue.$log.debug("\t...set customized experiment specific evaluation", newExperimentSpecificEvaluation);
            baseEvaluation.experimentSpecificEvaluation = newExperimentSpecificEvaluation;

        }

        // 2.3 ImplementationBased

        if (baseEvaluation.implementationSpecificEvaluationResults
            && baseEvaluation.implementationSpecificEvaluationResults.length > 0) {

            let newImplementationSpecificEvaluationResults = [];

            newImplementationSpecificEvaluationResults = Utils.deepCopyObject(baseEvaluation.implementationSpecificEvaluationResults);

            newImplementationSpecificEvaluationResults.map(evaluationResult => {
                evaluationResult["criteriaId"] = evaluationResult.criteria.id;
                delete evaluationResult.criteria;

                evaluationResult["affectedDeviceCategoryIds"] = evaluationResult.affectedDeviceCategories.map(deviceCategory => {
                    return deviceCategory.id;
                })
                delete evaluationResult.affectedDeviceCategories;
            })

            baseEvaluation.implementationSpecificEvaluationResults = newImplementationSpecificEvaluationResults;
        }

        return baseEvaluation;
    },

    transformDeviceCategoryForPersistence: function(deviceCategoryToCreateOrUpdate) {

        // do deep copy to not destroy visualisation
        let deviceCategoryModifiedForPersistence = Util.deepCopyObject(deviceCategoryToCreateOrUpdate);
        Vue.$log.debug("\t..got request to transform device category for persistence", deviceCategoryToCreateOrUpdate);

        // replace sensors with sensorIds
        deviceCategoryModifiedForPersistence["sensorIds"] = deviceCategoryToCreateOrUpdate.sensors.map(sensor => {
            return sensor.id;
        });

        Vue.$log.debug("\t\t... transformed device category for persistence", deviceCategoryModifiedForPersistence);

        return deviceCategoryModifiedForPersistence;
    },

    transformExperimentSpecificEvaluationCriteriaForPersistence: function(experimentSpecificCriteriaToCreateOrUpdate) {

        // do deep copy to not destroy visualisation
        let experimentSpecificCriteriaModifiedForPersistence = Util.deepCopyObject(experimentSpecificCriteriaToCreateOrUpdate);

        Vue.$log.debug("\t..got request to transform experiment specific evaluation criteria for persistence", experimentSpecificCriteriaToCreateOrUpdate);

        // replace result metrics with result metric Ids, if SET!

        if(experimentSpecificCriteriaToCreateOrUpdate.resultMetrics
           &&  experimentSpecificCriteriaToCreateOrUpdate.resultMetrics.length>0) {

            experimentSpecificCriteriaModifiedForPersistence["resultMetricIds"] = [];
            experimentSpecificCriteriaModifiedForPersistence.resultMetricIds =
                experimentSpecificCriteriaToCreateOrUpdate.resultMetrics.map(resultMetric => {
                    return resultMetric.id;
                });
            delete experimentSpecificCriteriaModifiedForPersistence.resultMetrics;
        }

        Vue.$log.debug("\t\t... transformed experiment specific evaluation criteria for persistence", experimentSpecificCriteriaModifiedForPersistence);
        return experimentSpecificCriteriaModifiedForPersistence;
    },




};

export default transformers;
const constants = {
    eventNames: {
        global: {
            editBaseEvent: "edit-base",
            editCharacteristicsEvent: "edit-characteristics",
            editTargetArchitectureEvent: "edit-target-architecture",
            editEvaluationEvent: "edit-evaluation",
            updateBaseRequired: "update-base-required",
            updateDatasetsRequired: "update-datasets-required",
            userLoggedIn: "userLoggedIn",
            userLoggedOut: "userLoggedOut",
            userHasSession: "userHasSession",
        },
        local: {
            artifactNeedsCreation: "artifact-needs-creation",
            artifactsSelected: "artifacts-selected",
            artifactAdded:"artifact-added",
            artifactCreatedUpdated:"artifact-created-updated",
            cancelEditing: "cancel-editing",
            closeModelDialog: 'close-model-dialog',
            closeDialog: 'close-dialog',
            openReviewDialog: 'open-review-dialog',
            showArtifactDetails: 'show-artifact-details',
            evaluationCriteriaSelected: 'evaluation-criteria-selected',
            artifactReviewStateUpdated: "artifact-review-state-updated",
            targetArtifactsToReviewReloadRequired: "target-artefacts-to-review-reload-required",
            deleteArtifact: "delete-artifact",
            editArtifact: "edit-artifact",
            duplicateArtifact: "duplicate-artifact",
            addSpecificsArtifact: "add-specifics-artifact",
            amountOfArtifactsShownChanged: "amount-of-artifacts-changed",
        }
    },
    processingsteptypes: {
        windowing: "windowing",
        feature: "feature",
        simple: "simple",
        matching: "matching",
        storage: "storage",
        decision: "decision",
        transformer: "transformer",
        filter: "filter",
        getAll: function() {
            return [
                this.windowing,
                this.feature,
                this.matching,
                this.storage,
                this.decision,
                this.transformer,
                this.filter
            ]
        },
        getAllNoFeature: function() {
            return [
                this.windowing,
                this.matching,
                this.storage,
                this.decision,
                this.transformer,
                this.filter
            ]
        }
    },
    modellingProgress: {
        characteristics: "characteristics",
        targetArchitecture: "targetArchitecture",
        evaluation: "evaluation",
        reference: "reference",
        evaluationCriteriaGrants: "evaluationCriteriaGrants",
    },
    characteristics: {
        types: {
            physiological: "physiological",
            behavioral: "behavioral",
            soft: "soft",
            enriched_soft: "enriched_soft",
            continuous: "continuous",
            transitional: "transitional",
        }
    }
    ,
    referenceAssetType: {
        dataset: "dataset",
        base: "base",
        processingStep: "processingStep",
        evaluationCriteria: "evaluationCriteria",
        resultMetric: "resultMetric",
    },
    fusionTypes: {
        noFusion: {
            value: "noFusion",
            name: "no fusion"
        },
        scoreFusion: {
            name: "score fusion",
            value: "scoreFusion"
        },
        decisionFusion: {
            name: "decision fusion",
            value: "decisionFusion",
        },
        preparesScoreFusion: {
            name: "prepares score fusion",
            value: "preparesScoreFusion"
        },
        preparesDecisionFusion: {
            name: "prepares decision fusion",
            value: "preparesDecisionFusion",
        },
        getFusionTypeName: function(specificFusionTypeValue) {

            if(specificFusionTypeValue) {
                return this[Object.keys(this).find(function (fusionType) {
                    return fusionType === specificFusionTypeValue
                })].name;
            }

            return "missing";
        },
        getAll: function() {
            return [
                this.noFusion,
                this.preparesScoreFusion,
                this.scoreFusion,
                this.preparesDecisionFusion,
                this.decisionFusion
            ]
        },
        doesFusion: function(fusionType) {
            if(fusionType.value === this.decisionFusion.value
                || fusionType.value === this.scoreFusion.value) {
                return true;
            }

            return false;
        }
    },
    evaluationTypes: {
        implementationSpecific: "implementation_specific",
        experimentSpecific: "experiment_specific",
        enriched_experimentSpecific: "enriched_experiment_specific",
        getEvaluationTypeName: function(specificEvaluationTypeValue) {

            if(specificEvaluationTypeValue === this.implementationSpecific) {
                return "implementation specific"
            } else if(specificEvaluationTypeValue === this.experimentSpecific) {
                return "experiment specific"
            }

            return "missing";
        },
    },
    resultGranularity: {
       exact: "exact",
       range: "range",
    },
    grantingLevels: {
        granted: {
            name: "granted",
            value: "granted"
        },
        quasiGranted: {
            name: "quasi-granted",
            value: "quasiGranted"
        },
        notGranted: {
            name: "not granted",
            value: "notGranted"
        },
        notDecidedYet: {
            name: "not decided yet",
            value: "notDecidedYet"
        },
        getAll: function() {
            return [
                this.notGranted,
                this.quasiGranted,
                this.granted,
                this.notDecidedYet,
            ]
        },
        getGrantingLevelName: function(specificGrantingLevelValue) {

            if(specificGrantingLevelValue) {
                return constants.grantingLevels[
                    Object.keys(constants.grantingLevels).find(function (grantingLevel) {
                      return grantingLevel === specificGrantingLevelValue
                    })
                ].name;
            }

            return "missing";
        },
    },
    rules: {
        atLeastOneElementRequired(listOfElements, elementDescription) {
            if(listOfElements.length>0) {
                return true;
            }
            return "At least one "+elementDescription+" is required";
        }
    },
    participantInformation: {
        allDetailsKnown: {
            text: "I know all participant details",
            value: "allDetailsKnown"
        },
        numberParticipantsKnown: {
            text: "I know at least the number of participants",
            value: "numberParticipantsKnown"
        },
        noDetailsKnown: {
            text: "I do NOT know any participant details",
            value: "noDetailsKnown"
        },
    },
    routes: {
        start: {
          path: "/",
          name: "start-page",
        },
        modelDatasets: {
            path: "/model/datasets",
            name: "model-datasets",
        },
        browseDatasets: {
            path: "/browse/datasets",
            name: "browse-datasets",
        },
        modelSampleDevices: {
            path: "/model/sampledevices",
            name: "model-sampledevices",
        },
        browseSampleDevices: {
            path: "/browse/sampledevices",
            name: "browse-sampledevices",
        },
        browseBiometricSystems: {
            path: "/browse/biometricsystems",
            name: "browse-biometric-systems",
        },
        browseCriteria: {
            path: "/browse/evaluation_criteria",
            name: "browse-criteria",
        },
        artifactsRequiringReview: {
            path: "/artifacts/review",
            name: "artifacts-requiring-review"
        },
        ownArtifactsInReview: {
            path: "/artifacts/me/review",
            name: "my-artifacts-in-review",
        }
    },
    units: {
        percent: "percent",
        milliseconds: "milliseconds",
        seconds: "seconds",
        none: "none",
        milliwatt: "milliwatt",
        kiloBytes: "kilobytes",
        getUnitSign: function(unit) {
            switch(unit) {
                case this.percent:
                    return "%";
                case this.milliwatt:
                    return "mW";
                case this.milliseconds:
                    return "ms";
                case this.seconds:
                    return "s";
                case this.kiloBytes:
                    return "kB";
                case this.none:
                    return "";
                default:
                    return "";
            }
        },
        getAll: function() {
            return [
                this.percent,
                this.milliseconds,
                this.seconds,
                this.none,
                this.milliwatt,
                this.kiloBytes
            ]
        }
    },
    importExport: {
        artefactTypes: {
            base: "base",
            datasets: "datasets",
            implementationCriteria: "implementation_criteria",
            experimentCriteria: "experiment_criteria",
            devices: "devices",
        }
    },
    review: {
        artifactType: {
            artifactsInReview: "artifactsInReview",
            base: "base",
            baseEvaluationExtension: "baseEvaluationExtension",
            datasets: "datasets",
            sensors: "sensors",
            sensorDimensions: "sensorDimensions",
            samplingContexts: "samplingContexts",
            biometricCharacteristics: "biometricCharacteristics",
            deviceCategories: "deviceCategories",
            resourcesToProtect: "resourcesToProtect",
            implementationCriteria: "implementationCriteria",
            experimentCriteria: "experimentCriteria",
            sampleDevices: "sampleDevices",
            features: "features",
            biometricProcessingSteps: "biometricProcessingSteps",
            resultMetrics: "resultMetrics",
        },
        lifecycleState: {
            reviewed: "reviewed",
            verified: "verified",
            created: "created",
            rejected: "rejected",
        }
    },
    roles: {
        reviewer: "reviewer",
        modeller: "modeller",
        admin: "admin",
    }
};

export default constants;
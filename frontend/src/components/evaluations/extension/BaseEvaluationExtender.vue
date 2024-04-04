<template>
    <v-tabs grow>
        <v-tab>
            Evaluation Extension
        </v-tab>
        <v-tab>
            Evaluation Criteria Grants Extension
        </v-tab>
        <v-tab-item>
            <BaseEvaluationEditor
                :parent-base-name="baseToExtend.name"
                :base-characteristics="baseToExtend.biometricCharacteristics"
                :deployment-locations="allModelledDeviceCategories"
                :base-evaluation-state="baseEvaluationToMerge"
                v-on:set-evaluation = "setBaseEvaluationToMerge"
            />
        </v-tab-item>
        <v-tab-item v-if="baseEvaluationCriteriaGrantsToMerge && baseEvaluationCriteriaGrantsToMerge.length>0">
            <EvaluationCriteriaGrantBrowser
                :base-evaluation="baseEvaluationExtension.baseEvaluationToMerge"
                :evaluation-criteria-grants="baseEvaluationCriteriaGrantsToMerge"
                edit-mode
                v-on:set-evaluation-criteria-grants="updateEvaluationCriteriaGrantsToMerge"
                />
        </v-tab-item>
    </v-tabs>
</template>

<script>
    import BaseEvaluationEditor from "@/components/evaluations/BaseEvaluationEditor.vue";
    import {Util} from "@/helpers/util";
    import constants from "@/helpers/constants";
    import {DeviceCategoryServiceAPI} from "@/service/api/DeviceCategoryServiceAPI";
    import EvaluationCriteriaGrantBrowser
        from "@/components/evaluations/criteria/grant/EvaluationCriteriaGrantBrowser.vue";
    import {EvaluationScenarioServiceAPI} from "@/service/api/EvaluationScenarioServiceAPI";
    import Vue from "vue";

    export default {
        name: "BaseEvaluationExtender",
        components: {EvaluationCriteriaGrantBrowser, BaseEvaluationEditor},
        props: {
            baseForExtension: {
                type: Object,
                default: function () {
                    return {};
                }
            },
            baseEvaluationExtensionToEdit: {
                type: Object,
                default: function() {
                    return {};
                }
            }
        },
        data() {
            return {
                baseToExtend: {},
                baseEvaluationToMerge: {},
                baseEvaluationCriteriaGrantsToMerge: [],
                baseEvaluationExtension: {
                    baseNameForExtension: "",
                    baseEvaluationToMerge: {},
                    evaluationCriteriaGrantsToMerge: [],
                }
            };
        },
        watch: {
            baseForExtension: {
                immediate: true,
                deep: true,
                handler: function(newBaseForExtension) {
                    if(newBaseForExtension) {
                        this.$log.debug("\t...set base for extension", newBaseForExtension);
                        this.baseToExtend = Util.deepCopyObject(newBaseForExtension);
                        this.baseEvaluationExtension.baseNameForExtension = this.baseToExtend.name;
                    }
                }
            },
            baseEvaluationExtensionToEdit: {
                immediate: true,
                deep: true,
                handler: function(baseEvaluationExtensionToEdit) {
                    if(baseEvaluationExtensionToEdit && baseEvaluationExtensionToEdit.baseNameForExtension) {
                        this.$log.debug("\t...set base evaluation extension to edit", baseEvaluationExtensionToEdit);
                        this.baseEvaluationExtension = Util.deepCopyObject(baseEvaluationExtensionToEdit);
                        this.baseEvaluationToMerge = this.baseEvaluationExtension.baseEvaluationToMerge;
                        this.baseEvaluationCriteriaGrantsToMerge = this.baseEvaluationExtension.evaluationCriteriaGrantsToMerge;
                    }
                }
            }
        },
        computed: {
            allModelledDeviceCategories() {

                return DeviceCategoryServiceAPI.getAllModelledDeviceCategoriesForBase(this.baseToExtend.targetArchitecture);

            },
        },
        methods: {
            setBaseEvaluationToMerge: function(baseEvaluationToMerge) {

                this.$log.debug("...set base evaluation to merge with existing, reviewed one", baseEvaluationToMerge);
                this.baseEvaluationExtension.baseEvaluationToMerge = baseEvaluationToMerge;
                this.$emit(constants.eventNames.local.artifactCreatedUpdated, this.baseEvaluationExtension);
                this.$log.debug("\t...possibly update evaluation criteria grants")

                let criteriaUsedInEvaluation = [];

                // get all criteria from implementation specific evaluations
                baseEvaluationToMerge.implementationSpecificEvaluationResults.forEach((implSpecificResult)=> {
                    criteriaUsedInEvaluation.push(implSpecificResult.criteria);
                })

                Vue.$log.debug("criterias referenced by impl specific results", criteriaUsedInEvaluation);

                let criteriaIdsFromExperimentResults = [];
                if(baseEvaluationToMerge.experimentSpecificEvaluation &&
                    baseEvaluationToMerge.experimentSpecificEvaluation.experimentSpecificEvaluationSetups) {
                    // if experiment specific evaluation exists, get criteria to retrieve for grants
                    baseEvaluationToMerge.experimentSpecificEvaluation.experimentSpecificEvaluationSetups.forEach((experimentSpecificResult) => {
                        experimentSpecificResult.specificResults.forEach((result) => {
                            criteriaIdsFromExperimentResults.push(result.resultMetric.parentEvaluationCriteriaId);
                        });
                    })

                    if(criteriaIdsFromExperimentResults.length>0) {
                        Vue.$log.debug("ids of criteria from experiment-specific evaluations to retrieve", criteriaIdsFromExperimentResults);
                        EvaluationScenarioServiceAPI.getExperimentSpecificEvaluationCriteriasById(criteriaIdsFromExperimentResults).then(response => {

                            Vue.$log.debug("\tcriteria:", response.data);
                            //merge with potential implementation based criteria
                            criteriaUsedInEvaluation = criteriaUsedInEvaluation.concat(response.data);

                            // merge criteria
                            this.baseEvaluationCriteriaGrantsToMerge =
                                EvaluationScenarioServiceAPI.mergeWithExistingGrants(criteriaUsedInEvaluation, this.baseEvaluationCriteriaGrantsToMerge);
                            this.baseEvaluationExtension.evaluationCriteriaGrantsToMerge
                                = this.baseEvaluationCriteriaGrantsToMerge;
                            this.$emit(constants.eventNames.local.artifactCreatedUpdated, this.baseEvaluationExtension);
                            this.$log.debug("\t\t...updated evaluation criteria grants", this.baseEvaluationCriteriaGrantsToMerge);
                        });
                    }
                } else {
                    this.baseEvaluationCriteriaGrantsToMerge =
                        EvaluationScenarioServiceAPI.mergeWithExistingGrants(criteriaUsedInEvaluation, this.baseEvaluationCriteriaGrantsToMerge);
                    this.baseEvaluationExtension.evaluationCriteriaGrantsToMerge
                         = this.baseEvaluationCriteriaGrantsToMerge;
                    this.$emit(constants.eventNames.local.artifactCreatedUpdated, this.baseEvaluationExtension);
                    this.$log.debug("\t\t...updated evaluation criteria grants", this.baseEvaluationCriteriaGrantsToMerge);
                }


            },
            updateEvaluationCriteriaGrantsToMerge: function(evaluationCriteriaGrantsToMerge) {
                this.$log.debug("...set evaluation criteria grants to merge", evaluationCriteriaGrantsToMerge);
                this.baseEvaluationExtension.evaluationCriteriaGrantsToMerge = evaluationCriteriaGrantsToMerge;
                this.$emit(constants.eventNames.local.artifactCreatedUpdated, this.baseEvaluationExtension);
            }
        },
    }
</script>

<style scoped>

</style>
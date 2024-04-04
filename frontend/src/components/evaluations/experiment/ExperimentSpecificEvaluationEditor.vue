<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card>
        <v-card-title>
            <div>
            <div class="headline">Evaluation based on Experiments</div>
            <span>Please specify the used datasets, biometric systems, and the final evaluation setups</span>
            </div>
        </v-card-title>
        <v-card-text>
            <v-container fluid>
                <v-row>
                    <DatasetSelector
                        :base-characteristics="baseCharacteristics"
                        :used-datasets-state="usedDatasets"
                        v-on:used-datasets-set="setUsedDatasets">
                    </DatasetSelector>
                </v-row>
                <v-row>
                    <template v-if="usedDatasets && usedDatasets.length > 0">
                        <BiometricSystemSelector
                                :datasets="usedDatasets"
                                :parent-base-name="parentBaseName"
                                :existing-biometric-systems-from-outside="biometricSystems"
                                v-on:biometric-systems-saved = "setBiometricSystems"
                                :specific-results-based-on-systems="experimentSpecificEvaluationSetups"
                                v-on:result-indizes-to-delete = "deleteResultIndizes"
                        />
                        <template v-if="biometricSystems && biometricSystems.length > 0">
                            <v-divider></v-divider>
                            <v-col cols="12" lg="10">
                                <div class="headline">Evaluation Result</div>
                                <span>Please add your evaluation results based on the defined biometric systems above
                                </span>
                            </v-col>
                            <v-col lg="2">
                                <v-btn text block color="primary" @click="evaluationResultDialog = true">Add Evaluation Results</v-btn>
                            </v-col>
                            <v-dialog v-model="evaluationResultDialog" hide-overlay persistent>
                                <ExperimentSpecificSetupEditor
                                        :existing-result-descriptions="existingResultDescriptions"
                                        :biometric-systems="biometricSystems"
                                        :pre-selected-result="evaluationResultToEdit"
                                        :evaluation-result-dialog="evaluationResultDialog"
                                        v-on:evaluation-result-saved = "addEvaluationResult"
                                        v-on:close-model-dialog = "evaluationResultDialog = false"
                                />
                            </v-dialog>
                            <v-col cols="12" lg="12">
                                <ExperimentSpecificSetupBrowser v-if="(experimentSpecificEvaluationSetups && experimentSpecificEvaluationSetups.length>0)"
                                                                :experiment-specific-evaluation-setups="experimentSpecificEvaluationSetups"
                                                                :biometric-systems="biometricSystems"
                                                                :modelling-mode="true"
                                                                :edit-mode="true"
                                                                v-on:edit-result="editEvaluationResult"
                                >
                                </ExperimentSpecificSetupBrowser>
                            </v-col>
                        </template>
                    </template>
                </v-row>
            </v-container>
        </v-card-text>
        <v-card-actions>
            <v-btn text
                   :disabled="!evaluationValid"
                   @click="saveExperimentSpecificEvaluation"
                   color="primary"
            >Save experiment specific evaluation</v-btn>
            <v-tooltip v-if="!evaluationValid"  top>
                <template v-slot:activator="{ on }">
                    <v-btn text color="error" v-on="on" @click.stop="closeDialog(false)">Cancel</v-btn>
                </template>
                <span>No experiment specific evaluation will be saved!</span>
            </v-tooltip>
        </v-card-actions>
    </v-card>
</template>

<script>

    import ExperimentSpecificSetupEditor from "./setup/ExperimentSpecificSetupEditor";
    import constants from "../../../helpers/constants";
    import ExperimentSpecificSetupBrowser from "./setup/ExperimentSpecificSetupBrowser";
    import {Util} from "../../../helpers/util";
    import BaseComponent from "../../base/BaseComponent";
    import BiometricSystemSelector
        from "@/components/evaluations/experiment/biometricsystem/BiometricSystemSelector.vue";
    import DatasetSelector from "@/components/evaluations/experiment/data/DatasetSelector.vue";

    export default {
        components: {
            DatasetSelector,
            BiometricSystemSelector,
            ExperimentSpecificSetupBrowser, ExperimentSpecificSetupEditor},
        name: "ExperimentSpecificEvaluationEditor",
        extends: BaseComponent,
        props:{
            baseCharacteristics: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            experimentSpecificEvaluationState: {
                type: Object,
                default: function() {
                    return {};
                }
            }
        },
        data() {
            return {
                usedDatasets: [],
                biometricSystems:[],
                experimentSpecificEvaluationSetups: [],
                evaluationResultToEdit: {},
                newEvaluationSetupValid: false,
                newDataSetDialog: false,
                continueBaseEditing: false,
                evaluationResultDialog: false,
                indexEvaluationResultToEdit: -1,
            }
        },
        watch: {
            experimentSpecificEvaluationState: {
                deep: true,
                immediate: true,
                handler: function(newState) {
                    if(!Util.isEmpty(newState)) {
                        this.$log.debug("Set experiment specific evaluation state", newState);
                        this.experimentSpecificEvaluationSetups = newState.experimentSpecificEvaluationSetups;
                        if(newState.biometricSystems && newState.biometricSystems.some(system => system.id)) {
                            this.biometricSystems = newState.biometricSystems;
                        }
                        if(newState.usedDatasets && newState.usedDatasets.some(dataset => dataset.id)) {
                            this.usedDatasets = newState.usedDatasets;
                        }
                    } else {
                        this.$log.debug("No experiment specific evaluation state exists, start fresh!")
                        this.experimentSpecificEvaluationSetups = []
                        this.biometricSystems = []
                        this.usedDatasets = []
                    }
                }
            },
            usedDatasets: {
                deep:true,
                handler: function(datasets) {
                    this.$log.debug("Used datasets in exp eval changed to", datasets);
                }
            },
            biometricSystems: {
                deep:true,
                handler: function(biometricSystems) {
                    this.$log.debug("biometricSystems in exp eval changed to", biometricSystems);
                }
            }
        },
        mounted:function() {
        },
        computed: {
            evaluationValid() {
                return this.usedDatasets && this.usedDatasets.length>0
                    && this.biometricSystems && this.biometricSystems.length>0
                    && this.experimentSpecificEvaluationSetups && this.experimentSpecificEvaluationSetups.length>0
            },
            existingResultDescriptions() {

                if(this.experimentSpecificEvaluationSetups) {
                    return this.experimentSpecificEvaluationSetups.map(evaluationResult => evaluationResult.description);
                }

                return [];
            }
        },
        methods: {
            setUsedDatasets: function(usedDatasets) {
                this.usedDatasets = usedDatasets;
            },
            setBiometricSystems: function(biometricSystems) {
                this.biometricSystems = biometricSystems;
            },
            addEvaluationResult: function(evaluationResult) {
                if(this.indexEvaluationResultToEdit===-1) {
                    this.$log.debug("Add specific results!", evaluationResult);
                    this.experimentSpecificEvaluationSetups.push(evaluationResult);
                } else {
                    this.$log.debug("saving evaluation result to edit at index "+this.indexEvaluationResultToEdit+" with", evaluationResult);
                    this.experimentSpecificEvaluationSetups.splice(this.indexEvaluationResultToEdit,1,evaluationResult);
                    this.$log.debug("Current evaluation setups to show in browser", this.experimentSpecificEvaluationSetups);
                    this.indexEvaluationResultToEdit = -1;
                    this.evaluationResultToEdit = {};
                }
            },
            saveExperimentSpecificEvaluation: function() {
                this.closeDialog(true);
            },
            editEvaluationResult: function(evaluationResultToEdit) {
                this.$log.debug("Request to edit evaluation result ", evaluationResultToEdit);
                this.indexEvaluationResultToEdit = this.experimentSpecificEvaluationSetups.indexOf(evaluationResultToEdit);
                this.evaluationResultToEdit = this.experimentSpecificEvaluationSetups[this.indexEvaluationResultToEdit];
                this.evaluationResultDialog = true;
            },
            closeDialog: function(evaluationModelledSuccessfully) {
                if(evaluationModelledSuccessfully) {
                    this.emitUpdatedExperimentSpecificEvaluation();
                } else {
                    this.$emit("reset-experiment-specific-evaluation");
                }
                this.$emit(constants.eventNames.local.closeModelDialog);
            },
            emitUpdatedExperimentSpecificEvaluation: function() {
                this.$emit("set-experiment-specific-evaluation", {
                    biometricSystems: this.biometricSystems,
                    experimentSpecificEvaluationSetups: this.experimentSpecificEvaluationSetups,
                    usedDatasets: this.usedDatasets,
                });
            },
            deleteResultIndizes:function(indizesToDelete) {
                this.experimentSpecificEvaluationSetups = this.experimentSpecificEvaluationSetups.filter((result, index) => {
                    return indizesToDelete.indexOf(index) === -1;
                });
                this.emitUpdatedExperimentSpecificEvaluation();
            }
        }
    }
</script>

<style scoped>

</style>
<template>
    <v-card>
        <v-card-title primary-title>
            <div>
                <div class="headline">Experiment Setup's specifics and results</div>
                <span>Please specify the respective setup and results you got</span>
            </div>
        </v-card-title>
        <v-card-text>
            <v-container fluid grid-list-md>
                <v-row row wrap>
                    <v-col cols="12" lg="6">
                        <v-text-field
                                label="Description"
                                hint="If needed describe further details for this specific result"
                                required
                                :rules="[rules.resultDescription]"
                                v-model="experimentSpecificEvaluationSetupToEdit.description"
                        />
                    </v-col>
                    <v-col cols="12" lg="6">
                        <v-select
                                :items="biometricSystems"
                                v-model="experimentSpecificEvaluationSetupToEdit.biometricSystems"
                                :menu-props="{ maxHeight: '400', closeOnContentClick: true }"
                                label="Mark the biometric systems you used for this specific result"
                                multiple
                                small-chips
                                item-text="name"
                                item-value="name"
                                persistent-hint
                                return-object
                        />
                    </v-col>
                </v-row>
                <template  v-if="experimentSpecificEvaluationSetupToEdit.specificResults.length>0">
                    <v-row>
                        <v-col cols="12" lg="12">
                            <div class="subtitle-2">Specific Experiment Setup's Results</div>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col cols="12" lg="12" >
                            <ExperimentSpecificResultsBrowser
                                :experiment-specific-evaluation-setups="experimentSpecificEvaluationSetupToEdit.specificResults"
                                v-on:experiment-specific-results-modified="updateSetupSpecificResults"/>
                        </v-col>
                    </v-row>
                </template>
                <v-row>
                    <v-col cols="12" lg="6">
                        <EvaluationCriteriaPicker
                                :show-as-browser="false"
                                :evaluation-type="experimentEvaluationType"
                                :pre-selected-criteria-and-metrics="preSelectedCriteriaAndMetrics"
                                v-on:evaluation-criteria-selected="setExperimentSpecificCriteria"/>
                    </v-col>
                    <v-col lg="6" v-if="specificExperimentBasedEvaluationCriteria.length>0">
                        <ExperimentSpecificResultEditor
                                :experiment-specific-evaluation-criteria="specificExperimentBasedEvaluationCriteria"
                                :already-reported-experiment-specific-results="experimentSpecificEvaluationSetupToEdit.specificResults"
                                v-on:results-added="addSpecificResults"
                        />
                    </v-col>
                </v-row>
            </v-container>
        </v-card-text>
        <v-card-actions>
            <v-btn v-if="newEvaluationSetup" color="primary" text @click="addSaveEvaluationSetup()" :disabled="!allowAddEvaluationResult">Add Evaluation Setup with results</v-btn>
            <v-btn v-if="!newEvaluationSetup" color="primary" text @click="addSaveEvaluationSetup()" :disabled="!allowAddEvaluationResult">Save Evaluation Setup with results</v-btn>
            <v-btn color="error" text @click="closeDialog">Cancel</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>

    import constants from "../../../../helpers/constants";
    import EvaluationCriteriaPicker from "../../criteria/EvaluationCriteriaSelector.vue";
    import ExperimentSpecificResultsBrowser from "../result/ExperimentSpecificResultsBrowser";
    import ExperimentSpecificResultEditor from "../result/ExperimentSpecificResultEditor";
    import {EvaluationScenarioServiceAPI} from "../../../../service/api/EvaluationScenarioServiceAPI";
    import {Util} from "../../../../helpers/util";

    export default {
        components: {ExperimentSpecificResultEditor, ExperimentSpecificResultsBrowser, EvaluationCriteriaPicker},
        name: "ExperimentSpecificSetupEditor",
        props: {
            biometricSystems: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            existingResultDescriptions: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            preSelectedResult: {
                type: Object,
                default: function() {
                    return {};
                }
            },
        },
        data() {
            return {
                experimentSpecificEvaluationSetupToEdit: {
                    description: "",
                    biometricSystems: [],
                    specificResults: [],
                },
                newEvaluationSetup: true,
                evaluationResultsHeaders: [
                    { text: 'Name', value: 'name' },
                    { text: 'Biometric Systems', value: 'biometric_systems' },
                    { text: 'Metrics', value: 'metrics' },
                ],
                selectedResultMetrics: [],
                rules: {
                    resultDescription: value => {
                        const indexDescription = this.existingResultDescriptions.findIndex(function(result) {
                            return result.description === value;
                        })
                        if(indexDescription>-1) {
                            return 'This description is already given, please choose a different one'
                        } else {
                            return true;
                        }
                    },
                },
                specificExperimentBasedEvaluationCriteria: [],
                preSelectedCriteriaAndMetrics: [],
            }
        },
        computed: {
            allowAddEvaluationResult() {
                return this.experimentSpecificEvaluationSetupToEdit.description.length>0
                    && this.experimentSpecificEvaluationSetupToEdit.biometricSystems.length>0
                    && this.experimentSpecificEvaluationSetupToEdit.specificResults.length>0
            },
            experimentEvaluationType: function() {
                return constants.evaluationTypes.experimentSpecific;
            },
        },
        watch: {
            preSelectedResult: {
                deep: true,
                immediate: true,
                handler: function(preSelectedResult) {
                    this.$log.debug("Request to set specific result to edit from outside, validate", preSelectedResult);
                    if(!Util.isEmpty(preSelectedResult)) {
                        if (preSelectedResult.description.length > 0
                            && preSelectedResult.biometricSystems.length > 0
                            && preSelectedResult.specificResults.length > 0 != null) {

                            this.newEvaluationSetup = false;

                            this.$log.debug("VALID => set experimentSetupToEdit after matching criteria are resolved")
                            EvaluationScenarioServiceAPI.getExperimentSpecificEvaluationCriteriasById(
                                preSelectedResult.specificResults.map(result => result.resultMetric.parentEvaluationCriteriaId)
                            ).then(response => {
                                this.$log.debug("\tresolved criteria", response);
                                for (let i = 0; i < response.data.length; i++) {
                                    preSelectedResult.specificResults.forEach(result => {
                                        this.$log.debug("check if " + result.resultMetric.parentEvaluationCriteriaId + " equals " + response.data[i].id);
                                        if (result.resultMetric.parentEvaluationCriteriaId === response.data[i].id) {
                                            result.resultMetric["criteria"] = response.data[i];
                                        }
                                    });
                                }

                                this.experimentSpecificEvaluationSetupToEdit = preSelectedResult;
                                this.evaluationResultDialog = true;
                            });

                        } else {
                            this.$log.debug("INVALID => do NOT set experimentSetupToEdit")
                        }
                    } else {
                        this.$log.debug("INVALID => do NOT set experimentSetupToEdit")
                    }
                }
            },
        },
        methods: {
            addSaveEvaluationSetup: function()  {

                // remove enriched criteria information, they were only needed here

                this.experimentSpecificEvaluationSetupToEdit.specificResults.forEach(function(specificResult) {
                    // delete enriched criteria information
                    delete specificResult.resultMetric.criteria;
                });

                this.$emit("evaluation-result-saved", this.experimentSpecificEvaluationSetupToEdit);
                this.experimentSpecificEvaluationSetupToEdit = {
                    description: "",
                    biometricSystems: [],
                    specificResults: [],
                };

                this.newEvaluationSetup = true;

                this.closeDialog();
            },
            updateSetupSpecificResults: function(specificResults) {
                this.experimentSpecificEvaluationSetupToEdit.specificResults = specificResults;
            },
            addSpecificResults: function(specificResults) {
                this.$log.debug("add specific results ",specificResults);
                this.$log.debug("\tto existing", this.experimentSpecificEvaluationSetupToEdit.specificResults);
                this.experimentSpecificEvaluationSetupToEdit.specificResults =
                    this.experimentSpecificEvaluationSetupToEdit.specificResults.concat(specificResults);
                //reset selected criteria
                this.specificExperimentBasedEvaluationCriteria = [];
                this.preSelectedCriteriaAndMetrics = [];
            },
            deleteMetricsResults: function(metricResult) {
                const index = this.experimentSpecificEvaluationSetupToEdit.specificResults.indexOf(metricResult);
                this.experimentSpecificEvaluationSetupToEdit.specificResults.splice(index, 1);
            },
            closeDialog: function() {
                this.$emit(constants.eventNames.local.closeModelDialog);
            },
            setExperimentSpecificCriteria: function(experimentSpecificCriteria) {
                this.specificExperimentBasedEvaluationCriteria = experimentSpecificCriteria;
            }
        }
    }
</script>

<style scoped>

</style>
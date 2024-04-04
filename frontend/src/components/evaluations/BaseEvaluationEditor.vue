<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
        <v-row row wrap>
            <v-col cols="12" lg="6">
                <div class="headline">Evaluation</div>
            </v-col>
            <v-col cols="3">
                <v-dialog v-model="experimentSpecificEvaluationDialog" hide-overlay transition="dialog-bottom-transition">
                  <template v-slot:activator="{ on }">
                    <v-btn
                        color="primary"
                        text
                        :disabled="!isEmpty(baseEvaluation.experimentSpecificEvaluation.experimentSpecificEvaluationSetups)"
                        v-on="on"
                    >
                      Add Experiment Specific Evaluations
                    </v-btn>
                  </template>
                  <ExperimentSpecificEvaluationEditor
                      v-on:close-model-dialog="experimentSpecificEvaluationDialog = false"
                      v-on:set-experiment-specific-evaluation = setExperimentSpecificEvaluations
                      v-on:reset-experiment-specific-evaluation = resetExperimentSpecificEvaluations
                      :parent-base-name="parentBaseName"
                      :base-characteristics="baseCharacteristics"
                      :experiment-specific-evaluation-state="baseEvaluation.experimentSpecificEvaluation"
                  />
                </v-dialog>
            </v-col>
            <v-col cols="3">
                <v-btn
                        color="primary"
                        text
                        @click="implementationSpecificEvaluationDialog = true"
                        :disabled="baseEvaluation.implementationSpecificEvaluationResults
                                      && baseEvaluation.implementationSpecificEvaluationResults.length>0"
                >
                    Add Implementation Specific Evaluations
                </v-btn>
            </v-col>
        </v-row>
        <v-row v-if="baseEvaluation.experimentSpecificEvaluation.biometricSystems">
            <v-col cols="12" lg="12">
                <ExperimentSpecificEvaluationBrowser
                        :base-characteristics="baseCharacteristics"
                        :experiment-specific-evaluation="baseEvaluation.experimentSpecificEvaluation"
                        v-on:edit-experiment-specific-evaluation="editExperimentSpecificEvaluation"
                        v-on:reset-experiment-specific-evaluation="deleteExperimentSpecificEvaluationDialog = true"
                />
            </v-col>
        </v-row>
        <v-dialog v-model="deleteExperimentSpecificEvaluationDialog" persistent >
            <v-card>
                <v-card-title class="headline">Delete Experiment Specific Evaluations?</v-card-title>
                <v-card-text>This will delete all information on <i>used datasets</i>, <i>built biometric systems</i>,
                    and all <i>evaluation setups and results</i></v-card-text>
                <v-card-actions>
                    <v-spacer/>
                    <v-btn color="primary" text @click="deleteExperimentSpecificEvaluationDialog = false">Cancel</v-btn>
                    <v-btn color="error" text @click="deleteExperimentSpecificEvaluation">OK</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-row v-show="baseEvaluation.implementationSpecificEvaluationResults && baseEvaluation.implementationSpecificEvaluationResults.length>0">
            <v-col cols="12" lg="12">
                <ImplementationSpecificEvaluationBrowser
                        :reported-implementation-specific-results="baseEvaluation.implementationSpecificEvaluationResults"
                        v-on:edit-implementation-specific-evaluation="editImplementationSpecificEvaluationResult"
                        v-on:reset-implementation-specific-evaluation="deleteImplementationSpecificEvaluationDialog = true"
                />
            </v-col>
        </v-row>
        <ImplementationSpecificEvaluation
                v-on:close-model-dialog="implementationSpecificEvaluationDialog = false"
                v-on:set-implementation-specific-evaluation-results = setImplementationSpecificEvaluationResults
                v-on:reset-implementation-specific-evaluation-results = resetImplementationSpecificEvaluationResults
                :implementation-specific-evaluation-results-state="baseEvaluation.implementationSpecificEvaluationResults"
                :device-categories="deviceCategories"
                :implementation-specific-evaluation-dialog="implementationSpecificEvaluationDialog"
            />
        <v-dialog v-model="deleteImplementationSpecificEvaluationDialog" persistent >
            <v-card>
                <v-card-title class="headline">Delete Implementation Specific Evaluations?</v-card-title>
                <v-card-text>This will delete all reported implementation specific results</v-card-text>
                <v-card-actions>
                    <v-spacer/>
                    <v-btn color="primary" text @click="deleteImplementationSpecificEvaluationDialog = false">Cancel</v-btn>
                    <v-btn color="error" text @click="deleteImplementationSpecificEvaluationResults">OK</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>

    import ExperimentSpecificEvaluationBrowser from "./experiment/ExperimentSpecificEvaluationBrowser";
    import ImplementationSpecificEvaluation from "./implementation/ImplementationSpecificEvaluation";
    import ImplementationSpecificEvaluationBrowser from "./implementation/ImplementationSpecificEvaluationBrowser";
    import {Util} from "../../helpers/util";
    import BaseComponent from "../base/BaseComponent";
    import ExperimentSpecificEvaluationEditor
        from "@/components/evaluations/experiment/ExperimentSpecificEvaluationEditor.vue";

    export default {
        components: {
            ExperimentSpecificEvaluationEditor,
            ImplementationSpecificEvaluationBrowser,
            ImplementationSpecificEvaluation, ExperimentSpecificEvaluationBrowser},
        name: "BaseEvaluationEditor",
        extends: BaseComponent,
        props:{
            baseCharacteristics: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            deviceCategories: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            baseEvaluationState: {
                type: Object,
                default: function() {
                    return {};
                }
            }
        },
        data() {
            return {
                baseEvaluation: {
                    implementationSpecificEvaluationResults: [],
                    experimentSpecificEvaluation: {},
                },
                experimentSpecificEvaluationDialog: false,
                deleteExperimentSpecificEvaluationDialog: false,
                implementationSpecificEvaluationDialog: false,
                deleteImplementationSpecificEvaluationDialog: false,
            }
        },
        watch: {
            baseEvaluation: {
              deep: true,
              handler: function(newBaseEvaluation) {
                  if(!newBaseEvaluation.implementationSpecificEvaluationResults
                      || !newBaseEvaluation.experimentSpecificEvaluation
                      || (newBaseEvaluation.experimentSpecificEvaluation
                          && newBaseEvaluation.experimentSpecificEvaluation.experimentSpecificResults
                          && newBaseEvaluation.experimentSpecificEvaluation.experimentSpecificResults.length===0
                          && newBaseEvaluation.implementationSpecificEvaluationResults
                          && newBaseEvaluation.implementationSpecificEvaluationResults.length===0)) {

                      this.$log.debug("base evaluation changed to invalid, reset!", newBaseEvaluation);
                      this.resetEvaluation();
                  }
              }
            },

            baseEvaluationState: {
                deep: true,
                immediate: true,
                handler: function(newBaseEvaluationState) {
                    this.$log.debug("request to set base evaluation state from outside", newBaseEvaluationState);
                    if(this.isBaseEvaluationValid(newBaseEvaluationState)) {
                        this.$log.debug("\tVALID => set baseEvaluationState");
                        this.baseEvaluation = newBaseEvaluationState;
                    } else {
                        this.$log.debug("\tINVALID => do NOT set baseEvaluationState");
                    }
                }
            }
        },
        computed: {
            baseEvaluationValid:function() {
                return this.isBaseEvaluationValid(this.baseEvaluation);
            }
        },
        methods: {
            isBaseEvaluationValid: function(newBaseEvaluationState){
                return (newBaseEvaluationState.experimentSpecificEvaluation
                    && newBaseEvaluationState.experimentSpecificEvaluation.experimentSpecificEvaluationSetups
                    && newBaseEvaluationState.experimentSpecificEvaluation.experimentSpecificEvaluationSetups.length>0)
                    || (newBaseEvaluationState.implementationSpecificEvaluationResults
                        && newBaseEvaluationState.implementationSpecificEvaluationResults.length>0);
            },
            setExperimentSpecificEvaluations: function(experimentSpecificEvaluation) {
                this.baseEvaluation.experimentSpecificEvaluation = experimentSpecificEvaluation;
                this.updateEvaluation();
            },
            resetExperimentSpecificEvaluations: function() {
                this.baseEvaluation.experimentSpecificEvaluation = {};
                this.updateEvaluation();
            },
            editExperimentSpecificEvaluation: function() {
                this.experimentSpecificEvaluationDialog = true;
            },
            deleteExperimentSpecificEvaluation: function() {
                this.baseEvaluation.experimentSpecificEvaluation = {};
                this.deleteExperimentSpecificEvaluationDialog = false;
                this.updateEvaluation();
            },
            setImplementationSpecificEvaluationResults: function(implementationSpecificEvaluationResults) {
                this.baseEvaluation.implementationSpecificEvaluationResults =
                    implementationSpecificEvaluationResults;
                this.updateEvaluation();
            },
            resetImplementationSpecificEvaluationResults: function() {
                this.baseEvaluation.implementationSpecificEvaluationResults = [];
                this.updateEvaluation();
            },
            editImplementationSpecificEvaluationResult: function() {
                this.implementationSpecificEvaluationDialog = true;
            },
            deleteImplementationSpecificEvaluationResults: function() {
                this.baseEvaluation.implementationSpecificEvaluationResults = [];
                this.deleteImplementationSpecificEvaluationDialog = false;
                this.updateEvaluation();
            },
            updateEvaluation: function() {
                this.$emit("set-evaluation", this.baseEvaluation);
            },
            resetEvaluation: function() {
                this.$emit("reset-evaluation");
            },
            isEmpty: function(object) {
                return Util.isEmpty(object);
            },

        }
    }
</script>

<style scoped>

</style>
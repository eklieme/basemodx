<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-dialog v-model="showDialog" hide-overlay transition="dialog-bottom-transition">
        <v-card>
            <v-card-title>
                <div>
                    <div class="headline">Evaluation based on Implementation</div>
                    <span>Please report the implementation specific evaluations you did based on the available deployment locations</span>
                </div>
            </v-card-title>
            <v-card-text>
                <v-container fluid>
                    <v-row v-if="reportedImplementationSpecificResults.length>0">
                        <v-col cols="12" lg="12">
                            <ImplementationSpecificResultsBrowser
                                    :reported-implementation-specific-results="reportedImplementationSpecificResults"
                                    :device-categories="deviceCategories"
                                    :allow-editing="true"
                                    v-on:implementation-specific-results-modified="updateSpecificResults"
                            />
                        </v-col>
                    </v-row>
                    <v-row v-if="deviceCategories && deviceCategories.length>0">
                        <v-col cols="12" lg="5">
                            <EvaluationCriteriaPicker
                                    :evaluation-type="implementationEvaluationType"
                                    :pre-selected-criteria-and-metrics="preSelectedEvaluationCriteria"
                                    v-on:evaluation-criteria-selected="setImplementationSpecificCriteria"/>
                        </v-col>
                        <v-col lg="7" v-if="specificImplementationBasedEvaluationCriteria.length>0">
                            <ImplementationSpecificResultEditor
                                    :implementation-specific-evaluation-criteria="specificImplementationBasedEvaluationCriteria"
                                    :device-categories="deviceCategories"
                                    v-on:report-implementation-specific-results="addImplementationSpecificResults"
                            />
                        </v-col>
                    </v-row>
                    <v-row v-else>
                        <span class="error--text subtitle-2">You have not modelled any deployment locations yet. Since implementation specific evaluations
                        can only be made referring to one or more deployment locations, at least <b>one</b> is needed.
                        To model a respective location please navigate back to the target architecture.</span>
                    </v-row>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-btn text
                       :disabled="!implementationSpecificEvaluationValid"
                       @click="saveImplementationSpecificEvaluation"
                       color="primary"
                >Save implementation specific evaluation</v-btn>
                <v-btn text color="error" @click.stop="closeDialog(implementationSpecificEvaluationValid)">Close</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>

    import constants from "../../../helpers/constants";
    import ImplementationSpecificResultEditor from "./ImplementationSpecificResultEditor";
    import EvaluationCriteriaPicker from "../criteria/EvaluationCriteriaSelector.vue";
    import ImplementationSpecificResultsBrowser from "./ImplementationSpecificResultsBrowser";

    export default {
        components: {ImplementationSpecificResultsBrowser, EvaluationCriteriaPicker, ImplementationSpecificResultEditor},
        name: "ImplementationSpecificEvaluation",
        props:{
            deviceCategories: {
                type: Array,
                handler: function() {
                    return [];
                }
            },
            implementationSpecificEvaluationResultsState: {
                type: Array,
                handler: function() {
                    return [];
                }
            },
            implementationSpecificEvaluationDialog: {
                type: Boolean,
                default: false,
            }
        },
        data() {
            return {
                specificImplementationBasedEvaluationCriteria: [],
                preSelectedEvaluationCriteria: [],
                reportedImplementationSpecificResults: [],
                showDialog:false,
            }
        },
        watch: {
            implementationSpecificEvaluationResultsState: {
                deep: true,
                handler: function(newResults) {
                    if(newResults.length>0) {
                        this.$log.debug("Set implementation specific evaluation results state", newResults);
                        this.reportedImplementationSpecificResults = newResults;
                    } else {
                        this.$log.debug("No implementation specific evaluation results state exists, start fresh!")
                        this.reportedImplementationSpecificResults = [];
                    }
                }
            },
            deviceCategories: {
                deep: true,
                handler: function(deviceCategories) {
                    this.$log.debug("deviceCategories changed for implementationSpecificResults", deviceCategories);
                }
            },
            implementationSpecificEvaluationDialog: {
              deep:true,
              immediate: true,
              handler: function(implementationSpecificEvaluationDialog) {
                this.showDialog = implementationSpecificEvaluationDialog;
              }
            }
        },
        created:function() {
        },
        computed: {
            implementationSpecificEvaluationValid: function() {
                if(this.deviceCategories && this.deviceCategories.length===0) {
                    return false;
                }

                if(this.reportedImplementationSpecificResults.length===0) {
                    return false;
                } else {

                    let reportedResultsAreValid = true;

                    this.reportedImplementationSpecificResults.forEach(function(result) {
                        if(result.affectedDeviceCategories.length === 0) {
                            reportedResultsAreValid = false;
                        }
                    });

                    return reportedResultsAreValid;
                }
            },
            implementationEvaluationType: function() {
                return constants.evaluationTypes.implementationSpecific;
            },
        },
        methods: {
            saveImplementationSpecificEvaluation: function() {
                this.closeDialog(true);
            },
            closeDialog: function(implementationModelledSuccessfully) {
                if(implementationModelledSuccessfully) {
                    this.$emit("set-implementation-specific-evaluation-results", this.reportedImplementationSpecificResults);
                } else {
                    this.$emit("reset-implementation-specific-evaluation-results");
                }
                this.$emit(constants.eventNames.local.closeModelDialog);
            },
            setImplementationSpecificCriteria: function(implementationSpecificCriteria) {
                this.specificImplementationBasedEvaluationCriteria = implementationSpecificCriteria;
            },
            addImplementationSpecificResults: function(reportedResults) {
                this.reportedImplementationSpecificResults =
                    this.reportedImplementationSpecificResults.concat(reportedResults);
                //reset criteria selection:
                this.preSelectedEvaluationCriteria = [];
                this.specificImplementationBasedEvaluationCriteria = [];
            },
            updateSpecificResults: function(updatedResults) {
                this.reportedImplementationSpecificResults = updatedResults;
            }
        }
    }

</script>

<style scoped>

</style>
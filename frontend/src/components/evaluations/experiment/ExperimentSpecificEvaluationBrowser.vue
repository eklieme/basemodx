<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card>
        <v-card-title>
            Experiment Specific Evaluation Details
        </v-card-title>
        <v-card-text>
            <v-container fluid>
                <v-row>
                    <v-col cols="12" lg="12">
                        <div
                                class="title font-weight-light grey--text"
                        >
                        Used Datasets for Experiments
                        </div>
                        <DatasetBrowser
                                :edit-mode="false"
                                :datasets-to-show="experimentSpecificEvaluation.usedDatasets"
                        />
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" lg="12">
                        <div
                                class="title font-weight-light grey--text"
                        >
                            Biometric Systems created for Experiments
                        </div>
                        <BiometricSystems
                            :specific-results-based-on-systems="experimentSpecificEvaluation.experimentSpecificEvaluationSetups"
                            :existing-biometric-systems-from-outside="experimentSpecificEvaluation.biometricSystems"
                            :datasets="experimentSpecificEvaluation.usedDatasets"
                            :parent-base-name="parentBaseName"
                            :edit-mode="false"/>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" lg="12">
                        <div
                                class="title font-weight-light grey--text"
                        >
                            Specific Evaluations based on the datasets and the biometric systems
                        </div>
                        <ExperimentSpecificSetupBrowser
                                :edit-mode="false"
                                :modelling-mode="false"
                                :biometric-systems="experimentSpecificEvaluation.biometricSystems"
                                :experiment-specific-evaluation-setups="experimentSpecificEvaluation.experimentSpecificEvaluationSetups"
                        />
                    </v-col>
                </v-row>
            </v-container>
        </v-card-text>
        <v-card-actions v-if="editMode">
            <v-btn color="primary" text @click="$emit('edit-experiment-specific-evaluation')">Edit Experiment Specific Evaluation</v-btn>
            <v-btn color="error" text @click="$emit('reset-experiment-specific-evaluation')">Reset Experiment Specific Evaluation</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>


    import DatasetBrowser from "./data/DatasetBrowser";
    import ExperimentSpecificSetupBrowser from "./setup/ExperimentSpecificSetupBrowser";
    import BiometricSystems from "./biometricsystem/BiometricSystemSelector.vue";
    export default {
        components: {BiometricSystems, ExperimentSpecificSetupBrowser, DatasetBrowser},
        name: "ExperimentSpecificEvaluationBrowser",
        props:{
            baseCharacteristics: {
                type: Array,
                default: function() {
                    return [];
                },
            },
            experimentSpecificEvaluation: {
                type: Object,
                default: function() {
                    return {};
                }
            },
            editMode: {
                type: Boolean,
                default: true,
            },
            parentBaseName: {
              type: String,
              default: "",
            }
        },
        data() {
            return {

            }
        },
        watch: {
        },
        created:function() {
        },
        computed: {
        },
        methods: {
        }
    }
</script>

<style scoped>

</style>
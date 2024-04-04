<template>
    <v-container fluid>
        <v-tabs grow>
            <v-tab :disabled="!baseEvaluation.experimentSpecificEvaluation.usedDatasets">
                Experiment-specific Evaluation
            </v-tab>
            <v-tab :disabled="baseEvaluation.implementationSpecificEvaluationResults.length===0">
                Implementation-specific Evaluation
            </v-tab>
            <v-tab-item>
                <ExperimentSpecificEvaluationBrowser
                    v-if="baseEvaluation.experimentSpecificEvaluation.usedDatasets"
                    :characteristics="characteristics"
                    :edit-mode="false"
                    :experiment-specific-evaluation="baseEvaluation.experimentSpecificEvaluation"
                    :parent-base-name="parentBaseName"
                />
            </v-tab-item>
            <v-tab-item>
                <ImplementationSpecificEvaluationBrowser
                    v-if="baseEvaluation.implementationSpecificEvaluationResults && baseEvaluation.implementationSpecificEvaluationResults.length>0"
                    :reported-implementation-specific-results="baseEvaluation.implementationSpecificEvaluationResults"
                    :edit-mode="false"
                />
            </v-tab-item>
        </v-tabs>
    </v-container>
</template>

<script>

    import ExperimentSpecificEvaluationBrowser from "./experiment/ExperimentSpecificEvaluationBrowser";
    import ImplementationSpecificEvaluationBrowser from "./implementation/ImplementationSpecificEvaluationBrowser";

    export default {
        components: {
            ImplementationSpecificEvaluationBrowser,
            ExperimentSpecificEvaluationBrowser,},
        props: {
            characteristics: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            baseEvaluation: {
                type: Object,
                default: function() {
                    return {
                        experimentSpecificEvaluation: {},
                        implementationSpecificEvaluationResults: [],
                    }
                }
            },
            parentBaseName: {
              type: String,
              default: "",
            }
        },
        watch: {
            baseEvaluation: {
                deep: true,
                handler: function(evaluation) {
                    this.$log.debug("BaseEvaluation for browser changed to", evaluation);
                },
            }
        },
        data() {
            return {
                datasetHeaders: [
                    {text: "name", value: "datasetsUsed"},
                    {text: "#Sampled Biometrics", value: "biometricSystemsUsed"},
                    {text: "", value: "evaluationSetups"}
                ],
                enrichedDatasets: [],
            }
        },
        methods: {
        }
    }
</script>

<style scoped>

</style>
<template>
    <v-container fluid>
        <v-row row wrap>
            <v-col cols="12" lg="5">
                <EvaluationCriteriaPicker
                    :evaluation-type="evaluationType"
                    v-on:implementation-based-criteria-selected="setImplementationSpecificCriteria"
                    v-on:experiment-based-criteria-selected="setExperimentSpecificCriteria"/>
            </v-col>
            <v-col lg="7" v-if="specificImplementationBasedEvaluationCriteria.length>0">
                <ImplementationSpecificResultEditor
                        :implementation-specific-evaluation-criteria="specificImplementationBasedEvaluationCriteria"

                />
            </v-col>
            <v-col lg="7" v-if="specificExperimentBasedEvaluationCriteria.length>0">
                <v-row>
                    <v-col cols="12" lg="12">
                        <div class="subtitle-2">Specific Experiment Results</div>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" lg="12">
                        <v-data-table
                                :headers="metricsResultsHeaders"
                                :items="specificExperimentBasedEvaluationCriteria"
                                hide-default-footer
                        >
                            <template v-slot:body = " {items} ">
                                <tbody>
                                    <tr v-for="item in items" :key="item.name">
                                        <td>{{ item.metric.name + " ("+item.metric.criteria+")"}}</td>
                                        <td>
                                            <v-edit-dialog
                                                    :return-value.sync="item.result"
                                                    large
                                                    lazy
                                                    persistent
                                            >
                                                <div>{{ item.result }}</div>
                                                <template v-slot:input>
                                                    <div class="mt-3 title">Update Result</div>
                                                </template>
                                                <template v-slot:input>
                                                    <v-text-field
                                                        v-model="item.result"
                                                        label="Edit"
                                                        single-line
                                                        type="number"
                                                        autofocus
                                                    />
                                                </template>
                                            </v-edit-dialog>
                                        </td>
                                        <td>{{ item.metric.unit }}</td>
                                    </tr>
                                </tbody>
                            </template>
                            <template v-slot:footer>
                                <v-btn text color="primary" :disabled="!allowAddSpecificResults" @click="addSpecificResults">Add Results</v-btn>
                            </template>
                        </v-data-table>
                    </v-col>
                </v-row>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>

    import constants from "../../../helpers/constants";
    import EvaluationCriteriaPicker from "../criteria/EvaluationCriteriaSelector.vue";
    import ImplementationSpecificResultEditor from "../implementation/ImplementationSpecificResultEditor";

    export default {
        components: {ImplementationSpecificResultEditor, EvaluationCriteriaPicker},
        name: "EvaluationResults",
        props: {
            evaluationType: {
                type: String,
                default: constants.evaluationTypes.experimentSpecific,
            }
        },
        mounted() {
            if(this.experimentSpecificCriteria) {
                this.getMetrics();
                this.getExperimentSpecificEvaluationCriterias();
            } else {
                this.getImplementationSpecificEvaluationCriterias();
            }
        },
        watch: {
        },
        data() {
            return {
                isLoading: false,
                types: [],
                selectedCriteriasMetricsIds: [],
                selectedMetricsResults: [],
                items: [
                    {
                        id: 1,
                        name: 'Usability',
                        locked: false,
                        children: [],
                    },
                    {
                        id: 2,
                        name: 'Deployability',
                        locked: false,
                        children: [],
                    },
                    {
                        id: 3,
                        name: 'Security',
                        locked: false,
                        children: [],
                    }
                ],
                existingMetrics:[],
                existingImplementationEvaluationCriteria:[],
                specificExperimentBasedEvaluationCriteria: [],
                specificImplementationBasedEvaluationCriteria: [],
                metricsResultsHeaders: [
                    { text: 'Metric (criteria)', value: 'metric' },
                    { text: 'Result (click to edit)', value: 'result' },
                    { text: 'Unit', value: 'unit' },
                ],
                allExperimentSpecificEvaluationCriterias: [],
            }
        },
        computed: {
            allowAddSpecificResults() {
                return this.specificExperimentBasedEvaluationCriteria.filter(function(specificResult) {
                    return specificResult.result>0
                }).length===this.specificExperimentBasedEvaluationCriteria.length;
            },

            experimentSpecificCriteria: function() {
                return this.evaluationType === constants.evaluationTypes.experimentSpecific;
            },
        },
        methods: {
            setImplementationSpecificCriteria: function(implementationSpecificCriteria) {
                this.specificImplementationBasedEvaluationCriteria = implementationSpecificCriteria;
            },
            setExperimentSpecificCriteria: function(experimentSpecificCriteria) {
                this.specificExperimentBasedEvaluationCriteria = experimentSpecificCriteria;
            },
            addSpecificResults: function() {
                this.$emit("results-added", this.specificExperimentBasedEvaluationCriteria);
                this.specificExperimentBasedEvaluationCriteria = [];
                this.selectedCriteriasMetricsIds = [];
            }
        }
    }
</script>

<style scoped>

</style>
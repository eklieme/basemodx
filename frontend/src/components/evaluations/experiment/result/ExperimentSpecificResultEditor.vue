<template>
    <v-container fluid>
        <v-row>
            <v-col cols="12" lg="12">
                <div class="headline">Specific Experiment Results</div>
            </v-col>
        </v-row>
        <v-row justify="center" align="center" v-for="result in experimentSpecificResults" v-bind:key="result.resultMetric.criteria.id">
            <v-col cols="12" lg="4">
                <v-tooltip top>
                    <template v-slot:activator="{ on }">
                        <span v-on="on"><strong>Criteria:</strong> {{ result.resultMetric.name + " ("+result.resultMetric.criteria.category+")"}}</span>
                    </template>
                    {{result.resultMetric.criteria.description}}
                </v-tooltip>
            </v-col>
            <v-col lg="4">
                <v-radio-group v-model="result.resultGranularity" row>
                    <v-radio label="exact" :value="resultGranularities[0]"/>
                    <v-radio label="range" :value="resultGranularities[1]"/>
                </v-radio-group>
            </v-col>
            <v-col lg="4" v-if="result.resultGranularity === resultGranularities[0]">
                <v-edit-dialog
                        :return-value.sync="result.result"
                        large
                        lazy
                        persistent
                >
                    <div><strong>Result:</strong> {{ result.result }} {{ result.resultMetric.unit === 'none' ? "" : result.resultMetric.unit }} (click to edit)</div>
                    <template v-slot:input>
                        <div class="mt-3 title">Update Result</div>
                        <v-text-field
                                v-model="result.result"
                                label="Edit"
                                single-line
                                type="number"
                                autofocus
                        />
                    </template>
                </v-edit-dialog>
            </v-col>
            <v-col lg="4" v-else>
                <v-edit-dialog
                        :return-value.sync="result.from"
                        large
                        lazy
                        persistent
                >
                    <div><strong>from:</strong> {{ result.from }} {{ result.resultMetric.unit }} (click to edit)</div>
                    <template v-slot:input>
                        <div class="mt-3 title">Update From</div>
                        <v-text-field
                                v-model="result.from"
                                label="Edit"
                                single-line
                                type="number"
                                autofocus
                        />
                    </template>
                </v-edit-dialog>
                <v-edit-dialog
                        :return-value.sync="result.to"
                        large
                        lazy
                        persistent
                >
                    <div><strong>to:</strong> {{ result.to }} {{ result.resultMetric.unit }} (click to edit)</div>
                    <template v-slot:input>
                        <div class="mt-3 title">Update To</div>
                        <v-text-field
                                v-model="result.to"
                                label="Edit"
                                single-line
                                type="number"
                                autofocus
                        />
                    </template>
                </v-edit-dialog>
            </v-col>
            <v-col cols="12" lg="12">
                <v-textarea
                        label="Describe the specific result's context"
                        required
                        v-model="result.description"
                />
            </v-col>
        </v-row>
        <v-row>
            <v-btn text color="primary" :disabled="!allowAddSpecificResults" @click="addSpecificResults">Add Results</v-btn>
        </v-row>
    </v-container>
</template>

<script>

    import constants from "../../../../helpers/constants";

    export default {
        components: {},
        name: "ExperimentSpecificResultEditor",
        props: {
            experimentSpecificEvaluationCriteria: {
                type: Array,
                handler: function() {
                    return [];
                }
            },
            alreadyReportedExperimentSpecificResults: {
                type: Array,
                handler: function() {
                    return [];
                }
            }
        },
        data() {
            return {
                experimentSpecificResults: [],
            }
        },
        mounted() {
            this.initializeSpecificResults();
        },
        watch: {
            experimentSpecificEvaluationCriteria: {
                deep: true,
                handler: function(newlySelectedCriteria)  {
                    this.$log.debug("Selected experiment specific criteria changed", newlySelectedCriteria);
                    this.initializeSpecificResults();
                }

            }
        },
        computed: {
            allowAddSpecificResults() {
                return this.experimentSpecificResults.filter(function(specificResult) {
                    return specificResult.result ||
                        (specificResult.to>0 && specificResult.from>0
                         && (specificResult.from !== specificResult.to))
                }).length===this.experimentSpecificResults.length;
            },
            resultGranularities() {
                return [constants.resultGranularity.exact,constants.resultGranularity.range];
            }
        },
        methods: {
            addSpecificResults: function() {
                this.$emit("results-added", this.experimentSpecificResults);
            },
            initializeSpecificResults: function() {
                if(this.experimentSpecificEvaluationCriteria.length > 0) {
                    //some criteria are selected
                    this.$log.debug("initially creating specific result skeleton for experiment specific results")
                    this.experimentSpecificResults = this.experimentSpecificEvaluationCriteria.map(metric => {
                        return {
                            resultMetric: metric,
                            result: 0.0,
                            to: 0.0,
                            from: 0.0,
                            description: "",
                            resultGranularity: constants.resultGranularity.exact,
                        }
                    });
                    this.$log.debug("specific result skeleton", this.experimentSpecificResults);
                } else {
                    // selected set changed, do nothing yet
                    this.$log.debug("Selected set of criteria changed, do nothing YET")
                }
            }
        }
    }
</script>

<style scoped>

</style>
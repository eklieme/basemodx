<template>
    <v-data-table
            :headers="metricsResultsHeaders"
            :items="experimentSpecificEvaluationSetupsToShow"
            hide-default-footer
    >
        <template v-slot:body=" { items } ">
            <tbody>
            <tr v-for="item in items" :key="item.name">
                <td>{{ item.resultMetric.name + " ("+item.resultMetric.criteria.name+", "+item.resultMetric.criteria.category+")" }}</td>
                <td v-if="item.resultGranularity === 'exact'">
                    <v-edit-dialog
                            :return-value.sync="item.result"
                            large
                            lazy
                            persistent
                    >
                        <div>{{ item.result }} {{ item.resultMetric.unit === 'none' ? "" : item.resultMetric.unit }}</div>
                        <template v-slot:input>
                            <div class="mt-3 title">Update Specific Result</div>
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
                <td v-else>
                    <v-edit-dialog
                            :return-value.sync="item.from"
                            large
                            lazy
                            persistent
                    >
                        <div><strong>from:</strong> {{ item.from }} {{ item.resultMetric.unit === 'none' ? "" : item.resultMetric.unit }} (click to edit)</div>
                        <template v-slot:input>
                            <div class="mt-3 title">Update From</div>
                            <v-text-field
                                    v-model="item.from"
                                    label="Edit"
                                    single-line
                                    type="number"
                                    autofocus
                            />
                        </template>
                    </v-edit-dialog>
                    <v-edit-dialog
                            :return-value.sync="item.to"
                            large
                            lazy
                            persistent
                    >
                        <div><strong>to:</strong> {{ item.to }} {{ item.resultMetric.unit === 'none' ? "" : item.resultMetric.unit }} (click to edit)</div>
                        <template v-slot:input>
                            <div class="mt-3 title">Update To</div>
                            <v-text-field
                                    v-model="item.to"
                                    label="Edit"
                                    single-line
                                    type="number"
                                    autofocus
                            />
                        </template>
                    </v-edit-dialog>
                </td>
                <td>{{ item.description }}</td>
                <td>
                    <v-icon
                            small
                            @click="deleteMetricsResults(item)"
                    >
                        delete
                    </v-icon>
                </td>
            </tr>
            </tbody>
        </template>
    </v-data-table>
</template>

<script>

    export default {
        components: {},
        name: "ExperimentSpecificResultsBrowser",
        props: {
            experimentSpecificEvaluationSetups: {
                type: Array,
                handler: function() {
                    return [];
                }
            },
            allowEditing: {
                type: Boolean,
                default: false,
            }
        },
        data() {
            return {
                metricsResultsHeaders: [
                    { text: 'Metric (criteria)', value: 'metric' },
                    { text: 'Specific Result (click to edit)', value: 'result' },
                    { text: 'Description', value: 'description' },
                    { text: 'Action', value: 'action' },
                ],
                experimentSpecificEvaluationSetupsToShow: [],
            }
        },
        mounted() {
        },
        watch: {
          experimentSpecificEvaluationSetups: {
            deep: true,
            immediate: true,
            handler: function(newExperimentSpecificEvaluationSetups) {
              if(newExperimentSpecificEvaluationSetups) {
                this.$log.debug("\t...set experiment specific evaluation setups to show", newExperimentSpecificEvaluationSetups);
                this.experimentSpecificEvaluationSetupsToShow = newExperimentSpecificEvaluationSetups
              }
            }
          }
        },
        computed: {
        },
        methods: {
            deleteMetricsResults: function(result) {
                const index = this.experimentSpecificEvaluationSetups.indexOf(result);
                this.experimentSpecificEvaluationSetupsToShow.splice(index, 1);
                this.$emit("experiment-specific-results-modified", this.experimentSpecificEvaluationSetups);
            }
        }
    }
</script>

<style scoped>

</style>
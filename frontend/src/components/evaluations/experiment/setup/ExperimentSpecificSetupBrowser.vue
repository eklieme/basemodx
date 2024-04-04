<template>
    <v-container fluid>
        <v-row v-if="editMode">
            <v-col cols="12" lg="12">
                <div class="subtitle-1">Modelled Evaluation Results</div>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12">
                <v-data-table
                        :headers="experimentSpecificEvaluationSetupsHeadersToShow"
                        :items="enrichedExperimentSpecificEvaluationSetups"
                        v-if="enrichedExperimentSpecificEvaluationSetups.length>0"
                        hide-default-footer
                >
                    <template v-slot:body =" { items } ">
                        <tbody>
                            <tr v-for="item in items" :key="item.name">
                                <td>{{ item.description }}</td>
                                <td>{{ item.biometricSystems.map(biometricSystem => biometricSystem.name).join(", ") }}</td>
                                <td>{{ summarizeSpecificResults(item) }}</td>
                                <td>
                                    <template v-if="editMode">
                                        <v-icon
                                                @click="$emit('edit-result', item)"
                                        >
                                            edit
                                        </v-icon>
                                    </template>
                                    <template v-if="modellingMode">
                                        <v-icon
                                                @click="deleteEvaluationResult(item)"
                                        >
                                            delete
                                        </v-icon>
                                        <v-icon
                                                @click="duplicateEvaluationResult(item)"
                                        >
                                            file_copy
                                        </v-icon>
                                    </template>
                                </td>
                            </tr>
                        </tbody>
                    </template>
                </v-data-table>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>

    import {Util} from "../../../../helpers/util";
    import constants from "../../../../helpers/constants";

    export default {

        components: {},
        name: "ExperimentSpecificSetupBrowser",
        props: {
            experimentSpecificEvaluationSetups: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            biometricSystems: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            editMode: {
                type: Boolean,
                default: false,
            },
            modellingMode: {
                type: Boolean,
                default: false,
            }
        },
        data() {
            return {
                experimentSpecificEvaluationSetupsHeaders: [
                    { text: 'Name', value: 'name' },
                    { text: 'Biometric Systems', value: 'biometric_systems' },
                    { text: 'Metrics', value: 'metrics' },
                    { text: 'Action', value: 'action' },
                ],
                enrichedExperimentSpecificEvaluationSetups: [],
            }
        },
        computed: {
            experimentSpecificEvaluationSetupsHeadersToShow: function() {
                if(this.editMode) {
                    return this.experimentSpecificEvaluationSetupsHeaders;
                }
                return [
                    this.experimentSpecificEvaluationSetupsHeaders[0],
                    this.experimentSpecificEvaluationSetupsHeaders[1],
                    this.experimentSpecificEvaluationSetupsHeaders[2],
                ]
            }
        },
        watch: {
            experimentSpecificEvaluationSetups: {
                deep: true,
                immediate: true,
                handler: function(evaluationSetups) {
                    this.setLocalEvaluationResults(evaluationSetups);
                }
            },
            biometricSystems: {
                deep: true,
                immediate: true,
                handler: function(biometricSystems) {
                    this.setBiometricSystems(biometricSystems);
                }
            }
        },
        mounted: function() {
            this.setLocalEvaluationResults(this.experimentSpecificEvaluationSetups);
            //EventBus.$on(constants.eventNames.global.editBaseEvent, this.setEvaluationResultsForBASEEditing);
        },
        methods: {
            summarizeSpecificResults: function(item) {
                this.$log.debug("item to summarize", item);
                return item.specificResults.map(specificResult =>
                    specificResult.resultMetric.name
                    +" "
                    +(specificResult.resultGranularity === constants.resultGranularity.exact ?
                        specificResult.result : (specificResult.from +"-"+specificResult.to))
                    +" "
                    +constants.units.getUnitSign(specificResult.resultMetric.unit)
                    +(specificResult.description && specificResult.description.length>0 ? " ("+specificResult.description+") " : ""))
                    .join("; ")
            },
            setLocalEvaluationResults: function(evaluationResults) {
                this.enrichedExperimentSpecificEvaluationSetups = evaluationResults;

                this.$log.debug("Enriching evaluation results with biometric system informations ");

                /*
                 per default, we only get the ids of the used biometric systems. If the systems are provided in
                 addition, we can directly map them
                */

                this.setBiometricSystems();

                this.$log.debug("evaluation setups to show in browser",this.enrichedExperimentSpecificEvaluationSetups);
            },
            setBiometricSystems: function() {

                this.$log.debug("Merging biometric systems in specific results", this.biometricSystems);
                if(this.biometricSystems.length>0) {
                    this.biometricSystems.forEach((biometricSystem) => {

                        if(this.enrichedExperimentSpecificEvaluationSetups
                            && this.enrichedExperimentSpecificEvaluationSetups.length>0) {

                            this.enrichedExperimentSpecificEvaluationSetups.forEach(function (evaluationResult) {
                                const indexBiometricSystem = evaluationResult.biometricSystems.indexOf(biometricSystem.id);
                                if (indexBiometricSystem > -1) {
                                    evaluationResult.biometricSystems.splice(indexBiometricSystem,1, biometricSystem);
                                }
                            });

                        }
                    });
                }

            },
            deleteEvaluationResult: function(evaluationResult) {
                const index = this.enrichedExperimentSpecificEvaluationSetups.indexOf(evaluationResult);
                this.enrichedExperimentSpecificEvaluationSetups.splice(index, 1);
            },
            duplicateEvaluationResult: function(evaluationResult) {
                const copyEvaluationResult = Util.deepCopyObject(evaluationResult);
                copyEvaluationResult.description = evaluationResult.description+" 2";
                this.enrichedExperimentSpecificEvaluationSetups.push(copyEvaluationResult);
            },
        }
    }
</script>

<style scoped>

</style>
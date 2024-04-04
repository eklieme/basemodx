<template>
    <v-container fluid>
        <v-row v-if="editMode">
            <v-col cols="12" lg="12">
                <span class="headline">Evaluation Criteria Grants</span>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12">
                <v-data-table
                        :headers="evaluationCriteriaGrantsHeaders"
                        :items="evaluationCriteriaGrants"
                >
                    <template v-slot:item.grantingLevel = {item}>
                        <v-select
                                :items="grantingLevels"
                                v-model="item.grantingLevel"
                                item-text="name"
                                return-object
                                v-if="editMode"
                        />
                        <span v-else>{{item.grantingLevel.name}}</span>
                    </template>
                    <template v-slot:item.criteriaType = {item}>
                        {{criteriaTypeName(item.evaluationCriteria.type)}}
                    </template>
                    <template v-slot:item.action = {item}>
                        <v-btn color="primary" text
                                @click="showLinkedEvaluations(item)"
                        >
                            Show linked Evaluations
                        </v-btn>
                    </template>
                </v-data-table>
            </v-col>
        </v-row>
        <v-dialog v-model="showLinkedEvaluationResultsDialog" persistent >
            <v-card>
                <v-card-title class="headline">Linked evaluations</v-card-title>
                <ImplementationSpecificResultsBrowser
                    v-if="evaluationCriteriaGrantForDetailIsImplementationSpecific"
                    :allow-editing="false"
                    :reported-implementation-specific-results="implementationSpecificResultsToShow"
                />
                <ExperimentSpecificSetupBrowser
                    v-else
                    :edit-mode="false"
                    :modelling-mode="false"
                    :biometric-systems="baseEvaluation.experimentSpecificEvaluation.biometricSystems"
                    :experiment-specific-evaluation-setups="experimentSpecificResultsToShow"
                />
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" text @click="showLinkedEvaluationResultsDialog = false">Close</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>
    import ExperimentSpecificSetupBrowser from "../../experiment/setup/ExperimentSpecificSetupBrowser";
    import constants from "../../../../helpers/constants";
    import ImplementationSpecificResultsBrowser from "../../implementation/ImplementationSpecificResultsBrowser";
    import {Util} from "@/helpers/util";

    export default {
        name: "EvaluationCriteriaGrantBrowser",
        components: {ImplementationSpecificResultsBrowser, ExperimentSpecificSetupBrowser},
        props: {
            evaluationCriteriaGrants: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            baseEvaluation: {
                type: Object,
                default: function() {
                    return {};
                }
            },
            editMode: {
                type: Boolean,
                default: false,
            },
        },
        data() {
          return {
              showLinkedEvaluationResultsDialog: false,
              implementationSpecificResultsToShow: [],
              experimentSpecificResultsToShow: [],
              evaluationCriteriaGrantsToEdit: [],
              evaluationCriteriaGrantsHeaders: [
                  { text: 'Evaluation Criteria', value: 'evaluationCriteria.name' },
                  { text: 'Criteria Category', value: 'evaluationCriteria.category' },
                  { text: 'Criteria Type', value: 'criteriaType' },
                  { text: 'Granting Level', value: 'grantingLevel' },
                  { text: 'Action', value: 'action' },
              ],
              grantToShowEvaluationsFor:{},
          }
        },
        methods: {
            showLinkedEvaluations: function(grantToEdit) {

                this.$log.debug("show linked evaluations for grant", grantToEdit);

                this.grantToShowEvaluationsFor = grantToEdit;

                // set respective results to show in detail browser
                if(this.evaluationCriteriaGrantForDetailIsImplementationSpecific) {
                    // filter for implementation specific results
                    this.implementationSpecificResultsToShow = this.baseEvaluation.implementationSpecificEvaluationResults.filter((result) => {
                       return result.criteria.id === grantToEdit.evaluationCriteria.id;
                    });

                    this.$log.debug("Filtered implementation specific results to show evaluations for grant, "+
                        this.implementationSpecificResultsToShow.length+" remaining based on",this.baseEvaluation.implementationSpecificEvaluationResults);
                } else {
                    // filter for experiment specific results
                    this.experimentSpecificResultsToShow = this.baseEvaluation.experimentSpecificEvaluation.experimentSpecificEvaluationSetups.filter((result) => {
                        return result.specificResults.filter((specificResult) => {
                            return specificResult.resultMetric.parentEvaluationCriteriaId ===
                                grantToEdit.evaluationCriteria.id;
                        }).length>0
                    });
                    this.$log.debug("Filtered experiment specific results to show evaluations for grant, "+
                        this.experimentSpecificResultsToShow.length+" remaining based on",this.baseEvaluation.experimentSpecificEvaluation.experimentSpecificEvaluationSetups);
                }

                this.showLinkedEvaluationResultsDialog = true;
            },
            criteriaTypeName: function(criteriaTypeValue) {
                return constants.evaluationTypes.getEvaluationTypeName(criteriaTypeValue);
            },
        },
        computed: {
            grantingLevels: function () {
                return constants.grantingLevels.getAll();
            },
            evaluationCriteriaGrantForDetailIsImplementationSpecific: function() {
                if(this.grantToShowEvaluationsFor.evaluationCriteria
                    && this.grantToShowEvaluationsFor.evaluationCriteria.type === constants.evaluationTypes.implementationSpecific) {
                    return true;
                }
                return false;
            },
        },
        mounted() {
        },
        watch: {
            evaluationCriteriaGrants: {
                immediate: true,
                deep: true,
                handler: function(evaluationCriteriaGrants) {
                    if(evaluationCriteriaGrants && evaluationCriteriaGrants.length>0) {
                        this.$log.debug("List of evaluation criteria grants to show were changed!", evaluationCriteriaGrants);
                        this.evaluationCriteriaGrantsToEdit = Util.deepCopyObject(evaluationCriteriaGrants);
                    }
                }
            },
            evaluationCriteriaGrantsToEdit: {
                immediate: true,
                deep: true,
                handler: function(changedListOfEvaluationCriteriaGrantsToEdit) {
                    if(changedListOfEvaluationCriteriaGrantsToEdit && changedListOfEvaluationCriteriaGrantsToEdit.length>0) {
                        this.$log.debug("List of evaluation criteria grants were changed!", changedListOfEvaluationCriteriaGrantsToEdit);
                        this.$emit("set-evaluation-criteria-grants", changedListOfEvaluationCriteriaGrantsToEdit)
                    }
                }
            },
        }
    }
</script>

<style scoped>

</style>
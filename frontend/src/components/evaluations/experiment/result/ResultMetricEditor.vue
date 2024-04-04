<template>
    <v-col :lg="activatorWidth">
        <v-dialog v-model="editMetricDialog" persistent>
            <template v-slot:activator="{ on }">
                <v-btn v-on="on" text slot="activator" color="info" @click="getAllExperimentSpecificEvaluationCriterias">Add Result Metric</v-btn>
            </template>
            <v-card>
                <v-card-title>
                    <span class="headline">Add new Result Metric</span>
                </v-card-title>
                <v-card-text>
                    <v-form ref="form">
                        <v-container fluid>
                            <v-row>
                                <v-col cols="12" lg="4">
                                    <v-text-field
                                            label="Name of Metric"
                                            :rules = "[checkNewMetricNameUnique]"
                                            required
                                            v-model="resultMetricToEdit.name"
                                    />
                                </v-col>
                                <v-col lg="4">
                                    <v-select
                                            :items="units"
                                            label="Measurement Unit"
                                            required
                                            v-model="resultMetricToEdit.unit"
                                   />
                                </v-col>
                                <v-col lg="4">
                                    <v-select
                                            :items="experimentSpecificEvaluationcriterias"
                                            label="Parent Criteria"
                                            required
                                            v-model="parentCriteria"
                                            item-text="name"
                                            item-value="name"
                                            persistent-hint
                                            return-object
                                            single-line
                                    />
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col cols="12" lg="12" sm12 md12>
                                    <v-text-field
                                            label="Description of Metric"
                                            required
                                            v-model="resultMetricToEdit.description"
                                    />
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col cols="12" lg="12" sm12 md12>
                                    <ReferenceEditor
                                        :description-content-of-reference="'result metric'"
                                        :pre-filled-reference="resultMetricToEdit.reference"
                                        :reference-asset-type="resultMetricReferenceAssetType"
                                        v-on:set-reference = "setReference"
                                    />
                                </v-col>
                            </v-row>
                        </v-container>
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-btn color="primary" text @click="addSaveMetricToEdit()" :disabled="!metricToEditValid">{{editMode ? 'Save ':'Add '}} Metric</v-btn>
                    <v-btn color="error" text @click="cancelEditing">Cancel</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-col>
</template>

<script>

    import {EvaluationScenarioServiceAPI} from "../../../../service/api/EvaluationScenarioServiceAPI";
    import constants from "../../../../helpers/constants";
    import toast from "../../../../helpers/toast";
    import {Util} from "@/helpers/util";
    import ReferenceEditor from "@/components/reference/ReferenceEditor.vue";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";

    export default {
        components: {ReferenceEditor},
        name: "ResultMetricEditor",
        mixins: [LoggedInUserMixin],
        data() {
            return {
                resultMetricToEdit: {
                    id: null,
                    name: "",
                    description:"",
                    unit:"",
                    parentEvaluationCriteriaId: "",
                    parentCriteria: {},
                    reference: {},
                },
                parentCriteria:{},
                experimentSpecificEvaluationcriterias: [],
                editMetricDialog:false,
                existingResultMetrics: [],
                editMode: false,
            }
        },
        props: {
            activatorWidth: {
                type: String,
                default: "xs2",
            },
            resultMetricForEditing: {
                type: Object,
                default: function() {
                  return {};
                }
            },
        },
        watch: {
            resultMetricForEditing: {
              immediate: true,
              deep: true,
              handler: function(resultMetricForEditing) {
                if(resultMetricForEditing
                  && resultMetricForEditing.id) {
                  this.$log.debug("\t..set result metric for editing", resultMetricForEditing);
                  this.resultMetricToEdit = Util.deepCopyObject(resultMetricForEditing);
                  this.editMode=true;
                  this.editMetricDialog = true;
                  this.parentCriteria = this.experimentSpecificEvaluationcriterias.filter(criteria => {
                    return this.resultMetricToEdit.parentEvaluationCriteriaId === criteria.id;
                  })[0];
                } else {
                  this.editMode=false;
                  this.editMetricDialog = false;
                }
              }
            }
        },
        mounted: function() {
            this.getAllExistingMetrics();
            this.getAllExperimentSpecificEvaluationCriterias();
        },
        methods: {
            cancelEditing: function() {
                this.editMetricDialog = false;
                this.$emit(constants.eventNames.local.cancelEditing);
            },
            setReference: function(reference) {
                this.resultMetricToEdit.reference = reference;
            },
            checkNewMetricNameUnique: function(newMetricName) {
                if(this.editMode) {
                  if(this.resultMetricToEdit.name===this.resultMetricForEditing.name) {
                    return true;
                  }
                }
                const context = this;
                const indexCriteria = this.existingResultMetrics.findIndex(function(resultMetric) {
                    return resultMetric.name.toLowerCase() === newMetricName.toLowerCase()
                        && resultMetric.parentEvaluationCriteriaId === context.parentCriteria.id;
                })

                if(indexCriteria>-1) {
                    return 'A metric with this name already exists for the criteria "'+
                        this.parentCriteria.name+'", please choose a different name or parent criteria'
                } else {
                    return true;
                }
            },
            getAllExistingMetrics: function() {
                EvaluationScenarioServiceAPI.getResultMetrics().then(
                    (response) => {
                        this.existingResultMetrics = response.data;
                })
            },
            addSaveMetricToEdit: function() {

                this.resultMetricToEdit.parentEvaluationCriteriaId = this.parentCriteria.id;

                // initialize, if no id is available
                if(!this.resultMetricToEdit.id) {
                    this.setInitialModelLifecycleStateForDomainElement(this.resultMetricToEdit);
                }
                // save metric
                EvaluationScenarioServiceAPI.createUpdateResultMetric(this.resultMetricToEdit).then(
                    (response) => {
                        if(!this.resultMetricToEdit.id) {
                          this.resultMetricToEdit["id"] = response.data;
                        }
                        toast.success("Successfully created new result metric '"+this.resultMetricToEdit.name+"'");
                        this.updateCriteriaCategory();
                        this.getAllExistingMetrics();
                        this.$emit(constants.eventNames.local.artifactCreatedUpdated, this.resultMetricToEdit);
                    }
                );
            },
            updateCriteriaCategory: function() {

                // add metric to criterias metric list
                if(!this.parentCriteria.resultMetrics) {
                    this.parentCriteria.resultMetrics = [];
                }
                if(!this.parentCriteria.resultMetrics.find(metric => {
                    return metric.id === this.resultMetricToEdit.id
                })) {
                    this.$log.debug("...result metric is not yet part of parent criteria...push!", this.resultMetricToEdit);
                    this.parentCriteria.resultMetrics.push(this.resultMetricToEdit);

                    // add type for criteria
                    this.parentCriteria.type = constants.evaluationTypes.experimentSpecific;

                    // save result (and thus the result metric list)
                    EvaluationScenarioServiceAPI.createUpdateEvaluationCriteria(this.parentCriteria).then(
                        () => {
                            this.$emit('metric-added', this.parentCriteria.category);
                            this.resetEntitiesToEdit();
                        }
                    );

                } else {
                    this.$log.debug("...result metric is ALREADY part of parent criteria, do NOTHING", this.resultMetricToEdit);
                    this.resetEntitiesToEdit();
                }


            },
            getAllExperimentSpecificEvaluationCriterias: function() {
                return EvaluationScenarioServiceAPI.getExperimentSpecificEvaluationCriterias().then((response) => {
                    this.experimentSpecificEvaluationcriterias = response.data;
                });
            },
            resetEntitiesToEdit: function() {
                this.resultMetricToEdit = {
                    id:null,
                    description:"",
                    parentEvaluationCriteriaId: "",
                    name: "",
                    unit: "",
                    reference: {},
                    modelledElementDetail: {},
                };
                this.parentCriteria = {
                    name: "",
                    category: "",
                };
                this.editMetricDialog = false;
            }
        },
        computed: {
            units: function() {
                return constants.units.getAll();
            },
            resultMetricReferenceAssetType: function() {
                return constants.referenceAssetType.resultMetric;
            },
            metricToEditValid: function() {

              return this.checkNewMetricNameUnique(this.resultMetricToEdit.name) && this.resultMetricToEdit.description.length > 0
                  && this.resultMetricToEdit.name.length > 0
                  && this.resultMetricToEdit.unit.length > 0
                  && this.parentCriteria.category.length > 0;
            }
        }
    }
</script>

<style scoped>

</style>
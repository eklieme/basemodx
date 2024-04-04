<template>
    <v-container fluid>
        <v-row row wrap>
            <v-col cols="12" lg="5">
                <div class="headline" v-if="!standAlone">Which {{experimentSpecificCriteria ? 'experiment specific' : 'implementation specific'}} evaluation criteria {{(experimentSpecificCriteria ? "and metrics" : "")}} were targeted?</div>
                <div class="headline" v-else>Browse {{experimentSpecificCriteria ? 'experiment specific' : 'implementation specific'}} evaluation criteria {{(experimentSpecificCriteria ? "and metrics" : "")}}</div>
            </v-col>
            <template v-if="editMode">
                <EvaluationCriteriaEditor
                        :activator-width="'3'"
                        :criteria-type="evaluationType"
                        :type-selectable="false"
                        v-on:criteria-added="updateAvailableEvaluationCriteria"
                        :evaluation-criteria-for-editing="evaluationCriteriaToEdit"
                        v-on:artifact-created-updated="updateAvailableEvaluationCriteria"
                        v-on:cancel-editing="evaluationCriteriaToEdit = {}"/>
                <v-spacer
                    />
                <ResultMetricEditor
                        v-if="experimentSpecificCriteria"
                        :activator-width="'3'"
                        :result-metric-for-editing="resultMetricToEdit"
                        v-on:metric-added="updateCriteriaCategorySubTree"
                        v-on:artifact-created-updated="updateCriteriaCategorySubTree"
                        v-on:cancel-editing="resultMetricToEdit = {}"/>
            </template>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12">
                <template v-if="anyCriteriaExists">
                    <v-text-field
                            v-model="searchCriteriaAndMetrics"
                            label="Search Criteria and Metrics"
                            clearable
                    ></v-text-field>
                    <v-treeview
                            v-model="selectedCriteriasMetricsIds"
                            :load-children="fetchCriteriasForEvaluationCategory"
                            :items="evaluationCriteriaTreeData"
                            :search="searchCriteriaAndMetrics"
                            item-disabled="locked"
                            :open-on-click="false"
                            :selectable="criteriaSelectable"
                            transition
                    >
                        <template v-slot:append="{ item }">
                            <a><span
                                    v-if="item.unit"
                                    @click="showEvaluationCriteriaDetails(item)"
                            >
                              metric
                            </span></a>
                            <v-icon v-if="item.unit && userCanEdit(item, getUniqueUserId())" @click="editResultMetric(item)">
                                edit
                            </v-icon>
                            <a><span
                                    v-if="item.category && !item.unit"
                                    @click="showEvaluationCriteriaDetails(item)"
                            >
                              criteria
                            </span></a>
                            <v-icon v-if="item.category && !item.unit && userCanEdit(item, getUniqueUserId())" @click="editEvaluationCriteria(item)">
                                edit
                            </v-icon>
                        </template>
                    </v-treeview>
                    <span class="subtitle-2" v-if="specificExperimentBasedEvaluationResultMetrics.length === 0 && !standAlone">You need to check at least one metric per criteria. Please
                    add one in case none is available.</span>
                </template>
                <span v-else>No {{criteriaType}} criteria exists, please <router-link to="/model/criteria">model</router-link> a new one!</span>
            </v-col>
        </v-row>
        <v-dialog v-model="criteriaDetailDialog">
          <v-card>
            <v-card-title>
              Details for {{evaluationCriteriaSelectedForDetails.name}} (<span v-if="evaluationCriteriaSelectedForDetails.category">Evaluation Criteria, {{evaluationCriteriaSelectedForDetails.category}}</span><span v-else>Metric</span>)
            </v-card-title>

            <v-card-text>
              <v-row>
                <v-col cols="12" lg="1">
                  <b>Description:</b>
                </v-col>
                <v-col lg="11">
                  {{evaluationCriteriaSelectedForDetails.description}}
                </v-col>
              </v-row>
              <template v-if="evaluationCriteriaSelectedForDetails.category">
                <v-row>
                  <v-col cols="12" lg="1">
                    <b>Granting Rules:</b>
                  </v-col>
                  <v-col lg="4">
                    <i>Granted:</i>
                    {{evaluationCriteriaSelectedForDetails.grantedRule.rule}}
                  </v-col>
                  <v-col lg="3">
                    <i>Quasi-Granted:</i>
                    {{evaluationCriteriaSelectedForDetails.quasiGrantedRule.rule}}
                  </v-col>
                  <v-col lg="4">
                    <i>Not-Granted:</i>
                    {{evaluationCriteriaSelectedForDetails.notGrantedRule.rule}}
                  </v-col>
                </v-row>
                <v-row>
                  <v-col cols="12" lg="1">
                    <b>Reference:</b>
                  </v-col>
                  <v-col lg="11">
                    <ReferencesViewer :reference="evaluationCriteriaSelectedForDetails.reference"/>
                  </v-col>
                </v-row>
              </template>
              <template v-else>
                <v-row>
                  <v-col cols="12" lg="1">
                    <b>Unit:</b>
                  </v-col>
                  <v-col lg="11">
                    {{evaluationCriteriaSelectedForDetails.unit}}
                  </v-col>
                </v-row>
              </template>
            </v-card-text>

            <v-divider></v-divider>

            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn
                  color="primary"
                  text
                  @click="criteriaDetailDialog = false"
              >
                Close
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
    </v-container>
</template>

<script>

    import {EvaluationScenarioServiceAPI} from "../../../service/api/EvaluationScenarioServiceAPI";
    import EvaluationCriteriaEditor from "./EvaluationCriteriaEditor.vue";
    import constants from "../../../helpers/constants";
    import ReferencesViewer from "@/components/reference/ReferencesViewer";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import ResultMetricEditor from "@/components/evaluations/experiment/result/ResultMetricEditor.vue";
    import BrowserForReviewableArtifactsMixin from "@/mixins/BrowserForReviewableArtifactsMixin";

    export default {
        components: {ResultMetricEditor, ReferencesViewer, EvaluationCriteriaEditor},
        name: "EvaluationCriteriaSelector",
        mixins:[LoggedInUserMixin, BrowserForReviewableArtifactsMixin],
        data() {
            return {
                isLoading: false,
                types: [],
                selectedCriteriasMetricsIds: [],
                selectedMetricsResults: [],
                criteriaDetailDialog: false,
                evaluationCriteriaTreeData: [
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
                specificExperimentBasedEvaluationResultMetrics: [],
                specificImplementationBasedEvaluationCriteria: [],
                metricsResultsHeaders: [
                    { text: 'Metric (criteria)', value: 'metric' },
                    { text: 'Result (click to edit)', value: 'result' },
                    { text: 'Unit', value: 'unit' },
                ],
                existingExperimentSpecificEvaluationCriterias: [],
                selectedExperimentSpecificEvaluationCriteriaWithNoMetrics: [],
                searchCriteriaAndMetrics: null,
                evaluationCriteriaExportArtefacts: [],
                allowSelectionForExport: false,
                artefactType: "",
                evaluationCriteriaSelectedForDetails: {},
                evaluationCriteriaToEdit: {},
                resultMetricToEdit:{},
            }
        },
        props: {
            evaluationType: {
                type: String,
                default: constants.evaluationTypes.experimentSpecific,
            },
            preSelectedCriteriaAndMetrics: {
                type: Array,
                handler: function() {
                    return [];
                }
            },
            standAlone: {
                type: Boolean,
                default: false,
            },
            editMode: {
                type: Boolean,
                default: true,
            },
        },
        mounted() {
            // initiate selected metrics if existing
            if(this.preSelectedCriteriaAndMetrics && this.preSelectedCriteriaAndMetrics.length>0) {
                this.selectedCriteriasMetricsIds = this.preSelectedCriteriaAndMetrics;
            }
            // update criteria
            this.updateAvailableEvaluationCriteria();

            // set artefact type
            if(this.evaluationType === constants.evaluationTypes.experimentSpecific) {
                this.artefactType = constants.importExport.artefactTypes.experimentCriteria;
            } else {
                this.artefactType = constants.importExport.artefactTypes.implementationCriteria;
            }

        },
        watch: {
            selectedCriteriasMetricsIds(currentlySelectedCriteriaAndMetricIds) {

                this.$log.debug("User selected "+(this.experimentSpecificCriteria ? "result metrics" : "criteria"));

                if(this.experimentSpecificCriteria) {
                    this.setSelectedCriteriaAndMetricsForExperimentSpecificEvaluations(
                        currentlySelectedCriteriaAndMetricIds
                    );
                } else {
                    this.setSelectedCriteriasForImplementationSpecificEvaluations(
                        currentlySelectedCriteriaAndMetricIds
                    );
                }

            },
            preSelectedCriteriaAndMetrics: {
                immediate: true,
                deep: true,
                handler: function(preSelectedCriteriaAndMetrics) {
                    this.selectedCriteriasMetricsIds = preSelectedCriteriaAndMetrics;
                }
            }
        },
        computed: {
            allowAddSpecificResults() {
                return this.specificExperimentBasedEvaluationResultMetrics.filter(function(specificResult) {
                    return specificResult.result>0
                }).length===this.specificExperimentBasedEvaluationResultMetrics.length;
            },

            experimentSpecificCriteria: function() {
                return this.evaluationType === constants.evaluationTypes.experimentSpecific;
            },
            anyCriteriaExists: function() {
                return this.evaluationCriteriaTreeData.map(item => item.children).flat().length>0;
            },
            criteriaSelectable: function() {
                return !this.standAlone || this.allowSelectionForExport
            },
            criteriaType: function() {
                return this.experimentSpecificCriteria ? "experiment specific" : "implementation specific";
            },
        },
        methods: {
            editEvaluationCriteria: function(evaluationCriteriaToEdit) {
                this.evaluationCriteriaToEdit = evaluationCriteriaToEdit;
            },
            editResultMetric: function(resultMetricToEdit) {
                this.resultMetricToEdit = resultMetricToEdit;
            },
            showEvaluationCriteriaDetails: function(item) {
              this.criteriaDetailDialog = true;
              this.evaluationCriteriaSelectedForDetails = item;
            },
            setSelectedCriteriaAndMetricsForExperimentSpecificEvaluations: function(currentlySelectedCriteriaAndMetricIds) {

                // make sure we have the most recent criteria and metrics

                const context = this;

                this.$log.debug("Set selected criteria and result metrics based on ids", currentlySelectedCriteriaAndMetricIds);

                if(currentlySelectedCriteriaAndMetricIds) {
                    EvaluationScenarioServiceAPI.getExperimentSpecificEvaluationCriterias().then(
                        (response) => {

                            this.existingExperimentSpecificEvaluationCriterias = response.data;

                            this.selectedExperimentSpecificEvaluationCriteriaWithNoMetrics = response.data.filter(criteria => {
                                return currentlySelectedCriteriaAndMetricIds.indexOf(criteria.id) !== -1;
                            });

                            this.$log.debug("selected experiment specific criteria with no result metric yet ", this.selectedExperimentSpecificEvaluationCriteriaWithNoMetrics)

                            //reset metrics
                            this.existingMetrics = [];

                            this.existingExperimentSpecificEvaluationCriterias.forEach(function (criteria) {
                                if (criteria.resultMetrics) {
                                    context.existingMetrics = context.existingMetrics.concat(criteria.resultMetrics);
                                }
                            });

                            const selectedMetrics = [];

                            for (const leaf of currentlySelectedCriteriaAndMetricIds) {
                                this.existingMetrics.forEach(function (item) {
                                    if (item.id === leaf) {
                                        selectedMetrics.push(item)
                                    }
                                });
                            }

                            this.$log.debug("The following metrics are set by the user", selectedMetrics);


                            if (this.specificExperimentBasedEvaluationResultMetrics.length === 0) {

                                this.$log.debug("Set experiment evaluation result metrics initially!");

                                selectedMetrics.forEach(function (selectedResultMetric) {

                                    context.setCriteriaForMetric(selectedResultMetric);

                                    context.specificExperimentBasedEvaluationResultMetrics.push(selectedResultMetric);

                                });
                            } else {

                                //check if new selected metrics came up
                                const selectedMetricIds = selectedMetrics.map(selectedResultMetric => selectedResultMetric.id);

                                const filteredResults = this.specificExperimentBasedEvaluationResultMetrics.filter(function (specificResult) {
                                    return selectedMetricIds.indexOf(specificResult.id) > -1
                                });

                                this.$log.debug("Schnittmenge of selected and existing", filteredResults);

                                if (filteredResults.length === selectedMetrics.length && filteredResults.length === this.specificExperimentBasedEvaluationResultMetrics.length) {
                                    //nothing changed
                                } else if (filteredResults.length < this.specificExperimentBasedEvaluationResultMetrics.length && filteredResults.length === selectedMetrics.length) {
                                    //after filtering, more specific results than selected metrics exist. Delete them, they were probably unticked
                                    //no category enrichment needed
                                    this.specificExperimentBasedEvaluationResultMetrics = this.specificExperimentBasedEvaluationResultMetrics.filter(function (specificResult) {
                                        return filteredResults.map(filteredResult => filteredResult.id).indexOf(specificResult.id) > -1
                                    });
                                } else if (filteredResults.length === this.specificExperimentBasedEvaluationResultMetrics.length && filteredResults.length < selectedMetrics.length) {
                                    //after filtering, more selected metrics than specific results exist. Add them, they were probably ticked
                                    //do enrichment to make sure category is properly set
                                    selectedMetrics.filter(function (selectedResultMetric) {
                                        return filteredResults.map(filteredResult => filteredResult.id).indexOf(selectedResultMetric.id) === -1
                                    }).forEach(function (selectedResultMetric) {

                                        // set parent category
                                        context.setCriteriaForMetric(selectedResultMetric);

                                        context.$log.debug("selected result metric", selectedResultMetric);

                                        context.specificExperimentBasedEvaluationResultMetrics.push(selectedResultMetric);
                                    });
                                }

                            }

                            this.$log.debug("selected metrics", this.specificExperimentBasedEvaluationResultMetrics);

                            if (this.standAlone) {
                                /*
                            mark for export, therefor, we need to get the criteria based on the result metrics
                             */

                                let parentCriteriaIds = []

                                this.specificExperimentBasedEvaluationResultMetrics.forEach(metric => {
                                    parentCriteriaIds.push(metric.parentEvaluationCriteriaId);
                                });


                                EvaluationScenarioServiceAPI.getExperimentSpecificEvaluationCriteriasById(parentCriteriaIds).then(
                                    response => {
                                        // concat with categories that do not have a result metric declared, yet
                                        this.evaluationCriteriaExportArtefacts = response.data.concat(this.selectedExperimentSpecificEvaluationCriteriaWithNoMetrics);
                                        this.$log.debug("Set experiment specific criteria to export to", this.evaluationCriteriaExportArtefacts);
                                    }
                                );


                            } else {
                                // inform parent
                                this.$emit(constants.eventNames.local.evaluationCriteriaSelected, this.specificExperimentBasedEvaluationResultMetrics);
                            }

                        });
                }
            },
            setSelectedCriteriasForImplementationSpecificEvaluations: function(currentlySelectedCriteriaAndMetricIds) {

                // make sure we have the most recent criteria and metrics

                const context = this;

                EvaluationScenarioServiceAPI.getImplementationSpecificEvaluationCriterias().then(
                    (response) => {

                        this.existingImplementationEvaluationCriteria = response.data;

                        const selectedImplementationSpecificCriteria = [];

                        for (const leaf of currentlySelectedCriteriaAndMetricIds) {
                            this.existingImplementationEvaluationCriteria.forEach(function (item) {
                                if (item.id === leaf) {
                                    selectedImplementationSpecificCriteria.push(item)
                                }
                            });
                        }

                        this.$log.debug("The following implementation specific criteria are set by the user", selectedImplementationSpecificCriteria);

                        if (this.specificImplementationBasedEvaluationCriteria.length === 0) {

                            this.$log.debug("Set specific implementation specific results initially!");

                            selectedImplementationSpecificCriteria.forEach(function (selectedResultCriteria) {

                                context.specificImplementationBasedEvaluationCriteria.push(selectedResultCriteria);

                            });
                        } else {

                            //check if new selected criteria came up
                            const selectedCriteriaIds = selectedImplementationSpecificCriteria.map(selectedCriteria => selectedCriteria.id);

                            const filteredResults = this.specificImplementationBasedEvaluationCriteria.filter(function (specificResult) {
                                return selectedCriteriaIds.indexOf(specificResult.id) > -1
                            });

                            this.$log.debug("Schnittmenge of selected and existing criteria", filteredResults);

                            const that = this;

                            if (filteredResults.length === selectedImplementationSpecificCriteria.length && filteredResults.length === this.specificImplementationBasedEvaluationCriteria.length) {
                                //nothing changed
                            } else if (filteredResults.length < this.specificImplementationBasedEvaluationCriteria.length && filteredResults.length === selectedImplementationSpecificCriteria.length) {
                                //after filtering, more specific results than selected metrics exist. Delete them, they were probably unticked
                                //no category enrichment needed
                                this.specificImplementationBasedEvaluationCriteria = this.specificImplementationBasedEvaluationCriteria.filter(function (specificResult) {
                                    return filteredResults.map(filteredResult => filteredResult.id).indexOf(specificResult.id) > -1
                                });
                            } else if (filteredResults.length === this.specificImplementationBasedEvaluationCriteria.length && filteredResults.length < selectedImplementationSpecificCriteria.length) {
                                //after filtering, more selected metrics than specific results exist. Add them, they were probably ticked
                                //do enrichment to make sure category is properly set
                                selectedImplementationSpecificCriteria.filter(function (selectedResultCriteria) {
                                    return filteredResults.map(filteredResult => filteredResult.id).indexOf(selectedResultCriteria.id) === -1
                                }).forEach(function (selectedResultCriteria) {

                                    that.$log.debug("selected result criteria", selectedResultCriteria);

                                    that.specificImplementationBasedEvaluationCriteria.push(selectedResultCriteria);
                                });
                            }

                        }

                        this.$log.debug("selected specific implementation based criteria", this.specificImplementationBasedEvaluationCriteria);
                        if(this.standAlone) {
                            // inform for import / export
                            this.evaluationCriteriaExportArtefacts = this.specificImplementationBasedEvaluationCriteria;
                        } else {
                            // inform parent component
                            this.$emit(constants.eventNames.local.evaluationCriteriaSelected, this.specificImplementationBasedEvaluationCriteria);
                        }
                    });
            },
            setCriteriaForMetric: function(selectedResultMetric) {

                this.$log.debug("Search criteria category for metric "+selectedResultMetric.name+" based on all criteria", this.existingExperimentSpecificEvaluationCriterias);
                this.$log.debug("\t metric id: "+selectedResultMetric.id);

                const context = this;

                // set parent category
                selectedResultMetric.criteria = this.existingExperimentSpecificEvaluationCriterias.find(function (criteria) {

                    if(!criteria.resultMetrics) {
                        return false;
                    }

                    const index = criteria.resultMetrics.findIndex(function(metric) {
                        return metric.id === selectedResultMetric.id;
                    });

                    context.$log.debug("Found metric at index "+index+" of category "+criteria.category);

                    return index > -1;

                });

                this.$log.debug("Found criteria category "+selectedResultMetric.criteria.category+" for metric "+selectedResultMetric.name);

            },
            fetchCriteriasForEvaluationCategory: function(item) {

                const context = this;
                if(this.experimentSpecificCriteria) {
                    return EvaluationScenarioServiceAPI.getExperimentSpecificEvaluationCriteriasByCategory(item.name).then((response) => {
                        context.fillCriteriaCategorySubTree(item, response.data);
                    });
                } else {
                    return EvaluationScenarioServiceAPI.getImplementationSpecificEvaluationCriteriasByCategory(item.name).then((response) => {
                        context.fillCriteriaCategorySubTree(item, response.data);
                    });
                }
            },
            fillCriteriaCategorySubTree:function(parentCategory, children) {

                const context = this;

                parentCategory.children = children;
                this.$log.debug("Children of category ", parentCategory.name, parentCategory.children);
                if(parentCategory.children != null) {
                    if(parentCategory.children.length === 0) {
                        // No children available yet
                        parentCategory.locked = true;
                        this.$log.debug("No criteria for category "+parentCategory.name+" exist, you need to create one!");
                    } else {
                        parentCategory.children.forEach(function (child) {
                            context.$log.debug("Adding Children", child)
                            if (Object.prototype.hasOwnProperty.call(child, "resultMetrics") && child["resultMetrics"] != null) {
                                context.$log.debug("renaming 'resultMetrics' to 'children'", child["resultMetrics"]);
                                Object.defineProperty(child, "children",
                                    Object.getOwnPropertyDescriptor(child, "resultMetrics"));
                                delete child["resultMetrics"];
                            }

                        });
                    }
                }
            },
            getMetrics: function() {
                EvaluationScenarioServiceAPI.getResultMetrics().then((response) => {
                    this.existingMetrics = response.data;
                });
            },
            updateAvailableEvaluationCriteria: function() {
                this.evaluationCriteriaTreeData.forEach(criteria => {
                    criteria.locked = false;
                    this.fetchCriteriasForEvaluationCategory(criteria);
                });
                this.evaluationCriteriaToEdit = {};
            },
            updateCriteriaCategorySubTree: function(categoryName) {

                this.$log.debug("...update sub tree for category", categoryName);
                for(const category of this.evaluationCriteriaTreeData) {
                    if(category.name.toLowerCase() === categoryName.toLowerCase()) {
                        this.fetchCriteriasForEvaluationCategory(category);
                    }
                }
                this.resultMetricToEdit = {}

            },
            addSpecificResults: function() {
                this.$emit("results-added", this.specificExperimentBasedEvaluationResultMetrics);
                this.resetSelection();
            },
            setSelectableForExport: function(selectable) {
                this.allowSelectionForExport = selectable;
                if(!selectable) {
                    this.evaluationCriteriaExportArtefacts = [];
                    this.resetSelection();
                }
            },
            resetSelection() {

                this.specificExperimentBasedEvaluationResultMetrics = [];
                this.selectedCriteriasMetricsIds = [];
            }
        }
    }
</script>

<style scoped>

</style>
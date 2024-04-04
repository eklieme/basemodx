<template>
    <v-container fluid>
        <ImportExport v-if="standAlone && userIsReviewer"
            :artefact-name="'BASE'"
            :export-artefacts="base"
            :allow-export-selection="allowExport"
            :show-export-selection="allowExport"
            :artefact-type="baseArtefactType"
            v-on:selection-for-export-allowed="setAllowSelection"
            v-on:export-finished="resetExportSelection"/>
        <v-row v-if="standAlone" row wrap align="center">
            <v-col cols="12" lg="12">
                <div class="headline">Last Modelled Systems</div>
            </v-col>
        </v-row>
        <v-row v-if="standAlone">
          <v-col cols="12" lg="12">
              <v-select
                  label="Modelling Progress Filter"
                  v-model="modellingProgressFilter"
                  :items="modellingProgresses">
              </v-select>
          </v-col>
        </v-row>
        <v-row v-if="!baseRetrievalRunning && standAlone">
            <v-col cols="12" lg="12">
                <v-text-field
                        v-model="searchBaseFilter"
                        append-icon="search"
                        label="Search by BASE name"
                        single-line
                        hide-details
                ></v-text-field>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12">
                <v-data-table
                        v-model="selectedBase"
                        :headers="baseHeaders"
                        :items="base"
                        class="elevation-1"
                        :loading="baseRetrievalRunning"
                        :search="searchBaseFilter"
                        :single-select="false"
                        :show-select="enableSelectionForExport"
                >
                    <template v-slot:item.name = {item}>
                        {{item.name}}
                    </template>
                    <template v-slot:item.lastModification = {item}>
                        {{item.lastModifiedDate}}
                    </template>
                    <template v-slot:item.characteristics = {item}>
                        {{summarizeCharacteristics(item.biometricCharacteristics)}}
                    </template>
                    <template v-slot:item.targetArchitecture = {item}>
                        {{summarizeTargetArchitecture(item.targetArchitecture)}}
                    </template>
                    <template v-slot:item.expEvaluation = {item}>
                        {{summarizeExperimentalEvaluation(item.baseEvaluation.experimentSpecificEvaluation)}}
                    </template>
                    <template v-slot:item.implEvaluation = {item}>
                        {{summarizeImplementationEvaluation(item.baseEvaluation.implementationSpecificEvaluationResults)}}
                    </template>
                    <template v-slot:item.evaluationCriteriaGrants = {item}>
                        {{summarizeEvaluationCriteriaGrants(item.evaluationCriteriaGrants)}}
                    </template>
                    <template v-slot:item.reference = {item}>
                        {{summarizeReference(item.reference)}}
                    </template>
                    <template v-if="showLifecycleStateInformation" v-slot:item.lifecycleState = {item}>
                      Modelled by {{ getLifecycleStateSummary(getUniqueUserId(), item) }}
                    </template>
                    <template v-slot:item.actions = {item}>
                      <ArtifactBrowserActionUtils :artifact-for-actions="item"
                                                  :show-review-action="showReviewAction"
                                                  :user-is-reviewer="userIsReviewer"
                                                  :artifact-detail-view-available="true"
                                                  :edit-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                                                  :delete-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                                                  :add-specifics-allowed="evaluationsCanBeAdded(item)"
                                                  v-on:open-review-dialog="openReviewDialog"
                                                  v-on:edit-artifact="editBASE"
                                                  v-on:add-specifics-artifact="triggerBaseEvaluationExtension"
                                                  v-on:delete-artifact="requestBASEDelete"
                                                  v-on:show-artifact-details="showBASEDetails"
                                                  v-on:target-artefacts-to-review-reload-required="loadBaseToReview"/>
                    </template>
                </v-data-table>
            </v-col>
        </v-row>
        <v-dialog v-model="showDeleteBaseDialog" persistent >
            <v-card>
                <v-card-title class="headline">Delete Behavioral Authentication System and Evaluations?</v-card-title>
                <v-card-text>Due to possible dependencies, this will <b>neither</b> delete the used characteristics, nor the dataset, nor the biometric systems or
                    the given target architecture. Nevertheless the overall composition will be lost as well as the evaluation setups with their specific
                results.</v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" text @click="showDeleteBaseDialog = false">Cancel</v-btn>
                    <v-btn color="error" text @click="deleteBASE">OK</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
      <v-dialog v-model="baseDetailsDialogOpened" fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card>
          <v-toolbar
              dark
              color="info"
              dense
          >
            <v-toolbar-title>
              <span class="subtitle-1">Details for BASE '{{baseOfInterest.name}}'</span>
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn
                icon
                dark
                @click="baseDetailsDialogOpened = false"
            >
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </v-toolbar>
          <BaseDetailsViewer :base-to-show="baseOfInterest"/>
        </v-card>
      </v-dialog>
      <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-type="constants.review.artifactType.base"
                            :artifact-to-review="currentArtifactToReview"
                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadBaseToReview"
      />
        <DialogContainer dialog-title="Extend Evaluation" :dialog-opened="baseEvaluationExtenderDialogOpened"
          v-on:close-dialog="closeBaseExtensionDialog">
            <template v-slot:content>
              <BaseEvaluationExtender
                :base-for-extension="baseForEvaluationExtension"
                v-on:artifact-created-updated="setBaseEvaluationExtension"/>
            </template>
            <template v-slot:actions>
                <v-btn text
                       :disabled="!baseEvaluationExtension"
                       @click="createUpdateBaseEvaluationExtension"
                       color="primary"
                >Submit Base Evaluation Extension</v-btn>
            </template>
        </DialogContainer>
    </v-container>
</template>

<script>

  import {BiometricAuthenticationSystemAndEvaluationAPI} from "../../service/api/BiometricAuthenticationSystemAndEvaluationAPI";
  import EventBus from "../../helpers/eventBus";
  import constants from "../../helpers/constants";
  import transformers from "../../service/transformers";
  import {Util as utils, Util} from "../../helpers/util";
  import ImportExport from "../util/ImportExport";
  import {EvaluationScenarioServiceAPI} from "../../service/api/EvaluationScenarioServiceAPI";
  import axios from "axios";
  import BrowserForReviewableArtifactsMixin from "../../mixins/BrowserForReviewableArtifactsMixin";
  import LoggedInUserMixin from "../../mixins/LoggedInUserMixin";
  import {ModelledElementReviewAPI} from "../../service/api/ModelledElementReviewAPI";
  import {BiometricSystemAPI} from "../../service/api/BiometricSystemAPI";
  import toast from "../../helpers/toast";
  import ArtifactWithReferenceMixin from "../../mixins/ArtifactWithReferenceMixin";
  import BaseDetailsViewer from "./BaseDetailsViewer.vue";
  import ArtifactReviewDialog from "../review/ArtifactReviewDialog.vue";
  import ArtifactBrowserActionUtils from "../review/ArtifactBrowserActionUtils.vue";
  import Vue from "vue";
  import {BiometricCharacteristicsService} from "@/service/api/BiometricCharacteristicsService";
  import DialogContainer from "@/components/util/DialogContainer.vue";
  import BaseEvaluationExtender from "@/components/evaluations/extension/BaseEvaluationExtender.vue";

  export default {
    components: {
        BaseEvaluationExtender,
        DialogContainer, ArtifactBrowserActionUtils, ArtifactReviewDialog, BaseDetailsViewer, ImportExport},
    mixins: [LoggedInUserMixin, BrowserForReviewableArtifactsMixin, ArtifactWithReferenceMixin],
    props: {
      standAlone: {
        type:Boolean,
        default:false,
      },
      baseToShow: {
        type: Array,
        default: function() {
          return [];
        },
      }
    },
    name: "BaseBrowser",
    data() {
        return {
            base:[],
            selectedBase: [],
            baseRetrievalRunning: false,
            searchBaseFilter:"",
            showDeleteBaseDialog: false,
            modellingProgressFilter: "reference",
            baseOfInterest: {},
            baseDetailsDialogOpened: false,
            enableSelectionForExport: false,
            baseEvaluationExtenderDialogOpened: false,
            baseExportArtefacts: [],
            baseForEvaluationExtension: {},
            baseEvaluationExtension: null,
        }
    },
    watch: {
        modellingProgressFilter: function() {
            this.getAllBase();
        },
        baseToShow: {
            deep: true,
            immediate: true,
            handler: function(baseToShow) {
              this.$log.debug("baseToShow changed", baseToShow);
              if(baseToShow && baseToShow.length>0) {
                this.base = utils.deepCopyObject(baseToShow);
              }
            }
        },
        base: {
          deep: true,
          immediate: true,
          handler: function(base) {
            this.$log.debug("overall bases changed");
            if(base && base.length>0) {
              this.updateBrowseableArtifactCount(constants.review.artifactType.base, this.base.length);
            }
          }
        },
        selectedBase: {
            deep: true,
            handler: function(newSelection) {

                // transform selected Base for export
                if (newSelection && newSelection.length > 0) {

                    this.$log.debug("Selected BASE for export", newSelection);
                    this.baseExportArtefacts = [];

                    let experimentSpecificCriteriaToQuery = [];
                    let tempExportBase = [];

                    newSelection.forEach((base, index) => {

                        let baseForExport = transformers.transformBaseForPersistence(base, true);
                        this.$log.debug(baseForExport);

                        if(baseForExport[4].length>0) {
                            experimentSpecificCriteriaToQuery.push({
                                index: index,
                                criteriaIds: baseForExport[4]
                            });
                        }

                        tempExportBase.push({
                            behavioralAuthenticationSystem: baseForExport[0],
                            datasets: baseForExport[1],
                            biometricSystems: baseForExport[2],
                            implementationSpecificEvaluationCriteria: baseForExport[3],
                            experimentSpecificEvaluationCriteria: [],
                        });

                    });

                    // do calls

                    let apiCalls = [];

                    experimentSpecificCriteriaToQuery.forEach(criteria => {
                        apiCalls.push(EvaluationScenarioServiceAPI.getExperimentSpecificEvaluationCriteriasById(criteria.criteriaIds));
                    });

                    if(apiCalls.length>0) {
                        axios.all(apiCalls).then(response => {
                            response.forEach((response, index) => {
                                this.$log.debug("assign evaluation criteria call "+index+" to original index "+experimentSpecificCriteriaToQuery[index].index);
                                tempExportBase[experimentSpecificCriteriaToQuery[index].index]
                                    .experimentSpecificEvaluationCriteria = response.data;
                            });

                            this.baseExportArtefacts = tempExportBase;
                            this.$log.debug("Set " + this.baseExportArtefacts.length + " base to export");

                        });
                    } else {
                        this.baseExportArtefacts = tempExportBase;
                        this.$log.debug("Set " + this.baseExportArtefacts.length + " base to export");
                    }
                } else {
                    this.baseExportArtefacts = [];
                }
            }
        }
    },
    computed: {

        baseHeaders: function() {
          if(this.showLifecycleStateInformation) {
            return [
              {text: 'Name', value: 'name'},
              {text: 'Last modification', value: 'lastModification'},
              {text: 'Characteristics', value: 'characteristics'},
              {text: 'Target Architecture', value: 'targetArchitecture'},
              {text: 'Experimental Evaluation', value: 'expEvaluation'},
              {text: 'Implementation Evaluation', value: 'implEvaluation'},
              {text: 'Evaluation Criteria Grants', value: 'evaluationCriteriaGrants'},
              {text: 'Reference', value: 'reference'},
              this.lifecycleStateHeader,
              {text: 'Actions', value: 'actions'},
            ]
          } else {
            return [{text: 'Name', value: 'name'},
            {text: 'Last modification', value: 'lastModification'},
            {text: 'Characteristics', value: 'characteristics'},
            {text: 'Target Architecture', value: 'targetArchitecture'},
            {text: 'Experimental Evaluation', value: 'expEvaluation'},
            {text: 'Implementation Evaluation', value: 'implEvaluation'},
            {text: 'Evaluation Criteria Grants', value: 'evaluationCriteriaGrants'},
            {text: 'Reference', value: 'reference'},
            {text: 'Actions', value: 'actions'}];
          }

        },
        allowExport: function() {
            return this.base.length > 0 && this.modellingProgressFilter === constants.modellingProgress.reference;
        },
        baseArtefactType: function() {
            return constants.importExport.artefactTypes.base;
        },
        modellingProgresses: function(){

            return [
                {text: "Characteristics modelled", value: constants.modellingProgress.characteristics},
                {text: "Target Architecture modelled", value:  constants.modellingProgress.targetArchitecture},
                {text: "Evaluation modelled", value:  constants.modellingProgress.evaluation},
                {text: "Criteria grants set", value:  constants.modellingProgress.evaluationCriteriaGrants},
                {text: "Completed modelling", value:  constants.modellingProgress.reference},
            ];
        },
    },
    mounted: function() {
        Vue.$log.info("base browser mounted");
        if(this.standAlone) {
          this.getAllBase();
          EventBus.$on(constants.eventNames.global.updateBaseRequired, () => {
            this.getAllBase();
          })
        }
    },
    methods: {
        getAllBase() {

            this.baseRetrievalRunning = true;
            BiometricAuthenticationSystemAndEvaluationAPI.getBiometricAuthenticationSystemOfSpecificModellingProgress(this.modellingProgressFilter)
                .then(response => {
                       this.base = response.data;
                       this.$log.debug("Found "+this.base.length+" base with status "+this.modellingProgressFilter)
                }).finally(()=> {
                    this.baseRetrievalRunning = false;
                });
        },
        setAllowSelection(allowSelection) {
            this.enableSelectionForExport = allowSelection;
            if(!allowSelection) {
                this.resetExportSelection();
            }
        },
        resetExportSelection() {
            this.selectedBase = [];
        },
        showBASEDetails(base) {
            this.baseOfInterest = base;
            this.baseDetailsDialogOpened = true;
        },
        closeBaseDetailsDialog() {
            this.baseDetailsDialogOpened = false;
        },
        editBASE(base) {
            this.$router.push({name: 'continue-base-modelling', params: { name: base.name, modelState: base } });
        },
        requestBASEDelete(base) {
            this.baseOfInterest = base;
            this.showDeleteBaseDialog = true;
        },
        deleteBASE() {
            const that = this;

            BiometricAuthenticationSystemAndEvaluationAPI.deleteBiometricAuthenticationSystemAndEvaluation(this.baseOfInterest)
                .then(() => {
                    this.showDeleteBaseDialog = false;
                    this.base.splice(this.base.findIndex(function(base) {
                        return base.id === that.baseOfInterest.id;
                    }),1);
                    EventBus.$emit(constants.eventNames.global.updateBaseRequired);
            });
        },
        summarizeTargetArchitecture(targetArchitecture) {
            if(targetArchitecture.skipTargetArchitecture) {
                return "no specific architecture considered";
            } else {
                let deviceCategories = new Set();
                Object.keys(targetArchitecture).forEach(function(key) {
                    // key: the name of the object key
                    // index: the ordinal position of the key within the object
                    if(key.indexOf("Categories")>0) {
                        if(targetArchitecture[key]) {
                            targetArchitecture[key].forEach(function (category) {
                                deviceCategories.add(category.name);
                            });
                        }
                    }
                });
                deviceCategories = [...deviceCategories];
                if(deviceCategories.length>0) {
                    return "Deployment on " + deviceCategories.join(", ");
                }
            }

            return "-";
        },
        summarizeExperimentalEvaluation(experimentSpecificEvaluation) {


            if(!Util.isEmpty(experimentSpecificEvaluation)
                && experimentSpecificEvaluation.usedDatasets
                && experimentSpecificEvaluation.biometricSystems
                && experimentSpecificEvaluation.experimentSpecificEvaluationSetups) {
                return experimentSpecificEvaluation.usedDatasets.length+ " dataset"+
                    (experimentSpecificEvaluation.usedDatasets.length > 1 ? "s, " : ", ")+
                    experimentSpecificEvaluation.biometricSystems.length+" biometric system"+
                    (experimentSpecificEvaluation.biometricSystems.length > 1 ? "s, " : ", ")+
                    experimentSpecificEvaluation.experimentSpecificEvaluationSetups.length+" evaluation setup"+
                    (experimentSpecificEvaluation.experimentSpecificEvaluationSetups.length > 1 ? "s" : "");
            }

            return "-";
        },
        summarizeImplementationEvaluation(implementationSpecificEvaluationResults) {

            if(implementationSpecificEvaluationResults && implementationSpecificEvaluationResults.length > 0) {
                return implementationSpecificEvaluationResults.length+" implementation setup"+
                    (implementationSpecificEvaluationResults.length > 1 ? "s" : "")
            }

            return "-";
        },
        summarizeEvaluationCriteriaGrants(evaluationCriteriaGrants) {

            return EvaluationScenarioServiceAPI.summarizeEvaluationCriteriaGrants(evaluationCriteriaGrants);

        },
        summarizeCharacteristics: function(characteristics) {
          this.$log.debug("\t...characteristics to summarize", characteristics);
          let summary = [];
          characteristics.forEach(characteristic => {
            summary.push(BiometricCharacteristicsService.transformCharacteristicForPresentation(characteristic).selectionText);
          });
          return summary.join(", ");
        },
        processArtifactReviewStateUpdate: function(reviewStateUpdate) {
          let updatedProcessingStep = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.processingSteps);
          if(updatedProcessingStep) {
            BiometricSystemAPI.createUpdateProcessingStep(updatedProcessingStep).then(() => {
              toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
            });
          }
        },
        loadBaseToReview: function() {
          ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.base).then((response) => {
            this.$log.debug("\t...load datasets to review, remaining:", response.data.length);
            this.base = response.data;
            EventBus.$emit(constants.eventNames.global.updateBaseRequired);
          });
        },
        evaluationsCanBeAdded: function(base) {
            if(base.modelledElementDetail.elementLifecycle.lifecycleState===constants.review.lifecycleState.reviewed
              && base.modellingProgress === constants.modellingProgress.reference) {
                return true;
            }
            return false;
        },
        triggerBaseEvaluationExtension: function(baseToExtend) {
          this.baseEvaluationExtenderDialogOpened = true;
          this.baseForEvaluationExtension = baseToExtend;
        },
        closeBaseExtensionDialog: function() {
          this.baseEvaluationExtenderDialogOpened = false;
        },
        setBaseEvaluationExtension: function(baseEvaluationExtension) {
            this.$log.debug("\t...received base evaluation extension in base browser", baseEvaluationExtension);
            this.baseEvaluationExtension = baseEvaluationExtension;
        },
        createUpdateBaseEvaluationExtension: function() {

            this.setInitialModelLifecycleStateForDomainElement(this.baseEvaluationExtension);

            EvaluationScenarioServiceAPI.createUpdateBaseEvaluationExtension(this.baseEvaluationExtension).then(() => {
                toast.success("Successfully submitted evaluation extension for base "+this.baseEvaluationExtension.baseNameForExtension, 2000);
                this.baseEvaluationExtenderDialogOpened = false;
            })
        }
    },
    created() {
      Vue.$log.info("base browser created");
    }
  }
</script>

<style>

</style>

<template>
    <v-container fluid>
        <ImportExport
                v-if="standAlone && userIsReviewer"
                :artefact-name="'Dataset(s)'"
                :show-export-selection="allowExport"
                :allow-export-selection="allowExport"
                :export-artefacts="datasets"
                :artefact-type="datasetArtefactType"
                v-on:selection-for-export-allowed="setEnableSelection"
                v-on:export-finished="resetSelection"
                v-on:import-finished="getAllDatasets"/>
        <v-row>
            <v-col cols="12" lg="12">
                <template v-if="datasets.length>0">
                    <div class="subtitle-1">{{browserTitle}}</div>
                    <v-text-field
                            v-if="standAlone"
                            v-model="searchDatasetFilter"
                            append-icon="search"
                            label="Search by Dataset name"
                            single-line
                            hide-details
                    ></v-text-field>
                    <v-data-table
                            :headers="datasetHeaders"
                            :items="datasets"
                            v-model="selectedDatasets"
                            hide-default-footer
                            :single-select="false"
                            :show-select="enableSelection"
                            :search="searchDatasetFilter"
                    >
                        <template v-slot:item.name = {item}>
                            {{item.name}}
                        </template>
                        <template v-slot:item.sampled_biometrics = {item}>
                            {{ sampledBehaviourSummary(item) }}
                        </template>
                        <template v-slot:item.sample_devices = {item}>
                            {{ sampledBehaviourSummaryDevices(item) }}
                        </template>
                        <template v-slot:item.participants = {item}>
                            {{ participantSummary(item) }}
                        </template>
                        <template v-slot:item.references = {item}>
                            {{ item.reference.furtherInformationReferences.length }}
                        </template>
                        <template v-if="showLifecycleStateInformation" v-slot:item.lifecycleState = {item}>
                          {{ getLifecycleStateSummary(getUniqueUserId(), item) }}
                        </template>
                        <template v-slot:item.actions = {item}>
                          <ArtifactBrowserActionUtils :artifact-for-actions="item"
                                                      :user-is-reviewer="userIsReviewer"
                                                      :show-review-action="showReviewAction"
                                                      :artifact-detail-view-available="true"
                                                      :edit-artifact-allowed="userCanEdit(item, getUniqueUserId()) && editMode"
                                                      :delete-artifact-allowed="userCanEdit(item, getUniqueUserId()) && editMode"
                                                      v-on:show-artifact-details="showDatasetDetails"
                                                      v-on:delete-artifact="deleteArtifact"
                                                      v-on:edit-artifact="editArtifact"
                                                      v-on:open-review-dialog="openReviewDialog"/>
                        </template>
                    </v-data-table>
                </template>
                <span v-else>
                  <div v-if="userIsAuthenticated">
                    <span v-if="standAlone">No dataset is modelled yet, please <router-link :to="{ name: 'model-datasets', params: {standAlone: true } }">model</router-link> a new one</span>
                    <span v-else>
                      No dataset is available, please <span @click="createNewDataset">model</span> a new one
                    </span>
                  </div>
                  <div v-else>
                    <span>No reviewed dataset exists yet, please login to model a new one!</span>
                  </div>
                </span>
                <v-dialog v-model="datasetDialog" hide-overlay transition="dialog-bottom-transition">
                    <DatasetEditor :datasetForEditing="datasetToShow"
                                   :edit-mode="editMode"
                                   v-on:close-model-dialog="datasetDialog = false"/>
                </v-dialog>
              <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                                    :user-is-reviewer="userIsReviewer"
                                    :artifact-type="constants.review.artifactType.datasets"
                                    :artifact-to-review="currentArtifactToReview"
                                    v-on:close-dialog="closeReviewDialog"
                                    v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                                    v-on:target-artefacts-to-review-reload-required="loadDatasetsToReview"
              />
            </v-col>
        </v-row>
    </v-container>
</template>

<script>

    import DatasetEditor from "./DatasetEditor";
    import Vue from "vue";
    import toast from "../../../../helpers/toast";
    import constants from "../../../../helpers/constants";
    import {DatasetAPI} from "@/service/api/DatasetAPI";
    import ImportExport from "../../../util/ImportExport";
    import ArtifactBrowserActionUtils from "../../../review/ArtifactBrowserActionUtils.vue";
    import BrowserForReviewableArtifactsMixin from "../../../../mixins/BrowserForReviewableArtifactsMixin";
    import ArtifactReviewDialog from "../../../review/ArtifactReviewDialog.vue";
    import loggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import {Util as utils} from "@/helpers/util";
    import {BiometricCharacteristicsService} from "@/service/api/BiometricCharacteristicsService";
    import {ModelledElementReviewAPI} from "@/service/api/ModelledElementReviewAPI";
    export default {

        components: {ArtifactReviewDialog, ArtifactBrowserActionUtils, ImportExport, DatasetEditor},
        name: "DatasetBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, loggedInUserMixin],
        props: {
            datasetsToShow: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            standAlone: {
                type: Boolean,
                default: false,
            },
            editMode: {
                type: Boolean,
                default: false,
            },
            browserTitle: {
                type: String,
                default: "Modelled Datasets",
            },
            allowSelection: {
                type: Boolean,
                default: false,
            },

        },
        mounted: function() {
            if(this.allowSelection) {
              this.enableSelection = true;
            }
        },
        data() {
            return {
                datasets:[],
                searchDatasetFilter: "",
                datasetDialog: false,
                selectedDatasets: [],
                datasetExportArtefacts: [],
                enableSelection: false,
                datasetToShow: {},
            }
        },
        computed: {
            allowExport: function() {
                return this.datasets.length > 0;
            },
            datasetArtefactType: function() {
                return constants.importExport.artefactTypes.datasets;
            },
            datasetHeaders: function() {
              if(this.showLifecycleStateInformation) {
                return [
                  {text: 'Name', value: 'name'},
                  {text: 'Sampled Biometrics', value: 'sampled_biometrics'},
                  {text: 'Sample devices', value: 'sample_devices'},
                  {text: 'Participants', value: 'participants'},
                  {text: 'References', value: 'references'},
                    this.lifecycleStateHeader,
                  {text: 'Actions', value: 'actions'},
                ]
              } else {
                return [
                  {text: 'Name', value: 'name'},
                  {text: 'Sampled Biometrics', value: 'sampled_biometrics'},
                  {text: 'Sample devices', value: 'sample_devices'},
                  {text: 'Participants', value: 'participants'},
                  {text: 'References', value: 'references'},
                  {text: 'Actions', value: 'actions'},
                ]
              }
            }
        },
        watch: {
            selectedDatasets: {
                deep: true,
                handler: function(newSelection) {
                    if(newSelection && newSelection.length>0) {
                        if(this.standAlone) {
                          this.datasetExportArtefacts = newSelection;
                        } else {
                          this.$emit("selected-datasets-changed", newSelection);
                        }
                    } else {
                        this.datasetExportArtefacts = [];
                    }
                }
            },
            datasetsToShow: {
                deep: true,
                immediate: true,
                handler: function(datasetsToShow) {
                  this.$log.debug("datasetsToShow changed", datasetsToShow);
                  if(datasetsToShow && datasetsToShow.length>0) {
                    this.datasets = utils.deepCopyObject(datasetsToShow);
                  } else {
                    this.datasets = [];
                  }
                }
            },
            datasets: {
                deep: true,
                immediate: true,
                handler: function(datasets) {
                  this.$log.debug("datasets changed", datasets);
                  if(datasets && datasets.length>0) {
                    this.updateBrowseableArtifactCount(constants.review.artifactType.datasets, this.datasets.length);
                  } else {
                    this.updateBrowseableArtifactCount(constants.review.artifactType.datasets, 0);
                  }
                }
              }
        },
        beforeRouteEnter (to, from, next) {
            Vue.$log.debug("before entering dataset browser using route information to, from:", to, from);
            if(to.path === constants.routes.browseDatasets.path) {
                DatasetAPI.getAllDatasets().then(
                    (response) => {

                        // in every case standalone!
                        to.params["standAlone"] = true;
                        to.params["showModellerInformation"] = true;
                        if(response.data.length === 0) {
                            toast.warning("No modelled datasets exists, you could model a new one!", 5000);
                        } else {
                            to.params["datasetsToShow"] = response.data;
                        }
                        next();
                    }
                );
            } else {
                next();
            }
        },
        methods: {
            createNewDataset: function() {
              this.$emit(constants.eventNames.local.artifactNeedsCreation);
            },
            showDatasetDetails: function(dataset) {
                this.datasetDialog = true;
                this.$log.debug("set dataset to show in editor", dataset);
                this.datasetToShow = dataset;
            },
            participantSummary: function(dataset) {
                if(dataset.participantInformation.participantDetailsUnknown) {
                    return "no information known"
                } else {
                    if(dataset.participantInformation.numberOfParticipants>0) {
                        return dataset.participantInformation.numberOfParticipants;
                    }
                    return dataset.participantInformation.participantDetails.length;
                }
            },
            sampledBehaviourSummary: function(dataset) {
                return dataset.sampledBiometrics.map(sampledBiometric => {
                    return BiometricCharacteristicsService.transformCharacteristicForPresentation(sampledBiometric.sampledCharacteristic).selectionText
                        + (sampledBiometric.supportCharacteristic ? " (context of '"+sampledBiometric.supportCharacteristic.userActivity.name+"')" : "")
                }).join(", ");
            },
            sampledBehaviourSummaryDevices: function(dataset) {
                return [...new Set(dataset.sampledBiometrics.map(behaviour => {
                    return behaviour.sampleDevices.map(device => {return device.deviceManufacturer+ " " +device.name})
                }).flat())].join(", ");
            },
            getSpecificDataset(datasetId) {
                return DatasetAPI.getSpecificDataset(datasetId).then((response) => {
                    return response.data;
                });
            },
            setEnableSelection(enableSelection) {
                this.enableSelection = enableSelection;
                if(!enableSelection) {
                    this.resetSelection();
                }
            },
            resetSelection() {
                this.selectedDatasets = [];
            },
            getAllDatasets() {
                DatasetAPI.getAllDatasets().then(response => {
                   this.datasets = response.data;
                });
            },
            processArtifactReviewStateUpdate: function() {
              // todo: specific rest endpoint to set review for all included artefacts (sample devices, evaluation contexts etc.)
            },
            loadDatasetsToReview: function() {
              ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.datasets).then((response) => {
                this.$log.debug("\t...load datasets to review, remaining:", response.data.length);
                this.datasets = response.data;
                this.$emit(constants.eventNames.local.amountOfArtifactsShownChanged, {
                    artifactType: constants.review.artifactType.datasets,
                    newCount: this.datasets.length
                });
              });
            }

        }
    }
</script>

<style scoped>

</style>
<template>
    <v-card>
        <v-card-title>
            <template v-if="editMode">
                <span v-if="!isNewDataset" class="headline">Specifics of dataset {{datasetToEdit.name}}</span>
                <span v-if="isNewDataset" class="headline">Model new dataset</span>
            </template>
            <span v-else class="headline">Specifics of dataset {{datasetToEdit.name}}</span>
        </v-card-title>
        <v-card-text>
            <v-container fluid>
                <v-row v-if="editMode">
                    <v-col cols="12" lg="12" sm12 md12>
                      <v-text-field
                          label="Dataset Name"
                          required
                          v-model="datasetToEdit.name"
                          :rules="[datasetNameRules.unique]"
                      />
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" lg="12">
                        <SampledBiometric
                                :edit-mode="editMode"
                                :base-characteristics="baseCharacteristics"
                                :sampled-biometrics="datasetToEdit.sampledBiometrics"
                                v-on:sampled-behaviour-set="setSampledBehaviour"
                        />
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" lg="12">
                        <ParticipantInformation
                                :edit-mode="editMode"
                                :participant-information="datasetToEdit.participantInformation"
                                v-on:participant-information-changed="addParticipantInformation"
                        />
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" lg="12">
                        <Reference
                                :pre-filled-reference="datasetToEdit.reference"
                                :reference-asset-type="datasetReferenceAssetType"
                                :description-content-of-reference="'Dataset'"
                                v-on:set-reference="setReference"
                                v-if="editMode"
                        />
                        <ReferencesViewer
                            v-else
                            :reference="datasetToEdit.reference">
                        </ReferencesViewer>
                    </v-col>
                </v-row>
            </v-container>
        </v-card-text>
        <v-card-actions>
            <template v-if="editMode">
                <v-btn color="primary" text @click="saveDataset()" :disabled="!datasetValid">{{isNewDataset ? 'Add' : 'Save'}} Dataset</v-btn>
                <v-btn v-if="!standAlone" color="red darken-1" text @click="closeModelDialog">Cancel</v-btn>
            </template>
            <v-btn v-if="!standAlone" color="error" text @click="closeModelDialog">Close</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>

    import SampledBiometric from "./SampledBiometrics.vue";
    import ParticipantInformation from "./ParticipantInformation";
    import {DatasetAPI} from "../../../../service/api/DatasetAPI";
    import Reference from "../../../reference/ReferenceEditor.vue";
    import constants from "../../../../helpers/constants";
    import ReferencesViewer from "../../../reference/ReferencesViewer";
    import toast from "../../../../helpers/toast";
    import Vue from "vue";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import {Util as Utils} from "@/helpers/util";

    export default {
        components: {ReferencesViewer, Reference, ParticipantInformation, SampledBiometric},
        name: "DatasetEditor",
        mixins: [LoggedInUserMixin],
        props: {
            baseCharacteristics: {
              type: Array,
              default: function() {
                return [];
              }
            },
            editMode: {
                type: Boolean,
                default: true,
            },
            namePreSelectedDataset: {
                type: String,
                default: "",
            },
            datasetForEditing: {
                type: Object,
                default: function() {
                    return {};
                }
            },
            standAlone: {
                type: Boolean,
                default: false,
            }
        },
        created: function() {
            if(this.editMode) {
              this.getAllDatasets();
            }
        },
        mounted: function() {
        },
        data() {
            return {
                allDatasets: [],
                datasetToEdit: {
                    name: "",
                    sampledBiometrics:[],
                    participantInformation:{
                        participantDetails: [],
                        numberOfParticipants: 0,
                        participantDetailsUnknown: true,
                    },
                    reference: {
                        assetModelledByOriginator: false,
                        originatorEmail: "",
                        furtherInformationReferences: [],
                        referenceAssetType: constants.referenceAssetType.dataset,
                    },
                    modelledElementDetail: {},
                },
                datasetNameRules: {
                  unique: () => this.datasetNameUnique || 'A dataset with this name already exists'
                },
                isNewDataset: true,
            }
        },
        beforeRouteEnter (to, from, next) {
            Vue.$log.debug("before route enter", to, from);
            if(to.path === constants.routes.modelDatasets.path) {
                to.params["standAlone"] = true;
            }
            next();
        },
        beforeRouteUpdate (to, from, next) {
            Vue.$log.debug("before route update", to, from);
            next();
        },
        watch: {
            datasetForEditing: {
                deep: true,
                immediate: true,
                handler: function(datasetForEditing) {
                    this.$log.debug("datasetForEditing changed", datasetForEditing);
                    if(datasetForEditing && datasetForEditing.sampledBiometrics && datasetForEditing.sampledBiometrics.length>0) {
                        this.datasetToEdit = Utils.deepCopyObject(datasetForEditing);
                        this.isNewDataset = false;
                    }
                }
            }
        },
        computed: {
            participantInformationValid() {
                if(this.datasetToEdit.participantInformation.participantDetailsUnknown) {
                    return true;
                } else {
                    return this.datasetToEdit.participantInformation.participantDetails.length>0
                    || this.datasetToEdit.participantInformation.numberOfParticipants>0;
                }
            },
            datasetNameUnique() {
              return this.allDatasets.filter(dataset => dataset.name.toLowerCase() === this.datasetToEdit.name.toLowerCase()).length===0
            },
            datasetValid() {
              let datasetNameValid = false;
              if(this.editMode) {
                if(this.datasetToEdit.name === this.datasetForEditing.name) {
                  datasetNameValid = true;
                }
              }
              return this.participantInformationValid
                  && (this.datasetNameUnique || datasetNameValid)
                  && this.datasetToEdit.sampledBiometrics.length>0
                  && this.datasetToEdit.name.length>0
                  && (this.datasetToEdit.reference
                      && this.datasetToEdit.reference.furtherInformationReferences.length>0
                      && (!this.datasetToEdit.reference.assetModelledByOriginator ||
                          (this.datasetToEdit.reference.assetModelledByOriginator && this.datasetToEdit.reference.originatorEmail.length>0)));
            },
            datasetReferenceAssetType() {
                return constants.referenceAssetType.dataset;
            }
        },
        methods: {
            resetDatasetToEdit: function() {
                this.$log.debug("reset dataset to edit!")
                this.datasetToEdit = {
                    name: "",
                    sampledBiometrics:[],
                    participantInformation:{
                        participantDetails: [],
                        participantDetailsUnknown: false,
                    },
                    reference: {
                        assetModelledByOriginator: false,
                        originatorEmail: "",
                        furtherInformationReferences: [],
                    },
                }
            },
            getAllDatasets: function() {
                DatasetAPI.getAllDatasets().then(response => {
                   this.allDatasets = response.data;
                });
            },
            setSampledBehaviour: function(sampledBiometrics) {
                this.datasetToEdit.sampledBiometrics = sampledBiometrics;
            },
            addParticipantInformation: function(participantInformation) {
                this.datasetToEdit.participantInformation = participantInformation;
            },
            saveDataset: function() {


                // before saving data set we need to add the sampling context ids
                // TODO => remove that f***** autoaddbox ^^

                DatasetAPI.getSamplingContexts().then(response => {

                  this.datasetToEdit.sampledBiometrics.forEach(sampledBiometric => {
                    sampledBiometric.samplingContexts.forEach(samplingContext => {
                      if(!samplingContext.id) {
                        samplingContext["id"] = response.data.filter(existingSamplingContext => {
                          return existingSamplingContext.description === samplingContext.description;
                        })[0].id;
                      }
                    });
                  });

                  let datasetToSave = DatasetAPI.transformDatasetForCreationUpdate(this.datasetToEdit);

                  if(!datasetToSave.id) {
                    this.setInitialModelLifecycleStateForDomainElement(datasetToSave);
                  }

                  DatasetAPI.createUpdateDataset(datasetToSave).then(
                      (response) => {

                        if(!this.datasetToEdit.id) {
                          this.datasetToEdit.id = response.data;
                        } else {
                          this.datasetToEdit = response.data;
                        }

                        toast.success("Dataset successfully "+(this.isNewDataset ? "added" : "saved"));

                        // inform whoever interested
                        this.$emit(constants.eventNames.local.artifactCreatedUpdated, this.datasetToEdit);

                        this.resetDatasetToEdit();
                        this.$emit(constants.eventNames.local.closeModelDialog);
                      }
                  );
                })

            },
            setReference: function(reference) {
                this.datasetToEdit.reference = reference;
            },
            closeModelDialog: function() {
                this.$emit(constants.eventNames.local.closeModelDialog);
            }
        }
    }
</script>

<style scoped>

</style>
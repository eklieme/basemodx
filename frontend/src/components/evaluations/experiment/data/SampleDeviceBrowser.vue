<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="sampleDevicesHeaders"
          :items="sampleDevices"
          v-model="selectedSampleDevices"
          :no-data-text="'No Sample Devices Available'"
          :show-select="showSelect"
      >
        <template v-if="showAddOption" v-slot:top>
          <v-dialog v-model="sampleDeviceEditorDialog" persistent>
            <template v-slot:activator="{ on }">
                <v-row row wrap>
                    <v-col cols="12" lg="4">
                      <v-btn v-on="on" text slot="activator" block color="primary">Add Sample Device</v-btn>
                    </v-col>
                    <v-col cols="12" lg="4">
                        <v-btn v-if="deviceCategoryEditingAllowed" @click="triggerDeviceCategoryEditing()" text block color="primary">Edit Device Category</v-btn>
                    </v-col>
                    <v-col cols="12" lg="4">
                      <v-btn v-if="deviceCategoryEditingAllowed" @click="triggerDeviceCategoryCreation()" text block color="primary">Add Device Category</v-btn>
                    </v-col>
                </v-row>
            </template>
            <SampleDeviceEditor
                v-on:sample-device-added="refreshSampleDevices"
                v-on:close-model-dialog="sampleDeviceEditorDialog = false">
            </SampleDeviceEditor>
          </v-dialog>
        </template>
        <template v-slot:item.name = {item}>
          {{item.name}}
        </template>
        <template v-slot:item.category = {item}>
          {{item.deviceCategory.name}}
        </template>
        <template v-slot:item.manufacturer = {item}>
          {{item.deviceManufacturer}}
        </template>
        <template v-slot:item.sensors = {item}>
          {{sensorSummary(item.deviceCategory.sensors)}}
        </template>
        <template v-if="showLifecycleStateInformation" v-slot:item.lifecycleState = {item}>
          {{ getLifecycleStateSummary(getUniqueUserId(), item) }}
        </template>
        <template v-slot:item.actions = {item}>
            <ArtifactBrowserActionUtils
                :show-review-action="showReviewAction"
                :artifact-for-actions="item"
                :user-is-reviewer="userIsReviewer"
                :delete-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                :edit-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                v-on:open-review-dialog="openReviewDialog"
                v-on:edit-artifact="editArtifact"
                v-on:delete-artifact="deleteArtifact"/>
        </template>
      </v-data-table>
      <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-type="constants.review.artifactType.sampleDevices"
                            :artifact-to-review="currentArtifactToReview"
                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadSampleDevicesToReview"
      />
      <DeviceCategoryEditor
              :edit-mode="editDeviceCategory"
              :existing-device-categories="allDeviceCategories"
              :editor-shown="deviceEditorDialogOpen"
              v-on:close-dialog="deviceEditorDialogOpen = false"
              v-on:artifact-added="refreshSampleDevices"/>
    </v-container>
</template>

<script>


    import {DatasetAPI} from "../../../../service/api/DatasetAPI";
    import {ModelledElementReviewAPI} from "../../../../service/api/ModelledElementReviewAPI";
    import BrowserForReviewableArtifactsMixin from "../../../../mixins/BrowserForReviewableArtifactsMixin";
    import toast from "../../../../helpers/toast";
    import ArtifactReviewDialog from "../../../review/ArtifactReviewDialog.vue";
    import ArtifactBrowserActionUtils from "../../../review/ArtifactBrowserActionUtils.vue";
    import LoggedInUserMixin from "../../../../mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";
    import {Util as utils} from "@/helpers/util";
    import SampleDeviceEditor from "@/components/evaluations/experiment/data/SampleDeviceEditor.vue";
    import DeviceCategoryEditor from "@/components/target_architecture/DeviceCategoryEditor.vue";
    import DeviceCategoryEditorMixin from "@/mixins/model/DeviceCategoryEditorMixin";

    export default {
        components: {DeviceCategoryEditor, SampleDeviceEditor, ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "SampleDeviceBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin, DeviceCategoryEditorMixin],
        props:{
          sampleDevicesToShow: {
            type: Array,
            default: function() {
              return [];
            }
          },
          showSelect: {
            type: Boolean,
            default: false,
          },
          showAddOption: {
            type: Boolean,
            default: false,
          },
          deviceCategoryEditingAllowed: {
            type: Boolean,
            default: false,
          },
        },
        data() {
            return {
              sampleDevices: [],
              selectedSampleDevices: [],
              sampleDeviceEditorDialog: false,
            }
        },
        watch: {
          sampleDevicesToShow: {
            deep: true,
            immediate: true,
            handler: function(sampleDevicesToShow) {
              this.$log.debug("sampleDevicesToShow changed", sampleDevicesToShow);
              if(sampleDevicesToShow && sampleDevicesToShow.length>0) {
                this.sampleDevices = utils.deepCopyObject(sampleDevicesToShow);
              }
            }
          },
          sampleDevices: {
            deep: true,
            immediate: true,
            handler: function(sampleDevices) {
              this.$log.debug("sampleDevices changed");
              if(sampleDevices && sampleDevices.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.sampleDevices, this.sampleDevices.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.sampleDevices, 0);
              }
            }
          },
          selectedSampleDevices: {
            deep: true,
            immediate: true,
            handler: function(selectedSampleDevices) {
              this.$log.debug("selectedSampleDevices changed");
              if(selectedSampleDevices && selectedSampleDevices.length>0) {
                this.$emit(constants.eventNames.local.artifactsSelected, selectedSampleDevices);
              } else {
                this.$emit(constants.eventNames.local.artifactsSelected, []);
              }
            }
          },
        },
        mounted: function() {
            this.getDeviceCategories();
        },
        created:function() {
        },
        computed: {
          sampleDevicesHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Device Category', value: 'category'},
                {text: 'Manufacturer', value: 'manufacturer'},
                {text: 'Sensors', value: 'sensors'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Device Category', value: 'category'},
                {text: 'Manufacturer', value: 'manufacturer'},
                {text: 'Sensors', value: 'sensors'},
                {text: 'Actions', value: 'actions'}
              ];
            }
          }
        },
        methods: {
          sensorSummary: function(sensors) {
            return sensors.map(sensor => {
              return sensor.name + " ("
                  +sensor.sensorDimensions.length
                  +" dims)"
              }).join(", ")
          },
          processArtifactReviewStateUpdate: function(reviewStateUpdate) {
            //todo => works for dimensions as well?!
            var updatedSampleDevice = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.sampleDevicesToShow);
            if(updatedSampleDevice) {
              DatasetAPI.createUpdateSampleDevice(updatedSampleDevice).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          loadSampleDevicesToReview: function() {
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.sampleDevices).then((sampleDevicesToReview) => {
              this.$log.debug("\t...set new sample devices to review, remaining:", sampleDevicesToReview.data.length);
              this.sampleDevices = sampleDevicesToReview.data;
            });
          },
          refreshSampleDevices: function() {
            DatasetAPI.getSampleDevices().then(response => {
              this.sampleDevices = response.data;
            })
          }

        }
    }
</script>

<style scoped>

</style>
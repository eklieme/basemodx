<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="sensorHeaders"
          :items="sensors"
          :show-select="allowSelection"
          v-model="selectedSensors"
      >
        <template v-slot:item.name = {item}>
          {{item.name}}
        </template>
        <template v-slot:item.dimensions = {item}>
          {{ item.sensorDimensions.map(dimension => dimension.name).join(",") }}
        </template>
        <template v-slot:item.continuous = {item}>
          {{item.continuous ? "Yes" : "No"}}
        </template>
        <template v-slot:item.actions = {item}>
            <ArtifactBrowserActionUtils
                :show-review-action="showReviewAction"
                :artifact-for-actions="item"
                :edit-artifact-allowed="userCanEditExtendableArtifact(item, getUniqueUserId())"
                :delete-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                :user-is-reviewer="userIsReviewer"
                v-on:open-review-dialog="openReviewDialog"
                v-on:delete-artifact="deleteArtifact"
                v-on:edit-artifact="editArtifact"/>
        </template>
      </v-data-table>
      <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-to-review="currentArtifactToReview"
                            :artifact-type="constants.review.artifactType.sensors"
                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadSensorsToReview"
      />
    </v-container>
</template>

<script>


    import {ModelledElementReviewAPI} from "../../service/api/ModelledElementReviewAPI";
    import BrowserForReviewableArtifactsMixin from "../../mixins/BrowserForReviewableArtifactsMixin";
    import toast from "../../helpers/toast";
    import ArtifactReviewDialog from "../review/ArtifactReviewDialog.vue";
    import ArtifactBrowserActionUtils from "../review/ArtifactBrowserActionUtils.vue";
    import {Util as utils} from "../../helpers/util";
    import constants from "@/helpers/constants";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import {SensorAPI} from "@/service/api/SensorAPI";

    export default {
        components: {ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "SensorBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props:{
          sensorsToShow: {
            type: Array,
            default: function() {
              return [];
            }
          },
          allowSelection: {
            type: Boolean,
            default: false,
          }
        },
        data() {
            return {
              sensors: [],
              selectedSensors:[],
            }
        },
        watch: {
          sensorsToShow: {
            deep: true,
            immediate: true,
            handler: function(sensorsToShow) {
              this.$log.debug("sensorsToShow changed", sensorsToShow);
              if(sensorsToShow && sensorsToShow.length>0) {
                this.sensors = utils.deepCopyObject(sensorsToShow);
              } else {
                this.sensors = [];
              }
            }
          },
          sensors: {
            deep: true,
            immediate: true,
            handler: function(sensors) {
              this.$log.debug("sensors changed", sensors);
              if(sensors && sensors.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.sensors, sensors.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.sensors, 0);
              }
            }
          },
          selectedSensors: {
            deep: true,
            immediate: true,
            handler: function(selectedSensors) {
              this.$log.debug("selectedSensors changed", selectedSensors);
              if(selectedSensors && selectedSensors.length>0) {
                this.$emit(constants.eventNames.local.artifactsSelected, selectedSensors);
              } else {
                this.$emit(constants.eventNames.local.artifactsSelected, []);
              }
            }
          }
        },
        created:function() {
        },
        computed: {
          sensorHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Dimensions', value: 'dimensions'},
                {text: 'Continuous', value: 'continuous'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Dimensions', value: 'dimensions'},
                {text: 'Continuous', value: 'continuous'},
                {text: 'Actions', value: 'actions'}
              ];
            }
          }
        },
        methods: {
          processArtifactReviewStateUpdate: function(reviewStateUpdate) {
            var updatedSensor = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.sensors);
            if(updatedSensor) {
              SensorAPI.createUpdateSensor(updatedSensor).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          loadSensorsToReview: function() {
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.sensors).then((response) => {
              this.$log.debug("\t...update sensors to review, remaining: ", response.data.length);
              this.sensors = response.data;
            })
          },
        }
    }
</script>

<style scoped>

</style>
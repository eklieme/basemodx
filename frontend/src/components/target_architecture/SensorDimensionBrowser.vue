<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="sensorDimensionHeaders"
          :items="sensorDimensions"
      >
        <template v-slot:item.name = {item}>
          {{item.name}}
        </template>
        <template v-slot:item.sensor = {item}>
          {{item.sensor ? item.sensor.name : 'Orphaned'}}
        </template>
        <template v-if="showLifecycleStateInformation" v-slot:item.lifecycleState = {item}>
          {{ getLifecycleStateSummary(getUniqueUserId(), item) }}
        </template>
        <template v-slot:item.actions = {item}>
            <ArtifactBrowserActionUtils
                :show-review-action="showReviewAction"
                :artifact-for-actions="item"
                :user-is-reviewer="userIsReviewer"
                :edit-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                :delete-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                v-on:open-review-dialog="openReviewDialog"
                v-on:delete-artifact="deleteArtifact"
                v-on:edit-artifact="editArtifact"/>
        </template>
      </v-data-table>
      <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-type="constants.review.artifactType.sensorDimensions"
                            :artifact-to-review="currentArtifactToReview"

                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadSensorDimensionsToReview"
      />
    </v-container>
</template>

<script>


    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";
    import {SensorAPI} from "@/service/api/SensorAPI";
    import BrowserForReviewableArtifactsMixin from "@/mixins/BrowserForReviewableArtifactsMixin";
    import ArtifactBrowserActionUtils from "@/components/review/ArtifactBrowserActionUtils.vue";
    import ArtifactReviewDialog from "@/components/review/ArtifactReviewDialog.vue";
    import {Util} from "@/helpers/util";
    import {ModelledElementReviewAPI} from "@/service/api/ModelledElementReviewAPI";
    import toast from "@/helpers/toast";

    export default {
        components: {ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "SensorDimensionBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props:{
          sensorDimensionsToShow: [],
        },
        data() {
            return {
              sensorDimensions: [],
            }
        },
        watch: {
          sensorDimensionsToShow: {
            deep: true,
            immediate: true,
            handler: function(sensorDimensionsToShow) {
              this.$log.debug("sensorDimensionsToShow changed", sensorDimensionsToShow);
              if(sensorDimensionsToShow && sensorDimensionsToShow.length>0) {
                this.sensorDimensions = Util.deepCopyObject(sensorDimensionsToShow);
              }
            }
          },
          sensorDimensions: {
            deep: true,
            immediate: true,
            handler: function(sensorDimensions) {
              this.$log.debug("sensorDimensions changed");
              if(sensorDimensions && sensorDimensions.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.sensorDimensions, this.sensorDimensions.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.sensorDimensions, 0);
              }
            }
          }
        },
        created:function() {
        },
        computed: {
          sensorDimensionHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Sensor', value: 'sensor'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Sensor', value: 'sensor'},
                {text: 'Actions', value: 'actions'}
              ];
            }
          }
        },
        methods: {
          processArtifactReviewStateUpdate: function(reviewStateUpdate) {
            var updatedSensorDimension = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.sensorDimensions);
            if(updatedSensorDimension) {
              SensorAPI.createUpdateSensorDimension(updatedSensorDimension).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          loadSensorDimensionsToReview: function() {
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.sensorDimensions).then((response) => {
              this.$log.debug("\t...load sensorDimensions to review, remaining:", response.data.length);
              this.sensorDimensions = response.data;
            });
          }
        }
    }
</script>

<style scoped>

</style>
<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="deviceCategoryHeaders"
          :items="deviceCategories"
      >
        <template v-slot:item.name = {item}>
          {{item.name}}
        </template>
        <template v-slot:item.sensors = {item}>
          {{sensorSummary(item.sensors) }}
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
                v-on:edit-artifact="editArtifact"
                v-on:delete-artifact="deleteArtifact"/>
        </template>
      </v-data-table>
      <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                            :artifact-type="constants.review.artifactType.deviceCategories"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-to-review="currentArtifactToReview"
                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadDeploymentLocationsToReview"
      />
    </v-container>
</template>

<script>


    import ArtifactBrowserActionUtils from "../review/ArtifactBrowserActionUtils.vue";
    import ArtifactReviewDialog from "../review/ArtifactReviewDialog.vue";
    import BrowserForReviewableArtifactsMixin from "../../mixins/BrowserForReviewableArtifactsMixin";
    import {Util as utils} from "../../helpers/util";
    import {ModelledElementReviewAPI} from "../../service/api/ModelledElementReviewAPI";
    import toast from "../../helpers/toast";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";
    import {DeviceCategoryServiceAPI} from "@/service/api/DeviceCategoryServiceAPI";

    export default {
        components: {ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "DeviceCategoryBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props:{
          deviceCategoriesToShow: {
            type: Array,
            default: function() {
              return [];
            }
          },
        },
        data() {
            return {
              deviceCategories: [],
            }
        },
        watch: {
          deviceCategoriesToShow: {
            deep: true,
            immediate: true,
            handler: function(deviceCategoriesToShow) {
              this.$log.debug("deviceCategoriesToShow changed", deviceCategoriesToShow);
              if(deviceCategoriesToShow && deviceCategoriesToShow.length>0) {
                this.deviceCategories = utils.deepCopyObject(deviceCategoriesToShow);
              }
            }
          },
          deviceCategories: {
            deep: true,
            immediate: true,
            handler: function(deviceCategories) {
              this.$log.debug("deviceCategories changed");
              if(deviceCategories && deviceCategories.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.deviceCategories, this.deviceCategories.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.deviceCategories, 0);
              }
            }
          },
        },
        created:function() {
        },
        computed: {
          deviceCategoryHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Sensors', value: 'sensors'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Sensors', value: 'sensors'},
                {text: 'Actions', value: 'actions'}
              ];
            }
          }
        },
        methods: {
          processArtifactReviewStateUpdate: function(reviewStateUpdate) {
            let updatedDeviceCategory = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.deviceCategories);
            if(updatedDeviceCategory) {
              DeviceCategoryServiceAPI.createUpdateDeviceCategory(updatedDeviceCategory).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          loadDeploymentLocationsToReview: function() {
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.deviceCategories).then((response) => {
              this.$log.debug("\t...update device categories, NOW remaining: ", response.data.length);
              this.deviceCategories = response.data;
            })
          },
          sensorSummary: function(sensors) {
            return sensors.map(sensor => {
              return sensor.name + " ("
                  +sensor.sensorDimensions.map(dimension => {
                    return dimension.name;
                  }).join(", ")
                  +")"
            }).join(", ")
          },
        }
    }
</script>

<style scoped>

</style>
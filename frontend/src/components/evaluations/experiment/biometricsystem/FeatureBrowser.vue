<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="featureHeaders"
          :items="features"
      >
        <template v-slot:item.name = {item}>
          {{item.name}}
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
                            :artifact-type="constants.review.artifactType.features"
                            :artifact-to-review="currentArtifactToReview"

                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadFeaturesToReview"
      />
    </v-container>
</template>

<script>


    import {ModelledElementReviewAPI} from "../../../../service/api/ModelledElementReviewAPI";
    import BrowserForReviewableArtifactsMixin from "../../../../mixins/BrowserForReviewableArtifactsMixin";
    import toast from "../../../../helpers/toast";
    import ArtifactReviewDialog from "../../../review/ArtifactReviewDialog.vue";
    import ArtifactBrowserActionUtils from "../../../review/ArtifactBrowserActionUtils.vue";
    import {Util as utils} from "../../../../helpers/util";
    import {BiometricSystemAPI} from "../../../../service/api/BiometricSystemAPI";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";

    export default {
        components: {ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "FeatureBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props:{
          featuresToShow: [],
        },
        data() {
            return {
              features: [],
            }
        },
        watch: {
          featuresToShow: {
            deep: true,
            immediate: true,
            handler: function(featuresToShow) {
              this.$log.debug("featuresToShow changed", featuresToShow);
              if(featuresToShow && featuresToShow.length>0) {
                this.features = utils.deepCopyObject(featuresToShow);
              }
            }
          },
          features: {
            deep: true,
            immediate: true,
            handler: function(features) {
              this.$log.debug("features changed");
              if(features && features.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.features, this.features.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.features, 0);
              }
            }
          }
        },
        created:function() {
        },
        computed: {
          featureHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Name', value: 'name'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Actions', value: 'actions'}
              ];
            }
          }
        },
        methods: {
          processArtifactReviewStateUpdate: function(reviewStateUpdate) {
            var updatedFeature = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.features);
            if(updatedFeature) {
              BiometricSystemAPI.createUpdateFeature(updatedFeature).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          loadFeaturesToReview: function() {
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.features).then((response) => {
              this.$log.debug("\t...load features to review, remaining:", response.data.length);
              this.features = response.data;
            });
          }
        }
    }
</script>

<style scoped>

</style>
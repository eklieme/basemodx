<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="resourcesToProtectHeaders"
          :items="resourcesToProtect"
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
                v-on:edit-artifact="editArtifact"
                v-on:delete-artifact="deleteArtifact"
                v-on:open-review-dialog="openReviewDialog"/>
        </template>
      </v-data-table>
      <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-type="constants.review.artifactType.resourcesToProtect"
                            :artifact-to-review="currentArtifactToReview"
                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadResourcesToProtectToReview"

      />
    </v-container>
</template>

<script>


    import ArtifactBrowserActionUtils from "../review/ArtifactBrowserActionUtils.vue";
    import ArtifactReviewDialog from "../review/ArtifactReviewDialog.vue";
    import BrowserForReviewableArtifactsMixin from "../../mixins/BrowserForReviewableArtifactsMixin";
    import {Util as utils} from "../../helpers/util";
    import {TargetArchitectureAPI} from "../../service/api/TargetArchitectureAPI";
    import {ModelledElementReviewAPI} from "../../service/api/ModelledElementReviewAPI";
    import toast from "../../helpers/toast";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";

    export default {
        components: {ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "ResourcesToProtectBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props:{
          resourcesToProtectToShow: [],
        },
        data() {
            return {
              resourcesToProtect: [],
            }
        },
        watch: {
          resourcesToProtectToShow: {
            deep: true,
            immediate: true,
            handler: function(resourcesToProtectToShow) {
              this.$log.debug("resourcesToProtectToShow changed", resourcesToProtectToShow);
              if(resourcesToProtectToShow && resourcesToProtectToShow.length>0) {
                this.resourcesToProtect = utils.deepCopyObject(resourcesToProtectToShow);
              }
            }
          },
          resourcesToProtect: {
            deep: true,
            immediate: true,
            handler: function(resourcesToProtect) {
              this.$log.debug("resourcesToProtect changed");
              if(resourcesToProtect && resourcesToProtect.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.resourcesToProtect, this.resourcesToProtect.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.resourcesToProtect, 0);
              }
            }
          },
        },
        created:function() {
        },
        computed: {
          resourcesToProtectHeaders: function() {
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
            var updatedResourceToProtect = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.resourcesToProtect);
            if(updatedResourceToProtect) {
              TargetArchitectureAPI.createUpdateResourceToProtect(updatedResourceToProtect).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          loadResourcesToProtectToReview: function() {
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.resourcesToProtect).then((response) => {
              this.$log.debug("\t...update resources to protect, remaining: ", response.data.length);
              this.resourcesToProtect = response.data;
            })
          }
        }
    }
</script>

<style scoped>

</style>
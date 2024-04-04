<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="processingStepHeaders"
          :items="processingSteps"
      >
        <template v-slot:item.name = {item}>
          {{item.name}}
        </template>
        <template v-slot:item.processingType = {item}>
          {{summarizeProcessingType(item)}}
        </template>
        <template v-slot:item.reference = {item}>
          {{summarizeReference(item.reference)}}
        </template>
        <template v-if="showLifecycleStateInformation" v-slot:item.lifecycleState = {item}>
          {{ getLifecycleStateSummary(getUniqueUserId(), item) }}
        </template>
        <template v-slot:item.actions = {item}>
            <ArtifactBrowserActionUtils
                :show-review-action="showReviewAction"
                :artifact-for-actions="item"
                :delete-artifact-allowed="userCanEdit(item,getUniqueUserId())"
                :edit-artifact-allowed="userCanEdit(item,getUniqueUserId())"
                :user-is-reviewer="userIsReviewer"
                v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                v-on:open-review-dialog="openReviewDialog"
                v-on:edit-artifact="editArtifact"
                v-on:delete-artifact="deleteArtifact"/>
        </template>
      </v-data-table>
      <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-type="constants.review.artifactType.biometricProcessingSteps"
                            :artifact-to-review="currentArtifactToReview"
                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadProcessingStepsToReview"
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
    import ArtifactWithReferenceMixin from "../../../../mixins/ArtifactWithReferenceMixin";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";

    export default {
        components: {ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "ProcessingStepBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, ArtifactWithReferenceMixin, LoggedInUserMixin],
        props:{
          processingStepsToShow: [],
        },
        data() {
            return {
              processingSteps: [],
            }
        },
        watch: {
          processingStepsToShow: {
            deep: true,
            immediate: true,
            handler: function(processingStepsToShow) {
              this.$log.debug("processingStepsToShow changed", processingStepsToShow);
              if(processingStepsToShow && processingStepsToShow.length>0) {
                this.processingSteps = utils.deepCopyObject(processingStepsToShow);
              }
            }
          },
          processingSteps: {
            deep: true,
            immediate: true,
            handler: function(processingSteps) {
              this.$log.debug("processingSteps changed");
              if(processingSteps && processingSteps.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.biometricProcessingSteps, this.processingSteps.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.biometricProcessingSteps, 0);
              }
            }
          },
        },
        created:function() {
        },
        computed: {
          processingStepHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Type', value: 'processingType'},
                {text: 'Reference', value: 'reference'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Type', value: 'processingType'},
                {text: 'Reference', value: 'reference'},
                {text: 'Actions', value: 'actions'}
              ];
            }
          }
        },
        methods: {
          processArtifactReviewStateUpdate: function(reviewStateUpdate) {
            let updatedProcessingStep = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.processingSteps);
            if(updatedProcessingStep) {
              BiometricSystemAPI.createUpdateProcessingStep(updatedProcessingStep).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          summarizeProcessingType: function(item) {
            let summary = "";
            if(item.isDeepLearning) {
              summary=" (DL)"
            }
            if(item.decisionMode) {
              summary+=", mode: "+item.decisionMode;
            }
            return item.processingType+summary;
          },
          loadProcessingStepsToReview: function() {
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.biometricProcessingSteps).then((response) => {
              this.$log.debug("\t...load datasets to review, remaining:", response.data.length);
              this.processingSteps = response.data;
            });
          }
        },
    }
</script>

<style scoped>

</style>
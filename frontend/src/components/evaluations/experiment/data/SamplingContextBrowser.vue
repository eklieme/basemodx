<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="samplingContextHeaders"
          :items="samplingContexts"
      >
        <template v-slot:item.description = {item}>
          {{item.description}}
        </template>
        <template v-slot:item.datasetsUsed = {item}>
          {{item.datasetIdsUsedIn.length}}
        </template>
        <template v-slot:item.datasetDetails = {item}>
          <span style="cursor: pointer" @click="showDatasetsUsedInDetails(item)">dataset(s) details</span>
        </template>
        <template v-if="showLifecycleStateInformation" v-slot:item.lifecycleState = {item}>
          Modelled by {{ getLifecycleStateSummary(getUniqueUserId, item) }}
        </template>
        <template v-slot:item.actions = {item}>
            <ArtifactBrowserActionUtils
                :show-review-action="showReviewAction"
                :artifact-for-actions="item"
                :user-is-reviewer="userIsReviewer"
                :edit-artifact-allowed="userCanEdit(item,getUniqueUserId())"
                :delete-artifact-allowed="userCanEdit(item,getUniqueUserId())"
                v-on:open-review-dialog="openReviewDialog"
                v-on:edit-artifact="editArtifact"
                v-on:delete-artifact="deleteArtifact"/>
        </template>
      </v-data-table>
      <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-to-review="currentArtifactToReview"
                            :artifact-type="constants.review.artifactType.samplingContexts"
                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadSamplingContextsToReview"

      />
      <v-dialog v-model="showDatasetsUsedInDialog" persistent hide-overlay transition="dialog-bottom-transition">
        <v-card>
          <v-card-text>
            <DatasetBrowser
              :datasets-to-show="datasetsUsedIn"
              :browser-title="datasetsUsedInBrowserTitle"
              :edit-mode="false"/>
          </v-card-text>
          <v-card-actions>
            <v-btn color="info" text @click="closeDatasetsUsedInDialog">Close</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
</template>

<script>


    import {ModelledElementReviewAPI} from "../../../../service/api/ModelledElementReviewAPI";
    import BrowserForReviewableArtifactsMixin from "../../../../mixins/BrowserForReviewableArtifactsMixin";
    import toast from "../../../../helpers/toast";
    import ArtifactReviewDialog from "../../../review/ArtifactReviewDialog.vue";
    import ArtifactBrowserActionUtils from "../../../review/ArtifactBrowserActionUtils.vue";
    import {Util as utils} from "../../../../helpers/util";
    import {DatasetAPI} from "../../../../service/api/DatasetAPI";
    import constants from "@/helpers/constants";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import DatasetBrowser from "@/components/evaluations/experiment/data/DatasetBrowser.vue";

    export default {
        components: {DatasetBrowser, ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "SamplingContextBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props:{
          samplingContextsToShow: [],
        },
        data() {
            return {
              samplingContexts: [],
              showDatasetsUsedInDialog: false,
              datasetsUsedIn: [],
              datasetsUsedInBrowserTitle: "",
            }
        },
        watch: {
          samplingContextsToShow: {
            deep: true,
            immediate: true,
            handler: function(samplingContextsToShow) {
              this.$log.debug("samplingContextsToShow changed", samplingContextsToShow);
              if(samplingContextsToShow && samplingContextsToShow.length>0) {
                this.samplingContexts = utils.deepCopyObject(samplingContextsToShow);
              }
            }
          },
          samplingContexts: {
            deep: true,
            immediate: true,
            handler: function(samplingContexts) {
              this.$log.debug("samplingContexts changed", samplingContexts);
              if(samplingContexts && samplingContexts.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.samplingContexts, samplingContexts.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.samplingContexts, 0);
              }
            }
          }
        },
        created:function() {
        },
        computed: {
          samplingContextHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Description', value: 'description'},
                {text: 'Datasets used', value: 'datasetsUsed'},
                {text: 'Dataset details', value: 'datasetDetails'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: 'Description', value: 'description'},
                {text: 'Datasets used', value: 'datasetsUsed'},
                {text: 'Dataset details', value: 'datasetDetails'},
                {text: 'Actions', value: 'actions'}
              ];
            }
          }
        },
        methods: {
          processArtifactReviewStateUpdate: function(reviewStateUpdate) {
            var updatedEvaluationContext = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.samplingContexts);
            if(updatedEvaluationContext) {
              DatasetAPI.createUpdateSamplingContext(updatedEvaluationContext).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          loadSamplingContextsToReview: function() {
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.samplingContexts).then((response) => {
              this.$log.debug("\t...update evaluation contexts to review, remaining: ", response.data.length);
              this.samplingContexts = response.data;
            })
          },
          showDatasetsUsedInDetails: function(samplingContextForDetails) {
            DatasetAPI.getSpecificDatasets(samplingContextForDetails.datasetIdsUsedIn).then((response) => {
              this.datasetsUsedInBrowserTitle = "Datasets, where evaluation context '"
                  +samplingContextForDetails.description+"' was reported";
              this.showDatasetsUsedInDialog = true;
              this.datasetsUsedIn = response.data;
            })
          },
          closeDatasetsUsedInDialog: function() {
            this.showDatasetsUsedInDialog = false;
          }
        }
    }
</script>

<style scoped>

</style>
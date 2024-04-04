<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="resultMetricHeaders"
          :items="resultMetrics"
      >
        <template v-slot:item.name = {item}>
          {{item.name}}
        </template>
        <template v-slot:item.description = {item}>
          {{item.description}}
        </template>
        <template v-slot:item.unit = {item}>
          {{item.unit}}
        </template>
        <template v-slot:item.evaluationCriteria = {item}>
          {{item.parentCriteria.name}}
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
                            :artifact-to-review="currentArtifactToReview"
                            :artifact-type="constants.review.artifactType.resultMetrics"
                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadResultMetricsToReview"
      />
    </v-container>
</template>

<script>


    import {ModelledElementReviewAPI} from "../../../../service/api/ModelledElementReviewAPI";
    import BrowserForReviewableArtifactsMixin from "../../../../mixins/BrowserForReviewableArtifactsMixin";
    import toast from "../../../../helpers/toast";
    import ArtifactReviewDialog from "../../../review/ArtifactReviewDialog.vue";
    import ArtifactBrowserActionUtils from "../../../review/ArtifactBrowserActionUtils.vue";
    import {EvaluationScenarioServiceAPI} from "../../../../service/api/EvaluationScenarioServiceAPI";
    import {Util as utils} from "../../../../helpers/util";
    import LoggedInUserMixin from "../../../../mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";

    export default {
        components: {ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "ResultMetricBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props:{
          resultMetricsToShow: [],
        },
        data() {
            return {
              resultMetrics: [],
            }
        },
        watch: {
          resultMetricsToShow: {
            deep: true,
            immediate: true,
            handler: function(resultMetricsToShow) {
              this.$log.debug("resultMetricsToShow changed", resultMetricsToShow);
              if(resultMetricsToShow && resultMetricsToShow.length>0) {
                this.resultMetrics = utils.deepCopyObject(resultMetricsToShow);
              }
            }
          },
          resultMetrics: {
            deep: true,
            immediate: true,
            handler: function(resultMetrics) {
              this.$log.debug("resultMetrics changed");
              if(resultMetrics && resultMetrics.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.resultMetrics, this.resultMetrics.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.resultMetrics, 0);
              }
            }
          },
        },
        created:function() {
        },
        computed: {
          resultMetricHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Description', value: 'description'},
                {text: 'Unit', value: 'unit'},
                {text: 'Evaluation Criteria', value: 'evaluationCriteria'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Description', value: 'description'},
                {text: 'Unit', value: 'unit'},
                {text: 'Evaluation Criteria', value: 'evaluationCriteria'},
                {text: 'Actions', value: 'actions'}
              ];
            }
          }
        },
        methods: {
          processArtifactReviewStateUpdate: function(reviewStateUpdate) {
            var updatedResultMetric = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.resultMetricsToShow);
            if(updatedResultMetric) {
              EvaluationScenarioServiceAPI.createUpdateResultMetric(updatedResultMetric).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          loadResultMetricsToReview: function() {
            this.$log.debug("...load all result metrics to review");
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.resultMetrics).then((response)=> {
              this.$log.debug("\t...found "+response.data.length+" remaining metrics to review");
              this.resultMetrics = response.data;
            });
          }
        }
    }
</script>

<style scoped>

</style>
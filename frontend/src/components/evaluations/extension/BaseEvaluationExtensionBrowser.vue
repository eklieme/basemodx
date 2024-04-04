<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="baseEvaluationExtensionHeaders"
          :items="baseEvaluationExtensions"
      >
        <template v-slot:item.parent_base = {item}>
          {{item.baseNameForExtension}}
        </template>
        <template v-slot:item.experiment_eval = {item}>
            {{summarizeExperimentBasedEvaluation(item)}}
        </template>
        <template v-slot:item.implement_eval = {item}>
            {{summarizeImplementationBasedEvaluation(item)}}
        </template>
        <template v-slot:item.eval_crit_grants = {item}>
            {{summarizeEvaluationCriteriaGrants(item)}}
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
                artifact-detail-view-available
                v-on:open-review-dialog="openReviewDialog"
                v-on:delete-artifact="deleteArtifact"
                v-on:edit-artifact="editArtifact"
                v-on:show-artifact-details="showBaseEvaluationExtensionDetails"/>
        </template>
      </v-data-table>
      <DialogContainer
        :dialog-title="baseEvaluationExtensionDetailsTitle"
        :dialog-opened="baseEvaluationExtensionDetailsDialogShown"
        v-on:close-dialog = "baseEvaluationExtensionDetailsDialogShown = false">
          <template v-slot:content>
              <BaseEvaluationViewer
                  :parent-base-name="baseEvaluationExtensionToShow.baseNameForExtension"
                  :base-evaluation="baseEvaluationExtensionToShow.baseEvaluationToMerge"
              />
          </template>
      </DialogContainer>
      <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-type="constants.review.artifactType.baseEvaluationExtension"
                            :artifact-to-review="currentArtifactToReview"

                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadBaseEvaluationExtensionsToReview"
      />
    </v-container>
</template>

<script>


    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";
    import {Util} from "@/helpers/util";
    import ArtifactBrowserActionUtils from "@/components/review/ArtifactBrowserActionUtils.vue";
    import ArtifactReviewDialog from "@/components/review/ArtifactReviewDialog.vue";
    import BrowserForReviewableArtifactsMixin from "@/mixins/BrowserForReviewableArtifactsMixin";
    import {ModelledElementReviewAPI} from "@/service/api/ModelledElementReviewAPI";
    import toast from "@/helpers/toast";
    import {EvaluationScenarioServiceAPI} from "@/service/api/EvaluationScenarioServiceAPI";
    import DialogContainer from "@/components/util/DialogContainer.vue";
    import BaseEvaluationViewer from "@/components/evaluations/BaseEvaluationViewer.vue";

    export default {
        components: {BaseEvaluationViewer, DialogContainer, ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "BaseEvaluationExtensionBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props:{
          baseEvaluationExtensionsToShow: [],
        },
        data() {
            return {
                baseEvaluationExtensions: [],
                baseEvaluationExtensionToShow: {},
                baseEvaluationExtensionDetailsDialogShown: false,
            }
        },
        watch: {
            baseEvaluationExtensionsToShow: {
            deep: true,
            immediate: true,
            handler: function(baseEvaluationExtensionsToShow) {
              this.$log.debug("baseEvaluationExtensionsToShow changed", baseEvaluationExtensionsToShow);
              if(baseEvaluationExtensionsToShow && baseEvaluationExtensionsToShow.length>0) {
                this.baseEvaluationExtensions = Util.deepCopyObject(baseEvaluationExtensionsToShow);
              }
            }
          },
            baseEvaluationExtensions: {
            deep: true,
            immediate: true,
            handler: function(baseEvaluationExtensions) {
              this.$log.debug("baseEvaluationExtensions changed");
              if(baseEvaluationExtensions && baseEvaluationExtensions.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.baseEvaluationExtension, this.baseEvaluationExtensions.length);
              } else {
                this.updateBrowseableArtifactCount(constants.review.artifactType.baseEvaluationExtension, 0);
              }
            }
          }
        },
        created:function() {
        },
        computed: {
          baseEvaluationExtensionHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Parent BASE', value: 'parent_base'},
                {text: 'Experimental Evaluation', value: 'experiment_eval'},
                {text: 'Implementation-based Evaluation', value: 'implement_eval'},
                {text: 'Evaluation Criteria Grants', value: 'eval_crit_grants'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: 'Parent BASE', value: 'parent_base'},
                {text: 'Experimental Evaluation', value: 'experiment_eval'},
                {text: 'Implementation-based Evaluation', value: 'implement_eval'},
                {text: 'Evaluation Criteria Grants', value: 'eval_crit_grants'},
                {text: 'Actions', value: 'actions'}
              ];
            }
          },
            baseEvaluationExtensionDetailsTitle: function() {
              if(this.baseEvaluationExtensionToShow.baseNameForExtension) {
                  return "Extension of Evaluation of BASE '"+this.baseEvaluationExtensionToShow.baseNameForExtension+"'";
              }
              return "";
            }
        },
        methods: {
          processArtifactReviewStateUpdate: function(reviewStateUpdate) {
            var updatedBaseEvaluationExtension = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.baseEvaluationExtensions);
            if(updatedBaseEvaluationExtension) {
              EvaluationScenarioServiceAPI.createUpdateBaseEvaluationExtension(updatedBaseEvaluationExtension).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          loadBaseEvaluationExtensionsToReview: function() {
            ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.baseEvaluationExtension).then((response) => {
              this.$log.debug("\t...load base evaluation extensions to review, remaining:", response.data.length);
              this.baseEvaluationExtensions = response.data;
            });
          },
          showBaseEvaluationExtensionDetails: function(baseEvaluationExtension) {
              this.$log.debug("\t...set base evaluation extension for details view", baseEvaluationExtension);
            this.baseEvaluationExtensionToShow = baseEvaluationExtension;
            this.baseEvaluationExtensionDetailsDialogShown = true;
          },
          summarizeExperimentBasedEvaluation: function(baseEvaluationExtension) {
              if(baseEvaluationExtension.baseEvaluationToMerge
                  && baseEvaluationExtension.baseEvaluationToMerge.experimentSpecificEvaluation
                  && baseEvaluationExtension.baseEvaluationToMerge.experimentSpecificEvaluation.experimentSpecificEvaluationSetups
                  && baseEvaluationExtension.baseEvaluationToMerge.experimentSpecificEvaluation.experimentSpecificEvaluationSetups.length>0) {
                  return baseEvaluationExtension.baseEvaluationToMerge.experimentSpecificEvaluation.experimentSpecificEvaluationSetups.length
                      +" experiment-based setups with "+
                      baseEvaluationExtension.baseEvaluationToMerge
                          .experimentSpecificEvaluation
                          .experimentSpecificEvaluationSetups.map(setup => {
                              return setup.specificResults.length
                          }).reduce((partialSum, a) => partialSum + a, 0)
                      + " result(s) using "+ baseEvaluationExtension.baseEvaluationToMerge
                          .experimentSpecificEvaluation.usedDatasets.length
                      +" dataset(s) and " +[...new Set(baseEvaluationExtension.baseEvaluationToMerge
                          .experimentSpecificEvaluation
                          .experimentSpecificEvaluationSetups.map(setup => {
                              return setup.biometricSystems.map(biometricSystem => {
                                return biometricSystem.id;
                              }).flat()
                          }))].reduce((partialSum, a) => partialSum + a.length, 0)
                      +" biometric system(s)";
              }
              return "-";
          },
          summarizeImplementationBasedEvaluation: function(baseEvaluationExtension) {
              if(baseEvaluationExtension.baseEvaluationToMerge
                  && baseEvaluationExtension.baseEvaluationToMerge.implementationSpecificEvaluationResults
                  && baseEvaluationExtension.baseEvaluationToMerge.implementationSpecificEvaluationResults.length>0) {
                  return baseEvaluationExtension.baseEvaluationToMerge.implementationSpecificEvaluationResults.length+
                      " implementation-based results";
              }
              return "-";
          },
          summarizeEvaluationCriteriaGrants: function(baseEvaluationExtension) {
              return EvaluationScenarioServiceAPI.summarizeEvaluationCriteriaGrants(baseEvaluationExtension.evaluationCriteriaGrantsToMerge);
          }
        }
    }
</script>

<style scoped>

</style>
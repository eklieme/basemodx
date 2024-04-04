<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
      <v-data-table
          :headers="evaluationCriteriaHeaders"
          :items="evaluationCriteria"
          :show-select="selectionAllowed"
          v-model="selectedEvaluationCriteria"
          single-select
      >
        <template v-slot:item.name = {item}>
          {{item.name}}
        </template>
        <template v-slot:item.category = {item}>
          {{item.category}}
        </template>
        <template v-slot:item.granted_rule = {item}>
          {{item.grantedRule.rule}}
        </template>
        <template v-slot:item.quasi_granted_rule = {item}>
          {{item.quasiGrantedRule.rule}}
        </template>
        <template v-slot:item.not_granted_rule = {item}>
          {{item.notGrantedRule.rule}}
        </template>
        <template v-if="showLifecycleStateInformation" v-slot:item.lifecycleState = {item}>
          {{ getLifecycleStateSummary(getUniqueUserId(), item) }}
        </template>
        <template v-slot:item.actions = {item}>
          <ArtifactBrowserActionUtils
              :show-review-action="showReviewAction"
              :artifact-for-actions="item"
              :edit-artifact-allowed="userCanEdit(item, getUniqueUserId())"
              :delete-artifact-allowed="userCanEdit(item, getUniqueUserId())"
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
                            :artifact-type="artifactType"
                            v-on:close-dialog="closeReviewDialog"
                            v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                            v-on:target-artefacts-to-review-reload-required="loadExperimentCriteriaToReview"
      />
    </v-container>
</template>

<script>


    import {EvaluationScenarioServiceAPI} from "../../../service/api/EvaluationScenarioServiceAPI";
    import {ModelledElementReviewAPI} from "../../../service/api/ModelledElementReviewAPI";
    import BrowserForReviewableArtifactsMixin from "../../../mixins/BrowserForReviewableArtifactsMixin";
    import ArtifactReviewDialog from "../../review/ArtifactReviewDialog.vue";
    import ArtifactBrowserActionUtils from "../../review/ArtifactBrowserActionUtils.vue";
    import toast from "../../../helpers/toast";
    import LoggedInUserMixin from "../../../mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";
    import {Util as utils} from "@/helpers/util";

    export default {
        components: {ArtifactBrowserActionUtils, ArtifactReviewDialog},
        name: "EvaluationCriteriaBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props:{
          evaluationCriteriaToShow: [],
          experimentRelatedCriteria: {
            type: Boolean,
            default: false,
          },
          criteriaCanBeSelected: {
            type: Boolean,
            default: false,
          }
        },
        data() {
            return {
              evaluationCriteria: [],
              selectedEvaluationCriteria: [],
              selectionAllowed: false,
            }
        },
        watch: {
          criteriaCanBeSelected: {
              immediate: true,
              handler: function(criteriaCanBeSelected) {
                  this.selectionAllowed = criteriaCanBeSelected;
              }
          },
          evaluationCriteriaToShow: {
            deep: true,
            immediate: true,
            handler: function(evaluationCriteriaToShow) {
              this.$log.debug("evaluationCriteriaToShow changed", evaluationCriteriaToShow);
              if(evaluationCriteriaToShow && evaluationCriteriaToShow.length>0) {
                this.evaluationCriteria = utils.deepCopyObject(evaluationCriteriaToShow);
              }
            }
          },
          selectedEvaluationCriteria: {
              deep: true,
              immediate: true,
              handler: function(selectedEvaluationCriteria) {
                  this.$log.debug("selectedEvaluationCriteria changed", selectedEvaluationCriteria);
                  if(selectedEvaluationCriteria && selectedEvaluationCriteria.length>0) {
                      this.$emit(constants.eventNames.local.artifactsSelected, selectedEvaluationCriteria);
                  }
              }
          },
          evaluationCriteria: {
            deep: true,
            immediate: true,
            handler: function(evaluationCriteria) {
              this.$log.debug("evaluationCriteria changed");
              if(evaluationCriteria && evaluationCriteria.length>0) {
                if(this.experimentRelatedCriteria) {
                  this.updateBrowseableArtifactCount(constants.review.artifactType.experimentCriteria, this.evaluationCriteria.length);
                } else {
                  this.updateBrowseableArtifactCount(constants.review.artifactType.implementationCriteria, this.evaluationCriteria.length);
                }
              } else {
                if(this.experimentRelatedCriteria) {
                  this.updateBrowseableArtifactCount(constants.review.artifactType.experimentCriteria, 0);
                } else {
                  this.updateBrowseableArtifactCount(constants.review.artifactType.implementationCriteria, 0);
                }
              }
            }
          },
        },
        created:function() {
        },
        computed: {
          evaluationCriteriaHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Category', value: 'category'},
                {text: 'Granted Rule', value: 'granted_rule'},
                {text: 'Quasi-Granted Rule', value: 'quasi_granted_rule'},
                {text: 'Not-Granted Rule', value: 'not_granted_rule'},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'},
              ];
            } else {
              return [
                {text: 'Name', value: 'name'},
                {text: 'Category', value: 'category'},
                {text: 'Granted Rule', value: 'granted_rule'},
                {text: 'Quasi-Granted Rule', value: 'quasi_granted_rule'},
                {text: 'Not-Granted Rule', value: 'not_granted_rule'},
                {text: 'Actions', value: 'actions'},
              ];
            }
          },
          artifactType: function() {
            if(this.experimentRelatedCriteria) {
              return constants.review.artifactType.experimentCriteria;
            }
            return constants.review.artifactType.implementationCriteria;
          }
        },
        methods: {
          processArtifactReviewStateUpdate: function (reviewStateUpdate) {
            var evaluationCriteriaToUpdate = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.evaluationCriteriaToShow);
            if (evaluationCriteriaToUpdate) {
              EvaluationScenarioServiceAPI.createUpdateEvaluationCriteria(evaluationCriteriaToUpdate).then(() => {
                toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
              });
            }
          },
          editCriteria: function (item) {
            console.log(item);
          },
          loadExperimentCriteriaToReview: function () {
            if(this.experimentRelatedCriteria) {
              ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.experimentCriteria).then((response) => {
                this.$log.debug("\t...load experiment specific criteria to review, remaining:", response.data.length);
                this.evaluationCriteria = response.data;
              });
            } else {
              ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.implementationCriteria).then((response) => {
                this.$log.debug("\t...load implementation specific criteria to review, remaining:", response.data.length);
                this.evaluationCriteria = response.data;
              });
            }
          }
        }
    }
</script>

<style scoped>

</style>
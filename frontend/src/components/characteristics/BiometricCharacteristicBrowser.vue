<template>
    <v-container fluid>
        <v-row row wrap>
            <v-col cols="12" lg="12">
                <v-data-table
                        :headers="characteristicsHeaders"
                        :items="biometricCharacteristics"
                        class="elevation-1"
                        :search="filterText"
                        :show-select="allowSelection"
                        v-model="selectedCharacteristics"
                >
                  <template v-slot:item.name = {item}>
                    {{characteristicsName(item)}}
                  </template>
                  <template v-slot:item.type = {item}>
                    {{characteristicsType(item.type)}}
                  </template>
                  <template v-slot:item.routineBased = {item}>
                    {{item.routineBased ? "Yes" : "No"}}
                  </template>
                  <template v-slot:item.transitional = {item}>
                    {{activityTransitionCharacteristics(item)}}
                  </template>
                  <template v-slot:item.activityDetails = {item}>
                    {{summarizeActivitySpecifics(item)}}
                  </template>
                  <template v-if="showLifecycleStateInformation" v-slot:item.lifecycleState = {item}>
                    {{ getLifecycleStateSummary(getUniqueUserId(), item) }}
                  </template>
                  <template v-slot:item.actions = {item}>
                    <ArtifactBrowserActionUtils :show-review-action="showReviewAction"
                                                :artifact-for-actions="item"
                                                :user-is-reviewer="userIsReviewer"
                                                :edit-artifact-allowed="userCanEdit(item, getUniqueUserId()) && editMode"
                                                :delete-artifact-allowed="userCanEdit(item, getUniqueUserId()) && editMode"
                                                v-on:open-review-dialog="openReviewDialog"
                                                v-on:edit-artifact="editArtifact"
                                                v-on:delete-artifact="deleteArtifact"
                          />
                  </template>
                </v-data-table>
              <ArtifactReviewDialog :show-review-dialog="reviewDialogOpened"
                                    :user-is-reviewer="userIsReviewer"
                                    :artifact-type="constants.review.artifactType.biometricCharacteristics"
                                    :artifact-to-review="currentArtifactToReview"
                                    v-on:close-dialog="closeReviewDialog"
                                    v-on:artifact-review-state-updated="processArtifactReviewStateUpdate"
                                    v-on:target-artefacts-to-review-reload-required="loadBiometricCharacteristicsToReview"
              />
            </v-col>
        </v-row>
    </v-container>
</template>

<script>

    import BrowserForReviewableArtifactsMixin from "../../mixins/BrowserForReviewableArtifactsMixin";
    import ArtifactBrowserActionUtils from "../review/ArtifactBrowserActionUtils.vue";
    import ArtifactReviewDialog from "../review/ArtifactReviewDialog.vue";
    import {ModelledElementReviewAPI} from "../../service/api/ModelledElementReviewAPI";
    import toast from "../../helpers/toast";
    import {BiometricCharacteristicsService} from "../../service/api/BiometricCharacteristicsService";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import constants from "@/helpers/constants";
    import {Util as utils} from "@/helpers/util";

    export default {
        components: {ArtifactReviewDialog, ArtifactBrowserActionUtils},
        name: "BiometricCharacteristicsBrowser",
        props: {
            biometricCharacteristicsToShow: Array,
            filterText: {
              type: String,
              default: "",
            },
            allowSelection: {
              type: Boolean,
              default: false,
            },
            preSelectedCharacteristics: {
              type: Array,
              default: function() {
                return [];
              }
            },
            editMode: {
              type: Boolean,
              default: true,
            }
        },
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        data() {
            return {
                editCharacteristicsDialog: false,
                biometricCharacteristics: [],
                selectedCharacteristics: [],
            }
        },
        watch: {
          biometricCharacteristicsToShow: {
            deep: true,
            immediate: true,
            handler: function(biometricCharacteristicsToShow) {
              this.$log.debug("biometricCharacteristicsToShow changed", biometricCharacteristicsToShow);
              if(biometricCharacteristicsToShow && biometricCharacteristicsToShow.length>0) {
                this.biometricCharacteristics = utils.deepCopyObject(biometricCharacteristicsToShow);
              } else {
                this.biometricCharacteristics = [];
              }
            }
          },
          biometricCharacteristics: {
            deep: true,
            immediate: true,
            handler: function(biometricCharacteristics) {
              this.$log.debug("biometricCharacteristics changed");
              if(biometricCharacteristics && biometricCharacteristics.length>0) {
                this.updateBrowseableArtifactCount(constants.review.artifactType.biometricCharacteristics, this.biometricCharacteristics.length);
              }
            }
          },
          selectedCharacteristics: {
            deep: true,
            immediate: true,
            handler: function(selectedCharacteristics) {
              this.$log.debug("selectedCharacteristics changed");
              this.$emit(constants.eventNames.local.artifactsSelected, selectedCharacteristics);
            }
          },
          preSelectedCharacteristics: {
            deep: true,
            immediate: true,
            handler: function(preSelectedCharacteristics) {
              if(preSelectedCharacteristics && preSelectedCharacteristics.length>0) {
                this.selectedCharacteristics = preSelectedCharacteristics;
              }
            }
          }
        },
        computed: {
          characteristicsHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                {text: "Name", value: "name"},
                {text: "Type", value: "type"},
                {text: "Is routine based?", value: "routineBased"},
                {text: "Transitional?", value: "transitional"},
                {text: "User Activity Specifics", value: "activityDetails"},
                this.lifecycleStateHeader,
                {text: 'Actions', value: 'actions'}
              ];
            } else {
              return [
                {text: "Name", value: "name"},
                {text: "Type", value: "type"},
                {text: "Is routine based?", value: "routineBased"},
                {text: "Transitional?", value: "transitional"},
                {text: "User Activity Specifics", value: "activityDetails"},
                {text: 'Actions', value: 'actions'}
              ];
            }
          },
        },
        methods: {
            characteristicsType(type) {
              if(type === constants.characteristics.types.enriched_soft) {
                  return constants.characteristics.types.soft;
              }
              return type;
            },
            characteristicsName(characteristic) {
              if(characteristic.type === constants.characteristics.types.behavioral) {
                return characteristic.userActivity.name;
              }
              if(characteristic.type === constants.characteristics.types.enriched_soft
                && characteristic.sourceBiometricCharacteristic) {
                return characteristic.name + " (inferred from "+this.characteristicsName(characteristic.sourceBiometricCharacteristic)+")";
              }
              return characteristic.name;
            },
            activityTransitionCharacteristics(characteristic) {
              if(characteristic.type === constants.characteristics.types.behavioral) {
                return characteristic.userActivity.type === constants.characteristics.types.transitional ? "Yes" : "No"
              }
              return "none";
            },
            summarizeActivitySpecifics(characteristic) {
                if(characteristic.type===constants.characteristics.types.behavioral) {
                  if (characteristic.userActivity.type === constants.characteristics.types.transitional && !characteristic.routineBased) {
                    return ("Transition from '" + characteristic.userActivity.situationBefore
                        + "' to '" + characteristic.userActivity.situationAfterwards + "'")
                  }
                  if (characteristic.userActivity.type !== constants.characteristics.types.transitional && !characteristic.routineBased) {
                    return ("Activity is based on the recurring action of '" + characteristic.userActivity.recurrentAction + "'")
                  }
                }
                return "none";
            },
            processArtifactReviewStateUpdate: function(reviewStateUpdate) {
              var updatedCharacteristic = ModelledElementReviewAPI.updateReviewState(reviewStateUpdate, this.biometricCharacteristics);
              if (updatedCharacteristic) {
                BiometricCharacteristicsService.createUpdateBiometricCharacteristic(updatedCharacteristic).then(() => {
                  toast.success(ModelledElementReviewAPI.getReviewStateUpdateSuccessfulMessage());
                });
              }
            },
            loadBiometricCharacteristicsToReview: function() {
              ModelledElementReviewAPI.getArtefactsToReviewForReviewerOfSpecificArtifactType(constants.review.artifactType.biometricCharacteristics).then((response) => {
                this.$log.debug("\t...update biometric characteristics to review, some remain", response.data.length);
                this.biometricCharacteristics = response.data;
              })
            },
        }
    }
</script>

<style scoped>

</style>
<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <v-dialog v-model="reviewDialogOpened" persistent hide-overlay transition="dialog-bottom-transition">
    <v-card>
      <v-card-title>
        <span class="headline">Review artifact '{{artifactName}}', modelled by {{modellerOfArtifact}}</span>
      </v-card-title>
      <v-card-text>
        <v-container fluid>
          <v-row cols="12" lg="12" v-if="userCanApproveOrReject">
            <v-col cols="12" v-if="artifactInReviewIsBase">
              <span class="error--text">If you approve a <b>Biometric Authentication System</b> and its <b>Evaluations</b> this decision also affects its included domain elements
              such as biometric characteristics, processing steps or datasets, i.e. they will be approved, too!</span>
            </v-col>
            <v-col cols="12" v-if="artifactInReviewIsDeviceCategory">
              <span class="error--text">If you approve a <b>Device Category</b> this decision also affects its included domain elements
              such as sensors or sensor dimensions, i.e. they will be approved, too!</span>
            </v-col>
            <v-col cols="12" v-if="artifactInReviewIsSampleDevice">
              <span class="error--text">If you approve a <b>Sample Device</b> this decision also affects its included domain elements
              such as its device category and the category's sensors, i.e. they will be approved, too!</span>
            </v-col>
            <v-col cols="12" v-if="artifactInReviewIsDataset">
              <span class="error--text">If you approve a <b>Dataset</b> this decision also affects its included domain elements
              such as sample devices, their device categories, or biometrics characteristics, i.e. they will be approved, too!</span>
            </v-col>
            <v-col cols="12" v-if="artifactInReviewIsBaseEvaluationExtension">
              <span class="error--text">If you approve an <b>Extension for an existing BASE Evaluation</b> this decision also affects its included domain elements
              such as any new sample devices, their device categories, or biometrics characteristics etc. that were created for the evaluation, i.e. they will be approved, too!
              Furthermore, the evaluation will be merged with the existing evaluations of the respective base.
              </span>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" lg="12">
              <v-card
                  flat
                  class="d-flex flex-column fill-height"
              >
                <v-card-title>
                  Review Conversation <span v-if="reviewConversation.length===0">&nbsp;(currently empty)</span>
                </v-card-title>
                <v-card-text class="flex-grow-1 overflow-y-auto">
                  <template v-for="(convEntry, index) in reviewConversation" >
                    <div :key="index"
                         :class="{ 'd-flex flex-row-reverse': conversationEntryCreatedByMe(convEntry) }"
                    >
                      <v-chip
                          :color="conversationEntryCreatedByMe(convEntry) ? 'primary' : ''"
                          dark
                          style="height:auto;white-space: normal;"
                          class="pa-4 mb-2"
                          :close="conversationEntryCreatedByMe(convEntry)"
                          @click:close="deleteMessage(index)"
                      >
                        {{ convEntry.message }}
                        <sub
                            class="ml-2"
                            style="font-size: 0.5rem;"
                        >{{ convEntry.creatorUniqueId }} at {{ convEntry.createdAt }}</sub>
                      </v-chip>
                    </div>
                  </template>
                </v-card-text>
                <v-card-text class="flex-shrink-1">
                  <v-text-field
                      v-model="newReviewConversationEntry.message"
                      label="write something"
                      type="text"
                      no-details
                      outlined
                      append-outer-icon="send"
                      @keyup.enter="addNewMessageToConversation"
                      @click:append-outer="addNewMessageToConversation"
                      hide-details
                  />
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <template v-if="userCanApproveOrReject">
          <v-btn color="info" text @click="approveReview">Approve Artifact</v-btn>
          <v-btn color="error" text @click="rejectReview">Reject Artifact</v-btn>
        </template>
        <v-btn color="info" text @click="updateConversation">Update Conversation</v-btn>
        <v-btn color="info" text @click="discardConversationChanges">Discard Conversation Changes</v-btn>
        <v-btn color="error" text @click="closeModelDialog">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>


    import {Util as utils} from "../../helpers/util";
    import constants from "../../helpers/constants";
    import {ModelledElementReviewAPI} from "@/service/api/ModelledElementReviewAPI";
    import toast from "@/helpers/toast";

    export default {
        name: "ArtifactReviewDialog",
        props:{
          artifactToReview: {},
          userIsReviewer: {
            type: Boolean,
            default: false,
          },
          showReviewDialog: {
            type: Boolean,
            default: false,
          },
          artifactType: {
            type: String,
            default: "",
          },
        },
        data() {
          return {
            newReviewConversationEntry: {
              message: "",
            },
            artifactReviewConversationMessages: [],
            currentUserUniqueId: "",
            modelledElementDetail: {},
            reviewDialogOpened: false,
          };
        },
        watch: {
          artifactToReview: {
            deep: true,
            immediate: true,
            handler: function(artifactToReview) {
              this.$log.debug("artifactToReview to edit changed", artifactToReview);
              if(artifactToReview.id) {
                this.modelledElementDetail = utils.deepCopyObject(artifactToReview.modelledElementDetail);
              }
            }
          },
          showReviewDialog: {
            handler: function(showReviewDialog) {
              this.reviewDialogOpened = showReviewDialog;
            }
          }
        },
        created:function() {
        },
        mounted: function() {
          if(this.$auth.getPayload()) {
            this.currentUserUniqueId = this.$auth.getPayload().sub;
          }
        },
        computed: {
          artifactName: function() {
              if(this.artifactType === constants.review.artifactType.baseEvaluationExtension) {
                  return "Base Evaluation Extension for BASE '"+this.artifactToReview.baseNameForExtension+"'";
              }
              return this.artifactToReview.name ? this.artifactToReview.name : this.artifactToReview.id
          },
          userCanApproveOrReject: function() {
            return this.userIsReviewer && this.$route.name !== constants.routes.ownArtifactsInReview.name;
          },
          artifactInReviewIsBase: function() {
            return constants.review.artifactType.base===this.artifactType
          },
          artifactInReviewIsDeviceCategory: function() {
            return constants.review.artifactType.deviceCategories===this.artifactType
          },
          artifactInReviewIsDataset: function() {
            return constants.review.artifactType.datasets===this.artifactType
          },
          artifactInReviewIsBaseEvaluationExtension: function() {
              return constants.review.artifactType.baseEvaluationExtension===this.artifactType
          },
          artifactInReviewIsSampleDevice: function() {
            return constants.review.artifactType.sampleDevices===this.artifactType
          },
          reviewConversation: function() {
            if(this.modelledElementDetail.elementLifecycle) {
              return this.modelledElementDetail.elementLifecycle.lifecycleConversation;
            }
            return [];
          },
          modellerOfArtifact: function() {
            if(this.modelledElementDetail) {
              return this.modelledElementDetail.modelledInitiallyBy;
            }
            return "";
          },
        },
        methods: {
          closeModelDialog: function() {
            this.$emit(constants.eventNames.local.closeDialog);
          },
          conversationEntryCreatedByMe: function(conversationEntry) {
            return conversationEntry.creatorUniqueId === this.currentUserUniqueId;
          },
          addNewMessageToConversation: function() {
            if(this.newReviewConversationEntry.message.length>0) {
              this.modelledElementDetail.elementLifecycle.lifecycleConversation.push( {
                message: this.newReviewConversationEntry.message,
                creatorUniqueId: this.currentUserUniqueId,
                createdAt: new Date().getTime(),
              });
              this.newReviewConversationEntry.message = "";
            }
          },
          deleteMessage: function(index) {
            this.$log.debug("delete message at index "+index);
            this.modelledElementDetail.elementLifecycle.lifecycleConversation.splice(index,1);
          },
          rejectReview: function() {
            this.$log.debug("approve artifact with id '"+this.artifactToReview.id+"'")
            this.modelledElementDetail.elementLifecycle.lifecycleState = constants.review.lifecycleState.rejected;
            this.notifyAboutReviewStateUpdate();
          },
          discardConversationChanges: function() {
            this.$log.debug("discard conversation changes!");
            this.modelledElementDetail.elementLifecycle.lifecycleConversation
                = utils.deepCopyObject(this.artifactToReview.modelledElementDetail.elementLifecycle.lifecycleConversation);
          },
          approveReview: function() {
            this.$log.debug("set artifact of type "+this.artifactType+" with id '"+this.artifactToReview.id+"' to reviewed!");
            ModelledElementReviewAPI.setArtifactReviewed(this.artifactType, this.artifactToReview.id).then(() => {
              this.notifyAboutTargetArtifactsReloadRequired();
              toast.success("...artifact with id '"+this.artifactToReview.id+"' is now reviewed and can be seen by everyone!")
            });
            this.closeModelDialog();
          },
          updateConversation: function() {
            this.$log.debug("update review conversation for artifact with id '"+this.artifactToReview.id+"'")
            this.notifyAboutReviewStateUpdate();
          },
          notifyAboutReviewStateUpdate() {
            var reviewStateUpdate = {
              id: this.artifactToReview.id,
              modelledElementDetail: this.modelledElementDetail,
            };
            this.$emit(constants.eventNames.local.artifactReviewStateUpdated, reviewStateUpdate);
          },
          notifyAboutTargetArtifactsReloadRequired() {
            this.$emit(constants.eventNames.local.targetArtifactsToReviewReloadRequired);
          }
        }
    }
</script>

<style scoped>

</style>
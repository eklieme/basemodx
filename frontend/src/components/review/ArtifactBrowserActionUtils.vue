<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <div>
    <template>
      <v-badge v-if="showReviewAction"
          :content="artifactReviewConversationLength"
          :value="artifactReviewConversationLength"
          color="blue"
      >
        <v-icon @click="showArtifactReviewDialog()"
        >
          compare_arrows
        </v-icon>
      </v-badge>
    </template>
    <v-icon v-if="editArtifactAllowed" @click="editArtifact"
    >
      edit
    </v-icon>
    <v-icon v-if="artifactDetailViewAvailable" @click="showArtifactDetails"
    >
      search
    </v-icon>
      <v-icon v-if="addSpecificsAllowed" @click="addSpecificsToArtifact">
          add
      </v-icon>
    <v-icon v-if="deleteArtifactAllowed" @click="deleteArtifact"
    >
      delete
    </v-icon>
    <v-icon v-if="duplicateArtifactAllowed" @click="duplicateArtifact"
    >
      file_copy
    </v-icon>
    <span v-if="noActionAvailable">No actions possible</span>
   </div>
</template>

<script>


    import {Util as utils} from "../../helpers/util";
    import constants from "../../helpers/constants";

    export default {
        name: "ArtifactBrowserActionUtils",
        props:{
          artifactForActions: {
            type: Object,
            default: function() {
              return {};
            }
          },
          userIsReviewer: {
            type: Boolean,
            default: false,
          },
          artifactDetailViewAvailable: {
            type: Boolean,
            default: false,
          },
          deleteArtifactAllowed: {
            type: Boolean,
            default: false,
          },
          editArtifactAllowed: {
            type: Boolean,
            default: false,
          },
          duplicateArtifactAllowed: {
              type: Boolean,
              default: false,
          },
          addSpecificsAllowed: {
            type: Boolean,
              default: false,
          },
          showReviewAction: {
            type: Boolean,
            default: false,
          }
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
          artifactForActions: {
            deep: true,
            immediate: true,
            handler: function(artifactForActions) {
              if(artifactForActions.id) {
                this.modelledElementDetail = utils.deepCopyObject(artifactForActions.modelledElementDetail);
              }
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
          artifactReviewConversationLength: function() {
            if (this.modelledElementDetail
                && this.modelledElementDetail.elementLifecycle
                && this.modelledElementDetail.elementLifecycle.lifecycleConversation) {
              return this.modelledElementDetail.elementLifecycle.lifecycleConversation.length;
            }
            return 0;
          },
          noActionAvailable: function() {
            return !this.showReviewAction && !this.editArtifactAllowed && !this.artifactDetailViewAvailable
              && !this.deleteArtifactAllowed;
          }
        },
        methods: {
          showArtifactReviewDialog: function() {
            this.$emit(constants.eventNames.local.openReviewDialog, this.artifactForActions);
          },
          showArtifactDetails: function() {
            this.$emit(constants.eventNames.local.showArtifactDetails, this.artifactForActions);
          },
          deleteArtifact: function() {
            this.$emit(constants.eventNames.local.deleteArtifact, this.artifactForActions);
          },
          duplicateArtifact: function() {
              this.$emit(constants.eventNames.local.duplicateArtifact, this.artifactForActions);
          },
          editArtifact: function() {
            this.$emit(constants.eventNames.local.editArtifact, this.artifactForActions);
          },
          addSpecificsToArtifact: function() {
              this.$emit(constants.eventNames.local.addSpecificsArtifact, this.artifactForActions);
          }
        }
    }
</script>

<style scoped>

</style>
import constants from "@/helpers/constants";

export default {
    data: () => ({
        reviewDialogOpened: false,
        currentArtifactToReview: {},
    }),
    props: {
        showReviewAction: {
            type: Boolean,
            default: false,
        },
        showLifecycleStateInformation: {
            type: Boolean,
            default: false,
        }
    },
    computed: {
        lifecycleStateHeader: function() {
            return {text: "Lifecycle State", value:"lifecycleState"};
        },
        constants: function() {
            return constants;
        }
    },
    created: function() {

    },
    mounted: function() {
    },
    methods: {
        openReviewDialog: function(itemToReview) {
            this.reviewDialogOpened = true;
            this.currentArtifactToReview = itemToReview;
        },
        closeReviewDialog: function() {
            this.reviewDialogOpened = false;
        },
        getLifecycleStateSummary: function(currentUserUniqueId, item) {
            let modelledBy = "";
            if(currentUserUniqueId===item.modelledElementDetail.modelledInitiallyBy){
                modelledBy = "me!";
            }
            modelledBy = item.modelledElementDetail.modelledInitiallyBy;

            return item.modelledElementDetail.elementLifecycle.lifecycleState+", modelled by "+modelledBy;
        },
        userCanEdit: function(item, uniqueUserId) {
            if(item.modelledElementDetail) {
                this.$log.debug("\t..evaluate whether logged in user '"
                    + uniqueUserId
                    + "' is allowed to edit element created by '"
                    + item.modelledElementDetail.modelledInitiallyBy + "' of lifecycle state '" + item.modelledElementDetail.elementLifecycle.lifecycleState + "'");
                return item.modelledElementDetail.modelledInitiallyBy === uniqueUserId
                    && item.modelledElementDetail.elementLifecycle.lifecycleState.toLowerCase() === constants.review.lifecycleState.created;
            }
            return false;
        },
        userCanEditExtendableArtifact: function(item, uniqueUserId) {
            return this.userCanEdit(item, uniqueUserId) || this.$userAuthService.isAuthenticated();
        },
        updateBrowseableArtifactCount: function(artifactType, newCount) {
            this.$log.debug("\t..inform about update of available "+newCount+" browsables for type '"+artifactType+"'");
            let userSpecific = false;
            if(this.$route.name === constants.routes.ownArtifactsInReview.name) {
                userSpecific = true;
            }
            this.$emit(constants.eventNames.local.amountOfArtifactsShownChanged, {
                artifactType: artifactType,
                newCount: newCount,
                userSpecific: userSpecific,
            });
        },
        deleteArtifact: function(artifact) {
            this.$log.debug("\t...trigger deleting of artifact", artifact);
            this.$emit(constants.eventNames.local.deleteArtifact, artifact);
        },
        editArtifact: function(artifact) {
            this.$log.debug("\t...trigger editing of artifact", artifact);
            this.$emit(constants.eventNames.local.editArtifact, artifact);
        }
    }
}
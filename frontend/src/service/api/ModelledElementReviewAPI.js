import api from './api';
import {Util as utils} from "../../helpers/util";
import Vue from "vue";
const API_URL_REVIEW = '/api/artifacts';

export class ModelledElementReviewAPI {

    constructor(){
    }

    static getElementsInReviewForMe() {
        return api.get(API_URL_REVIEW+"/me/review");
    }

    static getElementsInReviewForMeOfSpecificArtifactType(artifactType) {
        return api.get(API_URL_REVIEW+"/me/review/"+artifactType);
    }

    static getArtefactsToReviewForReviewer() {
        return api.get(API_URL_REVIEW+"/review");
    }

    static getArtefactsToReviewForReviewerOfSpecificArtifactType(artifactType) {
        return api.get(API_URL_REVIEW+"/review/"+artifactType);
    }

    static updateReviewState(reviewStateUpdate, artifactList) {
        Vue.$log.debug("update review state for element with id "+reviewStateUpdate.id);
        var artifactToUpdate = artifactList.find( artifact => {
            return artifact.id === reviewStateUpdate.id;
        });

        if(artifactToUpdate && artifactToUpdate.id) {
            Vue.$log.debug("\t...found artifact with id " + artifactToUpdate.id + ", update!");
            artifactToUpdate.modelledElementDetail = utils.deepCopyObject(reviewStateUpdate.modelledElementDetail);
        }

        return artifactToUpdate;
    }

    static getReviewStateUpdateSuccessfulMessage() {
        return "Update of review state successful!";
    }

    static setArtifactReviewed(artifactType, artifactId) {
        return api.post(API_URL_REVIEW+"/reviewed/"+artifactType+"/"+artifactId);
    }

}

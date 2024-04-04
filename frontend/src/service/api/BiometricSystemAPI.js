import api from './api';
import helper from "./apiHelperService";
import transformers from "../transformers";
const API_URL_PROCESSING_STEPS = '/api/biometricSystemProcessingSteps';
const API_URL_PROCESSING_STEPS_LIFECYCLE_AWARE = '/api/lcaware/biometricSystemProcessingSteps';
const API_URL_BIOMETRIC_SYSTEM = '/api/biometricSystems';
const API_URL_BIOMETRIC_SYSTEM_LIFECYCLE_AWARE = '/api/lcaware/biometricSystems';
const API_URL_FEATURES = '/api/features';
const API_URL_FEATURES_LIFECYCLE_AWARE = '/api/lcaware/features';

export class BiometricSystemAPI {

    constructor(){
    }

    static getBiometricSystems() {
        return api.get(API_URL_BIOMETRIC_SYSTEM_LIFECYCLE_AWARE);
    }

    static getBiometricSystemsByParentBaseName(parentBasename) {
        return api.get(API_URL_BIOMETRIC_SYSTEM_LIFECYCLE_AWARE+"/parentBaseName/"+parentBasename);
    }

    static getBiometricSystemById(biometricSystemId) {
        return api.get(API_URL_BIOMETRIC_SYSTEM_LIFECYCLE_AWARE+"/"+biometricSystemId);
    }

    static getSpecificBiometricSystems(biometricSystemIds) {

        return api.get(API_URL_BIOMETRIC_SYSTEM+"/search/findAllByIdIn", {params: {"ids": biometricSystemIds.join(",")},transformResponse:
                transformers.getExtractDomainObjectFromResponseTransformer("biometricSystems")});

    }

    static createUpdateBiometricSystem(biometricSystemToCreateOrUpdate) {
        return helper.createUpdateDomainObject(API_URL_BIOMETRIC_SYSTEM, biometricSystemToCreateOrUpdate)
    }

    static getProcessingSteps() {
        return api.get(API_URL_PROCESSING_STEPS_LIFECYCLE_AWARE);
    }


    static createUpdateProcessingStep(signalProcessingStepToCreateOrUpdate) {
        return helper.createUpdateDomainObject(API_URL_PROCESSING_STEPS, signalProcessingStepToCreateOrUpdate)
    }

    static deleteBiometricProcessingStep(biometricProcessingStep) {
        return api.delete(API_URL_PROCESSING_STEPS_LIFECYCLE_AWARE+"/"+biometricProcessingStep.id);
    }

    static getFeatures() {
        return api.get(API_URL_FEATURES_LIFECYCLE_AWARE);
    }

    static createUpdateFeature(feature) {
        return helper.createUpdateDomainObject(API_URL_FEATURES, feature)
    }

    static deleteFeature(feature) {
        return api.delete(API_URL_FEATURES_LIFECYCLE_AWARE+"/"+feature.id);
    }

}

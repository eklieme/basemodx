import api from './api';
import helper from "./apiHelperService";
import transformers from "../transformers";
const API_URL_BASE = '/api/biometricAuthenticationSystemAndEvaluations';
const API_URL_ENRICHED_BASE = '/api/base';

export class BiometricAuthenticationSystemAndEvaluationAPI {

    constructor(){
    }


    static getBiometricAuthenticationSystems() {
        return api.get(API_URL_ENRICHED_BASE, {transformResponse: transformers.furtherEnrichBaseObjectsFromResponseTransformer()});
    }

    static getBiometricAuthenticationSystemOfSpecificModellingProgress(modellingProgress) {
        return api.get(API_URL_ENRICHED_BASE+"/modellingProgress/"+modellingProgress.toUpperCase(),
            {
                    transformResponse: transformers.furtherEnrichBaseObjectsFromResponseTransformer()
                   }
            );
    }

    static getBiometricAuthenticationSystemWithSpecificName(name) {
        return api.get(API_URL_ENRICHED_BASE+"/name/"+name, {
            transformResponse: transformers.furtherEnrichBaseObjectFromResponseTransformer()
        });
    }

    static createUpdateBiometricAuthenticationSystem(behavioralAuthenticationSystemToCreateOrUpdate) {

        return helper.createUpdateDomainObject(API_URL_BASE,
            transformers.transformBaseForPersistence(behavioralAuthenticationSystemToCreateOrUpdate)[0],
            false);

    }

    static deleteBiometricAuthenticationSystemAndEvaluation(behavioralAuthenticationSystemToDelete) {
        return api.delete(API_URL_BASE+"/"+behavioralAuthenticationSystemToDelete.id);
    }

}
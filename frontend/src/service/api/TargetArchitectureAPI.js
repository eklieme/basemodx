import api from './api';
import helper from "./apiHelperService";
const API_URL_RESOURCES_TO_PROTECT_LIFECYCLE_AWARE = '/api/lcaware/resourceToProtects';
const API_URL_RESOURCES_TO_PROTECT = '/api/resourceToProtects';

export class TargetArchitectureAPI {

    constructor(){
    }

    static getResourcesToProtect() {
        return api.get(API_URL_RESOURCES_TO_PROTECT_LIFECYCLE_AWARE);
    }

    static createUpdateResourceToProtect(newResourceToProtect) {
        return helper.createUpdateDomainObject(API_URL_RESOURCES_TO_PROTECT, newResourceToProtect);
    }

    static deleteResourcesToProtect(resourcesToProtect) {
        return api.delete(API_URL_RESOURCES_TO_PROTECT_LIFECYCLE_AWARE+"/"+resourcesToProtect.id);
    }

}

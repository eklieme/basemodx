import api from "./api.js"
import helper from "./apiHelperService";
import transformers from "@/service/transformers";
import {Util} from "@/helpers/util";
const API_URL_LIFECYCLE_AWARE = '/api/lcaware/deviceCategories';
const API_URL = '/api/deviceCategories';
export class DeviceCategoryServiceAPI {

    constructor(){
    }

    static getDeviceCategories() {
        return api.get(API_URL_LIFECYCLE_AWARE);
    }

    static getDeviceCategoryById(deviceCategoryId) {
        return api.get(API_URL_LIFECYCLE_AWARE+"/"+deviceCategoryId);
    }

    static createUpdateDeviceCategory(newDeviceCategory) {
        return helper.createUpdateDomainObject(API_URL, transformers.transformDeviceCategoryForPersistence(newDeviceCategory));
    }

    static deleteDeviceCategory(deviceCategoryToDelete) {
        return api.delete(API_URL_LIFECYCLE_AWARE+"/"+deviceCategoryToDelete.id);
    }

    static getAllModelledDeviceCategoriesForBase(baseTargetArchitecture) {

        let deviceCategories = new Set();

        if(!Util.isEmpty(baseTargetArchitecture)) {
            Object.keys(baseTargetArchitecture).forEach(function (key) {
                // key: the name of the object key
                // index: the ordinal position of the key within the object
                if (key.indexOf("Categories") > 0) {
                    if(!Util.isEmpty(baseTargetArchitecture[key])) {
                        baseTargetArchitecture[key].forEach(function (deviceCategory) {
                            deviceCategories.add(deviceCategory.name);
                        });
                    }
                }
            });
        }

        return [...deviceCategories];

    }

}

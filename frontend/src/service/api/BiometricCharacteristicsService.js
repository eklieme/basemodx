import api from './api';
import helper from "./apiHelperService";
import constants from "@/helpers/constants";
const BIOMETRIC_CHARACTERISTICS_LIFECYCLE_AWARE_URL = '/api/lcaware/biometricCharacteristics';
const BIOMETRIC_CHARACTERISTICS_URL = '/api/biometricCharacteristics';
export class BiometricCharacteristicsService {

    constructor(){
    }

    static getBiometricCharacteristics() {
        return api.get(BIOMETRIC_CHARACTERISTICS_LIFECYCLE_AWARE_URL);
    }


    static createUpdateBiometricCharacteristic(newCharacteristic) {
        return helper.createUpdateDomainObject(BIOMETRIC_CHARACTERISTICS_URL, newCharacteristic);
    }

    static transformCharacteristicForPresentation(characteristic) {
        characteristic["viewId"] = characteristic.id+"-"+characteristic.type;
        switch(characteristic.type) {
            case constants.characteristics.types.behavioral:
                characteristic["selectionText"] = characteristic.userActivity.name+" ("+constants.characteristics.types.behavioral+", "+characteristic.userActivity.type+")";
                break;
            case constants.characteristics.types.physiological:
                characteristic["selectionText"] = characteristic.name+" ("+constants.characteristics.types.physiological+", "+characteristic.authenticationMode+")";
                break;
            case constants.characteristics.types.enriched_soft:
                characteristic["selectionText"] = characteristic.name+" ("+constants.characteristics.types.enriched_soft+")";
                break;
        }
        return characteristic;
    }


    static getUniqueIdentifier(biometricCharacteristic) {
        let uniqueIdentifier = "";
        uniqueIdentifier+=biometricCharacteristic.type;
        if(biometricCharacteristic.type === constants.characteristics.types.behavioral) {
            uniqueIdentifier+=", "+biometricCharacteristic.userActivity.name;
            if(biometricCharacteristic.routineBased) {
                uniqueIdentifier+="-routine-based";
            } else {
                uniqueIdentifier+="-not-routine-based";
                if(biometricCharacteristic.userActivity.type===constants.characteristics.types.continuous) {
                    uniqueIdentifier+="-continuous-"+biometricCharacteristic.userActivity.recurrentAction;
                } else {
                    uniqueIdentifier+="-transitional-"+biometricCharacteristic.userActivity.situationBefore+"-"+biometricCharacteristic.userActivity.situationAfterwards;
                }
            }
        } else if(biometricCharacteristic.type === constants.characteristics.types.physiological) {
            uniqueIdentifier+="-"+biometricCharacteristic.authenticationMode;
        } else {
            uniqueIdentifier+="-"+biometricCharacteristic.name;
        }
        return uniqueIdentifier;
    }

    static deleteBiometricCharacteristic(biometricCharacteristicToDelete) {
        return api.delete(BIOMETRIC_CHARACTERISTICS_LIFECYCLE_AWARE_URL+"/"+biometricCharacteristicToDelete.id);
    }


}

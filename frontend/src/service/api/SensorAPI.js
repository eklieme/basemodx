import api from "./api.js"
import helper from "./apiHelperService";
import Vue from "vue";
const API_URL_SENSORS_LIFECYCLE_AWARE = '/api/lcaware/sensors';
const API_URL_SENSORS = '/api/sensors';
const API_URL_SENSOR_DIMENSIONS = '/api/sensorDimensions';
const API_URL_SENSOR_DIMENSIONS_LIFECYCLE_AWARE = '/api/lcaware/sensorDimensions';
export class SensorAPI {

    constructor(){
    }

    static getSensors() {
        return api.get(API_URL_SENSORS_LIFECYCLE_AWARE);
    }

    static createUpdateSensor(newSensor) {
        return helper.createUpdateDomainObject(API_URL_SENSORS, newSensor);
    }

    static createUpdateSensorDimension(sensorDimensionToCreateUpdate) {
        return helper.createUpdateDomainObject(API_URL_SENSOR_DIMENSIONS, sensorDimensionToCreateUpdate);
    }

    static createSensorDimensions(sensorDimensions) {
        Vue.$log.debug("request to create sensor dimensions", sensorDimensions);
        let axiosCalls = [];
        sensorDimensions.forEach(sensorDimension => {
            if(sensorDimension.id && sensorDimension.id.length>0) {
                Vue.$log.debug("\t...sensor dimension already exists, resolve already existing id", sensorDimension.id);
                axiosCalls.push(Promise.resolve({'data':sensorDimension.id}));
            } else {
                Vue.$log.debug("\t...sensor dimension is new, create dimension");
                axiosCalls.push(helper.createUpdateDomainObject(API_URL_SENSOR_DIMENSIONS,sensorDimension))
            }

        });
        return Promise.all(axiosCalls);
    }

    static getSensorDimensions() {
        return api.get(API_URL_SENSOR_DIMENSIONS_LIFECYCLE_AWARE);
    }

    static deleteSensor(sensorToDelete) {
        return api.delete(API_URL_SENSORS_LIFECYCLE_AWARE+"/"+sensorToDelete.id);
    }

    static deleteSensorDimension(sensorDimensionToDelete) {
        return api.delete(API_URL_SENSOR_DIMENSIONS_LIFECYCLE_AWARE+"/"+sensorDimensionToDelete.id);
    }

}

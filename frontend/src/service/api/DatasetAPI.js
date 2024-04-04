import api from './api';
import helper from "./apiHelperService";
import transformers from "@/service/transformers";
import {Util as Utils} from "@/helpers/util";
import Vue from "vue";

const API_URL_DATASETS = '/api/dataSets';
const API_URL_DATASETS_LIFECYCLE_AWARE = '/api/lcaware/dataSets';
const API_URL_SAMPLE_DEVICES = '/api/sampleDevices';
const API_URL_SAMPLE_DEVICES_LIFECYCLE_AWARE = '/api/lcaware/sampleDevices';
const API_URL_EVAL_CONTEXT = '/api/samplingContexts';
const API_URL_SAMPLING_CONTEXT_LIFECYCLE_AWARE = '/api/lcaware/samplingContexts';

export class DatasetAPI {

    constructor(){
    }

    static getAllDatasets() {
        return api.get(API_URL_DATASETS_LIFECYCLE_AWARE);
    }

    static getSpecificDataset(datasetId) {
        return api.get(API_URL_DATASETS+"/"+datasetId);
    }

    static getSpecificDatasets(datasetIds) {
        return api.get(API_URL_DATASETS+"/search/findAllByIdIn", {params: {"ids": datasetIds.join(",")},
            transformResponse:
                transformers.getExtractDomainObjectFromResponseTransformer("dataSets")}
        );
    }

    static createUpdateDataset(dataSet) {
        return helper.createUpdateDomainObject(API_URL_DATASETS, dataSet);
    }

    static getSampleDevices() {
        return api.get(API_URL_SAMPLE_DEVICES_LIFECYCLE_AWARE);
    }

    static createUpdateSampleDevice(sampleDevice) {
        return helper.createUpdateDomainObject(API_URL_SAMPLE_DEVICES, sampleDevice);
    }


    static getSamplingContexts() {
        return api.get(API_URL_SAMPLING_CONTEXT_LIFECYCLE_AWARE);
    }

    static createUpdateSamplingContext(samplingContext) {
        return helper.createUpdateDomainObject(API_URL_EVAL_CONTEXT, samplingContext);
    }

    static deleteSamplingContext(samplingContext) {
        return api.delete(API_URL_SAMPLING_CONTEXT_LIFECYCLE_AWARE+"/"+samplingContext.id);
    }

    static deleteSamplingDevice(sampleDeviceToDelete) {
        return api.delete(API_URL_SAMPLE_DEVICES+"/"+sampleDeviceToDelete.id);
    }


    static transformDatasetForCreationUpdate(datasetToCreateUpdate) {

        Vue.$log.debug("\t...going to transform dataset", datasetToCreateUpdate);

        let transformedDataset = Utils.deepCopyObject(datasetToCreateUpdate);

        // major part is replacing all references with ids within sampled biometrics

        transformedDataset.sampledBiometrics = [];

        // first of all, we can get ids for sampled biometric characteristics and sample devices immediately as they have respective ids already

        datasetToCreateUpdate.sampledBiometrics.forEach(sampledBiometric => {
            let transformedSampledBiometric = {
                    sampleDeviceIds: [],
                    sampledCharacteristicId: null,
                    samplingContextIds: [],
                    supportCharacteristicId: null,
            };
            transformedSampledBiometric.sampleDeviceIds = sampledBiometric.sampleDevices.map(sampleDevice => {
               return sampleDevice.id;
            });
            transformedSampledBiometric.samplingContextIds = sampledBiometric.samplingContexts.map(samplingContext => {
                return samplingContext.id;
            });
            transformedSampledBiometric.sampledCharacteristicId = sampledBiometric.sampledCharacteristic.id;
            if(sampledBiometric.supportCharacteristic) {
                transformedSampledBiometric.supportCharacteristicId = sampledBiometric.supportCharacteristic.id;
            }
            transformedDataset.sampledBiometrics.push(transformedSampledBiometric);
        })

        Vue.$log.debug("\t...returning transformed dataset", transformedDataset);

        return transformedDataset;

    }

}

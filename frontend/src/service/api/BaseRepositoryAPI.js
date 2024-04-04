import api from './api';
const API_URL_IMPORT = '/api/repository/import';
const API_URL_EXPORT = '/api/repository/export';
const API_URL_TYPE_DEMO = "/demo";
const API_URL_TYPE_BASE = "/base";
const API_URL_TYPE_DEVICES = "/sampleDevices";
const API_URL_TYPE_DATASETS = "/datasets";
const API_URL_TYPE_CRITERIA_IMPLEMENTATION = "/criteria/implementation";
const API_URL_TYPE_CRITERIA_EXPERIMENTS = "/criteria/experiment";

export class BaseRepositoryAPI {

    constructor(){
    }

    static resetAndInitWithDemoData() {
        return api.get(API_URL_IMPORT+API_URL_TYPE_DEMO);
    }

    static exportBase() {
        return api.get(API_URL_EXPORT+API_URL_TYPE_BASE);
    }

    static exportDatasets() {
        return api.get(API_URL_EXPORT+API_URL_TYPE_DATASETS);
    }

    static importBase(baseImportArtefact) {
        return api.post(API_URL_IMPORT+API_URL_TYPE_BASE, baseImportArtefact);
    }

    static importDatasets(datasetsToImport) {
        return api.post(API_URL_IMPORT+API_URL_TYPE_DATASETS, datasetsToImport);
    }

    static importSampleDevices(sampleDevicesToImport) {
        return api.post(API_URL_IMPORT+API_URL_TYPE_DEVICES, sampleDevicesToImport);
    }

    static importExperimentSpecificCriteria(experimentSpecificCriteriaToImport) {
        return api.post(API_URL_IMPORT+API_URL_TYPE_CRITERIA_EXPERIMENTS, experimentSpecificCriteriaToImport);
    }

    static importImplementationBasedCriteria(implementationSpecificCriteriaToImport) {
        return api.post(API_URL_IMPORT+API_URL_TYPE_CRITERIA_IMPLEMENTATION, implementationSpecificCriteriaToImport);
    }

}

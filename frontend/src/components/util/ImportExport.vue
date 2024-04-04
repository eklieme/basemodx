<template>
    <v-row justify="center" align="center" dense>
        <v-col v-if="showExportButton" lg="2">
            <v-btn color="primary" text @click="exportArtefactsAsJson" right>Export {{exportArtefacts.length}} {{artefactName}}</v-btn>
        </v-col>
        <v-col lg="2">
            <v-dialog v-model="showImportDialog" persistent >
                <template v-slot:activator="{ on }">
                    <v-btn text v-on="on" slot="activator" color="primary" :disabled="!allowImport">{{importButtonLabel}}</v-btn>
                </template>
                <v-card>
                    <v-card-title v-if="showImportResult" class="headline">Import existing {{artefactName}}</v-card-title>
                    <v-card-title v-else class="headline">Import Results</v-card-title>
                    <v-container>
                        <template v-if="showImportResult">
                            <v-row>
                                <v-col cols="12" lg="12"><span>Your import was {{importResult.importResultStatus}}</span></v-col>
                            </v-row>
                            <v-row>
                                <v-col cols="12" lg="12">
                                    <v-expansion-panels flat>
                                        <v-expansion-panel>
                                            <v-expansion-panel-header>Show Import Remarks</v-expansion-panel-header>
                                            <v-expansion-panel-content>
                                                <v-data-table
                                                        :headers="importRemarksHeaders"
                                                        :items="importResult.importRemarks"
                                                />
                                            </v-expansion-panel-content>
                                        </v-expansion-panel>
                                    </v-expansion-panels>
                                </v-col>
                            </v-row>
                        </template>
                        <template v-else>
                            <v-row>
                                <v-col cols="12" lg="12">
                                    <v-file-input
                                            show-size
                                            v-model="importFile"
                                            accept="application/json"
                                            :label="fileToImportLabel"
                                    />
                                </v-col>
                            </v-row>
                            <v-row v-if="importRunning">
                                <v-col cols="12" lg="12">
                                    <v-progress-linear indeterminate/>
                                </v-col>
                            </v-row>
                        </template>
                    </v-container>
                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <template v-if="!showImportResult">
                            <v-btn color="primary" :disabled="!importFile.name" text @click="importArtefact">Import</v-btn>
                            <v-btn color="error" text @click="cancelImport">Cancel</v-btn>
                        </template>
                        <v-btn v-else color="primary" text @click="finishImport">OK</v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-col>
        <v-col v-if="menuOffset" lg="2">
          <v-btn color="primary" :disabled="resetRunning" @click="showResetPocAndInitAgainDialog = true" right>Reset Model Repository</v-btn>
          <v-dialog v-model="showResetPocAndInitAgainDialog" persistent >
            <v-card>
              <v-card-title class="headline">Reset Model Database and init with modeled example BASE data again?</v-card-title>
              <v-card-text>Are you sure you want to delete all changes you made to the model repository and reset to the initial version?</v-card-text>
              <div v-if="resetRunning">
                <v-card-text>This page will automatically refresh once the repository is initialized again, please wait!</v-card-text>
                <v-progress-linear indeterminate/>
              </div>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" text @click="showResetPocAndInitAgainDialog = false">Cancel</v-btn>
                <v-btn color="error" :disabled="resetRunning" text @click="resetAndInitForDemo">OK</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-col>
    </v-row>
</template>

<script>
    import constants from "../../helpers/constants";
    import toast from "../../helpers/toast";
    import {BaseRepositoryAPI} from "../../service/api/BaseRepositoryAPI";

    export default {
        name: "ImportExport",
        data() {
            return {
                allowSelection: false,
                showImportDialog: false,
                importRunning:false,
                importFile: {},
                importResult: {
                    importResultStatus: "",
                    importRemarks: [],
                },
                showImportResult: false,
                importRemarksHeaders: [
                    { text: 'Remark', value: 'remarkMessage' },
                    { text: 'Artefact', value: 'importArtefactType' },
                    { text: 'Severity', value: 'importRemarkLevel' },
                ],
                showResetPocAndInitAgainDialog: false,
                resetRunning: false,
            }
        },
        props: {
            artefactName: {
                type: String,
                default: ""
            },
            artefactType: {
                type: String,
                default: ""
            },
            showExportSelection: {
                type: Boolean,
                default: true,
            },
            allowExportSelection: {
                type: Boolean,
                default: true,
            },
            allowImport: {
                type: Boolean,
                default: true,
            },
            exportArtefacts: {
                type: Array,
                default: function() {
                    return [];
                }
            },
        },
        watch: {
            exportArtefacts: {
                deep: true,
                handler: function(newArtefacts) {
                    this.$log.debug("new Artefacts: ", newArtefacts);
                }
            }
        },
        computed: {
            menuOffset: function() {
                if(this.showExportButton) {
                  if (this.showResetButton) {
                    return 4;
                  }
                } else {
                  if(this.showResetButton) {
                    return 6;
                  }
                }

                return 8;
            },
            showExportButton: function() {
                return this.artefactType
                    && this.exportArtefacts.length > 0;
            },
            showResetButton: function() {
                return process.env.VUE_APP_DEMO_MODE==='true';
            },
            importButtonLabel: function() {
                return "Import "+this.artefactName;
            },
            exportSwitchLabel: function() {
                return "Mark "+this.artefactName+ " for export";
            },
            fileToImportLabel: function() {
                return "Choose file with exported "+ this.artefactName+ " to import";
            }
        },
        methods: {
            resetAndInitForDemo() {

              this.resetRunning = true;

              BaseRepositoryAPI.resetAndInitWithDemoData().then(()=> {
                this.$router.go();
              });

            },
            importArtefact() {

                this.importRunning = true;

                this.$log.debug("Start importing from file", this.importFile);
                const reader = new FileReader();
                // start reading
                reader.readAsText(this.importFile);

                reader.onload = onload => {
                    this.$log.debug("raw file content", onload.target.result);
                    const artefactToImport = JSON.parse(onload.target.result);

                    let request = {};

                    if(!artefactToImport || !artefactToImport.type) {
                        toast.error("Corrupt file, can not import", 5000);
                        this.resetImportRunning();
                        return;
                    }

                    if(artefactToImport.type && artefactToImport.type !== this.artefactType) {
                        toast.error("You try to import '"+artefactToImport.type
                            +"' records in the '"+this.artefactType
                            +"' section. Please select a correct export file.", 5000);
                        this.resetImportRunning();
                        return;
                    }

                    switch(artefactToImport.type) {
                        case constants.importExport.artefactTypes.base:
                            request = BaseRepositoryAPI.importBase(artefactToImport.data);
                            break;
                        case constants.importExport.artefactTypes.datasets:
                            request = BaseRepositoryAPI.importDatasets(artefactToImport.data);
                            break;
                        case constants.importExport.artefactTypes.devices:
                            request = BaseRepositoryAPI.importSampleDevices(artefactToImport.data);
                            break;
                        case constants.importExport.artefactTypes.implementationCriteria:
                            request = BaseRepositoryAPI.importImplementationBasedCriteria(artefactToImport.data);
                            break;
                        case constants.importExport.artefactTypes.experimentCriteria:
                            request = BaseRepositoryAPI.importExperimentSpecificCriteria(artefactToImport.data);
                            break;
                        default:
                            toast.error("No import backend service exists for the artefact name "+this.artefactName, 5000);
                            return;
                    }

                    request.then(response => {
                        this.$log.debug(response);
                        this.importResult = response.data;
                        this.showImportResult = true;
                        this.resetImportRunning();
                    });

                };

            },
            resetImportRunning() {
                this.importRunning = false;
            },
            exportArtefactsAsJson() {

                this.$log.debug("Starting export for artefact of type "+this.artefactType+ " ("+this.exportArtefacts.length+" artefacts)");

                let exportArtifacts = null;
                switch(this.artefactType) {
                  case constants.importExport.artefactTypes.base:
                    BaseRepositoryAPI.exportBase().then(response => {
                      exportArtifacts = response.data;
                      this.downloadArtifactsAsJSON(exportArtifacts);
                    })
                    break;
                  case constants.importExport.artefactTypes.datasets:
                    BaseRepositoryAPI.exportDatasets().then(response => {
                      exportArtifacts = response.data;
                      this.downloadArtifactsAsJSON(exportArtifacts);
                    })
                    break;

                  default:
                    toast.error("No export backend service exists for the artefact name "+this.artefactName, 5000);
                    return;
                }

            },
            downloadArtifactsAsJSON(exportArtifacts) {

              let file = new Blob([JSON.stringify({
                type: this.artefactType,
                data: exportArtifacts,
              }, null, 2)], {type: 'application/json'});

              let downloadLink = document.createElement('a');
              downloadLink.href = URL.createObjectURL(file);
              downloadLink.download = "baseMOD_" + this.artefactType + '_export_' + window.location.hostname + "_" + new Date().toISOString() + '.json';
              downloadLink.click();


              this.$emit("export-finished");
            },
            cancelImport() {
                this.resetImportRunning();
                this.showImportDialog = false
            },
            finishImport() {
                this.showImportDialog = false;
                this.showImportResult = false;
                this.importResult = {};
                this.importFile = {};
                this.$emit("import-finished");
                this.$router.go();
            },
        },
    }
</script>

<style scoped>

</style>
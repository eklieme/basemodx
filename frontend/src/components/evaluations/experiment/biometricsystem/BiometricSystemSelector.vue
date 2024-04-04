<template>
    <v-container fluid>
        <v-row>
            <v-col cols="12" lg="10" v-if="editMode">
                <div class="headline">Biometric Systems</div>
                <span>Based on the selected data sets one or more different biometric systems were built to evaluate a specific criteria. Please define your biometric system(s)
                </span>
            </v-col>
            <v-col lg="2">
                <v-dialog v-model="showBiometricSystemDialog" hide-overlay persistent>
                    <template v-slot:activator="{ on }">
                        <v-btn  v-if="editMode" text block slot="activator" color="info" v-on="on">Add Biometric System</v-btn>
                    </template>
                    <BiometricSystemEditor
                        :parent-base-name="parentBaseName"
                        v-on:biometric-system-saved="biometricSystemSaved"
                        v-on:close-model-dialog="showBiometricSystemDialog = false"
                        :biometric-system="biometricSystemToEdit"
                        :datasets="datasets"
                        :edit-mode="editMode">
                    </BiometricSystemEditor>
                </v-dialog>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12">
                <BiometricSystemBrowser
                    :biometric-systems-to-show="biometricSystemsToShowAndEdit"
                    :stand-alone="false"
                    :datasets="datasets"
                    :edit-mode="editMode"
                    v-on:delete-artifact="deleteBiometricSystem"
                    v-on:edit-artifact="editBiometricSystem"
                    v-on:duplicate-artifact="duplicateBiometricSystem">
                </BiometricSystemBrowser>
            </v-col>
        </v-row>

        <v-dialog v-model="showDependingDeleteDialog" persistent >
            <v-card>
                <v-card-title class="headline">Delete the biometric system {{dependingOnDelete.name}}?</v-card-title>
                <v-card-subtitle>The system you want to delete has further systems and possible results pending. If you delete the system
                they will be deleted, too!</v-card-subtitle>
                <v-card-text v-if="dependingOnDelete.systems.length>0">Systems: {{dependingOnDelete.systems.map(system => system.name).join(", ")}}</v-card-text>
                <v-card-text v-if="dependingOnDelete.systems.length>0">Results: {{dependingOnDelete.results.map(result => result.description).join(", ")}}</v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" text @click="showDependingDeleteDialog = false">Cancel</v-btn>
                    <v-btn color="error" text @click="deleteSystemAndDepending">OK</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>

    import BiometricSystemEditor from "./BiometricSystemEditor";
    import BiometricSystemBrowser from "./BiometricSystemBrowser";
    import {Util} from "../../../../helpers/util";
    import {BiometricSystemAPI} from "../../../../service/api/BiometricSystemAPI";
    import constants from "../../../../helpers/constants";
    import BaseComponent from "../../../base/BaseComponent";

    export default {
        components: {BiometricSystemBrowser, BiometricSystemEditor},
        name: "BiometricSystemSelector",
        extends: BaseComponent,
        props: {
            datasets: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            existingBiometricSystemsFromOutside: {
                type: Array,
                default: function() {
                    return [];
                },
            },
            editMode: {
                type: Boolean,
                default: true,
            },
            specificResultsBasedOnSystems: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            parentBaseName: {
                type: String,
                default: "",
            }
        },
        data() {
            return {
                biometricSystemsToShowAndEdit: [],
                showBiometricSystemDialog: false,
                indexBiometricSystemToEdit: -1,
                biometricSystemToEdit: {},
                biometricSystemEditMode: false,
                dependingOnDelete: {
                    name:"",
                    index: -1,
                    systems:[],
                    results: [],
                },
                showDependingDeleteDialog: false,
            }
        },
        computed: {


        },
        mounted: function() {
            if(!this.existingBiometricSystemsFromOutside || this.existingBiometricSystemsFromOutside.length===0) {
              this.$log.debug("...no biometric systems were provided from the outside...lets look whether there already existing" +
                  "some with the parent base '"+this.parentBaseName+"'");
              BiometricSystemAPI.getBiometricSystemsByParentBaseName(this.parentBaseName).then(response => {
                this.biometricSystemsToShowAndEdit = response.data;
                this.$log.debug("\t...found "+this.biometricSystemsToShowAndEdit.length+" biometric systems already");
                this.emitUpdatedSystemsMessage();
              })
            }
        },
        watch: {
            existingBiometricSystemsFromOutside: {
                deep: true,
                immediate: true,
                handler: function() {
                    if(this.existingBiometricSystemsFromOutside && this.existingBiometricSystemsFromOutside.length>0) {
                        this.$log.debug("Set biometric systems from outside in biometric system component", this.existingBiometricSystemsFromOutside);
                        this.biometricSystemsToShowAndEdit = this.existingBiometricSystemsFromOutside;
                    }
                }
            }
        },
        methods: {
            biometricSystemSaved: function(biometricSystem) {

                if(this.indexBiometricSystemToEdit!==-1) {
                    this.$log.debug("saving biometric system at index "+this.indexBiometricSystemToEdit+" with", biometricSystem);
                    this.biometricSystemsToShowAndEdit.splice(this.indexBiometricSystemToEdit,1,biometricSystem);
                    this.$log.debug("Current biometric systems to show in browser", this.biometricSystemsToShowAndEdit);
                    this.indexBiometricSystemToEdit = -1;
                } else {
                    this.$log.debug("...adding new biometric system", biometricSystem);
                    this.biometricSystemsToShowAndEdit.push(biometricSystem);
                }

                this.emitUpdatedSystemsMessage();
            },
            emitUpdatedSystemsMessage: function() {
                this.$emit("biometric-systems-saved", this.biometricSystemsToShowAndEdit);
            },
            deleteBiometricSystem: function(biometricSystem) {
                this.$log.debug("Request to delete biometric system");

                if(biometricSystem.fusionType === constants.fusionTypes.preparesScoreFusion.value
                    || biometricSystem.fusionType === constants.fusionTypes.preparesDecisionFusion.value) {

                    this.$log.debug("Biometric system prepares fusion, analyse for depending systems in", this.biometricSystemsToShowAndEdit);
                    // find potential fusion systems that use this system
                    const dependingSystems = this.biometricSystemsToShowAndEdit.filter(system => {
                        return system.fusedSystems.indexOf(biometricSystem.id)!==-1;
                    })

                    this.$log.debug("Found "+dependingSystems.length+" depending systems of system to delete");

                    if(dependingSystems.length>0) {

                        this.dependingOnDelete.systems = dependingSystems;

                        this.$log.debug("Depending system may be used for results, analyze ", this.specificResultsBasedOnSystems);

                        this.findDependingResults(dependingSystems);
                    }
                } else if(biometricSystem.fusionType === constants.fusionTypes.noFusion.value) {
                    this.dependingOnDelete.systems = [biometricSystem];

                    this.$log.debug("This system may be used for results, analyze ", this.specificResultsBasedOnSystems);

                    this.findDependingResults([biometricSystem]);
                }

                if(this.dependingOnDelete.systems.length===0) {
                    const index = this.biometricSystemsToShowAndEdit.indexOf(biometricSystem);
                    this.biometricSystemsToShowAndEdit.splice(index, 1);
                } else {
                    this.showDependingDeleteDialog = true;
                }
            },
            findDependingResults: function(dependingSystems) {
                // find potential results that are dependent
                let dependingResults = [];

                dependingSystems.forEach(dependingSystem => {
                    dependingResults = dependingResults.concat(this.specificResultsBasedOnSystems.filter(result => {
                        return result.biometricSystems.map(biosystem => biosystem.id).indexOf(dependingSystem.id)!==-1;
                    }));
                });

                this.$log.debug("Found "+dependingResults.length+" depending results of system to delete");
                if(dependingResults.length>0) {
                    this.dependingOnDelete.results = dependingResults;
                }
            },
            deleteSystemAndDepending: function() {
                this.showDependingDeleteDialog = false;

                // delete initial biometric system
                this.biometricSystemsToShowAndEdit.splice(this.dependingOnDelete.index, 1);

                // delete depending systems
                let indizesToDeleteSystems = []

                this.dependingOnDelete.systems.forEach(system => {
                    indizesToDeleteSystems.push(this.biometricSystemsToShowAndEdit.indexOf(system));
                });

                this.biometricSystemsToShowAndEdit = this.biometricSystemsToShowAndEdit.filter((system, index) => {
                   return indizesToDeleteSystems.indexOf(index) === -1;
                });

                this.emitUpdatedSystemsMessage();

                if(this.dependingOnDelete.results.length>0) {
                    let indizesToDeleteResults = []

                    // delete depending systems
                    this.dependingOnDelete.results.forEach(system => {
                        indizesToDeleteResults.push(this.specificResultsBasedOnSystems.indexOf(system));
                    });

                    this.$emit("result-indizes-to-delete", indizesToDeleteResults);
                }

            },
            duplicateBiometricSystem: function(biometricSystemToDuplicate) {
              this.$log.debug("...duplicate biometric system", biometricSystemToDuplicate);
              BiometricSystemAPI.getBiometricSystemById(biometricSystemToDuplicate.id).then(response => {

                let copyBiometricSystem = Util.deepCopyObject(response.data);
                delete copyBiometricSystem.id;
                copyBiometricSystem.name = biometricSystemToDuplicate.name+" 2";

                this.biometricSystemToEdit = copyBiometricSystem;
                this.showBiometricSystemDialog = true;

              });
            },
            editBiometricSystem: function(biometricSystem, allowChanges) {
                this.$log.debug("request to edit biometric system, start editor dialog");
                this.indexBiometricSystemToEdit = this.biometricSystemsToShowAndEdit.indexOf(biometricSystem);
                this.biometricSystemToEdit = this.biometricSystemsToShowAndEdit[this.indexBiometricSystemToEdit];
                this.showBiometricSystemDialog = true;
                this.biometricSystemEditMode = allowChanges;
            },
        }
    }
</script>

<style scoped>

</style>
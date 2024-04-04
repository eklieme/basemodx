<template>
    <v-card>
        <v-card-title primary-title>
            <div v-if="editMode">
                <div class="headline">New Biometric System's specifics</div>
            </div>
            <div v-else class="headline">Biometric System '{{biometricSystemToEdit.name}}' specifics</div>
        </v-card-title>
        <v-card-text>
            <v-container fluid grid-list-md>
                <v-row row>
                    <v-col cols="12" lg="12">
                        <div class="headline">General Information</div>
                    </v-col>
                </v-row>
                <v-row row>
                    <v-col cols="12" lg="4">
                        <v-text-field
                                label="Name"
                                hint="Choose a name to later connect your evaluation results to this system easier"
                                required
                                v-model="biometricSystemToEdit.name"
                                :rules=[biometricSystemWithNameforBaseUnique]
                                :disabled="!editMode"
                        ></v-text-field>
                    </v-col>
                    <v-col cols="12" lg="4">
                        <v-select
                                :items="allFusionTypes"
                                v-model="fusionType"
                                :menu-props="{ maxHeight: '400', closeOnContentClick: true }"
                                label="Select fusion type"
                                small-chips
                                :disabled="!editMode"
                                item-text="name"
                                item-value="value"
                                persistent-hint
                                return-object
                                v-on:change="fusionTypeChanged"
                        >
                        </v-select>
                    </v-col>
                    <v-col lg="4" v-if="isFusioningSystem">
                        <v-select
                                :items="biometricSystemsForFusionMode"
                                v-model="biometricSystemToEdit.fusedSystems"
                                :menu-props="{ maxHeight: '400', closeOnContentClick: true }"
                                label="Fused System's names"
                                multiple
                                small-chips
                                item-text="name"
                                item-value="viewId"
                                return-object
                                :disabled="!editMode"
                                :no-data-text="noDataTextFusedSystemsSelector">
                        </v-select>
                    </v-col>
                </v-row>
                <v-row v-if="showNonFusionProcessingStepSelection">
                    <v-col cols="12" lg="4">
                        <v-card flat>
                            <v-card-title>Data Capturing <v-icon>arrow_right</v-icon></v-card-title>
                            <v-btn color="primary" text @click="deviceEditorDialogOpen = true">Edit Device Category!</v-btn>
                            <v-card-text>
                                <v-treeview
                                        v-model="selectedTreeNodeIdsPossibleDataInputs"
                                        :items="possibleDataInputs"
                                        activatable
                                        open-on-click
                                        selectable
                                        transition
                                        :disabled="!editMode"
                                        v-if="editMode"
                                >
                                </v-treeview>
                                <div v-else
                                        key="title"
                                        class="title grey--text"
                                >
                                    {{sampleDeviceSummary}}
                                </div>
                            </v-card-text>
                        </v-card>
                    </v-col>
                    <v-col lg="8">
                        <v-card flat>
                            <v-card-title>
                                Signal Processing
                            </v-card-title>
                            <v-card-subtitle v-if="biometricSystemToEdit.signalProcessingSteps.length==0">At least one required</v-card-subtitle>
                            <v-card-text>
                                <v-select
                                        v-model="biometricSystemToEdit.signalProcessingSteps"
                                        :items="signalProcessingSteps"
                                        :label="'Select steps'"
                                        multiple
                                        small-chips
                                        :deletable-chips="editMode"
                                        return-object
                                        :clearable="editMode"
                                        :disabled="!editMode"
                                        item-text="name"
                                        item-value="viewId"
                                >
                                    <template v-slot:prepend-item>
                                        <v-list-item
                                                ripple
                                                @click="triggerProcessingStepOfType"
                                        >
                                            <v-list-item-content>
                                                <v-list-item-title>Create new Signal Processing Step</v-list-item-title>
                                            </v-list-item-content>
                                        </v-list-item>
                                        <v-divider class="mt-2"></v-divider>
                                    </template>
                                    <template v-slot:item = {item}>
                                        {{item.name}}&nbsp;
                                        <span>&nbsp;(type: {{item.processingType}})</span>
                                    </template>
                                    <template v-slot:selection="{ attrs, item, select, selected }">
                                        <v-chip
                                                v-bind="attrs"
                                                :input-value="selected"
                                                :close="editMode"
                                                @click="select"
                                                @click:close="removeProcessingStep(item)"
                                        >
                                            <v-avatar @click="editSignalProcessingStep(item)" v-if="editMode" left>
                                                <v-icon>edit</v-icon>
                                            </v-avatar>
                                            <strong>{{ item.name }}</strong>&nbsp;
                                            <span>&nbsp;({{item.processingType}})</span>
                                        </v-chip>
                                    </template>
                                </v-select>
                                <v-row align="center" justify="center" v-if="featureProcessingStepExists">
                                    <v-col cols="12" lg="6">
                                        <v-select
                                                :items="allFeatures"
                                                v-model="biometricSystemToEdit.features"
                                                small-chips
                                                clearable
                                                multiple
                                                :deletable-chips="editMode"
                                                :disabled="!editMode"
                                                return-object
                                                item-text="name"
                                                item-value="id"
                                                label="Please select at least one calculated feature"/>
                                    </v-col>
                                    <v-col lg="4">
                                        <v-text-field v-if="editMode"
                                                v-model="newFeatureName"
                                                label="Enter new feature name"
                                                :rules="[rules.newFeatureName]"
                                            />
                                    </v-col>
                                    <v-col lg="2" v-show="editMode">
                                        <v-btn text color="primary" :disabled="!addFeatureAllowed" @click="addNewFeature">Add</v-btn>
                                    </v-col>
                                </v-row>
                            </v-card-text>
                        </v-card>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col lg="4" v-if="showNonFusionProcessingStepSelection">
                        <v-card flat>
                            <v-card-title>
                                Template Creation / Matching
                            </v-card-title>
                            <v-card-subtitle v-if="!tempFurtherProcessingSteps.templateCreationMatching.length>0">At least one required</v-card-subtitle>
                            <v-card-text>
                                <v-select
                                        v-model="tempFurtherProcessingSteps.templateCreationMatching"
                                        :items="matchingSteps"
                                        :label="'Select steps'"
                                        multiple
                                        small-chips
                                        :deletable-chips="editMode"
                                        return-object
                                        :clearable="editMode"
                                        :disabled="!editMode"
                                        item-text="name"
                                        item-value="viewId"
                                >
                                    <template v-slot:prepend-item>
                                        <v-list-item
                                                ripple
                                                @click="triggerProcessingStepOfType(constants.processingsteptypes.matching)"
                                        >
                                            <v-list-item-content>
                                                <v-list-item-title>Create new matching Processing Step</v-list-item-title>
                                            </v-list-item-content>
                                        </v-list-item>
                                        <v-divider class="mt-2"></v-divider>
                                    </template>
                                </v-select>
                            </v-card-text>
                        </v-card>
                    </v-col>
                    <v-col lg="4">
                        <v-card flat>
                            <v-card-title>
                                Data Storage
                            </v-card-title>
                            <v-card-subtitle v-if="!tempFurtherProcessingSteps.storage.length>0 && showNonFusionProcessingStepSelection">At least one required</v-card-subtitle>
                            <v-card-subtitle v-if="!showNonFusionProcessingStepSelection">optional</v-card-subtitle>
                            <v-card-text>
                                <v-select
                                        v-model="tempFurtherProcessingSteps.storage"
                                        :items="storageSteps"
                                        :label="'Select steps'"
                                        multiple
                                        small-chips
                                        :deletable-chips="editMode"
                                        return-object
                                        :clearable="editMode"
                                        item-text="name"
                                        :disabled="!editMode"
                                        item-value="viewId"
                                >
                                    <template v-slot:prepend-item>
                                        <v-list-item
                                                ripple
                                                @click="triggerProcessingStepOfType(constants.processingsteptypes.storage)"
                                        >
                                            <v-list-item-content>
                                                <v-list-item-title>Create new storage Processing Step</v-list-item-title>
                                            </v-list-item-content>
                                        </v-list-item>
                                        <v-divider class="mt-2"></v-divider>
                                    </template>
                                </v-select>
                            </v-card-text>
                        </v-card>
                    </v-col>
                    <v-col lg="4" v-if="showDecisionStepSelection">
                        <v-card flat>
                            <v-card-title>
                                Decision
                            </v-card-title>
                            <v-card-subtitle v-if="!tempFurtherProcessingSteps.decision.length>0">At least one required</v-card-subtitle>
                            <v-card-text>
                                <v-select
                                        v-model="tempFurtherProcessingSteps.decision"
                                        :items="decisionSteps"
                                        :label="'Select steps'"
                                        multiple
                                        small-chips
                                        :deletable-chips="editMode"
                                        :clearable="editMode"
                                        return-object
                                        :disabled="!editMode"
                                        item-text="name"
                                        item-value="viewId"
                                >
                                    <template v-slot:prepend-item>
                                        <v-list-item
                                                ripple
                                                @click="triggerProcessingStepOfType(constants.processingsteptypes.decision)"
                                        >
                                            <v-list-item-content>
                                                <v-list-item-title>Create new decision Processing Step</v-list-item-title>
                                            </v-list-item-content>
                                        </v-list-item>
                                        <v-divider class="mt-2"></v-divider>
                                    </template>
                                    <template v-slot:item = {item}>
                                        {{item.name}} ({{item.decisionMode}})
                                    </template>
                                    <template v-slot:selection="{ attrs, item, select, selected }">
                                        <v-chip
                                                v-bind="attrs"
                                                :input-value="selected"
                                                :close="editMode"
                                                @click="select"
                                                @click:close="removeProcessingStep(item)"
                                        >
                                            <strong>{{ item.name }}</strong>&nbsp;
                                            <span>({{item.decisionMode}})</span>
                                        </v-chip>
                                    </template>
                                </v-select>
                            </v-card-text>
                        </v-card>
                    </v-col>
                </v-row>

                <v-row v-if="editMode">
                    <v-col lg="3" offset-lg="9">
                        <v-btn text color="primary"
                               @click="processingStepToEditDialog = true">Create New Processing Step</v-btn>
                    </v-col>
                </v-row>
                <v-dialog v-model="processingStepToEditDialog" persistent >
                  <ProcessingStepEditor
                      :feature-type-allowed="!featureProcessingStepExists"
                      :processing-step-for-editing="processingStepForEditing"
                      v-on:artifact-created-updated="newProcessingStepCreated"
                      v-on:close-dialog="cancelEditingNewProcessingStep"
                    />
                </v-dialog>
                <DeviceCategoryEditor
                    edit-mode
                    :existing-device-categories="allDeviceCategories"
                    :editor-shown="deviceEditorDialogOpen"
                    v-on:close-dialog="deviceEditorDialogOpen = false"
                    v-on:artifact-created-updated = "getAllSampleDevicesAndSensorsAndDimensions"/>
            </v-container>
        </v-card-text>
        <v-card-actions>
            <template v-if="editMode">
                <v-btn text
                       v-if="newBiometricSystem"
                       :disabled="!addBiometricSystemAllowed"
                       @click="addSaveBiometricSystem"
                       color="primary"
                    >Add Biometric System</v-btn>
                <v-btn text
                       :disabled="!addBiometricSystemAllowed"
                       @click="addSaveBiometricSystem"
                       v-if="!newBiometricSystem"
                       color="primary"
                    >Save Biometric System</v-btn>
                <v-btn color="error" text @click.stop="closeDialog">Cancel</v-btn>
            </template>
            <v-btn v-else color="error" text @click.stop="closeDialog">Close</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>

    import {BiometricSystemAPI} from "../../../../service/api/BiometricSystemAPI";
    import {DatasetAPI} from "../../../../service/api/DatasetAPI";
    import constants from "../../../../helpers/constants";
    import BaseComponent from "../../../base/BaseComponent";
    import {Util as Utils} from "../../../../helpers/util";
    import toast from "../../../../helpers/toast";
    import {SensorAPI} from "@/service/api/SensorAPI";
    import ProcessingStepEditor from "@/components/evaluations/experiment/biometricsystem/ProcessingStepEditor.vue";
    import DeviceCategoryEditor from "@/components/target_architecture/DeviceCategoryEditor.vue";
    import DeviceCategoryEditorMixin from "@/mixins/model/DeviceCategoryEditorMixin";

    export default {
        components: {DeviceCategoryEditor, ProcessingStepEditor},
        name: "BiometricSystemEditor",
        mixins: [DeviceCategoryEditorMixin],
        extends: BaseComponent,
        props: {
            datasets: {
                type: Array,
                default: function() {
                    return [];
                },
            },
            biometricSystem: {
                type: Object,
                default: function() {
                    return {};
                }
            },
            editMode: {
                type: Boolean,
                default: true,
            },
            parentBaseName: {
                type: String,
                default:"",
            }
        },
        data() {
            return {
                allSampleDevices: [],
                allSensors: [],
                allSensorDimensions: [],
                allBiometricSystemsOfThisBase:[],
                selectedTreeNodeIdsPossibleDataInputs: [],
                processingStepToEditDialog: false,
                tempFurtherProcessingSteps: {
                    templateCreationMatching:[],
                    storage:[],
                    decision:[],
                },
                biometricSystemToEdit: {
                    id:null,
                    parentBaseName: "",
                    name: "",
                    dataInputIds: [],
                    signalProcessingSteps: [],
                    furtherProcessingSteps: [],
                    features: [],
                    fusionType: "",
                    fusedSystems: [],
                },
                fusionType: {
                    name: constants.fusionTypes.noFusion.name,
                    value: constants.fusionTypes.noFusion.value,
                },
                possibleDataInputs: [],
                biometricSystemsForFusionMode: [],
                newBiometricSystem: true,
                allPossibleDataInputIds: [],
                allProcessingSteps: [],
                processingStepToEditIsNew: true,
                processingStepForEditing: {
                  id: null,
                  viewId:"",
                  reference: {},
                  type: "",
                  name: "",
                  processingType:"",
                  decisionMode: "",
                  isDeepLearning: false,
                  modelUrl: "",
                },
                allFeatures: [],
                indexProcessingStepToEdit: -1,
                newFeatureName: "",
                rules: {
                    newFeatureName: value => {
                        const indexFeature = this.allFeatures.findIndex(function(feature) {
                            return feature.name === value;
                        })

                        if(indexFeature>-1) {
                            return 'This feature already exist, please name a different one'
                        } else {
                            return true;
                        }
                    },
                },
                deviceCategoryToEdit: {},
                deviceEditorDialogOpen: false,
            }
        },
        computed: {
            sampleDeviceSummary() {
              if(this.allDeviceCategories && this.allDeviceCategories.length>0 && this.biometricSystemToEdit
                  && this.biometricSystemToEdit.dataInputIds && this.biometricSystemToEdit.dataInputIds.length>0) {

                    let usedDeviceCategorySensorsDims = {};

                    this.biometricSystemToEdit.dataInputIds.forEach(dataInputId => {

                        let deviceCategoryId = dataInputId.split("_")[1];

                        let deviceCategory = this.allDeviceCategories.find(deviceCategory => {
                          return deviceCategory.id === deviceCategoryId;
                        });

                        let sensor = this.allSensors.find(sensor => {
                          return sensor.sensorDimensionIds.indexOf(dataInputId.split("_")[0])>-1;
                        });

                        let dimension = this.allSensorDimensions.find(dimension => {
                            return dimension.id === dataInputId.split("_")[0];
                        })


                        let sensorDimsUsed = {};

                        if(usedDeviceCategorySensorsDims[deviceCategory.name]) {
                            sensorDimsUsed = usedDeviceCategorySensorsDims[deviceCategory.name];
                        }

                        let dimsUsed = new Set();

                        if(sensorDimsUsed[sensor.name]) {
                            dimsUsed = sensorDimsUsed[sensor.name];
                        }

                        dimsUsed.add(dimension.name);
                        sensorDimsUsed[sensor.name] = dimsUsed;

                        usedDeviceCategorySensorsDims[deviceCategory.name] = sensorDimsUsed;

                    });

                this.$log.debug("\t...dims, sensors, dims used by biometric system", usedDeviceCategorySensorsDims);

                let readableSummary = Object.keys(usedDeviceCategorySensorsDims).map(deviceCategory => {
                    return "'"+deviceCategory+"' using sensor(s) "+Object.keys(usedDeviceCategorySensorsDims[deviceCategory]).map(sensor => {
                        return "'"+sensor+"' with dimensions "+[...usedDeviceCategorySensorsDims[deviceCategory][sensor]].join(", ");
                    });
                }).join("; ");
                return "Data was captured with device(s) of categories "+readableSummary;

              }
              return "";
            },
            constants() {
              return constants;
            },
            processingStepReferenceAssetType() {
                return constants.referenceAssetType.processingStep;
            },
            addFeatureAllowed() {
                if(this.newFeatureName.length===0) {
                    return false;
                }
                return this.rules.newFeatureName(this.newFeatureName)===true;
            },
            featureProcessingStepExists() {
                if(!this.biometricSystemToEdit.signalProcessingSteps) {
                  return false;
                }
                return this.biometricSystemToEdit.signalProcessingSteps.some(step => {
                    return step.processingType === constants.processingsteptypes.feature;
                });
            },
            featuresValid() {
                if(this.featureProcessingStepExists) {
                    if(this.biometricSystemToEdit.features.length>0) {
                        return true;
                    }
                    return false;
                }
                return true;
            },
            noDataTextFusedSystemsSelector() {
                return 'Please define at least two biometric systems that prepare the respective fusion';
            },
            isFusioningSystem() {
                return constants.fusionTypes.doesFusion(this.fusionType);
            },
            showNonFusionProcessingStepSelection() {
                if(this.isFusioningSystem) {
                    return false;
                }
                return true;
            },
            showDecisionStepSelection() {
                if(this.fusionType.value === constants.fusionTypes.preparesScoreFusion.value) {
                    return false;
                }
                return true;
            },
            allFusionTypes() {
                return constants.fusionTypes.getAll();
            },
            addBiometricSystemAllowed() {

                this.$log.debug("verify whether system can be saved / added", "fusionType", this.fusionType.value);
                this.$log.debug("\t system", this.biometricSystemToEdit);

                const nameAvailable = this.biometricSystemToEdit.name.length > 0
                        && this.biometricSystemWithNameforBaseUnique();

                const inputsAvailable = this.selectedTreeNodeIdsPossibleDataInputs.length > 0;
                const signalProcessingAvailable = this.biometricSystemToEdit.signalProcessingSteps.length>0;
                const featuresValid = this.featuresValid;

                const matchingAvailable = this.tempFurtherProcessingSteps.templateCreationMatching.length>0;
                const decisionAvailable = this.tempFurtherProcessingSteps.decision.length>0;
                const storageAvailable = this.tempFurtherProcessingSteps.storage.length>0;

                const atLeastTwoFusedSystems = this.biometricSystemToEdit.fusedSystems.length > 1;

                this.$log.debug("\tName available: ", nameAvailable);
                this.$log.debug("\tinputs available: ", inputsAvailable);
                this.$log.debug("\tsignal processing available: ", signalProcessingAvailable);
                this.$log.debug("\tmatching available: ", matchingAvailable);
                this.$log.debug("\tdecision available: ", decisionAvailable);
                this.$log.debug("\tstorage available: ", storageAvailable);
                this.$log.debug("\tat least two systems for fusion: ", atLeastTwoFusedSystems);
                // do fusion type based decision if adding the system is allowed
                switch(this.fusionType.value) {

                    case constants.fusionTypes.noFusion.value:

                        return nameAvailable &&
                            inputsAvailable &&
                            signalProcessingAvailable &&
                            featuresValid &&
                            decisionAvailable && storageAvailable && matchingAvailable;

                    case constants.fusionTypes.preparesScoreFusion.value:

                        return nameAvailable &&
                            inputsAvailable &&
                            signalProcessingAvailable &&
                            featuresValid &&
                            storageAvailable && matchingAvailable;

                    case constants.fusionTypes.scoreFusion.value:

                        return nameAvailable && atLeastTwoFusedSystems && decisionAvailable;

                    case constants.fusionTypes.preparesDecisionFusion.value:

                        return nameAvailable &&
                            inputsAvailable &&
                            signalProcessingAvailable &&
                            featuresValid &&
                            decisionAvailable && storageAvailable && matchingAvailable;

                    case constants.fusionTypes.decisionFusion.value:

                        return nameAvailable && atLeastTwoFusedSystems && decisionAvailable;
                }

                return false;

            },
            processingTypes() {
                return constants.processingsteptypes.getAll();
            },
            signalProcessingSteps() {
                const processingTypes = [constants.processingsteptypes.windowing,
                                        constants.processingsteptypes.feature,
                                        constants.processingsteptypes.transformer,
                                        constants.processingsteptypes.filter];

                let signalProcessingSteps = this.allProcessingSteps
                    .filter(processingStep => processingTypes.indexOf(processingStep.processingType)!==-1).map(step => {
                        return {
                            viewId: step.name+"-"+step.processingType,
                            name: step.name,
                            id: step.id,
                            processingType: step.processingType
                        }
                    });

                this.$log.debug("\t...available signal processing steps: ", signalProcessingSteps);

                return signalProcessingSteps;
            },
            matchingSteps() {
                const processingTypes = [constants.processingsteptypes.matching];

                return this.allProcessingSteps
                    .filter(processingStep => processingTypes.indexOf(processingStep.processingType)!==-1)
                    .map(step => {return {
                        viewId: step.name+"-"+step.processingType,
                        name: step.name,
                        id: step.id,
                        processingType: step.processingType,
                    }});
            },
            storageSteps() {
                const processingTypes = [constants.processingsteptypes.storage];

                return this.allProcessingSteps
                    .filter(processingStep => processingTypes.indexOf(processingStep.processingType)!==-1)
                    .map(step => {return {
                        viewId: step.name+"-"+step.processingType,
                        name: step.name,
                        id: step.id,
                        processingType: step.processingType,
                    }});
            },
            decisionSteps() {
                const processingTypes = [constants.processingsteptypes.decision];

                return this.allProcessingSteps
                    .filter(processingStep => processingTypes.indexOf(processingStep.processingType)!==-1)
                    .map(step => {return {
                        viewId: step.name+"-"+step.processingType,
                        name: step.name,
                        id: step.id,
                        decisionMode: step.decisionMode,
                        processingType: step.processingType,
                    }});
            },
            selectedSensorDimensionInputs: function() {
                this.$log.debug("Filter all selected nodes ", this.selectedTreeNodeIdsPossibleDataInputs);
                this.$log.debug("\twhether they are a possible input", this.allPossibleDataInputIds);
                return this.selectedTreeNodeIdsPossibleDataInputs.filter(dataInputId => {
                    return this.allPossibleDataInputIds.indexOf(dataInputId.split("_")[0])>-1;
                });
            },

        },
        mounted: function() {

            //query initial data
            this.getAllProcessingSteps(true).then(response => {
                this.allProcessingSteps = response.data;
                //see if we were called with a system from outside to directly view
                this.evaluateBiometricSystemDetailsFromOutside();

            });
            this.getAllFeatures();
            this.getAllSampleDevicesAndSensorsAndDimensions();
            this.getAllBiometricSystemsOfThisBase();


        },
        watch: {
            biometricSystem: {
                immediate: true,
                deep: true,
                handler: function() {
                    this.evaluateBiometricSystemDetailsFromOutside();
                }
            },
            datasets: {
                immediate: true,
                deep: true,
                handler: function(datasets) {
                    this.$log.debug("set datasets from outside", datasets);
                }
            },
            tempFurtherProcessingSteps: {
                immediate: true,
                deep: true,
                handler: function(newFurtherSteps) {
                    this.biometricSystemToEdit.furtherProcessingSteps = newFurtherSteps.storage
                        .concat(newFurtherSteps.decision, newFurtherSteps.templateCreationMatching);
                    this.$log.debug("Updated system's further steps to", this.biometricSystemToEdit.furtherProcessingSteps);
                }
            },
            fusionType: {
                deep: true,
                immediate: true,
                handler: function(newFusionType) {

                    this.$log.debug("analysing which systems are usable for fusion mode ", newFusionType.value, this.allBiometricSystemsOfThisBase);

                    if(constants.fusionTypes.doesFusion(newFusionType)) {
                        if(this.parentBaseName.length>0) {
                          this.$log.debug("..update all biometric systems for this base", this.parentBaseName);
                          BiometricSystemAPI.getBiometricSystemsByParentBaseName(this.parentBaseName).then(response => {
                            this.allBiometricSystemsOfThisBase = response.data.map(system => {
                              system["viewId"] = system.name+"-"+system.parentBaseName;
                              return system;
                            });
                            this.$log.debug("...found systems: ", this.allBiometricSystemsOfThisBase.length);
                            if(newFusionType.value === constants.fusionTypes.decisionFusion.value) {
                              this.biometricSystemsForFusionMode = this.allBiometricSystemsOfThisBase.filter(biometricSystem => {
                                return biometricSystem.fusionType===constants.fusionTypes.preparesDecisionFusion.value;
                              })
                            } else {
                              this.biometricSystemsForFusionMode = this.allBiometricSystemsOfThisBase.filter(biometricSystem => {
                                return biometricSystem.fusionType===constants.fusionTypes.preparesScoreFusion.value;
                              });
                            }


                            this.$log.debug("Systems available for fusion", this.biometricSystemsForFusionMode);
                          });
                        }
                    }


                }
            }
        },
        methods: {
            biometricSystemWithNameforBaseUnique() {
              if(this.newBiometricSystem) {
                if (this.biometricSystemToEdit.name && this.biometricSystemToEdit.name.length > 0) {
                  if (this.allBiometricSystemsOfThisBase.filter(biometricSystem => {
                    return biometricSystem.name === this.biometricSystemToEdit.name;
                  }).length > 0) {
                    return 'This name is already given for base '+this.parentBaseName+', please choose a different one'
                  }
                }
              }
              return true;
            },
            addNewFeature: function() {
                BiometricSystemAPI.createUpdateFeature( {name: this.newFeatureName}).then(()=> {
                    toast.success("Successfully added new feature '"+this.newFeatureName+"'");
                    BiometricSystemAPI.getFeatures().then((response) => {
                      this.allFeatures = response.data;
                      this.biometricSystemToEdit.features.push(this.allFeatures[this.allFeatures.findIndex(feature => {
                        return feature.name === this.newFeatureName
                      })])
                      this.newFeatureName="";
                    });
                });
            },
            triggerDeviceCategoryEditor: function(deviceCategoryTreeViewItem) {
                this.$log.debug("\t...trigger device category editor from biometric system editor", deviceCategoryTreeViewItem);
                this.deviceCategoryToEdit = this.allDeviceCategories.find(deviceCategory => {
                    return deviceCategory.id === deviceCategoryTreeViewItem.id;
                });
            },
            cancelEditingNewProcessingStep: function() {
                this.processingStepToEditDialog = false;
            },
            determinePossibleDataInputs() {


              this.$log.debug("Create Inputs to pick based on datasets available", this.datasets);
              const possibleInputs = this.datasets.map(dataset => {

                let allDeviceCategoriesUsedIds = [];
                dataset.sampledBiometrics.forEach(sampledBiometric => {
                  sampledBiometric.sampleDevices.forEach(sampleDevice => {
                      allDeviceCategoriesUsedIds.push(sampleDevice.deviceCategory.id);
                  })
                })


                let setOfPossibleDeviceCategoryIds =  [...new Set(allDeviceCategoriesUsedIds)];

                this.$log.debug("\t..based on possible device category ids", setOfPossibleDeviceCategoryIds);
                return {

                  id: dataset.id,
                  name: dataset.name,
                  children: this.allDeviceCategories.filter(function(deviceCategory) {
                    return setOfPossibleDeviceCategoryIds.indexOf(deviceCategory.id)>-1
                  }).map(deviceCategory => {
                    return {
                      id: deviceCategory.id,
                      name: deviceCategory.name,
                      processingType: "dc",
                      children: deviceCategory.sensors.map(sensor => {
                        let sensorName = sensor.name;
                        if (sensor.continuous) {
                          sensorName += ", (continuous)"
                        }
                        return {
                          id: sensor.id,
                          name: sensorName,
                          processingType: "s",
                          children: sensor.sensorDimensions.map(dimension => {
                            return {
                              id: dimension.id+"_"+deviceCategory.id,
                              name: dimension.name,
                              processingType: "d",
                            }
                          })
                        }
                      })
                    }
                  })
                }
              });

              this.$log.debug("Tree view data ", possibleInputs);

              this.possibleDataInputs = possibleInputs;
            },
            editSignalProcessingStep:function(processingStepToEdit) {
                this.$log.debug("request to edit processing step", processingStepToEdit);
                this.indexProcessingStepToEdit = this.biometricSystemToEdit.signalProcessingSteps.indexOf(processingStepToEdit);
                this.processingStepForEditing = processingStepToEdit;
                if(this.processingStepForEditing.processingType === constants.processingsteptypes.feature) {
                    this.biometricSystemToEdit.features = this.biometricSystemToEdit.map(feature => feature.name);
                }
                this.$log.debug("set processing step to edit", this.processingStepToEdit);
                this.processingStepForEditing = false,
                this.processingStepToEditDialog = true;
            },
            triggerProcessingStepOfType: function(processingStepType) {
                if(processingStepType) {
                  this.processingStepForEditing.processingType = processingStepType;
                }
                this.processingStepToEditDialog = true;
            },
            removeProcessingStep(item) {
                this.$log.debug("request to delete processing step", item);
                switch(item.processingType) {
                    case constants.processingsteptypes.matching:
                        this.tempFurtherProcessingSteps.templateCreationMatching.splice(this.tempFurtherProcessingSteps.templateCreationMatching.indexOf(item), 1)
                        this.tempFurtherProcessingSteps.templateCreationMatching = [...this.tempFurtherProcessingSteps.templateCreationMatching]
                        this.$log.debug("matching steps afterwards: ", this.tempFurtherProcessingSteps.templateCreationMatching);
                        break;
                    case constants.processingsteptypes.storage:
                        this.tempFurtherProcessingSteps.storage.splice(this.tempFurtherProcessingSteps.storage.indexOf(item), 1)
                        this.tempFurtherProcessingSteps.storage = [...this.tempFurtherProcessingSteps.storage]
                        this.$log.debug("storage steps afterwards: ", this.tempFurtherProcessingSteps.storage);
                        break;
                    case constants.processingsteptypes.decision:
                        this.tempFurtherProcessingSteps.decision.splice(this.tempFurtherProcessingSteps.decision.indexOf(item), 1)
                        this.tempFurtherProcessingSteps.decision = [...this.tempFurtherProcessingSteps.decision]
                        this.$log.debug("decision steps afterwards: ", this.tempFurtherProcessingSteps.decision);
                        break;
                    default:
                        this.biometricSystemToEdit.signalProcessingSteps.splice(this.biometricSystemToEdit.signalProcessingSteps.indexOf(item), 1)
                        this.biometricSystemToEdit.signalProcessingSteps = [...this.biometricSystemToEdit.signalProcessingSteps]
                        this.$log.debug("signal processing steps afterwards: ", this.biometricSystemToEdit.signalProcessingSteps);
                }
            },
            newProcessingStepCreated: function({step, features, addToChain}) {

                this.$log.debug("\t...new processing step created, features, add to chain", step, features, addToChain);

                this.processingStepToEditDialog = false;

                this.getAllProcessingSteps(true).then((response) => {

                    this.allProcessingSteps = response.data;

                    if(addToChain) {
                        step.viewId = step.name+"-"+step.processingType;
                        this.$log.debug("directly add new step to chain!")
                        switch (step.processingType) {
                            case constants.processingsteptypes.matching:
                                this.tempFurtherProcessingSteps.templateCreationMatching.push(step);
                                break;
                            case constants.processingsteptypes.feature:
                              this.biometricSystemToEdit.features = Utils.deepCopyObject(features);
                              break;
                            case constants.processingsteptypes.storage:
                                this.tempFurtherProcessingSteps.storage.push(step);
                                break;
                            case constants.processingsteptypes.decision:
                                this.tempFurtherProcessingSteps.decision.push(step);
                                break;
                            default:
                                this.biometricSystemToEdit.signalProcessingSteps.push(step);
                        }
                    }
                });

            },
            closeDialog: function() {
                this.$emit(constants.eventNames.local.closeModelDialog);
            },
            evaluateBiometricSystemDetailsFromOutside: function() {

                this.$log.debug("request to set biometric system to edit from outside", this.biometricSystem);
                // set biometric system to edit in case we get one from outside
                if(this.biometricSystem.name) {

                    this.$log.debug("\t...is valid, setting biometric system to edit from outside", this.biometricSystem);
                    this.biometricSystemToEdit = Utils.deepCopyObject(this.biometricSystem);

                    if(this.biometricSystem.furtherProcessingSteps) {
                      // set specific lists based on further processing
                      this.tempFurtherProcessingSteps.templateCreationMatching = this.biometricSystem.furtherProcessingSteps.filter((step) => {
                        return step.processingType === constants.processingsteptypes.matching;
                      }).map(step => {
                        return {
                          id: step.id,
                          viewId: step.name + "-" + step.processingType,
                          name: step.name,
                          processingType: step.processingType
                        }
                      });

                      this.tempFurtherProcessingSteps.storage = this.biometricSystemToEdit.furtherProcessingSteps.filter((step) => {
                        return step.processingType === constants.processingsteptypes.storage;
                      }).map(step => {
                        return {
                          id: step.id,
                          viewId: step.name + "-" + step.processingType,
                          name: step.name,
                          processingType: step.processingType
                        }
                      });

                      this.tempFurtherProcessingSteps.decision = this.biometricSystemToEdit.furtherProcessingSteps.filter((step) => {
                        return step.processingType === constants.processingsteptypes.decision;
                      }).map(step => {
                        return {
                          id: step.id,
                          viewId: step.name + "-" + step.processingType,
                          name: step.name,
                          decisionMode: step.decisionMode,
                          processingType: step.processingType
                        }
                      });
                    }

                    if(this.biometricSystem.signalProcessingSteps) {
                      // ids for signal processing steps
                      this.biometricSystemToEdit.signalProcessingSteps = this.biometricSystemToEdit.signalProcessingSteps.map(step => {
                        return {
                          id: step.id,
                          viewId: step.name + "-" + step.processingType,
                          name: step.name,
                          processingType: step.processingType
                        }
                      });

                      this.$log.debug("Set further processing steps for editor", this.tempFurtherProcessingSteps);
                    }

                    // reset fusion type with name and set parent base name before
                    this.fusionType = {
                        name: constants.fusionTypes.getFusionTypeName(this.biometricSystemToEdit.fusionType),
                        value: this.biometricSystemToEdit.fusionType,
                    }

                    // reload full fused systems

                    BiometricSystemAPI.getBiometricSystemsByParentBaseName(this.biometricSystemToEdit.parentBaseName).then(response => {

                      this.allBiometricSystemsOfThisBase = response.data.map(system => {
                        system["viewId"] = system.name+"-"+system.parentBaseName;
                        return system;
                      });
                      this.biometricSystemToEdit.fusedSystems = this.allBiometricSystemsOfThisBase.filter(system => {
                        return this.biometricSystemToEdit.fusedSystems.indexOf(system.id) !== -1;
                      });

                      this.$log.debug("\t...set fused systems to", this.biometricSystemToEdit.fusedSystems);

                    });

                    this.newBiometricSystem = false;

                    const that = this;

                    //fill data input tree



                    that.selectedTreeNodeIdsPossibleDataInputs = this.biometricSystemToEdit.dataInputIds;
                    that.$log.debug("selected treenode ids: ", that.selectedTreeNodeIdsPossibleDataInputs);

                    this.$log.debug("\tbiometric system to edit (based from outside)", this.biometricSystemToEdit);
                }
            },
            fusionTypeChanged: function() {
                // reset any selected system fusion Ids
                this.biometricSystemToEdit.fusedSystems = [];
            },
            getAllSampleDevicesAndSensorsAndDimensions: function() {
                DatasetAPI.getSampleDevices().then((response) => {
                    this.allSampleDevices = response.data;
                    this.allSensors = this.allSampleDevices.map(sampleDevice => sampleDevice.deviceCategory.sensors).flat();
                    this.getDeviceCategoriesPromise().then(response => {
                        this.allDeviceCategories = response.data;
                        this.determinePossibleDataInputs();
                    })

                });
                SensorAPI.getSensorDimensions().then((response) => {
                   this.allPossibleDataInputIds = response.data.map(sensorDimension => sensorDimension.id);
                   this.allSensorDimensions = response.data;
                   this.$log.debug("retrieved possible Data Input Ids", this.allPossibleDataInputIds);
                });
            },
            getAllBiometricSystemsOfThisBase: function() {
              if(this.parentBaseName.length>0) {
                this.$log.debug("..update all biometric systems for this base", this.parentBaseName);
                BiometricSystemAPI.getBiometricSystemsByParentBaseName(this.parentBaseName).then(response => {
                  this.allBiometricSystemsOfThisBase = response.data;
                  this.$log.debug("...found systems: ", this.allBiometricSystemsOfThisBase.length);
                });
              }
            },
            getAllProcessingSteps: function(returnPromise) {
                if(returnPromise) {
                    return BiometricSystemAPI.getProcessingSteps();
                }
                BiometricSystemAPI.getProcessingSteps().then((response) => {
                    this.allProcessingSteps = response.data;
                    this.$log.debug("All processing steps", this.allProcessingSteps);
                });
            },
            prepareCreateUpdateOfSystem: function() {

                // set parentBaseName
                this.biometricSystemToEdit.parentBaseName = this.parentBaseName;

                this.$log.debug("name of parent base: ",this.biometricSystemToEdit.parentBaseName);

                // set current data inputs
                this.biometricSystemToEdit.dataInputIds = this.selectedSensorDimensionInputs;

                // change to fit enumeration of server side
                this.biometricSystemToEdit.fusionType = this.fusionType.value;


                // set types of processing steps
                this.biometricSystemToEdit.signalProcessingSteps.forEach((step) => {
                  step["type"] = constants.processingsteptypes.simple;
                });

                this.biometricSystemToEdit.furtherProcessingSteps.forEach((step) => {
                  if(step.processingType===constants.processingsteptypes.decision) {
                    step["type"] = constants.processingsteptypes.decision;
                  } else {
                    step["type"] = constants.processingsteptypes.simple;
                  }
                });

                if(this.biometricSystemToEdit.fusionType===constants.fusionTypes.decisionFusion.value) {
                  this.$log.debug("\t...remove all the other stuff like features because decision fusion etc.");
                  this.biometricSystemToEdit.features = [];
                  this.biometricSystemToEdit.signalProcessingSteps = [];
                  this.biometricSystemToEdit.furtherProcessingSteps = this.biometricSystemToEdit.furtherProcessingSteps.filter(processingStep => {
                    return processingStep.type === constants.processingsteptypes.decision;
                  });
                  this.biometricSystemToEdit.dataInputIds = [];
                }

                let biometricSystemToCreateUpdate = Utils.deepCopyObject(this.biometricSystemToEdit);

                // only persist ids
                biometricSystemToCreateUpdate["signalProcessingStepIds"] =
                    biometricSystemToCreateUpdate.signalProcessingSteps.map(processingStep => processingStep.id);
                delete biometricSystemToCreateUpdate.signalProcessingSteps;

                biometricSystemToCreateUpdate["furtherProcessingStepIds"] =
                    biometricSystemToCreateUpdate.furtherProcessingSteps.map(processingStep => processingStep.id);
                delete biometricSystemToCreateUpdate.furtherProcessingSteps;

                biometricSystemToCreateUpdate["featureIds"] =
                    biometricSystemToCreateUpdate.features.map(feature => feature.id);
                delete biometricSystemToCreateUpdate.features;

                biometricSystemToCreateUpdate["fusedSystemIds"] = this.biometricSystemToEdit.fusedSystems.map(system => system.id);
                delete biometricSystemToCreateUpdate.fusedSystems;

                return biometricSystemToCreateUpdate;

            },
            addSaveBiometricSystem: function() {

                let biometricSystemToCreateUpdate = this.prepareCreateUpdateOfSystem();

                BiometricSystemAPI.createUpdateBiometricSystem(
                    biometricSystemToCreateUpdate).then(
                    (response) => {
                        if(!this.biometricSystemToEdit.id) {
                            this.$log.debug("biometric system id is not set, set id based on response ", response);
                            this.biometricSystemToEdit["id"] = response.data;
                        }
                        this.$emit("biometric-system-saved", this.biometricSystemToEdit);
                        this.closeDialog();
                        this.resetBiometricSystem();
                    }
                );
            },
            resetBiometricSystem: function() {
                this.selectedTreeNodeIdsPossibleDataInputs = [];

                this.biometricSystemToEdit =  {
                    id: null,
                    parentBaseName: "",
                    name: "",
                    dataInputIds: [],
                    signalProcessingSteps: [],
                    furtherProcessingSteps: [],
                    features:[],
                    fusionType: constants.fusionTypes.noFusion,
                    fusionTechnique: {},
                    fusedSystems: [],
                };
                this.tempFurtherProcessingSteps = {
                    storage: [],
                    templateCreationMatching: [],
                    decision: [],
                };
                this.fusionType = {
                    name: constants.fusionTypes.noFusion.name,
                    value: constants.fusionTypes.noFusion.value,
                };
            },
            getAllFeatures: function() {
                BiometricSystemAPI.getFeatures().then((response) => {
                    this.allFeatures = response.data;
                });
            },
        }
    }
</script>

<style scoped>

</style>
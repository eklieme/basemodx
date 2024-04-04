<template>
    <v-card>
        <v-card-title>
            <span v-if="editMode" class="headline">Edit Sample Device</span>
            <span v-else class="headline">Add New Sample Device</span>
        </v-card-title>
        <v-card-text>
            <v-container fluid>
                <v-row wrap>
                    <v-col cols="12" lg="12">
                        <span class="title font-weight-light grey--text">Sample Device Specifics</span>
                    </v-col>
                    <v-col cols="12" lg="3">
                        <SelectAutoAddComboBox
                                :all-entities="allDeviceManufacturers"
                                :entity-name="'Manufacturer'"
                                :label = "'Select existing manufacturer or enter new one'"
                                :pre-selected-entity="sampleDevice.deviceManufacturer"
                                :deletable-chips="true"
                                v-on:entity-list-changed="manufacturerChanged">

                        </SelectAutoAddComboBox>
                    </v-col>
                    <v-col cols="12" lg="3">
                      <v-text-field
                          label="Model name"
                          :disabled="sampleDevice.deviceManufacturer.length===0"
                          v-model="sampleDevice.name"
                          :rules="[newSampleDeviceRules.nameUnique, newSampleDeviceRules.nameNotEmpty]"
                      />
                    </v-col>
                    <v-col cols="12" lg="3">
                        <v-select
                          label="Device Category"
                          :disabled="sampleDevice.deviceManufacturer.length===0 || sampleDevice.name.length===0"
                          :items="allDeviceCategories"
                          v-model="sampleDevice.deviceCategory"
                          return-object
                          item-text="name"
                        />
                    </v-col>
                    <v-col lg="3">
                      <h6 v-if="!standAlone">Please note that your <b>target architecture</b> will be modified once you a category and select
                      a sampling device from that category.</h6>
                      <v-btn color="primary" text @click="triggerDeviceCategoryCreation">Add Device Category</v-btn>
                        <v-btn color="primary" text @click="triggerDeviceCategoryEditing">Edit Device Category!</v-btn>
                    </v-col>
                </v-row>
            </v-container>
          <DeviceCategoryEditor
              :edit-mode="editDeviceCategory"
            :existing-device-categories="allDeviceCategories"
            :editor-shown="deviceEditorDialogOpen"
            v-on:close-dialog="deviceEditorDialogOpen = false"
            v-on:artifact-added="deviceCategoryAdded"/>
        </v-card-text>
        <v-card-actions>
            <v-btn color="primary" text @click="saveSampleDevice()" :disabled="!saveSampleDeviceAllowed">{{editMode ? 'Save Device' : 'Add New Device'}}</v-btn>
            <v-btn v-if="!standAlone" color="error" text @click="$emit(closeDialogEventName)">Cancel</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>

    import {DatasetAPI} from "../../../../service/api/DatasetAPI";
    import SelectAutoAddComboBox from "../../../util/SelectAutoAddComboBox";
    import constants from "../../../../helpers/constants";
    import toast from "../../../../helpers/toast";
    import DeviceCategoryEditor from "@/components/target_architecture/DeviceCategoryEditor.vue";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import DeviceCategoryEditorMixin from "@/mixins/model/DeviceCategoryEditorMixin";

    export default {
        components: {DeviceCategoryEditor, SelectAutoAddComboBox},
        name: "SampleDeviceEditor",
        mixins: [LoggedInUserMixin, DeviceCategoryEditorMixin],
        props: {
            sampleDeviceToEdit: {
                type: Object,
                default: function() {
                  return {};
                }
            },
            standAlone: {
                type: Boolean,
                default: false,
            }
        },
        data() {
            return {
                allSampleDevices: [],
                allDeviceManufacturers: [],
                sampleDevice: {
                    id: null,
                    deviceCategory: {},
                    name:"",
                    deviceManufacturer: "",
                    modelledElementDetail: {},
                },
                sensorToEdit: {
                    name:"",
                    continuous: false,
                    dimensions:[],
                },
                sensorsToCopy: [],
                sensorsCopied: false,
                sensorEditMode:false,
                sensorIndexToEdit: -1,
                newDimension:"",
                allModelsForManufacturer: [],
                sensorHeaders: [
                    { text: 'Name', value: 'name' },
                    { text: 'Dimensions', value: 'dimensions'},
                    { text: 'Continuous', value: 'continuous'},
                ],
                editMode:false,
                newSampleDeviceRules: {
                  nameUnique: () => this.sampleDeviceNameNotYetExisting || 'A sample device with that name from the manufacturer already exists',
                  nameNotEmpty: () => this.sampleDeviceNameNotEmpty || 'Please enter a sample device name',
                },
            }
        },
        beforeRouteEnter (to, from, next) {
            if(to.path === constants.routes.modelSampleDevices.path) {
                to.params["standAlone"] = true;
            }

            next();
        },
        mounted: function() {
            this.getAllSampleDeviceDetails();
        },
        computed: {
            closeDialogEventName: function() {
               return constants.eventNames.local.closeModelDialog;
            },
            saveSampleDeviceAllowed: function() {
                if(this.sampleDevice.deviceCategory && this.sampleDevice.deviceCategory.name) {
                  return this.sampleDeviceNameNotEmpty
                      && this.sampleDeviceNameNotYetExisting
                      && this.sampleDevice.deviceCategory.name.length > 0
                      && this.sampleDevice.deviceManufacturer.length > 0
                }
                return false;
            },
            sampleDeviceNameNotYetExisting: function() {
              if(this.editMode) {
                if(this.sampleDevice.name === this.sampleDeviceToEdit.name) {
                  return true;
                }
              }
              return this.allSampleDevices.filter(sampleDevice => {
                return sampleDevice.deviceManufacturer.toLowerCase()
                    ===this.sampleDevice.deviceManufacturer.toLowerCase()
                    && sampleDevice.name.toLowerCase()
                    ===this.sampleDevice.name.toLowerCase();
              }).length===0;
            },
            sampleDeviceNameNotEmpty: function() {
              return this.sampleDevice.name.length!==0;
            },
        },
        watch: {
            sampleDeviceToEdit: {
              immediate: true,
              deep: true,
              handler: function(sampleDeviceToEdit) {
                if(sampleDeviceToEdit && sampleDeviceToEdit.name && sampleDeviceToEdit.name.length>0) {
                  this.$log.debug("Set sample device to edit from outside", sampleDeviceToEdit);
                  this.sampleDevice.id = sampleDeviceToEdit.id;
                  this.sampleDevice.deviceCategory = sampleDeviceToEdit.deviceCategory;
                  this.sampleDevice.name = sampleDeviceToEdit.name;
                  this.sampleDevice.deviceManufacturer = sampleDeviceToEdit.deviceManufacturer;
                  this.sampleDevice.modelledElementDetail = sampleDeviceToEdit.modelledElementDetail;
                  this.editMode = true;
                }
              }
            }
        },
        methods: {
            getAllSampleDeviceDetails: function() {
                DatasetAPI.getSampleDevices().then((response) => {
                    this.allSampleDevices = response.data;
                    this.allDeviceManufacturers = [...new Set(response.data.map(x => x.deviceManufacturer))];
                    this.getDeviceCategories();
                });
            },
            deviceCategoryAdded() {
              this.getAllSampleDeviceDetails();
              this.deviceEditorDialogOpen = false;
            },
            saveSampleDevice: function() {

                this.sampleDevice.deviceCategoryId = this.sampleDevice.deviceCategory.id;

                if(!this.sampleDevice.id) {
                  this.setInitialModelLifecycleStateForDomainElement(this.sampleDevice);
                }
                DatasetAPI.createUpdateSampleDevice(this.sampleDevice).then(response => {

                    this.$log.debug("Persisted new Sample device, id="+response.data, response);

                    if(!this.standAlone) {

                        this.$log.debug("\tEmitting event to close dialog");
                        this.$emit(constants.eventNames.local.closeModelDialog);

                        this.$log.debug("\tEmitting event to signal new device added");
                        this.$emit("sample-device-added");

                    } else {
                      this.$log.debug("\tEmitting event to signal sample device updated");
                      this.$emit(constants.eventNames.local.artifactCreatedUpdated);
                    }

                    toast.success("Device successfully " + (this.editMode ? "saved" : "added"));

                    // reset sample device
                    this.sampleDevice = {
                        deviceCategoryId : "",
                        deviceCategory: {
                            name: "",
                            id: null,
                        },
                        name: "",
                        deviceManufacturer: "",
                    };


                    //  refresh data
                    this.getAllSampleDeviceDetails();

                });

            },
            manufacturerChanged:function(allEntities, selectedManufacturer) {
                this.allDeviceManufacturers = allEntities;
                this.sampleDevice.deviceManufacturer = selectedManufacturer;
                this.allModelsForManufacturer = this.allSampleDevices.filter(function(sampleDevice) {
                   return sampleDevice.deviceManufacturer === selectedManufacturer
                }).map(model => model.name);
            },
        }
    }
</script>

<style scoped>

</style>
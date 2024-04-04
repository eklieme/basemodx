<template>
  <v-dialog v-model="artifactEditorShown" persistent hide-overlay transition="dialog-bottom-transition">
    <v-card>
        <v-card-title>
            <span v-if="editMode" class="headline">Edit Device Category</span>
            <span v-else class="headline">Add New Device Category</span>
        </v-card-title>
        <v-card-text>
            <v-container fluid v-if="editMode">
              <v-row cols="12" lg="12">
                <v-col lg="12">
                  <v-select
                    :label="'Select Device Category to edit'"
                    :items="allDeviceCategories"
                    v-model="deviceCategoryToEdit"
                    item-text="name"
                    item-value="id"
                    return-object/>
                </v-col>
              </v-row>
              <div v-if="existingDeviceCategoryNameNotEmpty">
                <v-row cols="12" lg="12">
                  <v-col lg="6">
                    <h5>Current Sensors of Category</h5>
                    <SensorBrowser
                        :sensors-to-show="deviceCategoryToEdit.sensors"
                        v-on:edit-artifact="setSensorForEditing"
                        v-on:delete-artifact="deleteSensorFromDeviceCategory"
                    />
                  </v-col>
                  <v-col lg="6">
                    <h5>Further Available Sensors</h5>
                    <SensorBrowser
                        :sensors-to-show="sensorsAvailableToAssign"
                        allow-selection
                        v-on:artifacts-selected="markSensorsToAddToExistingDeviceCategory"
                        v-on:edit-artifact="setSensorForEditing"
                        v-on:delete-artifact="globallyDeleteSensor"
                    />
                    <v-btn color="primary" :disabled="sensorsToAddToExistingDeviceCategory.length===0" @click="addSensorsToExistingDeviceCategory" text> Add Sensor(s) to Device Category</v-btn>
                  </v-col>
                </v-row>
                <v-row cols="12" lg="12">
                  <v-col lg="12">
                    <h5>{{sensorEditMode ? 'Edit ':'Add new '}} Sensor</h5>
                    <SensorEditor
                      :all-existing-sensors="existingSensors"
                      v-on:artifact-created-updated="sensorCreatedUpdated"
                      :sensor-for-editing="selectedSensorToEdit"/>
                  </v-col>
                </v-row>
              </div>
            </v-container>
            <v-container fluid v-if="!editMode">
                <v-row cols="12" lg="12">
                    <v-col lg="12">
                        <v-text-field
                            label="Device Category Name"
                            v-model="newDeviceCategory.name"
                            :rules="[newDeviceCategoryRules.nameUnique, newDeviceCategoryRules.nameNotEmpty]"
                        />
                    </v-col>
                </v-row>
                <template v-if="newDeviceCategoryNameNotYetExisting && newDeviceCategoryNameNotEmpty">
                  <v-row cols="12" lg="12" >
                      <v-col lg="12">
                        <span class="text-h6">Select Sensors of Device Category <span class="text-h6" v-if="newDeviceCategory.sensors.length>0">&nbsp;({{newDeviceCategory.sensors.length}})</span></span>
                        <SensorBrowser
                            :sensors-to-show="existingSensors"
                            :allow-selection="true"
                            v-on:artifacts-selected="setSensorsForNewCategory"
                            v-on:delete-artifact="deleteSensorFromDeviceCategory"
                        />
                      </v-col>

                  </v-row>
                  <v-row cols="12" lg="12">
                    <v-col lg="12">
                    <v-expansion-panels
                        v-model="newSensorDetailsPanels"
                    >
                      <v-expansion-panel>
                        <v-expansion-panel-header>My Sensor is missing</v-expansion-panel-header>
                          <v-expansion-panel-content>
                            <SensorEditor
                              :all-existing-sensors="existingSensors"
                              v-on:artifact-created-updated="sensorCreatedUpdated"
                              />
                          </v-expansion-panel-content>
                        </v-expansion-panel>
                      </v-expansion-panels>
                    </v-col>
                  </v-row>
                </template>
            </v-container>
        </v-card-text>
        <v-card-actions>
            <v-btn v-if="editMode" color="primary" text @click="saveDeviceCategory()" :disabled="!saveExistingDeviceCategoryAllowed">Save Device Category</v-btn>
            <v-btn v-if="!editMode" color="primary" text @click="saveDeviceCategory()" :disabled="!saveNewDeviceCategoryAllowed">Add New Device Category</v-btn>
            <v-btn color="error" text @click="closeEditor()">Cancel</v-btn>
        </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>

    import {DeviceCategoryServiceAPI} from "@/service/api/DeviceCategoryServiceAPI";
    import constants from "@/helpers/constants";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import toast from "@/helpers/toast";
    import BrowserForReviewableArtifactsMixin from "@/mixins/BrowserForReviewableArtifactsMixin";
    import SensorBrowser from "@/components/target_architecture/SensorBrowser.vue";
    import SensorEditor from "@/components/target_architecture/SensorEditor.vue";
    import {SensorAPI} from "@/service/api/SensorAPI";
    import ArtifactEditorMixin from "@/mixins/model/ArtifactEditorMixin";
    import {Util as Utils} from "@/helpers/util";
    import SensorOperationsMixin from "@/mixins/model/SensorOperationsMixin";

    // TODO: if a new device category is created, the respective target architecture needs to be changed as well!
    export default {
        components: {SensorEditor, SensorBrowser},
        name: "DeviceCategoryEditor",
        mixins: [LoggedInUserMixin, BrowserForReviewableArtifactsMixin, ArtifactEditorMixin, SensorOperationsMixin],
        props: {
            standAlone: {
                type: Boolean,
                default: false,
            },
            existingDeviceCategories: {
                type: Array,
                default: function() {
                  return [];
                }
            },
            editMode: {
              type: Boolean,
              default: false,
            },
            deviceCategoryForEditing: {
              type: Object,
              default: function() {
                return {};
              }
            },

        },
        data() {
            return {
                allDeviceCategories: [],
                newDeviceCategory: {
                    id: null,
                    name:"",
                    sensors: [],
                    modelledElementDetail: {},
                },
                deviceCategoryToEdit: {
                  id: null,
                  name:"",
                  sensors: [],
                  modelledElementDetail: {},
                },
                deviceCategoryToEditOriginalName: "",
                sensorIndexToEdit: -1,
                model: "",
                category: "",
                sensorEditMode: false,
                sensorHeaders: [
                    { text: 'Name', value: 'name' },
                    { text: 'Dimensions', value: 'sensorDimensions'},
                    { text: 'Continuous', value: 'continuous'},
                    { text: 'Actions', value: 'actions'},
                ],
                newDeviceCategoryRules: {
                  nameUnique: () => this.newDeviceCategoryNameNotYetExisting || 'A device category with that name already exists',
                  nameNotEmpty: () => this.newDeviceCategoryNameNotEmpty || 'Please enter a device category name',
                },
                newSensorDetailsPanels: [],
                sensorsToAddToExistingDeviceCategory: [],
                selectedSensorToEdit: {
                  name: "",
                },
                sensorsAvailableToAssign: [],
            }
        },
        mounted() {
          this.getAllSensors();
        },
        beforeRouteEnter (to, from, next) {
            if(to.path === constants.routes.modelSampleDevices.path) {
                to.params["standAlone"] = true;
            }

            next();
        },
        computed: {
            constants: function() {
              return constants;
            },
            newDeviceCategoryNameNotYetExisting: function() {
              return this.allDeviceCategories.filter(deviceCategory => {
                return deviceCategory.name.toLowerCase()===this.newDeviceCategory.name.toLowerCase();
              }).length===0;
            },
            existingDeviceCategoryNameNotYetExisting: function() {
              return this.allDeviceCategories.filter(deviceCategory => {
                if(this.deviceCategoryToEditOriginalName == this.deviceCategoryToEdit.name) {
                  return false;
                } else {
                  return deviceCategory.name.toLowerCase() === this.deviceCategoryToEditOriginalName.toLowerCase();
                }
              }).length===0;
            },
            newDeviceCategoryNameNotEmpty: function() {
              return this.newDeviceCategory.name.length!==0;
            },
            existingDeviceCategoryNameNotEmpty: function() {
                if(this.deviceCategoryToEdit && this.deviceCategoryToEdit.name) {
                    return this.deviceCategoryToEdit.name.length !== 0;
                }
                return false;
            },
            saveNewDeviceCategoryAllowed: function() {
               return this.newDeviceCategoryNameNotEmpty
                   && this.newDeviceCategoryNameNotYetExisting
                   && this.newDeviceCategory.sensors.length > 0
            },
            saveExistingDeviceCategoryAllowed: function() {
              return this.existingDeviceCategoryNameNotEmpty
                  && this.existingDeviceCategoryNameNotYetExisting
            },
        },
        watch: {
          editorShown: {
            immediate: true,
            handler: function(editorShown) {
              if(editorShown) {
                this.artifactEditorShown = true;
              } else {
                this.artifactEditorShown = false;
              }
            }
          },
          existingDeviceCategories: {
            deep: true,
            immediate: true,
            handler: function(existingDeviceCategories) {
              if(existingDeviceCategories && existingDeviceCategories.length>0) {
                this.allDeviceCategories = existingDeviceCategories;
              }
            }
          },
          existingSensors: {
            deep: true,
            immediate: true,
            handler: function(existingSensors) {
              if(existingSensors && this.deviceCategoryToEdit) {
                this.determineSensorsAvailableToAssign();
              }
            }
          },
          deviceCategoryToEdit: {
            deep: true,
            immediate: true,
            handler: function(deviceCategoryToEdit) {
              if(deviceCategoryToEdit && deviceCategoryToEdit.name.length>0 &&
                this.deviceCategoryToEditOriginalName.length===0) {
                this.$log.debug("set original device category to edit name to ",deviceCategoryToEdit.name)
                this.deviceCategoryToEditOriginalName = deviceCategoryToEdit.name;
                this.determineSensorsAvailableToAssign();
              } else {
                this.deviceCategoryToEditOriginalName = "";
              }
            }
          },
          deviceCategoryForEditing: {
            deep: true,
            immediate: true,
            handler: function(deviceCategoryForEditing) {
              if(deviceCategoryForEditing
                  && this.editMode
                  && !this.editorOpened) {
                this.$log.debug("\t...trigger to open device category editor by setting device category from outside", deviceCategoryForEditing);
                this.deviceCategoryToEdit = Utils.deepCopyObject(deviceCategoryForEditing);
                this.$log.debug("\t\t...sensors already assigned to device category", this.deviceCategoryToEdit.sensors);
                this.openEditor();
              }
            }
          }
        },
        methods: {
            determineSensorsAvailableToAssign: function() {
              this.sensorsAvailableToAssign = this.existingSensors.filter(sensor => {
                let sensorNotYetAssignedToDeviceCategory = this.deviceCategoryToEdit.sensors.filter(otherSensor => {
                  return otherSensor.id === sensor.id
                }).length===0;
                if(sensorNotYetAssignedToDeviceCategory) {
                  this.$log.debug("\t..sensor with name/id '" + sensor.name + "/" + sensor.id + "' NOT YET in device category '" + this.deviceCategoryToEdit.name + "'");
                } else {
                  this.$log.debug("\t..sensor with name/id '" + sensor.name + "/" + sensor.id + "' already in device category '" + this.deviceCategoryToEdit.name + "'");
                }
                return sensorNotYetAssignedToDeviceCategory;
              })
            },
            saveDeviceCategory: function() {
              if(this.editMode) {
                DeviceCategoryServiceAPI.createUpdateDeviceCategory(this.deviceCategoryToEdit).then(response => {
                  this.$log.debug("\t..update device category with id", response.data);
                  toast.success("device category '" + this.deviceCategoryToEdit.name + "' successfully updated");
                  this.$emit(constants.eventNames.local.artifactCreatedUpdated, this.deviceCategoryToEdit);

                  this.deviceCategoryToEdit = {
                    id: null,
                    name: "",
                    sensors: [],
                    modelledElementDetail: {}
                  }

                });
              } else {

                this.setInitialModelLifecycleStateForDomainElement(this.newDeviceCategory);

                DeviceCategoryServiceAPI.createUpdateDeviceCategory(this.newDeviceCategory).then(response => {
                  this.$log.debug("\t..added device category with new id", response.data);
                  toast.success("device category '" + this.newDeviceCategory.name + "' successfully added");
                  this.$emit(constants.eventNames.local.artifactAdded, this.newDeviceCategory);
                  this.newDeviceCategory = {
                    id: null,
                    name: "",
                    sensors: [],
                    modelledElementDetail: {}
                  }
                });
              }
            },
            sensorCreatedUpdated(sensorCreatedUpdatedId) {
              var that = this;
              SensorAPI.getSensors().then(response => {
                that.existingSensors = response.data;
                if(that.editMode) {
                  that.$log.debug("device category in edit mode, refresh device category");
                  if(that.sensorEditMode) {
                    DeviceCategoryServiceAPI.getDeviceCategoryById(this.deviceCategoryToEdit.id).then(response => {
                      that.deviceCategoryToEdit.sensors = response.data.sensors;
                      that.deviceCategoryToEdit.sensorIds = response.data.sensorIds;
                    })
                  } else {
                    that.deviceCategoryToEdit.sensors.push(that.existingSensors.find(sensor => {
                      return sensor.id === sensorCreatedUpdatedId
                    }));
                  }
                } else {
                  that.$log.debug("device category is new, add or refresh sensor");
                  if(that.sensorEditMode) {
                    // an existing sensor was edited, refresh existing sensors and replace in new device category if already assigned
                    that.$log.debug("\t...updating sensor with id in new device category", sensorCreatedUpdatedId);
                    that.updateSensorInDeviceCategorySensors(this.newDeviceCategory, sensorCreatedUpdatedId);
                  } else {
                    that.$log.debug("\t...add sensor");
                    that.newDeviceCategory.sensors.push(that.existingSensors.find(sensor => {
                      return sensor.id === sensorCreatedUpdatedId
                    }));
                  }
                }
              })
            },
            updateSensorInDeviceCategorySensors(deviceCategoryToUpdate, sensorCreatedUpdatedId) {
              this.$log.debug("\t...updating sensor in device category", deviceCategoryToUpdate);
              let index = deviceCategoryToUpdate.sensors.findIndex(sensor => {
                return sensor.id === sensorCreatedUpdatedId;
              });
              this.$log.debug("\t\t...determined index in device category's sensors for update", index);
              if(index!==-1) {
                this.$log.debug("\t\t...updating device category's sensors at index", index);
                deviceCategoryToUpdate.sensors[index] = this.existingSensors[this.existingSensors.findIndex(sensor => {return sensor.id===sensorCreatedUpdatedId})];
              }
            },
            deleteSensorFromDeviceCategory(sensorToDelete) {
              if(this.editMode) {
                this.deviceCategoryToEdit.sensors
                    .splice(this.deviceCategoryToEdit.sensors
                        .findIndex(sensor => sensor.name === sensorToDelete.name), 1);
                this.determineSensorsAvailableToAssign();
              } else {
                this.newDeviceCategory.sensors
                    .splice(this.newDeviceCategory.sensors
                        .findIndex(sensor => sensor.name === sensorToDelete.name), 1);
                this.determineSensorsAvailableToAssign();
              }
            },
            setSensorsForNewCategory(selectedSensors) {
              this.newDeviceCategory.sensors = selectedSensors;
            },
            markSensorsToAddToExistingDeviceCategory(sensorsToAdd) {
              this.sensorsToAddToExistingDeviceCategory = sensorsToAdd;
            },
            addSensorsToExistingDeviceCategory() {
              this.deviceCategoryToEdit.sensors = this.deviceCategoryToEdit.sensors.concat(this.sensorsToAddToExistingDeviceCategory);
              this.sensorsToAddToExistingDeviceCategory = [];
              this.determineSensorsAvailableToAssign();
            },
            setSensorForEditing(sensorForEditing) {
              this.$log.debug("\t set sensor for editing", sensorForEditing);
              this.selectedSensorToEdit = Utils.deepCopyObject(sensorForEditing);
              this.sensorEditMode = true;
            }
        }
    }
</script>

<style scoped>

</style>
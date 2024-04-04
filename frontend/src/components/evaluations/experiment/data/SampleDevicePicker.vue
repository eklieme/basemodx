<template>
    <v-container>
        <v-row>
            <v-col cols="12" lg="12">
                <template v-if="allSampleDevices.length>0">
                    <v-text-field
                            v-model="searchSampleComponents"
                            label="Search Sample Devices"
                            clearable
                    />
                    <v-treeview
                            v-model="selectedSampleComponentIds"
                            :items="sampleDeviceTree"
                            :search="searchSampleComponents"
                            activatable
                            open-on-click
                            :selectable="sampleComponentsCanBePicked"
                            transition
                    >
                        <template v-slot:append="{ item }">
                            <span
                                    v-if="item.processingType==='cat'"
                                    v-text="'device category'"
                            />
                            <span
                                    v-if="item.processingType==='ma'"
                                    v-text="'manufacturer'"
                            />
                            <span
                                    v-if="item.processingType==='mo'"
                                    v-text="'model'"
                            />
                            <span
                                    v-if="item.processingType==='s'"
                                    v-text="'sensor'"
                            />
                            <span
                                    v-if="item.processingType==='d'"
                                    v-text="'dimension'"
                            />
                        </template>
                    </v-treeview>
                </template>
                <span v-else-if="standAlone">
                    No sample device is modelled yet, please <router-link :to="{ name: 'model-sampledevices', params: {standAlone: true } }">model</router-link> a new one
                </span>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>

    import toast from "../../../../helpers/toast";
    import Vue from "vue";
    import constants from "../../../../helpers/constants";
    import {DatasetAPI} from "../../../../service/api/DatasetAPI";
    import {Util as Utils} from "@/helpers/util";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";

    export default {
        name: "SampleDevicePicker",
        components: {},
        mixins:[LoggedInUserMixin],
        data() {
            return {
                allDeviceManufacturers:[],
                allDeviceCategories:[],
                allSensors: [],
                allSensorDimensions: [],
                sampleDeviceTree: [],
                selectedSampleComponentIds: [],
                selectedSensorDimensions: [],
                selectedSampleDevices: [],
                searchSampleComponents: null,
                selectionForExportAllowed: false,
                sampleDevicesExportArtefacts: []
            }
        },
        watch: {
            selectedSampleComponentIds: {
                deep: true,
                handler: function(currentlySelectedSampleComponentIds) {

                    //const context = this;

                    this.$log.debug("Sample Component Ids were changed to " + currentlySelectedSampleComponentIds);
                    this.$log.debug("Sample Component Ids ", this.selectedSampleComponentIds);

                    /*this.selectedSensorDimensions = this.allSensorDimensions.filter(function (sensorDimension) {
                        //context.$log.debug("\t Checking for id "+sensorDimension.id+" of sensor dimension '"+sensorDimension.name+"' in list of sample component ids");
                        return context.selectedSampleComponentIds.indexOf(sensorDimension.id) > -1;
                    });

                    this.$log.debug("List of selected sensor dimensions after sample component ids were changed", this.selectedSensorDimensions);

                    // order is important
                    this.selectedSampleDevices = this.allSampleDevices.filter(function (sampleDevice) {
                        context.$log.debug("\t Checking for id "+sampleDevice.id+" of sample device '"+sampleDevice.name+"' in parentDeviceIds of selected Dimensions");
                        return context.selectedSensorDimensions.filter(function (sensorDimension) {
                            return sensorDimension.parentDeviceId === sampleDevice.id;
                        }).length>0;
                    });

                    this.$log.debug("List of selected sample devices after sample component ids were changed", this.selectedSampleDevices);

                    if(this.standAlone) {
                        // selection important for import/export
                        this.sampleDevicesExportArtefacts = this.selectedSampleDevices;
                    } else {
                        // selection important for parent component
                        this.$emit("sample-components-selected", {
                            selectedSampleDevices: this.selectedSampleDevices,
                            selectedSensorDimensions: this.selectedSensorDimensions,
                            selectedSensors: this.selectedSampleDevices.map(device => device.deviceCategory.sensors).flat()
                        });
                    }*/
                }

            },
            allSampleDevices: {
                deep: true,
                handler: function() {
                    this.createSampleDeviceTree();
                }
            },
            preselectedSamplingDimensionIds: {
                deep: true,
                handler: function(newSelection) {
                    this.$log.debug("preselected device's sensor dimension from outside changed to", newSelection);
                    this.updateSelectedSampleDimensions(newSelection);
                }
            }
        },
        props: {
            selectable: {
                type: Boolean,
                default: true,
            },
            allSampleDevices: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            standAlone: {
                type: Boolean,
                default: false,
            },
            preselectedSamplingDimensionIds: {
                type: Array,
                default: function() {
                    return [];
                }
            }
        },
        computed: {
            allowExport: function() {
                return this.allSampleDevices.length>0;
            },
            sampleComponentsCanBePicked: function() {
                return this.selectable || this.selectionForExportAllowed
            },
            sampleDeviceArtefactType: function() {
                return constants.importExport.artefactTypes.devices;
            }
        },
        mounted() {
            // once mounted, create tree
            this.createSampleDeviceTree();
            this.updateSelectedSampleDimensions(this.preselectedSamplingDimensionIds);
        },
        beforeRouteEnter (to, from, next) {
            Vue.$log.debug("before entering sample device picker using route to, from", to, from);

            if(to.name === constants.routes.browseSampleDevices.name) {
                to.params["selectable"] = false;
                to.params["standAlone"] = true;
                DatasetAPI.getSampleDevices().then(response => {
                    to.params["allSampleDevices"] = response.data;
                    next();
                });
            } else {
                next();
            }


        },
        methods: {
            createSampleDeviceTree: function() {


                if(this.allSampleDevices.length>0 ) {


                    let deviceCategories = [];

                    this.allSampleDevices.forEach(sampleDevice => {
                      let existingDeviceCategoryInList = deviceCategories.find(deviceCategory => {
                        return deviceCategory.id === sampleDevice.deviceCategory.id;
                      });
                      if (existingDeviceCategoryInList) {
                        let existingManufacturerInList = null;
                        let manufacturersListAvailable = true;
                        if(!existingDeviceCategoryInList.manufacturers) {
                          manufacturersListAvailable = null;
                        } else {
                          existingManufacturerInList = existingDeviceCategoryInList.manufacturers.find(manufacturer => {
                            return manufacturer.name === sampleDevice.deviceManufacturer;
                          });
                        }
                        if(existingManufacturerInList) {
                          existingManufacturerInList.sampleDevices.push(sampleDevice);
                        } else {
                          if(!manufacturersListAvailable) {
                            existingDeviceCategoryInList["manufacturers"] = [];
                          }
                          existingDeviceCategoryInList.manufacturers.push({
                            name: sampleDevice.deviceManufacturer,
                            sampleDevices: [sampleDevice]
                          });
                        }
                      } else {
                        let parentDeviceCategory = Utils.deepCopyObject(sampleDevice.deviceCategory);
                        parentDeviceCategory["manufacturers"] = [];
                        parentDeviceCategory.manufacturers.push({
                          name: sampleDevice.deviceManufacturer,
                          sampleDevices: [sampleDevice]
                        });
                        deviceCategories.push(parentDeviceCategory);
                      }
                    });

                    let manufacturerIndex = 0;

                    this.sampleDeviceTree = deviceCategories.map(deviceCategory => {
                      return {

                        id: deviceCategory.id,
                        name: deviceCategory.name,
                        processingType: "cat",
                        children: deviceCategory.manufacturers.map(manufacturer => {
                          return {
                            id: ++manufacturerIndex,
                            name: manufacturer.name,
                            processingType: "ma",
                            children: manufacturer.sampleDevices.map(sampleDevice => {
                              return {
                                id: sampleDevice.id,
                                name: sampleDevice.name,
                                processingType: "mo",
                                children: sampleDevice.deviceCategory.sensors.map(sensor => {
                                  let sensorName = sensor.name;
                                  if (sensor.continuous) {
                                    sensorName += ", (continuous)"
                                  }
                                  return {
                                    id: sensor.id + "_" + sampleDevice.id,
                                    name: sensorName,
                                    processingType: "s",
                                    children: sensor.sensorDimensions.map(dimension => {
                                      return {
                                        id: dimension.id + "_" + sampleDevice.id,
                                        name: dimension.name,
                                        processingType: "d",
                                      }
                                    })
                                  }
                                })
                              };
                            })
                          };
                        })
                      };
                    });

                } else {
                    toast.error("No sample device is modelled yet, please model a new one", 5000);
                }
            },
            setSelectable(enableSelection) {
                this.selectionForExportAllowed = enableSelection;
                if(!enableSelection) {
                    this.sampleDevicesExportArtefacts = [];
                    this.resetExportSelection();
                }
            },
            resetExportSelection() {
                this.selectedSampleDevices = [];
                this.selectedSensorDimensions = [];
                this.selectedSampleComponentIds = [];
            },
            updateSelectedSampleDimensions(preselectedSamplingDimensionIds) {
                this.$log.debug("Updating selection of sample device dimesion from outside to", preselectedSamplingDimensionIds);
                this.selectedSampleComponentIds = preselectedSamplingDimensionIds;
            }
        }
    }
</script>

<style scoped>

</style>
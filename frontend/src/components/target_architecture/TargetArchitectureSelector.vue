<template>
    <v-container fluid>
        <v-row row wrap>
            <v-col cols="12" lg="3">
                <div class="headline">Define the <b>target architecture</b> of <i>your</i> authentication system</div>
                <span>Of course, each task can be solved on multiple types of devices</span>
            </v-col>
            <v-col lg="3">
                <v-checkbox
                        v-model="targetArchitecture.skipTargetArchitecture"
                        :label="'I had no specific architecture in mind!'"
                        v-on:change="updateTargetArchitecture"
                ></v-checkbox>
            </v-col>
            <v-col lg="2">
              <v-btn color="primary" text @click="showDeviceCategoryEditorDialog(false)">Add New Device Category</v-btn>
            </v-col>
            <v-col lg="2">
              <v-btn color="primary" text @click="showDeviceCategoryEditorDialog(true)">Edit Existing Device Category</v-btn>
            </v-col>
            <v-col lg="2">
              <ArchitectureInfo/>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="5">
                <v-select
                    label="1. Data Capturing"
                    :items="existingDeviceCategories"
                    item-text="name"
                    multiple
                    chips
                    deletable-chips
                    v-model="targetArchitecture.dataCapturingDeviceCategories"
                    return-object
                  >
                  <template v-slot:prepend-item v-if="existingDeviceCategories.length>0">
                    <v-list-item
                        ripple
                        @mousedown.prevent
                        @click="applySelectedDeviceCategoriesToAll(targetArchitecture.dataCapturingDeviceCategories)"
                    >
                      <v-list-item-content>
                        <v-list-item-title>
                          Apply Categories for all Steps
                        </v-list-item-title>
                      </v-list-item-content>
                    </v-list-item>
                    <v-divider class="mt-2"></v-divider>
                  </template>
                </v-select>
            </v-col>
            <v-col offset-lg="1" lg="5">
              <v-select
                  label="2. Signal Processing"
                  :items="existingDeviceCategories"
                  item-text="name"
                  multiple
                  chips
                  deletable-chips
                  v-model="targetArchitecture.signalProcessingDeviceCategories"
                  return-object
              >
                <template v-slot:prepend-item>
                  <v-list-item
                      ripple
                      @mousedown.prevent
                      @click="applySelectedDeviceCategoriesToAll(targetArchitecture.signalProcessingDeviceCategories)"
                  >
                    <v-list-item-content>
                      <v-list-item-title>
                        Apply Categories for all Steps
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-divider class="mt-2"></v-divider>
                </template>
              </v-select>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="5">
              <v-select
                  label="3. Template Creation"
                  :items="existingDeviceCategories"
                  item-text="name"
                  multiple
                  chips
                  deletable-chips
                  v-model="targetArchitecture.signatureCreationDeviceCategories"
                  return-object
              >
                <template v-slot:prepend-item v-if="existingDeviceCategories.length>0">
                  <v-list-item
                      ripple
                      @mousedown.prevent
                      @click="applySelectedDeviceCategoriesToAll(targetArchitecture.signatureCreationDeviceCategories)"
                  >
                    <v-list-item-content>
                      <v-list-item-title>
                        Apply Categories for all Steps
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-divider class="mt-2"></v-divider>
                </template>
              </v-select>
            </v-col>
            <v-col offset-lg="1" lg="5">
              <v-select
                  label="4. Data Storage"
                  :items="existingDeviceCategories"
                  item-text="name"
                  multiple
                  chips
                  deletable-chips
                  v-model="targetArchitecture.dataStorageDeviceCategories"
                  return-object
              >
                <template v-slot:prepend-item v-if="existingDeviceCategories.length>0">
                  <v-list-item
                      ripple
                      @mousedown.prevent
                      @click="applySelectedDeviceCategoriesToAll(targetArchitecture.dataStorageDeviceCategories)"
                  >
                    <v-list-item-content>
                      <v-list-item-title>
                        Apply Categories for all Steps
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-divider class="mt-2"></v-divider>
                </template>
              </v-select>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="5">
              <v-select
                  label="5. Matching"
                  :items="existingDeviceCategories"
                  item-text="name"
                  multiple
                  chips
                  deletable-chips
                  v-model="targetArchitecture.matchingDeviceCategories"
                  return-object
              >
                <template v-slot:prepend-item v-if="existingDeviceCategories.length>0">
                  <v-list-item
                      ripple
                      @mousedown.prevent
                      @click="applySelectedDeviceCategoriesToAll(targetArchitecture.matchingDeviceCategories)"
                  >
                    <v-list-item-content>
                      <v-list-item-title>
                        Apply Categories for all Steps
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-divider class="mt-2"></v-divider>
                </template>
              </v-select>
            </v-col>
            <v-col offset-lg="1" lg="5">
              <v-select
                  label="6. Decision"
                  :items="existingDeviceCategories"
                  item-text="name"
                  multiple
                  chips
                  deletable-chips
                  v-model="targetArchitecture.decisionDeviceCategories"
                  return-object
              >
                <template v-slot:prepend-item v-if="existingDeviceCategories.length>0">
                  <v-list-item
                      ripple
                      @mousedown.prevent
                      @click="applySelectedDeviceCategoriesToAll(targetArchitecture.decisionDeviceCategories)"
                  >
                    <v-list-item-content>
                      <v-list-item-title>
                        Apply Categories for all Steps
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-divider class="mt-2"></v-divider>
                </template>
              </v-select>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12" >
                <SelectAutoAddComboBox
                    :all-entities="existingResourcesToProtectNames"
                    :entity-name="'Resource to protect'"
                    :label="'Mark your systems resource to protect'"
                    :disabled="targetArchitecture.skipTargetArchitecture"
                    v-on:entity-list-changed="resourceToProtectSet"
                    :pre-selected-entity="targetArchitecture.resourceToProtectName">
                </SelectAutoAddComboBox>
            </v-col>
        </v-row>
      <DeviceCategoryEditor
        :editor-shown="deviceCategoryEditorDialogShown"
        :existing-device-categories="existingDeviceCategories"
        :edit-mode="deviceCategoryEditMode"
        v-on:close-model-dialog="deviceCategoryEditorDialogShown = false"
        v-on:artifact-added="deviceCategoryAdded"
        />
    </v-container>
</template>

<script>
    import SelectAutoAddComboBox from "../util/SelectAutoAddComboBox";
    import {TargetArchitectureAPI} from "../../service/api/TargetArchitectureAPI";
    import ArchitectureInfo from "./ArchitectureInfo";
    import {DeviceCategoryServiceAPI} from "@/service/api/DeviceCategoryServiceAPI";
    import DeviceCategoryEditor from "@/components/target_architecture/DeviceCategoryEditor.vue";
    import {Util} from "@/helpers/util";

    export default {
        name: "TargetArchitectureSelector",
        components: {DeviceCategoryEditor, ArchitectureInfo, SelectAutoAddComboBox},
        mounted: function () {
            this.getDeviceCategories();
            this.getResourcesToProtect();
        },
        props: {
            targetArchitectureState: {
                type: Object,
                default: function() {
                    return {};
                }
            }
        },
        data() {
          return {
              existingDeviceCategories: [],
              existingResourcesToProtectNames: [],
              existingResourcesToProtect: [],
              targetArchitecture: {
                  skipTargetArchitecture: false,
                  dataCapturingDeviceCategories: [],
                  signalProcessingDeviceCategories: [],
                  signatureCreationDeviceCategories: [],
                  dataStorageDeviceCategories: [],
                  matchingDeviceCategories: [],
                  decisionDeviceCategories: [],
                  resourceToProtectName: "",
              },
              deviceCategoryEditorDialogShown: false,
              deviceCategoryEditMode: false,
          }
        },
        watch: {
            targetArchitectureState: {
                deep: true,
                immediate: true,
                handler: function(newTargetArchitectureState) {
                    this.$log.debug("request to set target architecture state from outside", newTargetArchitectureState);
                    if(this.validateTargetArchitecture(newTargetArchitectureState)) {
                        this.$log.debug("\t VALID => set target architecture state");
                        this.targetArchitecture = Util.deepCopyObject(newTargetArchitectureState);
                    } else {
                        this.$log.debug("\t INVALID => do NOT set target architecture state");
                    }
                }
            },
        },
        methods: {
            getDeviceCategories() {
                DeviceCategoryServiceAPI.getDeviceCategories().then((response) => {
                    if(response && response.status === 200) {
                        this.existingDeviceCategories = response.data;
                    }
                });
            },
            deviceCategoryAdded() {
              this.getDeviceCategories();
              this.deviceCategoryEditorDialogShown = false;
            },
            showDeviceCategoryEditorDialog(editMode) {
              this.deviceCategoryEditorDialogShown = true;
              if(editMode) {
                this.deviceCategoryEditMode = true;
              } else {
                this.deviceCategoryEditMode = false;
              }
            },
            getResourcesToProtect: function() {
                TargetArchitectureAPI.getResourcesToProtect().then((response) => {
                    if(response && response.status === 200) {
                        this.existingResourcesToProtectNames = response.data.map(resource => resource.name);
                        this.existingResourcesToProtect = response.data;
                    }
                });
            },
            resourceToProtectSet: function(allResources, selectedResource, isNewResource, allSelectedResources) {
                this.$log.debug("resourceToProtectSet", allResources, selectedResource, isNewResource, allSelectedResources);
                this.$log.debug("\tallSelectedResources", allSelectedResources);
                this.existingResourcesToProtectNames = allResources;
                this.targetArchitecture.resourceToProtectName = selectedResource;
                if(isNewResource) {
                    TargetArchitectureAPI.createUpdateResourceToProtect({name: selectedResource});
                    TargetArchitectureAPI.getResourcesToProtect().then((response) => {
                      if(response && response.status === 200) {
                        this.existingResourcesToProtect = response.data;
                        this.updateTargetArchitecture();
                      }
                    });
                } else {
                  this.updateTargetArchitecture();
                }
            },
            updateTargetArchitecture() {
                if(this.validateTargetArchitecture(this.targetArchitecture)) {
                    this.$log.debug("\t...look for resources to protect in all resources by name",this.existingResourcesToProtect, this.targetArchitecture.resourceToProtectName);
                    this.targetArchitecture.resourceToProtect = this.existingResourcesToProtect.find(resourceToProtect => {
                      return resourceToProtect.name === this.targetArchitecture.resourceToProtectName;
                    });
                    this.$log.debug("\t...set resource to protect to", this.targetArchitecture.resourceToProtect);
                    this.$emit('set-target-architecture', this.targetArchitecture);
                } else {
                    this.$emit('reset-target-architecture');
                }
            },
            validateTargetArchitecture(targetArchitectureState) {
                this.$log.debug("validating target architecture", targetArchitectureState);

                if(targetArchitectureState.skipTargetArchitecture) {

                    return true;

                } else {

                    if (targetArchitectureState.signatureCreationDeviceCategories
                        && targetArchitectureState.signatureCreationDeviceCategories.length > 0
                        && targetArchitectureState.dataStorageDeviceCategories
                        && targetArchitectureState.dataStorageDeviceCategories.length > 0
                        && targetArchitectureState.signalProcessingDeviceCategories
                        && targetArchitectureState.signalProcessingDeviceCategories.length > 0
                        && targetArchitectureState.matchingDeviceCategories
                        && targetArchitectureState.matchingDeviceCategories.length > 0
                        && targetArchitectureState.dataCapturingDeviceCategories
                        && targetArchitectureState.dataCapturingDeviceCategories.length > 0
                        && targetArchitectureState.decisionDeviceCategories
                        && targetArchitectureState.decisionDeviceCategories.length > 0
                        && targetArchitectureState.resourceToProtectName.length > 0) {


                        return true;

                    }
                }

                return false;
            },
            applySelectedDeviceCategoriesToAll(deploymentLocations) {
                this.$log.debug("Apply locations to all", deploymentLocations);
                deploymentLocations.forEach(deploymentLocation => {
                    if(!this.targetArchitecture.dataCapturingDeviceCategories.find(location => {return location.location === deploymentLocation.location })) {
                        this.targetArchitecture.dataCapturingDeviceCategories.push(deploymentLocation);
                    }
                    if(!this.targetArchitecture.signalProcessingDeviceCategories.find(location => {return location.location === deploymentLocation.location })) {
                        this.targetArchitecture.signalProcessingDeviceCategories.push(deploymentLocation);
                    }
                    if(!this.targetArchitecture.dataStorageDeviceCategories.find(location => {return location.location === deploymentLocation.location })) {
                        this.targetArchitecture.dataStorageDeviceCategories.push(deploymentLocation);
                    }
                    if(!this.targetArchitecture.matchingDeviceCategories.find(location => {return location.location === deploymentLocation.location })) {
                        this.targetArchitecture.matchingDeviceCategories.push(deploymentLocation);
                    }
                    if(!this.targetArchitecture.decisionDeviceCategories.find(location => {return location.location === deploymentLocation.location })) {
                        this.targetArchitecture.decisionDeviceCategories.push(deploymentLocation);
                    }
                    if(!this.targetArchitecture.signatureCreationDeviceCategories.find(location => {return location.location === deploymentLocation.location })) {
                        this.targetArchitecture.signatureCreationDeviceCategories.push(deploymentLocation);
                    }
                });
            }

        },
    }
</script>

<style scoped>

</style>
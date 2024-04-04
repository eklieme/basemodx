<template>
  <v-container fluid>
    <v-row lg="12">
      <v-col lg="3">
        <v-text-field
            label="Sensor Name"
            :disabled="sensorAlreadyReviewed"
            v-model="sensorToEdit.name"
            :rules="[newSensorRules.nameUnique, newSensorRules.nameNotEmpty]"
        />
      </v-col>
      <v-col lg="3" v-if="!sensorAlreadyReviewed">
        <v-text-field
            label="Add dimension"
            required
            v-model="newDimension"
            @keyup.enter.native="appendDimension"
        >
          <template v-slot:append>
            <span v-if="newDimension && newDimension.length>0">(press <kbd>enter</kbd> to add "<strong>{{ newDimension }}</strong>")</span>
          </template>
        </v-text-field>
      </v-col>
      <v-col lg="3">
        <v-combobox
            v-model="sensorToEdit.sensorDimensions"
            :deleteable-chips="!sensorAlreadyReviewed"
            :clearable="!sensorAlreadyReviewed"
            :disabled="sensorAlreadyReviewed"
            label="Sensor dimensions"
            multiple
            persistent-hint
            small-chips
            item-text="name"
            item-value="name"
        />
      </v-col>
      <v-col lg="3">
        <v-checkbox
            v-model="sensorToEdit.continuous"
            label="Continuous Signal"
            :disabled="sensorAlreadyReviewed"
        />
      </v-col>
    </v-row>
    <v-row v-if="sensorAlreadyReviewed">
        <v-col lg="3">
          <v-text-field
              label="Add further dimension(s)"
              required
              v-model="newDimension"
              @keyup.enter.native="appendAdditionalDimension"
          >
            <template v-slot:append>
              <span v-if="newDimension && newDimension.length>0">(press <kbd>enter</kbd> to add "<strong>{{ newDimension }}</strong>")</span>
            </template>
          </v-text-field>
        </v-col>
        <v-col lg="3">
          <v-combobox
              v-model="additionalSensorDimensions"
              deleteable-chips
              clearable
              label="Further Sensor Dimensions"
              multiple
              persistent-hint
              small-chips
              item-text="name"
              item-value="name"
          />
        </v-col>
    </v-row>
    <v-row>
      <v-col lg="2">
        <v-btn color="primary" text v-if="!sensorEditMode" :disabled="!newSensorValid" @click="createUpdateSensor" plain>
          Add Sensor
        </v-btn>
        <v-btn color="primary" text v-if="sensorEditMode" :disabled="!saveSensorValid" @click="createUpdateSensor" plain>
          Save Sensor
        </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>

    import {SensorAPI} from "@/service/api/SensorAPI";
    import constants from "@/helpers/constants";
    import {Util as Utils} from "@/helpers/util";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";

    export default {
        name: "SensorEditor",
        components: {},
        mixins:[LoggedInUserMixin],
        props: {
          allExistingSensors: {
            type: Array,
            default: function () {
              return [];
            },
          },
          sensorForEditing: {
            type: Object,
            default: function() {
              return {};
            }
          }
        },
        data() {
          return {

            sensorToEdit: {
                name: "",
                id: null,
                continuous: false,
                sensorDimensions: [],
                modelledElementDetail: {},
            },
            allSensors: [],
            sensorEditMode:false,
            newSensorRules: {
              nameUnique: () => this.sensorNameNotYetExisting || 'A sensor that name already exists',
              nameNotEmpty: () => this.sensorNameNotEmpty || 'Please enter a sensor name',
            },
            sensorSearch: "",
            newDimension:"",
            additionalSensorDimensions: [],
          }
        },
        computed: {
          sensorAlreadyReviewed: function() {
            if(this.sensorToEdit.modelledElementDetail && this.sensorToEdit.modelledElementDetail.elementLifecycle) {
              return this.sensorToEdit.modelledElementDetail.elementLifecycle.lifecycleState !== constants.review.lifecycleState.created;
            }
            return false;
          },
          sensorNameNotYetExisting: function() {
            if(this.sensorToEdit.name) {
              return this.allSensors.filter(sensor => {
                return sensor.name.toLowerCase() === this.sensorToEdit.name.toLowerCase();
              }).length === 0;
            }

            return true;
          },
          sensorNameNotYetExistingWhileEditing: function() {
            if(this.sensorForEditing.name.toLowerCase()===this.sensorToEdit.name.toLowerCase()) {
              return true;
            }
            return this.allSensors.filter(sensor => {
              return sensor.name.toLowerCase()===this.sensorToEdit.name.toLowerCase();
            }).length===0;
          },
          sensorNameNotEmpty: function() {
            return this.sensorToEdit.name.length!==0;
          },
          newSensorValid: function() {
            return this.sensorNameNotEmpty && this.sensorNameNotYetExisting && this.sensorToEdit.sensorDimensions.length>0;
          },
          saveSensorValid: function() {
            return this.sensorNameNotEmpty && this.sensorNameNotYetExistingWhileEditing && this.sensorToEdit.sensorDimensions.length>0;
          }
        },
        methods: {
          appendDimension: function() {
            this.sensorToEdit.sensorDimensions.push({name:this.newDimension});
            this.newDimension = "";
          },
          appendAdditionalDimension: function() {
            this.additionalSensorDimensions.push({name:this.newDimension});
            this.newDimension = "";
          },
          resetSensorToEdit: function() {
            this.sensorToEdit.id = null;
            this.sensorToEdit.name = "";
            this.sensorToEdit.sensorDimensions = [];
            this.sensorToEdit.continuous = false;
            this.sensorToEdit.modelledElementDetail = {},
            this.sensorEditMode = false;
          },
          createUpdateSensor: function() {
            let sensorDimensionIds = [];

            if (this.sensorAlreadyReviewed) {
              // check which dimensions were added / removed
              let sensorUpdateRequired = false;
              let additionalDimensions = this.sensorForEditing.sensorDimensions.filter(dimension => {
                return dimension.modelledElementDetail.elementLifecycle.lifecycleState===constants.review.lifecycleState.created;
              })
              this.$log.debug("\t...check whether there was a change in the additional dimensions", additionalDimensions);
              let deletedDimensions = additionalDimensions.filter(dimension => {
                return this.additionalSensorDimensions.filter(additionalDimension => {
                  return additionalDimension.name === dimension.name;
                }).length === 0;
              })
              if(deletedDimensions.length>0) {
                this.$log.debug("\t\t..."+deletedDimensions.length+" were deleted, remove from sensor to edit");
                this.sensorToEdit.sensorDimensionIds = this.sensorToEdit.sensorDimensionIds.filter(sensorDimensionId => {
                  return deletedDimensions.filter(deletedDimension => {
                    return deletedDimension.id === sensorDimensionId;
                  }).length === 0;
                })
                this.$log.debug("\t\t..."+this.sensorToEdit.sensorDimensionIds.length+" dimensions remain!");
                sensorUpdateRequired = true;
              } else {
                this.$log.debug("\t\t...NO additional dimension was removed, go on");
              }
              if(this.additionalSensorDimensions.length>0) {
                this.$log.debug("..."+this.additionalSensorDimensions.length+" additional sensor dimensions were reported, persist and add to sensor to edit!");
                SensorAPI.createSensorDimensions(this.additionalSensorDimensions)
                    .then(
                        (responses) => {
                          this.$log.debug("\t...got responses for sensor dimension creation", responses);
                          responses.forEach(response => {
                            sensorDimensionIds.push(response.data);
                          });

                          this.$log.debug("\t..sensor dimensions to add", sensorDimensionIds);
                          this.sensorToEdit.sensorDimensionIds = this.sensorToEdit.sensorDimensionIds.concat(sensorDimensionIds);
                          this.$log.debug("\t\t..sensor to persist", this.sensorToEdit);
                          this.persistSensorUpdate(this.sensorToEdit);

                        });
              } else {
                this.$log.debug("...NO additional sensor dimensions were reported no need to change");
                if(sensorUpdateRequired) {
                  this.persistSensorUpdate(this.sensorToEdit);
                } else {
                  this.sensorEditMode = false;
                }
              }
            } else {

              SensorAPI.createSensorDimensions(this.sensorToEdit.sensorDimensions)
                  .then(
                      (responses) => {
                        this.$log.debug("\t...got responses for sensor dimension creation", responses);
                        responses.forEach(response => {
                          sensorDimensionIds.push(response.data);
                        });

                        let newSensor = {
                          id: this.sensorToEdit.id,
                          name: this.sensorToEdit.name,
                          continuous: this.sensorToEdit.continuous,
                          sensorDimensionIds: sensorDimensionIds,
                          modelledElementDetail: {},
                        }

                        this.setInitialModelLifecycleStateForDomainElement(newSensor);

                        this.persistSensorUpdate(newSensor);

                      });
            }
          },
          persistSensorUpdate(sensorToCreateUpdate) {
            SensorAPI.createUpdateSensor(sensorToCreateUpdate).then((response) => {
              let createdUpdatedSensorId = response.data;
              if(sensorToCreateUpdate.id) {
                createdUpdatedSensorId = sensorToCreateUpdate.id;
              }
              this.resetSensorToEdit();
              this.sensorEditMode = false;
              this.$emit(constants.eventNames.local.artifactCreatedUpdated, createdUpdatedSensorId);
            });
          }
        },
        watch: {
          allExistingSensors: {
            deep: true,
            immediate: true,
            handler: function(allExistingSensors) {
              this.$log.debug("allExistingSensors changed", allExistingSensors);
              if(allExistingSensors && allExistingSensors.length>0) {
                this.allSensors = Utils.deepCopyObject(allExistingSensors);
              }
            }
          },
          sensorForEditing: {
            deep: true,
            immediate: true,
            handler: function(sensorForEditing) {
              this.$log.debug("sensorForEditing changed", sensorForEditing);
              if(sensorForEditing && sensorForEditing.name && sensorForEditing.name.length>0) {
                this.sensorEditMode = true;
                this.sensorToEdit = Utils.deepCopyObject(sensorForEditing);
                if(this.sensorAlreadyReviewed) {
                  this.$log.debug("\t...sensor already reviewed, check if not yet reviewed dimensions exist");
                  this.additionalSensorDimensions = this.sensorToEdit.sensorDimensions.filter(dimension => {
                    return dimension.modelledElementDetail.elementLifecycle.lifecycleState===constants.review.lifecycleState.created;
                  });
                  this.sensorToEdit.sensorDimensions = this.sensorToEdit.sensorDimensions.filter(dimension => {
                    return dimension.modelledElementDetail.elementLifecycle.lifecycleState!==constants.review.lifecycleState.created;
                  })
                }
              } else {
                this.resetSensorToEdit();
              }
            }
          }
        }
    }
</script>

<style scoped>

</style>
<template>
  <div>
    <v-btn :disabled="activatorDisabled" text block :color="activatorColor" @click="openEditor()">{{buttonText}}</v-btn>
    <v-dialog v-model="artifactEditorShown" persistent hide-overlay transition="dialog-bottom-transition">
        <v-card>
            <v-card-title>
                <span class="headline">Add new biometric characteristic</span>
            </v-card-title>
            <v-card-text>
                <v-container grid-list-md>
                    <v-row wrap>
                      <v-col cols="12" lg="12">
                        <p>Type</p>
                        <v-radio-group
                            v-model="characteristicsType"
                            row
                        >
                          <v-radio v-if="allowNewPhysiologicalCharacteristic"
                              label="Physiological"
                              :value="constants.characteristics.types.physiological"
                          ></v-radio>
                          <v-radio v-if="allowNewBehavioralCharacteristic"
                              label="Behavioral"
                              :value="constants.characteristics.types.behavioral"
                          ></v-radio>
                          <v-radio v-if="allowNewSoftCharacteristic"
                              label="Soft"
                              :value="constants.characteristics.types.soft"
                          ></v-radio>
                        </v-radio-group>
                      </v-col>
                    </v-row>
                    <div v-show="characteristicsType===behavioralCharacteristicType">
                      <v-row wrap>
                          <v-col cols="12" lg="8">
                              <v-text-field
                                  label="User Activity Name"
                                  required
                                  v-model="newBehavioralCharacteristic.userActivity.name"
                                  :rules="[newBiometricCharacteristicRules.behavioralBiometricUnique]"
                              />
                          </v-col>
                          <v-col lg="4" v-if="routineBasedCharacteristicsAllowed">
                              <v-tooltip top>
                                  <template v-slot:activator="{ on }">
                                      <v-checkbox
                                              v-model="newBehavioralCharacteristic.routineBased"
                                              label="routine-Based Characteristic?"
                                              v-on="on"
                                      />
                                  </template>
                                  <span>A Routine-Based characteristic focuses on features of goals of user activities,
                                      such as visited locations (which, how often, how long was the stay)<br> or used
                                      applications (which app, how often used, how long etc.). A Non-routine-Based Characteristics
                                      focus on the features of the activity itself such as walking or typing.</span>
                              </v-tooltip>
                          </v-col>
                          <div v-show="!newBehavioralCharacteristic.routineBased">

                          </div>
                      </v-row>
                      <v-row>
                          <v-col cols="12" lg="12" v-show="!newBehavioralCharacteristic.routineBased">
                              <v-checkbox
                                      v-model="newBehavioralCharacteristic.userActivity.isTransitional"
                                      label="Transitional user activity?"
                              />
                          </v-col>
                      </v-row>
                      <v-row>
                          <v-col cols="12" lg="12" v-show="!newBehavioralCharacteristic.userActivity.isTransitional && !newBehavioralCharacteristic.routineBased">
                              <v-text-field
                                      label="Recurrent action"
                                      required
                                      v-model="newBehavioralCharacteristic.userActivity.recurrentAction"
                              />
                          </v-col>
                      </v-row>
                      <v-row>
                          <v-col cols="12" lg="6" v-show="newBehavioralCharacteristic.userActivity.isTransitional && !newBehavioralCharacteristic.routineBased">
                              <v-text-field
                                      label="Situation before the action"
                                      required
                                      v-model="newBehavioralCharacteristic.userActivity.situationBefore"
                              />
                          </v-col>
                          <v-col lg="6" v-show="newBehavioralCharacteristic.userActivity.isTransitional && !newBehavioralCharacteristic.routineBased">
                              <v-text-field
                                      label="Situation after action"
                                      required
                                      v-model="newBehavioralCharacteristic.userActivity.situationAfterwards"
                              />
                          </v-col >
                      </v-row>
                    </div>
                    <div v-show="characteristicsType===physiologicalCharacteristicType">
                      <v-row wrap>
                        <v-col cols="12" lg="8">
                          <v-text-field
                              label="Name"
                              required
                              v-model="newPhysiologicalCharacteristic.name"
                              :rules="[newBiometricCharacteristicRules.physiologicalBiometricUnique]"
                          />
                        </v-col>
                        <v-col cols="12" lg="4">
                          <p>Authentication mode</p>
                          <v-radio-group
                              v-model="newPhysiologicalCharacteristic.authenticationMode"
                              row
                          >
                            <v-radio
                                label="One-Off"
                                value="one-off"
                            ></v-radio>
                            <v-radio
                                label="Continuous"
                                value="continuous"
                            ></v-radio>
                          </v-radio-group>
                        </v-col>
                      </v-row>
                    </div>
                    <div v-show="characteristicsType===softCharacteristicType">
                      <v-row wrap>
                        <v-col cols="12" lg="6">
                          <v-text-field
                              label="Name"
                              required
                              v-model="newSoftCharacteristic.name"
                              :rules="[newBiometricCharacteristicRules.softBiometricUnique]"
                          />
                        </v-col>
                        <v-col lg="6">
                          <v-select
                              :items="allPhysiologicalAndBehavioralCharacteristics"
                              v-model="newSoftCharacteristic.sourceBiometricCharacteristic"
                              :menu-props="{ maxHeight: '400' }"
                              label="From which biometric characteristic was it inferred?"
                              chips
                              clearable
                              item-text="name"
                              item-value="viewId"
                              persistent-hint
                              return-object
                          />
                        </v-col>
                      </v-row>
                    </div>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-btn v-if="!currentlyEditingExistingCharacteristic" color="primary" text @click="createUpdateBiometricCharacteristic()" :disabled="!newCharacteristicValid">Add Biometric Characteristic</v-btn>
                <v-btn v-if="currentlyEditingExistingCharacteristic" color="primary" text @click="createUpdateBiometricCharacteristic()" :disabled="!saveCharacteristicIsAllowed">Save Biometric Characteristic</v-btn>
                <v-btn color="error" text @click="closeEditor()">Cancel</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
  </div>
</template>

<script>

    import {BiometricCharacteristicsService} from "../../service/api/BiometricCharacteristicsService";
    import constants from "@/helpers/constants";
    import {Util as Utils} from "@/helpers/util";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import ArtifactEditorMixin from "@/mixins/model/ArtifactEditorMixin";

    export default {
        components: {},
        name: "BiometricCharacteristicEditor",
        mixins: [LoggedInUserMixin, ArtifactEditorMixin],
        props: {
            buttonText: String,
            routineBasedCharacteristicsAllowed: {
                type: Boolean,
                default: true,
            },
            activatorColor: {
                type: String,
                default: "info",
            },
            activatorDisabled: {
                type: Boolean,
                default: false,
            },
            allAvailableCharacteristics: {
                type: Array,
                default: function () {
                  return [];
                }
            },
            allowNewBehavioralCharacteristic: {
                type: Boolean,
                default: true,
            },
            allowNewPhysiologicalCharacteristic: {
              type: Boolean,
              default: true,
            },
            allowNewSoftCharacteristic: {
              type: Boolean,
              default: true,
            },
            biometricCharacteristicToEdit: {
              type: Object,
              default: function() {
                return {};
              }
            },
        },
        data() {
            return {
                allCharacteristics: [],
                characteristicsType: "",
                newBehavioralCharacteristic: {
                    id: null,
                    type: constants.characteristics.types.behavioral,
                    routineBased: false,
                    userActivity:{
                        name: "",
                        type:"",
                        isTransitional:false,
                        recurrentAction: "",
                        situationBefore: "",
                        situationAfterwards: "",
                    },
                    modelledElementDetail: {},
                },
                newPhysiologicalCharacteristic: {
                  id: null,
                  name: "",
                  type: constants.characteristics.types.physiological,
                  authenticationMode: "",
                  modelledElementDetail: {},
                },
                newSoftCharacteristic: {
                  id: null,
                  name: "",
                  type: constants.characteristics.types.soft,
                  modelledElementDetail: {},
                  sourceBiometricCharacteristic: {},
                },
                newBiometricCharacteristicRules: {
                  softBiometricUnique: () => this.softBiometricNotYetExisting || 'A soft biometric with these characteristics already exists',
                  behavioralBiometricUnique: () => this.behavioralBiometricNotYetExisting || 'A behavioral biometric with these characteristics already exists',
                  physiologicalBiometricUnique: () => this.physiologicalBiometricNotYetExisting || 'A physiological biometric with these characteristics already exists'
                },
            }
        },
        mounted: function() {
        },
        methods: {
            createUpdateBiometricCharacteristic: function() {

                if(this.characteristicsType===constants.characteristics.types.behavioral) {

                  this.setInitialModelLifecycleAndUniqueIdentifierForCharacteristic(this.newBehavioralCharacteristic);
                  if (this.newBehavioralCharacteristic.userActivity.isTransitional) {
                    this.newBehavioralCharacteristic.userActivity.type = constants.characteristics.types.transitional;
                  } else {
                    this.newBehavioralCharacteristic.userActivity.type = constants.characteristics.types.continuous;
                  }


                  BiometricCharacteristicsService.createUpdateBiometricCharacteristic(this.newBehavioralCharacteristic).then(
                      (response) => {

                        this.$emit(constants.eventNames.local.artifactCreatedUpdated, response.data);
                        this.newBehavioralCharacteristic = {
                          routineBased: false,
                          type: constants.characteristics.types.behavioral,
                          userActivity: {
                            name: "",
                            type: "",
                            recurrentAction: "",
                            situationBefore: "",
                            situationAfterwards: "",
                          },
                        };
                        this.closeEditor();
                      }
                  );
                } else if(this.characteristicsType===constants.characteristics.types.physiological) {
                  this.setInitialModelLifecycleAndUniqueIdentifierForCharacteristic(this.newPhysiologicalCharacteristic);
                  BiometricCharacteristicsService.createUpdateBiometricCharacteristic(this.newPhysiologicalCharacteristic).then(
                      (response) => {
                        this.$emit(constants.eventNames.local.artifactCreatedUpdated, response.data);
                        this.newPhysiologicalCharacteristic = {
                          name: "",
                          authenticationMode: "",
                          type: constants.characteristics.types.physiological,
                        };
                        this.closeEditor();
                      }
                  );
                } else {
                  this.setInitialModelLifecycleAndUniqueIdentifierForCharacteristic(this.newSoftCharacteristic);
                  this.newSoftCharacteristic["sourceBiometricCharacteristicId"] = this.newSoftCharacteristic.sourceBiometricCharacteristic.id;
                  BiometricCharacteristicsService.createUpdateBiometricCharacteristic(this.newSoftCharacteristic).then(
                      (response) => {
                        this.$emit(constants.eventNames.local.artifactCreatedUpdated, response.data);
                        this.newSoftCharacteristic = {
                          name: "",
                          type: constants.characteristics.types.soft,
                        };
                        this.closeEditor()
                      }
                  );
                }
            },
            setInitialModelLifecycleAndUniqueIdentifierForCharacteristic: function(biometricCharacteristic) {
              this.$log.debug("...set initial lifecycle state and unique id for characteristic", biometricCharacteristic);
              if(!biometricCharacteristic.id) {
                this.setInitialModelLifecycleStateForDomainElement(biometricCharacteristic);
              }
              biometricCharacteristic.uniqueIdentifier = BiometricCharacteristicsService.getUniqueIdentifier(biometricCharacteristic);
            }
        },
        computed: {
            currentlyEditingExistingCharacteristic: function(){
              return this.editMode && this.biometricCharacteristicToEdit;
            },
            softBiometricNotYetExisting: function() {
              return this.softCharacteristics.filter(softCharacteristic => {
                return softCharacteristic.name.toLowerCase()===this.newSoftCharacteristic.name;
              }).length===0;
            },
            allPhysiologicalAndBehavioralCharacteristics: function() {
              return this.allCharacteristics.filter(characteristic => {
                return characteristic.type === constants.characteristics.types.behavioral
                  || characteristic.type === constants.characteristics.types.physiological
              }).map(characteristic => {
                return BiometricCharacteristicsService.transformCharacteristicForPresentation(characteristic);
              });
            },
            behavioralBiometricNotYetExisting: function() {
              if(this.currentlyEditingExistingCharacteristic) {
                // while editing, we need to pay attention that we are not allowing the creation of an entity that already exists
                // but in the same time, we should allow to change parts under the same name
                return this.behavioralCharacteristics.filter(behavioralCharacteristic => {

                  let activityNameEqual = behavioralCharacteristic.userActivity.name.toLowerCase().replace(" ", "")
                      === this.newBehavioralCharacteristic.userActivity.name.toLowerCase().replace(" ", "");

                  if(this.newBehavioralCharacteristic.userActivity.name
                    === this.biometricCharacteristicToEdit.userActivity.name) {
                    return false;
                  }

                  return activityNameEqual;
                }).length===0;
              } else {
                return this.behavioralCharacteristics.filter(behavioralCharacteristic => {

                  let activityNameEqual = behavioralCharacteristic.userActivity.name.toLowerCase().replace(" ", "")
                      === this.newBehavioralCharacteristic.userActivity.name.toLowerCase().replace(" ", "");

                  if (this.newBehavioralCharacteristic.routineBased && behavioralCharacteristic.routineBased) {
                    return activityNameEqual;
                  } else {
                    if (this.newBehavioralCharacteristic.type === constants.characteristics.types.continuous
                        && behavioralCharacteristic.type === constants.characteristics.types.continuous) {
                      return activityNameEqual
                          && behavioralCharacteristic.userActivity.recurrentAction.toLowerCase().replace(" ", "")
                          === this.newBehavioralCharacteristic.userActivity.recurrentAction.toLowerCase().replace(" ", "")
                    } else if(this.newBehavioralCharacteristic.type === constants.characteristics.types.transitional
                        && behavioralCharacteristic.type === constants.characteristics.types.transitional) {
                      return activityNameEqual
                          && behavioralCharacteristic.userActivity.situationBefore.toLowerCase().replace(" ", "")
                          === this.newBehavioralCharacteristic.userActivity.situationBefore.toLowerCase().replace(" ", "")
                          && behavioralCharacteristic.userActivity.situationAfterwards.toLowerCase().replace(" ", "")
                          === this.newBehavioralCharacteristic.userActivity.situationAfterwards.toLowerCase().replace(" ", "")
                    }
                  }
                }).length === 0;
              }
            },
            physiologicalBiometricNotYetExisting: function() {
              return this.physiologicalCharacteristics.filter(physiologicalCharacteristic => {
                return physiologicalCharacteristic.name.toLowerCase()===this.newPhysiologicalCharacteristic.name
                  && physiologicalCharacteristic.authenticationMode===this.newPhysiologicalCharacteristic.authenticationMode;
              }).length===0;
            },
            saveCharacteristicIsAllowed: function() {
              if(this.characteristicsType===constants.characteristics.types.behavioral) {
                return this.newBehavioralCharacteristicValid;
              }
              if(this.characteristicsType===constants.characteristics.types.soft) {
                return this.newSoftCharacteristicValid;
              }
              return this.newPhysiologicalCharacteristicValid;
            },
            newCharacteristicValid: function() {
              if(this.characteristicsType===constants.characteristics.types.behavioral) {
                return this.newBehavioralCharacteristicValid && this.behavioralBiometricNotYetExisting;
              }
              if(this.characteristicsType===constants.characteristics.types.soft) {
                return this.newSoftCharacteristicValid && this.softBiometricNotYetExisting;
              }
              return this.newPhysiologicalCharacteristicValid && this.physiologicalBiometricNotYetExisting;
            },
            newBehavioralCharacteristicValid: function() {
              this.$log.debug("Verifying new behavioral characteristic valid", this.newBehavioralCharacteristic);
                if(!this.newBehavioralCharacteristic.routineBased) {
                    if (this.newBehavioralCharacteristic.userActivity.isTransitional) {
                        return this.newBehavioralCharacteristic.userActivity.name.length > 0
                            && this.newBehavioralCharacteristic.userActivity.situationBefore.length > 0
                            && this.newBehavioralCharacteristic.userActivity.situationAfterwards.length > 0
                    } else {
                        return this.newBehavioralCharacteristic.userActivity.name.length > 0
                            && this.newBehavioralCharacteristic.userActivity.recurrentAction.length > 0
                    }
                } else {
                    this.$log.debug("return ")
                    return this.newBehavioralCharacteristic.userActivity.name.length > 0;
                }
            },
            newPhysiologicalCharacteristicValid: function() {
              this.$log.debug("Evaluating new physiological characteristic", this.newPhysiologicalCharacteristic);
              return this.newPhysiologicalCharacteristic.name.length>0 && this.newPhysiologicalCharacteristic.authenticationMode.length>0;
            },
            newSoftCharacteristicValid: function() {
              this.$log.debug("Evaluating new soft characteristic", this.newSoftCharacteristic);
              return this.newSoftCharacteristic.name.length>0;
            },
            behavioralCharacteristicType: function() {
              return constants.characteristics.types.behavioral;
            },
            physiologicalCharacteristicType: function() {
              return constants.characteristics.types.physiological;
            },
            softCharacteristicType: function() {
              return constants.characteristics.types.soft;
            },
            behavioralCharacteristics: function() {
              if(this.allCharacteristics) {
                return this.allCharacteristics.filter(characteristic => {
                  return characteristic.type === this.behavioralCharacteristicType;
                })
              }

              return [];
            },
            physiologicalCharacteristics: function() {
              if(this.allCharacteristics) {
                return this.allCharacteristics.filter(characteristic => {
                  return characteristic.type === this.physiologicalCharacteristicType;
                })
              }

              return [];
            },
            softCharacteristics: function() {
              if(this.allCharacteristics) {
                return this.allCharacteristics.filter(characteristic => {
                  return characteristic.type === this.softCharacteristicType;
                })
              }

              return [];
            },
            constants: function() {
              return constants;
            }
        },
        watch: {
          allAvailableCharacteristics: {
            immediate: true,
            deep: true,
            handler: function(allAvailableCharacteristics) {
              if(allAvailableCharacteristics && allAvailableCharacteristics.length>0) {
                this.allCharacteristics = allAvailableCharacteristics;
              }
            }
          },
          biometricCharacteristicToEdit: {
            immediate: true,
            deep: true,
            handler: function(biometricCharacteristicToEdit) {
              if(biometricCharacteristicToEdit && this.editMode) {
                this.$log.debug("Biometric characteristic to edit changed", biometricCharacteristicToEdit);
                // in any case, set id

                switch(biometricCharacteristicToEdit.type) {
                  case constants.characteristics.types.enriched_soft:
                    this.newSoftCharacteristic = Utils.deepCopyObject(biometricCharacteristicToEdit);
                    this.characteristicsType = constants.characteristics.types.soft;
                    break;
                  case constants.characteristics.types.behavioral:
                    // manual copy attribute by attribute as some transformations are required
                    this.newBehavioralCharacteristic.id = biometricCharacteristicToEdit.id;
                    this.newBehavioralCharacteristic.modelledElementDetail = biometricCharacteristicToEdit.modelledElementDetail;
                    this.newBehavioralCharacteristic.routineBased = biometricCharacteristicToEdit.routineBased;
                    this.newBehavioralCharacteristic.userActivity.name = biometricCharacteristicToEdit.userActivity.name;
                    this.newBehavioralCharacteristic.userActivity.type = biometricCharacteristicToEdit.userActivity.type;
                    this.newBehavioralCharacteristic.userActivity.isTransitional =
                        (biometricCharacteristicToEdit.userActivity.type===constants.characteristics.types.transitional);

                    if(this.newBehavioralCharacteristic.userActivity.isTransitional) {
                      this.newBehavioralCharacteristic.userActivity.situationBefore = biometricCharacteristicToEdit.userActivity.situationBefore;
                      this.newBehavioralCharacteristic.userActivity.situationAfterwards = biometricCharacteristicToEdit.userActivity.situationAfterwards;
                    } else {
                      this.newBehavioralCharacteristic.userActivity.recurrentAction = biometricCharacteristicToEdit.userActivity.recurrentAction;
                    }
                    this.characteristicsType = constants.characteristics.types.behavioral;
                    break;
                  case constants.characteristics.types.physiological:
                    this.newPhysiologicalCharacteristic = Utils.deepCopyObject(biometricCharacteristicToEdit);
                    this.characteristicsType = constants.characteristics.types.physiological;
                    break;
                }

                this.openEditor();
              }
            }
          }
        }
    }
</script>

<style scoped>

</style>
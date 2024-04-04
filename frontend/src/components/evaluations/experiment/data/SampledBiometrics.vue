<template>
    <v-container fluid>
        <v-row row wrap>
            <v-col cols="12" lg="12">
                <span class="headline">Sampled Biometrics</span>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" :lg="editMode ? '9' : '12'">
                <v-data-table
                        v-if="sampledBiometrics.length>0"
                        :headers="sampledBiometricHeadersToShow"
                        :items="sampledBiometrics"
                        hide-default-footer
                >
                    <template v-slot:body=" {items} ">
                        <tbody>
                            <tr v-for="item in items" :key="item.name">
                                <td>{{ BiometricCharacteristicsService.transformCharacteristicForPresentation(item.sampledCharacteristic).selectionText }}</td>
                                <td>{{ item.supportCharacteristic ? item.supportCharacteristic.userActivity.name : "-" }}</td>
                                <td>{{ item.samplingContexts.map(context => context.description).join(", ") }}</td>
                                <td>{{ summarizeSamplingDevices(item)}}</td>
                                <template v-if="editMode">
                                    <td>
                                        <v-icon
                                                @click="deleteSampledBiometric(item)"
                                        >
                                            delete
                                        </v-icon>
                                        <v-icon
                                                @click="editSampledBiometric(item)"
                                        >
                                            edit
                                        </v-icon>
                                        <v-icon
                                                @click="duplicateSampledBiometric(item)"
                                        >
                                            file_copy
                                        </v-icon>
                                    </td>
                                </template>
                            </tr>
                        </tbody>
                    </template>
                    <template v-slot:no-data>
                        <v-btn color="primary" @click="resetSensors">Reset</v-btn>
                    </template>
                </v-data-table>
                <div
                        v-if="sampledBiometrics.length === 0 "
                        key="title"
                        class="title font-weight-light grey--text"
                >
                    Describe the what biometric characteristic(s) was sampled in terms of contexts and device(s)
                </div>
            </v-col>
            <v-col offset-lg="1" lg="2">
                <v-dialog v-model="sampledBiometricDialog" transition="dialog-bottom-transition">
                    <template v-slot:activator="{ on }">
                        <v-btn v-if="editMode" text block v-on="on" color="primary" >Add Sampled Biometric</v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">Add specifics of sampled biometric characteristic</span>
                        </v-card-title>
                        <v-card-text>
                          <v-expansion-panels
                              v-model="sampledBiometricPanels"
                          >
                            <v-expansion-panel>
                              <v-expansion-panel-header>Sampled Biometric Characteristics&nbsp;{{sampledBiometricToEdit.sampledCharacteristic ? sampledBiometricCharacteristicSummary : "(not set)"}}</v-expansion-panel-header>
                              <v-expansion-panel-content>
                                <v-row>
                                  <v-col cols="12" lg="4">
                                    <v-select
                                        :readonly = "singleCharacteristicFocus"
                                        :items="baseCharacteristicsToChooseFrom"
                                        v-model="sampledBiometricToEdit.sampledCharacteristic"
                                        :menu-props="{ maxHeight: '400' }"
                                        label="Which of the BASE's characteristic(s) was sampled?"
                                        item-text="selectionText"
                                        persistent-hint
                                        return-object
                                    />
                                  </v-col>
                                  <v-col lg="4">
                                    <v-select
                                        v-if="sampledBiometricIsBehavioral"
                                        :items="supportCharacteristics"
                                        v-model="sampledBiometricToEdit.supportCharacteristic"
                                        :menu-props="{ maxHeight: '400' }"
                                        label="Was there any support characteristic?"
                                        chips
                                        clearable
                                        item-text="userActivity.name"
                                        item-value="userActivity.name"
                                        persistent-hint
                                        return-object
                                    />
                                  </v-col>
                                  <v-col lg="4">
                                    <BiometricCharacteristicEditor
                                        v-if="sampledBiometricIsBehavioral"
                                        :button-text="'Edit Support Characteristics'"
                                        :routine-based-characteristics-allowed="false"
                                        allow-new-behavioral-characteristic
                                        :allow-new-physiological-characteristic="false"
                                        :allow-new-soft-characteristic="false"
                                        v-on:artifact-created-updated="getAllCharacteristics"/>
                                  </v-col>
                                </v-row>
                              </v-expansion-panel-content>
                            </v-expansion-panel>
                            <v-expansion-panel>
                              <v-expansion-panel-header>Sample Devices {{sampledBiometricToEdit.sampleDevices.length>0 ? "("+sampledBiometricToEdit.sampleDevices.length+")" : "(not set)"}}</v-expansion-panel-header>
                              <v-expansion-panel-content>
                                <SampleDeviceBrowser
                                    :sample-devices-to-show="allSampleDevices"
                                    show-select
                                    show-add-option
                                    device-category-editing-allowed
                                    v-on:artifacts-selected="updateUsedSampleDevices"
                                  />
                              </v-expansion-panel-content>
                            </v-expansion-panel>
                            <v-expansion-panel>
                              <v-expansion-panel-header>Sampling Contexts {{sampledBiometricToEdit.samplingContexts.length>0 ? "("+sampledBiometricToEdit.samplingContexts.length+")" : "(not set)"}}</v-expansion-panel-header>
                              <v-expansion-panel-content>
                                <v-row>
                                  <v-col cols="12" lg="12">
                                    <SelectAutoAddComboBoxMultiple
                                        :entity-name="'Sampling Context'"
                                        :all-entities="allSamplingContexts"
                                        :pre-selected-entities="sampledBiometricToEdit.samplingContexts.map(context => context.description)"
                                        :label="'Annotate the sampling situation with further context'"
                                        v-on:entity-list-changed="samplingContextsChanged">
                                    </SelectAutoAddComboBoxMultiple>
                                  </v-col>
                                </v-row>
                              </v-expansion-panel-content>
                            </v-expansion-panel>
                          </v-expansion-panels>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="primary" text @click="saveSampledBiometric()" :disabled="!sampledBiometricValid">
                                {{isNewSampledBiometric ? 'Add' : 'Save'}} Sampled Biometric</v-btn>
                            <v-btn color="error" text @click="sampledBiometricDialog = false">Cancel</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>

    import {BiometricCharacteristicsService} from "@/service/api/BiometricCharacteristicsService";
    import {DatasetAPI} from "@/service/api/DatasetAPI";
    import {Util} from "@/helpers/util";
    import constants from "@/helpers/constants";
    import SelectAutoAddComboBoxMultiple from "@/components/util/SelectAutoAddComboBoxMultiple.vue";
    import SampleDeviceBrowser from "@/components/evaluations/experiment/data/SampleDeviceBrowser.vue";
    import {SensorAPI} from "@/service/api/SensorAPI";
    import BiometricCharacteristicEditor from "@/components/characteristics/BiometricCharacteristicEditor.vue";

    export default {
        components: {
          SampleDeviceBrowser, SelectAutoAddComboBoxMultiple, BiometricCharacteristicEditor},
        name: "SampledBiometric",
        props: {
            baseCharacteristics: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            sampledBiometrics: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            editMode: {
                type: Boolean,
                default: true,
            }
        },
        mounted: function () {
            this.evaluateSingleCharacteristicOnly();
            this.getAllCharacteristics();
            this.getSampleDevices();
            this.getAllSensorDimensions();
            this.getSamplingContexts();
        },
        data() {
            return {
                sampledBiometricPanels: [],
                sampledBiometricHeaders: [
                    { text: 'Sampled Biometric', value: 'sampled_biometric' },
                    { text: 'Support Behaviour', value: 'supBehaviour'},
                    { text: 'Evaluation Contexts', value: 'evalContexts'},
                    { text: 'Sampling Devices', value: 'samplingDevices'},
                    { text: 'Action', value: 'action'},
                ],
                sampledBiometricToEdit: {
                    sampledCharacteristic: null,
                    supportCharacteristic: null,
                    sampleDevices: [],
                    samplingContexts: [],
                },
                sampledBiometricsToEdit:[],
                baseCharacteristicsToChooseFrom: [],
                isNewSampledBiometric: true,
                indexSampledBiometricToEdit: -1,
                selectedSamplingContexts: [],
                allSamplingContexts: [],
                allSampleDevices: [],
                sampleDeviceEditorDialog: false,
                allDeviceManufacturers: [],
                allDeviceCategories: [],
                allSensorDimensions: [],
                selectedSensors: [],
                sampleDeviceTree: [],
                sampledBiometricDialog:false,
                allCharacteristics: [],
                searchSamplingContexts: "",
                searchSampleComponents: null,
            }
        },
        computed: {
            supportCharacteristics() {
              return this.allCharacteristics.filter(biometricCharacteristic => {
                return biometricCharacteristic.type === constants.characteristics.types.behavioral;
              });
            },
            BiometricCharacteristicsService() {
              return BiometricCharacteristicsService
            },
            constants() {
              return constants;
            },
            sampledBiometricHeadersToShow() {
                if(!this.editMode) {
                    return [
                        this.sampledBiometricHeaders[0],
                        this.sampledBiometricHeaders[1],
                        this.sampledBiometricHeaders[2],
                        this.sampledBiometricHeaders[3],
                        ]
                }
                return this.sampledBiometricHeaders;
            },
            sampledBiometricIsBehavioral() {
              return this.sampledBiometricToEdit.sampledCharacteristic
                  && this.sampledBiometricToEdit.sampledCharacteristic.type===constants.characteristics.types.behavioral
            },
            sampledBiometricCharacteristicSummary() {
              let summaryText = "";
              if(this.sampledBiometricToEdit.sampledCharacteristic) {
                summaryText+=this.sampledBiometricToEdit.sampledCharacteristic.selectionText;
                if(this.sampledBiometricToEdit.supportCharacteristic && this.sampledBiometricToEdit.supportCharacteristic.id.length>0) {
                  summaryText+=" (supported by "+this.sampledBiometricToEdit.supportCharacteristic.selectionText+")";
                }
              }
              return summaryText;
            },
            singleCharacteristicFocus() {
                if(!this.baseCharacteristicsToChooseFrom) {
                    return false;
                } else {
                    return this.baseCharacteristicsToChooseFrom.length === 1;
                }
            },

            sampledBiometricValid() {
                if(!this.sampledBiometricToEdit.sampledCharacteristic) {
                    return false;
                }
                return this.sampledBiometricToEdit.sampledCharacteristic
                    && this.sampledBiometricToEdit.sampleDevices.length>0;
            }
        },
        watch: {
            baseCharacteristics: {
              deep: true,
              immediate: true,
              handler: function(baseCharacteristics) {
                if(baseCharacteristics && baseCharacteristics.length>0) {
                  this.baseCharacteristicsToChooseFrom = baseCharacteristics.map(baseCharacteristic => {
                    return BiometricCharacteristicsService.transformCharacteristicForPresentation(baseCharacteristic);
                  });
                  this.$log.debug("Base Characteristics to choose from", baseCharacteristics);
                  this.evaluateSingleCharacteristicOnly();
                }
              }
            },
            sampledBiometrics: {
              deep: true,
              immediate: true,
              handler: function(sampledBiometricsToShow) {
                this.$log.debug("sampledBiometricsToShow changed", sampledBiometricsToShow);
                this.sampledBiometricsToEdit = sampledBiometricsToShow;
              }

            }
        },
        methods: {
            summarizeSamplingDevices:function(item) {
                return item.sampleDevices.map(device => device.name).join(",");
            },
            updateUsedSampleDevices(selectedSampleDevices) {
              this.sampledBiometricToEdit.sampleDevices = selectedSampleDevices;
            },
            evaluateSingleCharacteristicOnly: function() {
                if(this.singleCharacteristicFocus) {
                    this.$log.debug("Single characteristic focus only", this.baseCharacteristicsToChooseFrom);
                    this.sampledBiometricToEdit.sampledCharacteristic = this.baseCharacteristicsToChooseFrom[0];
                }
            },
            samplingContextsChanged: function(allSamplingContexts, selectedSamplingContext, isNewContext, allSelectedSamplingContexts) {
                this.$log.debug("sampling contexts changed!");
                if(isNewContext) {
                  DatasetAPI.createUpdateSamplingContext({description: selectedSamplingContext}).then(() => {
                    DatasetAPI.getSamplingContexts().then(response => {
                      this.allSamplingContexts = response.data.map(samplingContext => {return samplingContext.description});
                    });
                  });
                } else {
                  this.allSamplingContexts = allSamplingContexts;
                }

                this.sampledBiometricToEdit.samplingContexts  = allSelectedSamplingContexts.map(context => { return {description: context}});
            },
            getAllCharacteristics: function() {
                BiometricCharacteristicsService.getBiometricCharacteristics().then(response => {
                    this.allCharacteristics = response.data.map(characteristic => {
                      return BiometricCharacteristicsService.transformCharacteristicForPresentation(characteristic);
                    });
                    this.$log.debug("All existing behavioral characteristics", this.allCharacteristics);
                });
            },
            getAllSensorDimensions: function() {
                SensorAPI.getSensorDimensions().then(response => {
                    this.allSensorDimensions = response.data;
                });
            },
            getSampleDevices: function() {

                // get all sample devices
                this.$log.debug("requesting all sample devices");
                DatasetAPI.getSampleDevices().then(response => {
                    this.allSampleDevices = response.data;
                });

            },
            getSamplingContexts: function() {
                DatasetAPI.getSamplingContexts().then(response => {
                    // prepare List for
                    this.allSamplingContexts = response.data.map(context => context.description);
                });
            },
            saveSampledBiometric: function() {
                if(this.isNewSampledBiometric) {
                    this.sampledBiometricsToEdit.push(this.sampledBiometricToEdit);
                }
                this.$emit("sampled-behaviour-set", this.sampledBiometrics);

                this.sampledBiometricToEdit = {
                    supportCharacteristic: null,
                    sampleDevices: [],
                    sampledCharacteristic: null,
                    samplingContexts: [],
                };
                this.isNewSampledBiometric = true;
                this.sampledBiometricDialog = false;
            },
            deleteSampledBiometric: function(sampledBiometric) {
                const index = this.sampledBiometrics.indexOf(sampledBiometric);
                this.sampledBiometricsToEdit.splice(index, 1);
            },
            duplicateSampledBiometric: function(behaviour) {
                this.sampledBiometricsToEdit.push(Util.deepCopyObject(behaviour));
            },
            editSampledBiometric: function(sampledBiometric) {
                this.indexSampledBiometricToEdit = this.sampledBiometrics.indexOf(sampledBiometric);
                this.$log.debug("index of sampled biometric to edit", this.indexSampledBiometricToEdit);
                this.sampledBiometricToEdit = this.sampledBiometrics[this.indexSampledBiometricToEdit];
                if(this.sampledBiometricToEdit.supportCharacteristic && this.sampledBiometricToEdit.supportCharacteristic.id) {
                  this.sampledBiometricToEdit.supportCharacteristic =
                      BiometricCharacteristicsService.transformCharacteristicForPresentation(this.sampledBiometricToEdit.supportCharacteristic);
                }
                this.$log.debug("sampled biometric to edit", this.sampledBiometricToEdit);
                this.sampledBiometricDialog = true;
                this.isNewSampledBiometric = false;
            },
        }
    }
</script>

<style scoped>

</style>
<template>
    <v-container fluid>
        <v-row row wrap>
            <v-col cols="12" lg="12">
                <div class="headline">Which biometric characteristics are used by your authentication system?<span v-if="selectedCharacteristics.length>0">&nbsp;({{selectedCharacteristics.length}})</span></div>
                <span>Biometric authentication systems usually use one or more <i>physiological</i> or <i>behavioral</i> biometric characteristics
                        to identify persons or to verify claimed identities. Typical examples are systems based on
                        fingerprints or voice (<i>physiological</i>), or keystrokes or gait (<i>behavioral</i>), for example, or even a mixture of many. Which are included in your system?</span>
            </v-col>
        </v-row>
        <v-row>
            <v-col lg="1">
              <v-checkbox
                  v-model="showBehavioralCharacteristics"
                  :label="`Behavioral`"
              ></v-checkbox>
            </v-col>
            <v-col lg="1">
              <v-checkbox
                  v-model="showPhysiologicalCharacteristics"
                  :label="`Physiological`"
              ></v-checkbox>
            </v-col>
            <v-col lg="1">
              <v-checkbox
                  v-model="showSoftCharacteristics"
                  :label="`Soft`"
              ></v-checkbox>
            </v-col>
            <v-col lg="6">
              <v-text-field
                  label="Search Characteristic"
                  v-model="searchFilterCharacteristic"
                />
            </v-col>
            <v-col lg="3">
                <BiometricCharacteristicEditor
                        :button-text="'My Characteristic is missing'"
                        :activator-color="'red accent-1'"
                        :biometric-characteristic-to-edit="biometricCharacteristicToEdit"
                        :editing-allowed="editingAllowedExistingBiometricCharacteristic"
                        :all-available-characteristics="existingCharacteristics"
                        v-on:artifact-created-updated="addSelectedCharacteristic"/>
            </v-col>
        </v-row>
        <v-row cols="12" lg="12">
          <v-col lg="12">
            <BiometricCharacteristicBrowser
                :show-review-action="false"
                :biometric-characteristics-to-show="filteredCharacteristics"
                :show-lifecycle-state-information="true"
                :filter-text="searchFilterCharacteristic"
                :allow-selection="true"
                :pre-selected-characteristics="selectedCharacteristics"
                v-on:artifacts-selected="setSelectedCharacteristics"
                v-on:edit-artifact="editBiometricCharacteristic"
            />
          </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import {BiometricCharacteristicsService} from "../../service/api/BiometricCharacteristicsService";
    import BiometricCharacteristicEditor from "./BiometricCharacteristicEditor.vue";
    import constants from "@/helpers/constants";
    import BiometricCharacteristicBrowser from "@/components/characteristics/BiometricCharacteristicBrowser.vue";
    import BiometricCharacteristicOperationsMixin from "@/mixins/model/BiometricCharacteristicOperationsMixin";

    export default {
        name: "BiometricCharacteristicsSelector",
        components: {BiometricCharacteristicEditor, BiometricCharacteristicBrowser},
        mixins:[BiometricCharacteristicOperationsMixin],
        mounted: function () {
            this.getAllCharacteristics();
        },
        props: {
           characteristicsState: {
               type: Array,
               default: function() {
                   return [];
               }
           },
        },
        computed: {
          behavioralBiometric: function() {
            return constants.characteristics.types.behavioral;
          },
          physiologicalBiometric: function() {
            return constants.characteristics.types.physiological;
          },
          softBiometric: function() {
            return constants.characteristics.types.enriched_soft;
          },
          filteredCharacteristics: function() {
            this.$log.debug("recalculate sub set of statistics to show based on overall "+this.existingCharacteristics.length+" characteristics");
            if(this.existingCharacteristics && this.existingCharacteristics.length>0) {
              return this.existingCharacteristics.filter(characteristic => {
                if (this.showPhysiologicalCharacteristics && characteristic.type === this.physiologicalBiometric) {
                  return characteristic;
                }
                if (this.showBehavioralCharacteristics && characteristic.type === this.behavioralBiometric) {
                  return characteristic;
                }
                if (this.showSoftCharacteristics && characteristic.type === this.softBiometric) {
                  return characteristic;
                }
              });
            }
            return [];
          }
        },
        watch: {
            characteristicsState: {
                deep: true,
                immediate: true,
                handler: function(newCharacteristicsState) {
                    if(newCharacteristicsState) {
                        this.selectedCharacteristics = newCharacteristicsState;
                    }
                }
            },
            selectedCharacteristics: {
              deep: true,
              immediate: true,
              handler: function(selectedCharacteristics) {
                if(selectedCharacteristics) {
                  this.$log.debug("selected characteristics changed", selectedCharacteristics);
                }
              }
            }
        },
        data() {
          return {
              newSampleDeviceDialog: false,
              newCharacteristic: {
                  routineBased: false,
                  userActivity:{
                      name: "",
                      type:"",
                      isTransitional:false,
                      recurrentAction: "",
                      situationBefore: "",
                      situationAfterwards: "",
                  },
              },
              selectedCharacteristics: [],
              showBehavioralCharacteristics: true,
              showPhysiologicalCharacteristics: true,
              showSoftCharacteristics: true,
              searchFilterCharacteristic: "",
          }
        },
        methods: {
            addSelectedCharacteristic: function(newCharacteristicId) {
              this.$log.debug("got info on new characteristic with id just added", newCharacteristicId);
              BiometricCharacteristicsService.getBiometricCharacteristics().then((response) => {
                this.existingCharacteristics = response.data;
                this.selectedCharacteristics.push(this.existingCharacteristics.find(characteristic => characteristic.id === newCharacteristicId));
              });

            },
            setSelectedCharacteristics: function(chosenCharacteristics) {
              this.selectedCharacteristics = chosenCharacteristics;
              if(this.selectedCharacteristics.length>0) {
                this.$emit('set-characteristics',this.selectedCharacteristics);
              } else {
                this.$emit('reset-characteristics');
              }
            },
        }
    }
</script>

<style scoped>

</style>
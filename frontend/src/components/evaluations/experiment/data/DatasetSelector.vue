<template>
    <v-container fluid>
        <v-row row wrap>
            <v-col cols="12" lg="8">
                <div class="headline">Datasets</div>
                <span>Data collected in supervised or unsupervised studies are needed for an evaluation. Please define the datasets you used
                </span>
            </v-col>
          <v-col lg="2">
            <v-dialog v-model="datasetBrowserDialog"  transition="dialog-bottom-transition">
              <template v-slot:activator="{ on }">
                <v-btn text block v-on="on" color="info" >Dataset Browser</v-btn>
              </template>
              <v-card>
                <v-card-title>
                  <span class="headline">Available datasets for {{characteristicsSummary}} </span>
                </v-card-title>
                <v-card-text>
                  <DatasetBrowser
                      :stand-alone="false"
                      :datasets-to-show="allFittingDatasets"
                      :edit-mode="false"
                      :allow-selection="true"
                      :browser-title="'Mark the datasets you used for your experiment'"
                      v-on:selected-datasets-changed="setUsedDatasets"
                      v-on:artifact-needs-creation="datasetDialog=true"/>
                </v-card-text>
                <v-card-actions>
                  <v-btn color="info" text @click="datasetBrowserDialog = false">OK</v-btn><v-btn color="error" text @click="datasetBrowserDialog = false">Close</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-col>
          <v-col lg="2">
            <v-dialog v-model="datasetDialog"  hide-overlay transition="dialog-bottom-transition">
              <template v-slot:activator="{ on }">
                <v-btn text block v-on="on" color="info" >Model new Dataset</v-btn>
              </template>
              <DatasetEditor
                  :base-characteristics="baseCharacteristics"
                  v-on:artifact-created-updated = "datasetCreatedUpdated"
                  v-on:close-model-dialog="datasetDialog = false"/>
            </v-dialog>
          </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12">
                <DatasetBrowser v-if="usedDatasets.length>0"
                        :datasets-to-show="usedDatasets"
                        :browser-title="'Used Datasets Details'"
                        :delete-datasets-allowed="true"
                        :edit-mode="true"
                        v-on:selected-datasets-changed="setUsedDatasets"
                        v-on:delete-artifact="removeUsedDataset">

                </DatasetBrowser>
            </v-col>
        </v-row>
      <v-row>

      </v-row>
    </v-container>
</template>

<script>

    import DatasetEditor from "./DatasetEditor";
    import {DatasetAPI} from "../../../../service/api/DatasetAPI";
    import EventBus from "../../../../helpers/eventBus";
    import DatasetBrowser from "./DatasetBrowser";
    import constants from "../../../../helpers/constants";
    import {BiometricCharacteristicsService} from "@/service/api/BiometricCharacteristicsService";

    export default {

        components: {DatasetEditor, DatasetBrowser},
        name: "DatasetSelector",
        props: {
            baseCharacteristics: {
              type: Array,
              default: function() {
                return [];
              }
            },
            usedDatasetsState: {
                type:Array,
                default: function() {
                    return [];
                }
            }
        },
        mounted: function() {

            this.getAllDatasets();

            // in case a new dataset is created in general or any existing is updated, do update!
            EventBus.$on(constants.eventNames.global.updateDatasetsRequired, () => {
                this.getAllDatasets();
            });

        },
        data() {
            return {
                allFittingDatasets:[],
                usedDatasets: [],
                datasetDialog: false,
                datasetBrowserDialog: false,
            }
        },
        computed: {
          characteristicsSummary: function() {
            return this.baseCharacteristics.map(characteristic => {
              return BiometricCharacteristicsService.transformCharacteristicForPresentation(characteristic);
            }).map(characteristic => characteristic.selectionText).join(",");
          },
        },
        watch: {
            usedDatasets: {
                deep: true,
                handler: function() {
                    this.$emit("used-datasets-set", this.usedDatasets);
                }
            },
            usedDatasetsState: {
                deep: true,
                immediate: true,
                handler: function(newUsedDatasetsState) {
                    if(newUsedDatasetsState && newUsedDatasetsState.length>0) {
                        this.$log.debug("Set datasets from outside in used datasets browser", newUsedDatasetsState);
                        this.usedDatasets = newUsedDatasetsState;
                    }
                },
            }
        },
        methods: {
            getAllDatasets: function() {
                DatasetAPI.getAllDatasets().then((response) => {
                  this.allFittingDatasets = this.filterAllDatasetsForBaseSpecificSelection(response.data);
                });
            },
            filterAllDatasetsForBaseSpecificSelection: function(allDatasets) {
             return allDatasets.filter(dataset => {

                    return dataset.sampledBiometrics.filter(biometric => {

                      return this.baseCharacteristics.filter(characteristic => {
                        return biometric.sampledCharacteristic.uniqueIdentifier === characteristic.uniqueIdentifier
                      }).length>0;

                    }).length>0;

                  }).map(dataset => {
                    dataset["summary"] = dataset.name + " (" + [...new Set(dataset.sampledBiometrics.map(biometric => biometric.sampledCharacteristic.name))].join(",")+")"
                    return dataset;
                  });
            },
            datasetCreatedUpdated: function(dataset) {

                const indexDataset = this.usedDatasets.findIndex(function(usedDataset) {
                    return usedDataset.name === dataset.name
                });

                this.$log.debug("Selected dataset", dataset);

                if(indexDataset > -1) {
                    this.usedDatasets[indexDataset] = dataset;
                }  else {
                    DatasetAPI.getAllDatasets().then((response) => {
                      this.allFittingDatasets = this.filterAllDatasetsForBaseSpecificSelection(response.data);
                      this.usedDatasets.push(this.allFittingDatasets.find(fittingDataset => {
                        return fittingDataset.id === dataset.id;
                      }));
                    });
                }

            },
            setUsedDatasets: function(selectedDatasets) {
              this.usedDatasets = selectedDatasets;
            },
            removeUsedDataset: function(datasetToRemove) {
                this.$log.debug("removing dataset from used datasets", datasetToRemove);
                const index = this.usedDatasets.findIndex(dataset => {
                    return datasetToRemove.id === dataset.id;
                });
                this.$log.debug("\t...found dataset to remove from used datasets", index);
                this.usedDatasets.splice(index, 1);
            },

        }
    }
</script>

<style scoped>

</style>
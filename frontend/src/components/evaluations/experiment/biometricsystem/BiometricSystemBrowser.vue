<template>
    <v-container v-if="biometricSystems.length>0" fluid>
        <v-row>
            <v-col cols="12" lg="12">
              <span class="subtitle-1">Modelled Biometric Systems</span>
              <v-spacer></v-spacer>
              <v-text-field v-if="standAlone"
                  v-model="search"
                  append-icon="mdi-magnify"
                  label="Search"
                  single-line
                  hide-details
              ></v-text-field>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12">
                <v-data-table
                        :headers="biometricSystemsHeaders"
                        :items="biometricSystems"
                        :hide-default-footer="!standAlone"
                        :search="search"
                        :custom-filter="filterInAllAttributes"
                >
                    <template v-slot:item.name = {item}>
                    {{ item.name }}
                    </template>
                    <template v-if="standAlone" v-slot:item.parentBaseName = {item}>
                      <a @click="browseToBaseInformation(item)">{{ item.parentBaseName }}</a>
                    </template>
                    <template v-slot:item.fusionType = {item}>
                        {{ getFusionTypeName(item.fusionType) }}
                    </template>
                    <template v-slot:item.dataInputIds = {item}>
                        <span v-if="item.dataInputIds">{{ item.dataInputIds.length }}</span>
                        <span v-else>-</span>
                    </template>
                    <template v-slot:item.signalProcessingSteps = {item}>
                        {{ summarizeSignalProcessingSteps(item) }}
                    </template>
                    <template v-slot:item.furtherProcessingSteps = {item}>
                        {{ summarizeFurtherProcessingSteps(item)}}
                    </template>
                  <template v-if="showLifecycleStateInformation" v-slot:item.lifecycleState = {item}>
                    {{ getLifecycleStateSummary(getUniqueUserId(), item) }}
                  </template>
                    <template v-slot:item.action = {item}>
                        <ArtifactBrowserActionUtils
                            :artifact-for-actions="item"
                            :delete-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                            :edit-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                            :duplicate-artifact-allowed="userCanEdit(item, getUniqueUserId())"
                            :show-review-action="showReviewAction"
                            :user-is-reviewer="userIsReviewer"
                            :artifact-detail-view-available="true"
                            v-on:edit-artifact="editArtifact"
                            v-on:delete-artifact="deleteArtifact"
                            v-on:show-artifact-details="showBiometricSystemDetails"
                            />
                    </template>
                </v-data-table>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>

    import constants from "../../../../helpers/constants";
    import {BiometricSystemAPI} from "@/service/api/BiometricSystemAPI";
    import BrowserForReviewableArtifactsMixin from "@/mixins/BrowserForReviewableArtifactsMixin";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import {
      BiometricAuthenticationSystemAndEvaluationAPI
    } from "@/service/api/BiometricAuthenticationSystemAndEvaluationAPI";
    import ArtifactBrowserActionUtils from "@/components/review/ArtifactBrowserActionUtils.vue";

    export default {

        components: {ArtifactBrowserActionUtils},
        name: "BiometricSystemBrowser",
        mixins: [BrowserForReviewableArtifactsMixin, LoggedInUserMixin],
        props: {
            biometricSystemsToShow: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            datasets: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            editMode: {
                type: Boolean,
                default: false,
            },
            standAlone: {
              type: Boolean,
              default: false,
            }
        },
        data() {
            return {
                biometricSystems: [],
                search:"",
            }
        },
        computed: {
          biometricSystemsHeaders: function() {
            if(this.showLifecycleStateInformation) {
              return [
                { text: 'Name', value: 'name' },
                { text: 'Parent Base', value: 'parentBaseName', filterable:true},
                { text: 'Fusion Type', value: 'fusionType', filterable: true },
                { text: '#Data Inputs', value: 'dataInputIds' },
                { text: 'Signal Processing Steps', value: 'signalProcessingSteps', filterable: true },
                { text: 'Further Processing Steps', value: 'furtherProcessingSteps', filterable: true},
                this.lifecycleStateHeader,
                { text: 'Action', value: 'action' },
              ];
            } else {
              return [
                { text: 'Name', value: 'name' },
                { text: 'Parent Base', value: 'parentBaseName', filterable:true},
                { text: 'Fusion Type', value: 'fusionType', filterable: true },
                { text: '#Data Inputs', value: 'dataInputIds' },
                { text: 'Signal Processing Steps', value: 'signalProcessingSteps', filterable: true },
                { text: 'Further Processing Steps', value: 'furtherProcessingSteps', filterable: true},
                { text: 'Action', value: 'action' },
              ];
            }
          }
        },
        beforeRouteEnter (to, from, next) {
          if(to.path === constants.routes.browseBiometricSystems.path) {
            to.params["standAlone"] = true;
            to.params["showModellerInformation"] = true;
          }

          next();
        },
        watch: {
          biometricSystemsToShow: {
            deep: true,
            immediate: true,
            handler: function() {
              this.setBiometricSystemsBasedOnProvidedBiometricSystems();
            },
          }
        },
        methods: {
            getFusionTypeName: function(specificFusionTypeValue) {
                return constants.fusionTypes.getFusionTypeName(specificFusionTypeValue);
            },
            showBiometricSystemDetails: function(biometricSystem) {
              this.$emit(constants.eventNames.local.showArtifactDetails, biometricSystem);
            },
            getAllBiometricSystems: function() {
                BiometricSystemAPI.getBiometricSystems().then(response => {
                  this.biometricSystems = response.data;
                })
            },
            setBiometricSystemsBasedOnProvidedBiometricSystems: function() {
                if(this.biometricSystemsToShow && this.biometricSystemsToShow.length>0) {
                  this.$log.debug("Set biometric systems to show in browser based on passed systems");
                  this.biometricSystems = this.biometricSystemsToShow;
                }
            },
            browseToBaseInformation: function(biometricSystem) {
                this.$log.debug("browse to BASE "+biometricSystem.parentBaseName+" from biometric system "+biometricSystem.name);
                BiometricAuthenticationSystemAndEvaluationAPI.getBiometricAuthenticationSystemWithSpecificName(biometricSystem.parentBaseName).then(response => {
                  this.showBaseDetails(biometricSystem.parentBaseName, response.data);
                });

            },
            showBaseDetails(parentBaseName, baseData) {
              this.$log.debug("\t..show details for base", baseData);
              this.$router.push({name:"base-details", params: {baseToShow: baseData, name: parentBaseName}});
            },
            summarizeSignalProcessingSteps: function(biometricSystem) {
              return biometricSystem.signalProcessingSteps && biometricSystem.signalProcessingSteps.length>0 ? biometricSystem.signalProcessingSteps.map(step => {
                return step.name + " ("+step.processingType+(step.processingType==="feature" ? "s: "+biometricSystem.features.map(feature => feature.name).join(",") : "")
                    +")"
              }).join(",") : "-";
            },
            summarizeFurtherProcessingSteps: function(biometricSystem) {
              return biometricSystem.furtherProcessingSteps && biometricSystem.furtherProcessingSteps.length>0 ? biometricSystem.furtherProcessingSteps.map(step => {
                return step.name + " ("+step.processingType+")"
              }).join(",") : "-"
            },
            containedInSignalProcessing: function(search, item) {
              //this.$log.debug("Check if "+search.toLowerCase()+" in "+this.summarizeProcessingSteps(item).toLowerCase());
              return this.summarizeSignalProcessingSteps(item).toLowerCase().indexOf(search.toLowerCase())>0;
            },
            containedInFurtherProcessing: function(search, item) {
              //this.$log.debug("Check if "+search.toLowerCase()+" in "+this.summarizeProcessingSteps(item).toLowerCase());
              return this.summarizeFurtherProcessingSteps(item).toLowerCase().indexOf(search.toLowerCase())>0;
            },
            filterInAllAttributes (value, search, item) {
              return value && value.length>0
                  && search && search.length>0
                  && (this.containedInSignalProcessing(search, item)
                      || this.containedInFurtherProcessing(search, item));
            },
        },
        mounted: function() {
            if(this.standAlone) {
               this.$log.debug("Biometric system browser mounted as standalone");
               this.getAllBiometricSystems();
            } else {
               this.$log.debug("Biometric system browser not mounted standalone");
            }
        },
    }
</script>

<style scoped>

</style>
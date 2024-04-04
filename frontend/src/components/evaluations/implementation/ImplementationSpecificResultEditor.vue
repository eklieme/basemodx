<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-container fluid>
        <v-row justify="center" align="center" v-for="(result, index) in implementationSpecificResults" v-bind:key="result.criteria.id">
            <v-col cols="12" lg="6">
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <p v-on="on" class="title">{{result.criteria.name}}</p>
                    </template>
                </v-tooltip>
            </v-col>
            <v-col lg="6">
                <v-select
                        :items="deviceCategories"
                        v-model="result.affectedDeviceCategories"
                        label="Select affected deployment locations for this criteria"
                        multiple
                        small-chips
                        :rules="[rules.affectedDeviceCategories]"
                        item-text="deploymentLocation.location"
                        item-value="deploymentLocation.location"
                        persistent-hint
                        return-object
                >
                    <template v-slot:prepend-item>
                        <v-list-item
                                ripple
                                @click="toggleAllDeviceCategories(index)"
                        >
                            <v-list-item-action>
                                <v-icon :color="result.affectedDeviceCategories.length > 0? 'indigo darken-4' : ''">{{ selectAllIcon(index) }}</v-icon>
                            </v-list-item-action>
                            <v-list-item-content>
                                <v-list-item-title>Select All</v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                        <v-divider class="mt-2"></v-divider>
                    </template>
                </v-select>
            </v-col>
            <v-col cols="12" lg="12">
                <v-textarea
                        label="Describe your Implementation Specifica (e.g. Technologies, Frameworks, Protocols)"
                        required
                        v-model="result.description"
                />
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12">
                <v-btn text color="primary" :disabled="!allowAddImplementationSpecificResults" @click="reportImplementationSpecificResults">Add Results</v-btn>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>

    import constants from "../../../helpers/constants";

    export default {
        components: {},
        name: "ImplementationSpecificResultEditor",
        props:{
            deviceCategories: {
                type: Array,
                handler: function() {
                    return []
                },
            },
            implementationSpecificEvaluationCriteria: {
                type: Array,
                handler: function() {

                },
            }
        },
        data() {
            return {
                implementationSpecificResults: [],
                rules: {
                    affectedDeviceCategories: value => {
                        return constants.rules.atLeastOneElementRequired(value, "affected deployment location");
                    },
                }
            }
        },
        watch: {
            implementationSpecificEvaluationCriteria: {
                deep: true,
                handler: function()  {
                    this.initializeImplementationSpecificEvaluationResults();
                }

            }
        },
        mounted:function() {
            this.initializeImplementationSpecificEvaluationResults();
        },
        computed: {
            allowAddImplementationSpecificResults: function() {
                let resultsValid = true;

                this.implementationSpecificResults.every(function(result) {
                    if(result.description.length === 0 ||
                        result.affectedDeviceCategories.length === 0) {
                        resultsValid = false;
                    }
                });
                return resultsValid;
            }
        },
        methods: {
            initializeImplementationSpecificEvaluationResults: function() {
                if(this.implementationSpecificEvaluationCriteria.length > 0) {
                    //initial change, due create result skeleton
                    this.$log.debug("initially creating specific result skeleton for implementation specific results")
                    this.implementationSpecificResults = this.implementationSpecificEvaluationCriteria.map(criteria => {

                        return {
                            criteria: criteria,
                            affectedDeviceCategories: [],
                            description: "",
                        }

                    });
                } else {
                    // selected set changed, do nothing yet
                    this.$log.debug("Selected set of criteria changed, do nothing YET")
                }
            },
            toggleAllDeviceCategories: function(resultIndex) {
                this.$nextTick(() => {
                    if (this.allDeviceCategoriesSelectedForCriteria(resultIndex)) {
                        this.implementationSpecificResults[resultIndex].affectedDeviceCategories = [];
                    } else {
                        this.implementationSpecificResults[resultIndex].affectedDeviceCategories = this.deviceCategories.slice()
                    }
                });
            },
            allDeviceCategoriesSelectedForCriteria: function(resultIndex) {
                return this.deviceCategories.length
                    === this.implementationSpecificResults[resultIndex].affectedDeviceCategories.length;
            },
            selectedSomeLocations: function(resultIndex) {
                return this.implementationSpecificResults[resultIndex].affectedDeviceCategories.length > 0
                    && !this.allDeviceCategoriesSelectedForCriteria(resultIndex);
            },
            selectAllIcon: function(resultIndex) {
                if (this.allDeviceCategoriesSelectedForCriteria(resultIndex)) {
                    return 'mdi-close-box';
                }
                if (this.selectedSomeLocations(resultIndex)) {
                    return 'mdi-minus-box';
                }

                return 'mdi-checkbox-blank-outline'
            },
            reportImplementationSpecificResults: function() {

                // modify results prior to publish
                this.implementationSpecificResults.forEach(function(result) {

                    // set criteria type
                    result.criteria.type = constants.evaluationTypes.implementationSpecific;


                });
                this.$emit("report-implementation-specific-results", this.implementationSpecificResults);
            }
        }
    }
</script>

<style scoped>

</style>
<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-data-table
            :headers="implementationSpecificResultsHeader"
            :items="reportedImplementationSpecificResultsToShow"
            hide-default-footer
    >
        <template v-slot:body=" { items } ">
            <tbody>
            <tr v-for="item in items" :key="item.name">
                <td>{{ item.criteria.name + " ("+item.criteria.category+")" }}</td>
                <td>
                    <v-edit-dialog
                            :return-value.sync="item.affectedDeviceCategories"
                            large
                            lazy
                            persistent
                            v-if="allowEditing"
                    >
                        <div>{{ item.affectedDeviceCategories.join(", ") }}</div>
                        <template v-slot:input>
                            <div class="mt-3 title">Update Deployment Locations</div>
                            <v-select
                                    :items="deviceCategories"
                                    v-model="item.affectedDeviceCategories"
                                    label="Select affected device categories for this criteria"
                                    multiple
                                    small-chips
                                    :rules="[rules.affectedDeviceCategories]"
                                    item-text="deviceCategory.name"
                                    item-value="deviceCategory.name"
                                    persistent-hint
                                    return-object
                            />
                        </template>
                    </v-edit-dialog>
                    <span v-else>{{ summarizeDeviceCategories(item.affectedDeviceCategories) }}</span>
                </td>
                <td>
                    <v-edit-dialog
                            :return-value.sync="item.description"
                            large
                            lazy
                            persistent
                            v-if="allowEditing"
                    >
                        <div>{{ item.description }}</div>
                        <template v-slot:input>
                            <div class="mt-3 title">Update Description</div>
                            <v-textarea
                                    v-model="item.description"
                                    label="Edit"
                                    type="string"
                                    autofocus
                            />
                        </template>
                    </v-edit-dialog>
                    <span v-else>{{ item.description }}</span>
                <td v-if="allowEditing">
                    <v-icon
                            small
                            @click="deleteSpecificResult(item)"
                    >
                        delete
                    </v-icon>
                </td>
            </tr>
            </tbody>
        </template>
    </v-data-table>
</template>

<script>

    import constants from "../../../helpers/constants";

    export default {
        components: {},
        name: "ImplementationSpecificResultsBrowser",
        props:{
            reportedImplementationSpecificResults: {
                type: Array,
                handler: function() {
                    return [];
                }
            },
            deviceCategories: {
                type: Array,
                handler: function() {
                    return [];
                }
            },
            allowEditing: {
                type: Boolean,
                default: false,
            }
        },
        data() {
            return {
                implementationSpecificResultsHeader: [
                    { text: 'Criteria (category)', value: 'criteria' },
                    { text: 'Affected Device Categories', value: 'affectedDeviceCategories' },
                    { text: 'Description', value: 'description' },
                ],
                rules: {
                    affectedDeviceCategories: value => {
                        return constants.rules.atLeastOneElementRequired(value, "affected device category");
                    },
                },
                reportedImplementationSpecificResultsToShow: [],
            }
        },
        watch: {
            reportedImplementationSpecificResults: {
                deep: true,
                handler: function(reportedImplementationSpecificResults) {
                    this.$log.debug("Reported implementation specific experiment results changed to ", reportedImplementationSpecificResults);
                    this.reportedImplementationSpecificResultsToShow = reportedImplementationSpecificResults
                }
            }
        },
        created:function() {
        },
        computed: {
        },
        methods: {
            deleteSpecificResult: function(result) {
                const index = this.reportedImplementationSpecificResults.indexOf(result);
                this.reportedImplementationSpecificResultsToShow.splice(index, 1);
                this.$emit("implementation-specific-results-modified", this.reportedImplementationSpecificResultsToShow);
            },
            summarizeDeviceCategories: function(affectedDeviceCategories) {
                 this.$log.debug("summarizing affected device categories", affectedDeviceCategories);
                 return affectedDeviceCategories.map(deviceCategory => deviceCategory.name).join(",");
            }
        }
    }
</script>

<style scoped>

</style>
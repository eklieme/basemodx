<template>
    <v-container fluid>
        <v-row row wrap>
            <v-col cols="12" lg="12" v-if="!targetArchitecture.skipTargetArchitecture">
                <v-data-table
                        :headers="targetArchitectureHeaders"
                        :items="getSpecificTargetArchitectureItems"
                        class="elevation-1"
                        hide-default-footer
                        disable-sort
                >
                    <template slot="items" slot-scope="props">
                        <td><b>{{ props.item.task }}</b></td>
                        <td>{{ props.item.deviceCategories }}</td>
                    </template>
                </v-data-table>
            </v-col>
            <v-col cols="12" lg="12" v-else>
                No specific architecture exists
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    export default {
        props: {
            targetArchitecture: {
                type: Object,
                default: function() {
                    return {
                        dataCapturingDeviceCategories: [],
                        signalProcessingDeviceCategories: [],
                        signatureCreationDeviceCategories: [],
                        dataStorageDeviceCategories: [],
                        matchingDeviceCategories: [],
                        decisionDeviceCategories: [],
                    };
                }
            },
        },
        data() {
            return {
                targetArchitectureHeaders: [
                    { text: "Task", value: "task" },
                    { text: "Device Categories", value: "deviceCategories" },
                ],
            }
        },
        computed: {
            getSpecificTargetArchitectureItems: function() {
                return [
                        {
                            task: "Signal Aquisition",
                            deviceCategories: this.targetArchitecture.dataCapturingDeviceCategories.map(deviceCategory => deviceCategory.name).join(", "),
                        },
                        {
                            task: "Signal Processing",
                            deviceCategories: this.targetArchitecture.signalProcessingDeviceCategories.map(deviceCategory => deviceCategory.name).join(", "),
                        },
                        {
                            task: "Template Creation (Enrollment)",
                            deviceCategories: this.targetArchitecture.signatureCreationDeviceCategories.map(deviceCategory => deviceCategory.name).join(", "),
                        },
                        {
                            task: "Data Storage",
                            deviceCategories: this.targetArchitecture.dataStorageDeviceCategories.map(deviceCategory => deviceCategory.name).join(", "),
                        },
                        {
                            task: "Matching",
                            deviceCategories: this.targetArchitecture.matchingDeviceCategories.map(deviceCategory => deviceCategory.name).join(", "),
                        },
                        {
                            task: "Decision",
                            deviceCategories: this.targetArchitecture.decisionDeviceCategories.map(deviceCategory => deviceCategory.name).join(", "),
                        }
                    ];
            }
        },
        methods: {

        }
    }
</script>

<style scoped>

</style>
<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card>
        <v-card-title>
            Implementation Specific Evaluation Details
        </v-card-title>
        <v-card-text>
            <v-container fluid>
                <v-row>
                    <v-col cols="12" lg="12">
                        <ImplementationSpecificResultsBrowser
                                :reported-implementation-specific-results="reportedImplementationSpecificResults"
                                :allow-editing="editMode"
                        />
                    </v-col>
                </v-row>
            </v-container>
        </v-card-text>
        <v-card-actions v-if="editMode">
            <v-btn color="primary" text @click="$emit('edit-implementation-specific-evaluation')">Edit Implementation Specific Evaluation</v-btn>
            <v-btn color="error" text @click="$emit('reset-implementation-specific-evaluation')">Reset Implementation Specific Evaluation</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>

    import ImplementationSpecificResultsBrowser from "./ImplementationSpecificResultsBrowser";
    export default {
        components: {
            ImplementationSpecificResultsBrowser},
        name: "ImplementationSpecificEvaluationBrowser",
        props:{
            reportedImplementationSpecificResults: {
                type: Array,
                handler: function() {
                    return [];
                }
            },
            editMode: {
                type: Boolean,
                default: true,
            }
        },
        data() {
            return {

            }
        },
        watch: {
        },
        created:function() {
        },
        computed: {
        },
        methods: {
        }
    }
</script>

<style scoped>

</style>
<template>
    <div v-if="enabled">
        <v-card>
            <v-card-actions>
                <v-tooltip top>
                    <template v-slot:activator="{ on }">
                        <v-btn text primary :disabled="proceedActionDisabled" v-if="proceedActivated" color="primary" v-on="on" @click="$emit('proceed')">
                            {{proceedActionText}}
                        </v-btn>
                    </template>
                    <span v-html="proceedActionTooltip"></span>
                </v-tooltip>
                <v-tooltip top>
                    <template v-slot:activator="{ on }">
                        <v-btn text :disabled="updateActionDisabled" v-if="updateActivated" color="primary" v-on="on" @click="$emit('update')">
                            {{updateActionText}}
                        </v-btn>
                    </template>
                    <span v-html="updateActionTooltip"></span>
                </v-tooltip>
                <v-tooltip top>
                    <template v-slot:activator="{ on }">
                        <v-btn text :disabled="suspendActionDisabled" v-if="suspendActivated" editModetext color="info" v-on="on" @click="$emit('suspend')">
                            {{suspendActionText}}
                        </v-btn>
                    </template>
                    <span v-html="suspendActionTooltip"></span>
                </v-tooltip>
                <v-tooltip top>
                    <template v-slot:activator="{ on }">
                        <v-btn text :disabled="cancelActionDisabled" v-if="cancelActivated" editModetext color="error" v-on="on" @click="$emit('cancel')">
                            {{cancelActionText}}
                        </v-btn>
                    </template>
                    <span v-html="cancelActionTooltip"></span>
                </v-tooltip>
            </v-card-actions>
        </v-card>
    </div>
    <div v-else>
        <span>{{disabledInfoText}}</span>
    </div>
</template>

<script>

    export default {
        components: { },
        props: {
            enabled: {
                type: Boolean,
                default: true,
            },
            disabledInfoText: {
                type: String,
                default: "Navigation is currently disabled",
            },
            proceedActivated: {
                type: Boolean,
                default: true,
            },
            proceedActionDisabled: {
                type: Boolean,
                default: false,
            },
            proceedActionText: {
                type: String,
                default: "Proceed Modelling",
            },
            proceedActionTooltip: {
                type: String,
                default: "Will store your current model progress and continue to the next step"
            },
            updateActivated: {
                type: Boolean,
                default: false,
            },
            updateActionDisabled: {
                type: Boolean,
                default: false,
            },
            updateActionText: {
                type: String,
                default: "Update Model",
            },
            updateActionTooltip: {
                type: String,
                default: "Will update your current model but stay in the current step"
            },
            suspendActivated: {
                type: Boolean,
                default: true,
            },
            suspendActionDisabled: {
                type: Boolean,
                default: false,
            },
            suspendActionText: {
                type: String,
                default: "Suspend Modelling",
            },
            suspendActionTooltip: {
                type: String,
                default: "Will store your current model progress leave the modelling process"
            },
            cancelActivated: {
                type: Boolean,
                default: true,
            },
            cancelActionDisabled: {
                type: Boolean,
                default: false,
            },
            cancelActionText: {
                type: String,
                default: "Cancel Modelling",
            },
            cancelActionTooltip: {
                type: String,
                default: "Will <b>delete</b> your current model progress and leave the modelling process"
            }
        },
        computed: {

        },
        data() {
            return {
                deleteModelProgress: false,
            }
        },
        mounted: function() {
        },
        watch: {
        },
        methods: {
        }
    }
</script>

<style>
</style>

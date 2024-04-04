<template>
    <v-tabs grow>
        <v-tab>
            Experiment specific Evaluation Criteria
        </v-tab>
        <v-tab>
            Implementation specific Evaluation Criteria
        </v-tab>
        <v-tab-item>
            <EvaluationCriteriaSelector
                :key="componentKeyExperimentSpecific"
                :stand-alone="true"
                :edit-mode="editMode"
                :evaluation-type="experimentSpecificEvaluationType"
            />
        </v-tab-item>
        <v-tab-item>
            <EvaluationCriteriaSelector
                :key="componentKeyImplementationSpecific"
                :stand-alone="true"
                :edit-mode="editMode"
                :evaluation-type="implementationSpecificEvaluationType"
            />
        </v-tab-item>
    </v-tabs>
</template>

<script>
    import constants from "../../../helpers/constants";
    import Vue from "vue";
    import EvaluationCriteriaSelector from "@/components/evaluations/criteria/EvaluationCriteriaSelector.vue";

    export default {
        name: "StandaloneCriteriaPicker",
        components: {EvaluationCriteriaSelector},
        props: {
            editMode: {
                type: Boolean,
                default: true,
            }
        },
        data() {
            return {
                // to force a re-render once the route changes, see https://michaelnthiessen.com/force-re-render/
                componentKeyExperimentSpecific: 0,
                componentKeyImplementationSpecific: 0,
            };
        },
        computed: {
            experimentSpecificEvaluationType: function() {
                return constants.evaluationTypes.experimentSpecific;
            },
            implementationSpecificEvaluationType: function() {
                return constants.evaluationTypes.implementationSpecific;
            }
        },
        beforeRouteEnter (to, from, next) {
            Vue.$log.debug("before entering stand alone criteria browser using route to, from", to, from);
            if(to.name === constants.routes.browseCriteria.name) {
                to.params["editMode"] = false;
            }

            next(vm => {
                vm.componentKeyExperimentSpecific++;
                vm.componentKeyImplementationSpecific++;
            });
        },
    }
</script>

<style scoped>

</style>
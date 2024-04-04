<template>
    <v-card>
    <v-tabs
            v-model="baseDetailTab"
            centered
            grow
            background-color="transparent"
    >
        <v-tabs-slider></v-tabs-slider>
        <v-tab>
            Behavioral Characteristic(s)
        </v-tab>
        <v-tab>
            Target Architecture
        </v-tab>
        <v-tab>
            Evaluations
        </v-tab>
        <v-tab>
            Evaluation Criteria Grants
        </v-tab>
        <v-tab>
            References
        </v-tab>
        <v-tab-item>
            <BiometricCharacteristicBrowser
                :biometric-characteristics-to-show="localBaseToShow.biometricCharacteristics"
                :edit-mode="false"/>
        </v-tab-item>
        <v-tab-item>
            <TargetArchitectureViewer
                :target-architecture="localBaseToShow.targetArchitecture">
            </TargetArchitectureViewer>
        </v-tab-item>
        <v-tab-item>
            <EvaluationViewer
                :characteristics="localBaseToShow.biometricCharacteristics"
                :base-evaluation="localBaseToShow.baseEvaluation"
                :parent-base-name="localBaseToShow.name"/>
        </v-tab-item>
        <v-tab-item>
            <EvaluationCriteriaGrantBrowser
                :edit-mode="false"
                :base-evaluation="localBaseToShow.baseEvaluation"
                :evaluation-criteria-grants="localBaseToShow.evaluationCriteriaGrants"/>
        </v-tab-item>
        <v-tab-item>
            <ReferencesViewer
                :reference="localBaseToShow.reference"/>
        </v-tab-item>
    </v-tabs>
    </v-card>
</template>

<script>

    import EvaluationViewer from "../evaluations/BaseEvaluationViewer";
    import ReferencesViewer from "../reference/ReferencesViewer";
    import TargetArchitectureViewer from "../target_architecture/TargetArchitectureViewer";
    import EvaluationCriteriaGrantBrowser from "../evaluations/criteria/grant/EvaluationCriteriaGrantBrowser";
    import {Util as utils} from "../../helpers/util";
    import BiometricCharacteristicBrowser from "@/components/characteristics/BiometricCharacteristicBrowser.vue";

    export default {
        components: {
          BiometricCharacteristicBrowser,
            EvaluationCriteriaGrantBrowser,
            TargetArchitectureViewer,
            ReferencesViewer, EvaluationViewer},
        name: "BaseDetailsViewer",
        props: {
            baseToShow: {
                type: Object,
                default: function() {
                    return {};
                },
            },
        },
        computed: {
        },
        watch: {
          baseToShow: {
              deep: true,
              immediate: true,
              handler: function(base) {
                  this.$log.debug("Base to show changed to",base);
                  this.localBaseToShow = utils.deepCopyObject(this.baseToShow);
                  console.log("set local base to ",this.localBaseToShow);
              }
          }
        },
        mounted: function() {
        },
        data() {
            return {
                baseDetailTab: null,
                localBaseToShow: {},
            }
        },
        methods: {
        }
    }
</script>

<style>
</style>

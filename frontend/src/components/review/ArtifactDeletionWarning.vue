<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <v-dialog v-model="warningDialogOpened" persistent hide-overlay transition="dialog-bottom-transition">
    <v-card>
      <v-card-title>
        <h2>Attention Please!</h2>
      </v-card-title>
      <v-card-text>
        <v-container fluid >
          <v-row cols="12" lg="12">
            <v-col lg="12">
              <h3>In General, you are only allowed to delete artifacts that have not been accepted by a reviewer yet. Hence, any modelled entity you delete will only affect other modelled artifacts
               from <b>yourself</b> that have not been accepted by a reviewer yet. So it will not be possible to delete any already accepted artifact by yourself. This is only possible by admins but
              should be well considered. Nevertheless, the artifact of type '{{artifactType}}' you are about to delete may be have some side effects:</h3>
            </v-col>
          </v-row>
          <v-row cols="12" lg="12">
            <v-col cols="12" v-if="artifactToDeleteIsCharacteristic">
              <span class="error--text">If you <b>delete</b> a <b>biometric characteristic in review</b> this decision also affects any modelled BASE, where it was reported as
                a main or support characteristic. Any created BASE will be <b>deleted</b> in case it was the only biometric characteristic. In the case of datasets,
              the dataset will be <b>deleted as well</b> in case it was the only characteristic inside. As datasets are also part of BASE, a BASE's modelling progress will be
              <b>reset</b> to target architecture if a dataset is invalidated because of the missing biometric characteristic!</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsSensor">
              <span class="error--text">If you <b>delete</b> a <b>sensor in review</b> this decision also affects related domain elements,
              i.e. its included dimensions will be deleted as well and any device category where this sensor is currently assigned will be updated!</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsBaseEvaluationExtension">
              <span class="error--text">If you <b>delete</b> a <b>BASE evaluation extension in review</b> this decision only affects the new biometric systems that were created for the evaluation extension, i.e. they will be deleted.
              All other related artifacts such as datasets, evaluation criteria or result metrics will be kept and have to be deleted individually in case they also should be removed.</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsSamplingContext">
              <span class="error--text">If you <b>delete</b> a <b>sampling context in review</b> this decision also affects related domain elements,
              i.e. it will be removed from any dataset where it was reported!</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsSamplingDevice">
              <span class="error--text">If you <b>delete</b> a <b>sampling device in review</b> this decision also affects related domain elements,
              i.e. it will be removed from any dataset where it was reported. If no other sampling device remains in the dataset, the dataset will be
               <b>deleted</b> as well that could lead to a reset of the related BASE's evaluation.</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsDeviceCategory">
              <span class="error--text">If you <b>delete</b> a <b>device category in review</b> this decision also affects related domain elements,
              i.e. any assigned <b>sampling device</b> will be removed while the sensors assigned to the category <b>remain</b>. Furthermore, any affected part of any BASE's modelled <b>target architecture</b> will be modified.
              If at least one category of the target architecture (e.g. sampling, matching, or decision location) afterwards misses its device category, the
              target architecture will be <b>reset</b>!. Any implementation based evaluation based on this device category will also be removed that could lead
              to the deletion to further modification of the BASE's modelling state.</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsResourcesToProtect">
              <span class="error--text">If you <b>delete resources to protect in review</b> this decision also affects any modelled BASE, where they were reported,
              i.e. it will be removed from their respective target architecture!</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsFeature">
              <span class="error--text">If you <b>delete a feature in review</b> this decision also affects related domain elements, i.e it will be removed
              from any biometric system where it was used. If no other feature remains the feature processing step will be deleted as well. If no other
              signal processing step remains, the biometric system will be deleted. This affects any experiment specific evaluation and could lead to resetting
              the base to <b>target architecture</b> modelling state if no other biometric systems or results based on other biometric systems remain.</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsProcessingStep">
              <span class="error--text">If you <b>delete a processing step in review</b> this decision also affects related domain elements, i.e it will be removed
              from any biometric system where it was used. If no other processing step remains in the respective category (e.g., matching, decision), the biometric system will be deleted as well.
                This affects any experiment specific evaluation and could lead to resetting the BASE to <b>target architecture</b> modelling state if no other biometric systems or results based on other biometric systems remain.</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsResultMetric">
              <span class="error--text">If you <b>delete a result metric in review</b> this decision also affects related domain elements, i.e. it will be removed
              from any experiment specific evaluation criteria it was assigned to. If no other result metrics exists, the criteria will be deleted as well affecting
              any evaluation criteria grant based on the criteria. Furthermore, every evaluation result using the result metric will be deleted. If no other result
              remains, the experiment specific evaluation will be reset that could lead to <b>resetting</b> the related BASE to the modelling state of <b>target architecture</b> if
              no implementation-based evaluation results exist.</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsResourcesToProtect">
              <span class="error--text">If you <b>delete resources to protect in review</b> this decision also affects any modelled BASE, where they were reported,
              i.e. it will be removed from their respective target architecture!</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsExperimentSpecificEvaluationCriteria">
              <span class="error--text">If you <b>delete an experiment specific evaluation criteria in review</b> this decision also affects related domain elements, i.e.
              any evaluation criteria grant based on the criteria will be removed and every result metric assigned to that criteria will be deleted as well. If no other evaluation
              criteria grants remain the base modelling progress will be <b>reset to 'evaluation'</b>. Since any related result metric is deleted, any evaluation result based
              on that result metric gets deleted as well and potentially leads to a further reset of the related BASE to the modelling progress <b>target architecture</b> if no
              other result metrics were given for specific results.</span>
            </v-col>
            <v-col cols="12" v-if="artifactToDeleteIsImplementationSpecificEvaluationCriteria">
              <span class="error--text">If you <b>delete an implementation-based evaluation criteria in review</b> this decision also affects related domain elements, i.e.
              any evaluation criteria grant based on the criteria will be removed. If no other evaluation criteria grants remain the base modelling progress will be <b>reset to 'evaluation'</b>.
              Furthermore, any implementation specific evaluation result will be deleted and potentially lead to a further reset of the related BASE to the modelling progress <b>target architecture</b> if no
              experiment specific results were given.</span>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-btn color="primary" text @click="continueAction">Got that, continue</v-btn>
        <v-btn color="error" text @click="closeDialog">Cancel</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>

    import constants from "@/helpers/constants";

    export default {
        name: "ArtifactDeletionWarning",
        props:{
          warningShown: {
            type: Boolean,
            default: false,
          },
          artifactType: {
            type: String,
            default: function () {
              return "";
            }
          }
        },
        data() {
          return {
            warningDialogOpened: false,
          };
        },
        watch: {
          warningShown: {
            immediate: true,
            handler: function(warningShown) {
              if(warningShown) {
                this.warningDialogOpened = true;
              } else {
                this.warningDialogOpened = false;
              }
            }
          }
        },
        computed: {
          artifactToDeleteIsSensor: function() {
            return this.artifactType === constants.review.artifactType.sensors;
          },
          artifactToDeleteIsResourcesToProtect: function() {
            return this.artifactType === constants.review.artifactType.resourcesToProtect;
          },
          artifactToDeleteIsSamplingContext: function() {
            return this.artifactType === constants.review.artifactType.samplingContexts;
          },
          artifactToDeleteIsBaseEvaluationExtension: function() {
            return this.artifactType === constants.review.artifactType.baseEvaluationExtension;
          },
          artifactToDeleteIsCharacteristic: function() {
            return this.artifactType === constants.review.artifactType.biometricCharacteristics;
          },
          artifactToDeleteIsSamplingDevice: function() {
            return this.artifactType === constants.review.artifactType.sampleDevices;
          },
          artifactToDeleteIsDeviceCategory: function() {
            return this.artifactType === constants.review.artifactType.deviceCategories;
          },
          artifactToDeleteIsFeature: function() {
            return this.artifactType === constants.review.artifactType.features;
          },
          artifactToDeleteIsResultMetric: function() {
            return this.artifactType === constants.review.artifactType.resultMetrics;
          },
          artifactToDeleteIsExperimentSpecificEvaluationCriteria: function() {
            return this.artifactType === constants.review.artifactType.experimentCriteria;
          },
          artifactToDeleteIsImplementationSpecificEvaluationCriteria: function() {
            return this.artifactType === constants.review.artifactType.implementationCriteria;
          },
          artifactToDeleteIsProcessingStep: function() {
            return this.artifactType === constants.review.artifactType.biometricProcessingSteps;
          },
        },
        methods: {
          continueAction: function() {
            this.$emit(constants.eventNames.local.deleteArtifact);
          },
          closeDialog: function() {
            this.$emit(constants.eventNames.local.closeDialog);
          }
        },
        created:function() {
        },
        mounted: function() {
        },
    }
</script>

<style scoped>

</style>
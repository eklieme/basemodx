<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card>
        <v-card-title>
          <span v-if="showsOwnArtifactsForReview">My&nbsp;</span>Artifacts&nbsp;<span v-if="showsOwnArtifactsForReview">in review</span><span v-else>I need to review</span>&nbsp;({{totalTargetReviewElementsCount}})
        </v-card-title>
        <v-card-text>
            <v-container fluid>
              <v-tabs v-model="tab" grow>
                <v-tab :key="keyBaseTab" v-if="baseToReviewCount!==0">BASE ({{baseToReviewCount}})</v-tab>
                <v-tab :key="keyBaseEvaluationExtensionTab" v-if="baseEvaluationExtensionsToReviewCount!==0">BASE Evaluation Extensions ({{baseEvaluationExtensionsToReviewCount}})</v-tab>
                <v-tab :key="keyCharacteristicsTab" v-if="biometricCharacteristicsToReviewCount!==0">Biometric Characteristics ({{biometricCharacteristicsToReviewCount}})</v-tab>
                <v-tab :key="keyDeviceCategoriesTab" v-if="deviceCategoriesToReviewCount!==0">Device Categories ({{deviceCategoriesToReviewCount}})</v-tab>
                <v-tab :key="keySensorsTab" v-if="sensorsToReviewCount!==0">Sensors ({{sensorsToReviewCount}})</v-tab>
                <v-tab :key="keySensorDimensionsTab" v-if="sensorDimensionsToReviewCount!==0">Sensor Dimensions ({{sensorDimensionsToReviewCount}})</v-tab>
                <v-tab :key="keyResourcesToProtectTab" v-if="resourcesToProtectToReviewCount!==0">Resources To Protect ({{resourcesToProtectToReviewCount}})</v-tab>
                <v-tab :key="keyDatasetsTab" v-if="datasetsToReviewCount!==0">Datasets ({{datasetsToReviewCount}})</v-tab>
                <v-tab :key="keySamplingContextsTab" v-if="evaluationContextsToReviewCount!==0">Sampling Contexts ({{evaluationContextsToReviewCount}})</v-tab>
                <v-tab :key="keySampleDevicesTab" v-if="sampleDevicesToReviewCount!==0">Sample Devices ({{sampleDevicesToReviewCount}})</v-tab>
                <v-tab :key="keyBiometricProcessingSteps" v-if="biometricProcessingStepsToReviewCount!==0">Processing Steps ({{biometricProcessingStepsToReviewCount}})</v-tab>
                <v-tab :key="keyFeatures" v-if="featuresToReviewCount!==0">Features ({{featuresToReviewCount}})</v-tab>
                <v-tab :key="keyExperimentEvaluationCriteria" v-if="experimentCriteriaToReviewCount!==0">Experiment Evaluation Criteria ({{experimentCriteriaToReviewCount}})</v-tab>
                <v-tab :key="keyResultMetrics" v-if="resultMetricsToReviewCount!==0">Result Metrics ({{resultMetricsToReviewCount}})</v-tab>
                <v-tab :key="keyImplementationEvaluationCriteriaTab" v-if="implementationCriteriaToReviewCount!==0">Implementation Evaluation Criteria ({{implementationCriteriaToReviewCount}})</v-tab>
              </v-tabs>
              <v-tabs-items v-model="tab">
                <v-tab-item v-if="baseToReviewCount!==0" :key="keyBaseTab">
                  <BaseBrowser
                            :stand-alone="false"
                            :show-review-action="showReviewAction"
                            :base-to-show="targetReviewBase"
                            v-on:amount-of-artifacts-changed="updateArtifactsToReviewCount"/>
                </v-tab-item>
                <v-tab-item v-if="baseEvaluationExtensionsToReviewCount!==0" :key="keyBaseEvaluationExtensionTab">
                    <BaseEvaluationExtensionBrowser
                            :show-review-action="showReviewAction"
                            :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                            :base-evaluation-extensions-to-show="targetReviewBaseEvaluationExtensions"
                            v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount"
                            v-on:edit-artifact = "editArtifact($event, constants.review.artifactType.baseEvaluationExtension)"
                            v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.baseEvaluationExtension)"
                      />
                </v-tab-item>
                <v-tab-item v-if="biometricCharacteristicsToReviewCount!==0" :key="keyCharacteristicsTab">
                  <BiometricCharacteristicsBrowser
                            :biometric-characteristics-to-show="targetReviewCharacteristics"
                            :show-review-action="showReviewAction"
                            :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                            v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount"
                            v-on:edit-artifact = "editBiometricCharacteristic"
                            v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.biometricCharacteristics)"/>
                  <BiometricCharacteristicEditor
                            :edit-mode="editingAllowedExistingBiometricCharacteristic"
                            :biometric-characteristic-to-edit="biometricCharacteristicToEdit"
                            :all-available-characteristics="existingCharacteristics"
                            v-on:artifact-created-updated="updateTargetArtifactsForReview(constants.review.artifactType.biometricCharacteristics)"
                            v-on:close-dialog="resetBiometricCharacteristicToEdit()"
                  />
                </v-tab-item>
                <v-tab-item v-if="deviceCategoriesToReviewCount!==0" :key="keyDeviceCategoriesTab">
                  <DeviceCategoryBrowser
                            :device-categories-to-show="targetReviewDeviceCategories"
                            :show-review-action="showReviewAction"
                            :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                            v-on:edit-artifact = "editDeviceCategory"
                            v-on:delete-artifact = "markArtifactForDeletion($event, constants.review.artifactType.deviceCategories)"
                            v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount" />
                  <DeviceCategoryEditor
                            :edit-mode="editingAllowedExistingDeviceCategory"
                            :existing-device-categories="existingDeviceCategories"
                            :device-category-for-editing="deviceCategoryToEdit"
                            v-on:artifact-created-updated="updateTargetArtifactsForReview(constants.review.artifactType.deviceCategories)"
                            v-on:close-dialog="resetDeviceCategoryToEdit()"
                          />
                </v-tab-item>
                <v-tab-item v-if="sensorsToReviewCount!==0" :key="keySensorsTab">
                  <SensorBrowser
                            :sensors-to-show="targetReviewSensors"
                            :show-review-action="showReviewAction"
                            :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                            v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount"
                            v-on:edit-artifact="editArtifact($event, constants.review.artifactType.sensorDimensions)"
                            v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.sensorDimensions)"/>
                </v-tab-item>
                <v-tab-item v-if="sensorDimensionsToReviewCount!==0" :key="keySensorDimensionsTab">
                  <SensorDimensionBrowser
                      :sensor-dimensions-to-show="targetReviewSensorDimensions"
                      :show-review-action="showReviewAction"
                      :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                      v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount"
                      v-on:edit-artifact="editArtifact($event, constants.review.artifactType.sensorDimensions)"
                      v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.sensorDimensions)"/>
                </v-tab-item>
                <v-tab-item v-if="resourcesToProtectToReviewCount!==0" :key="keyResourcesToProtectTab">
                  <ResourcesToProtectBrowser
                            :resources-to-protect-to-show="targetReviewResourcesToProtect"
                            :show-review-action="showReviewAction"
                            :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                            v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount"
                            v-on:edit-artifact="editArtifact($event, constants.review.artifactType.resourcesToProtect)"
                            v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.resourcesToProtect)"/>
                </v-tab-item>
                <v-tab-item v-if="datasetsToReviewCount!==0" :key="keyDatasetsTab">
                  <DatasetBrowser :datasets-to-show="targetReviewDatasets"
                                  :delete-datasets-allowed="false"
                                  :edit-mode="false"
                                  :show-review-action="showReviewAction"
                                  :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                                  v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount"
                                  v-on:edit-artifact="editArtifact($event, constants.review.artifactType.datasets)"
                                  v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.datasets)"/>

                </v-tab-item>
                <v-tab-item v-if="evaluationContextsToReviewCount!==0" :key="keySamplingContextsTab">
                  <SamplingContextBrowser
                                  :sampling-contexts-to-show="targetReviewSamplingContexts"
                                  :show-review-action="showReviewAction"
                                  :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                                  v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount"
                                  v-on:edit-artifact="editArtifact($event, constants.review.artifactType.samplingContexts)"
                                  v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.samplingContexts)"/>
                </v-tab-item>
                <v-tab-item v-if="sampleDevicesToReviewCount!==0" :key="keySampleDevicesTab">
                  <SampleDeviceBrowser :sample-devices-to-show="targetReviewSampleDevices"
                                       :show-review-action="showReviewAction"
                                       :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                                       v-on:edit-artifact="editArtifact($event, constants.review.artifactType.sampleDevices)"
                                       v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.sampleDevices)"
                                       v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount" />
                </v-tab-item>
                <v-tab-item v-if="biometricProcessingStepsToReviewCount!==0" :key="keyBiometricProcessingSteps">
                  <ProcessingStepBrowser :processing-steps-to-show="targetReviewBiometricProcessingSteps"
                                       :show-review-action="showReviewAction"
                                       :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                                       v-on:edit-artifact="editArtifact($event, constants.review.artifactType.biometricProcessingSteps)"
                                       v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.biometricProcessingSteps)"
                                       v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount" />
                </v-tab-item>
                <v-tab-item v-if="featuresToReviewCount" :key="keyFeatures">
                  <FeatureBrowser :features-to-show="targetReviewFeatures"
                                  :show-review-action="showReviewAction"
                                  :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                                  v-on:edit-artifact="editArtifact($event, constants.review.artifactType.features)"
                                  v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.features)"
                                  v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount"/>
                </v-tab-item>
                <v-tab-item v-if="experimentCriteriaToReviewCount!==0" :key="keyExperimentEvaluationCriteria">
                  <ExperimentCriteriaBrowser :evaluation-criteria-to-show="targetReviewExperimentCriteria"
                                             experiment-related-criteria
                                             :show-review-action="showReviewAction"
                                             :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                                             v-on:edit-artifact="editArtifact($event, constants.review.artifactType.experimentCriteria)"
                                             v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.experimentCriteria)"
                                             v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount" />
                  <EvaluationCriteriaEditor v-if="artifactTypeForEditing===constants.review.artifactType.experimentCriteria"
                      :criteria-type="constants.evaluationTypes.experimentSpecific"
                      :evaluation-criteria-for-editing="artifactToEdit"
                      :type-selectable="false"
                      v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.experimentCriteria)"
                  />
                </v-tab-item>
                <v-tab-item v-if="resultMetricsToReviewCount!==0" :key="keyResultMetrics">
                  <ResultMetricBrowser :result-metrics-to-show="targetReviewResultMetrics"
                                       :show-review-action="showReviewAction"
                                       :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                                       v-on:edit-artifact="editArtifact($event, constants.review.artifactType.resultMetrics)"
                                       v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.resultMetrics)"
                                       v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount" />
                  <ResultMetricEditor v-if="artifactTypeForEditing===constants.review.artifactType.resultMetrics"
                      :result-metric-for-editing="artifactToEdit"
                      v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.resultMetrics)"
                    />
                </v-tab-item>
                <v-tab-item v-if="implementationCriteriaToReviewCount!==0" :key="keyImplementationEvaluationCriteriaTab">
                  <ExperimentCriteriaBrowser :evaluation-criteria-to-show="targetReviewImplementationCriteria"
                                             :show-review-action="showReviewAction"
                                             :show-lifecycle-state-information="!showsOwnArtifactsForReview"
                                             v-on:edit-artifact="editArtifact($event, constants.review.artifactType.implementationCriteria)"
                                             v-on:delete-artifact="markArtifactForDeletion($event, constants.review.artifactType.implementationCriteria)"
                                             v-on:amount-of-artifacts-changed = "updateArtifactsToReviewCount" />
                  <EvaluationCriteriaEditor v-if="artifactTypeForEditing===constants.review.artifactType.implementationCriteria"
                      :criteria-type="constants.evaluationTypes.implementationSpecific"
                      :evaluation-criteria-for-editing="artifactToEdit"
                      :type-selectable="false"
                      v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.implementationCriteria)"
                  />
                </v-tab-item>
              </v-tabs-items>
            </v-container>
        </v-card-text>
        <v-card-actions>
        </v-card-actions>
        <DialogContainer
            :dialog-opened="artifactEditorOpened"
            :dialog-title="'Artifact Editor'"
            v-on:close-dialog="artifactEditorOpened=false">
            <template v-slot:content>
              <SamplingContextEditor v-if="artifactTypeForEditing===constants.review.artifactType.samplingContexts"
                  :all-sampling-contexts="allSamplingContexts"
                  :sampling-context-to-edit="artifactToEdit"
                  v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.samplingContexts)"
              />
              <SensorEditor v-if="artifactTypeForEditing===constants.review.artifactType.sensors"
                  :all-existing-sensors="existingSensors"
                  :sensor-for-editing="artifactToEdit"
                  v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.sensors)"
                  v-on:close-dialog="artifactEditorOpened=false"
              />
              <SampleDeviceEditor v-if="artifactTypeForEditing===constants.review.artifactType.sampleDevices"
                  :sample-device-to-edit="artifactToEdit"
                  stand-alone
                  v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.sampleDevices)"
              />
              <ResourcesToProtectEditor v-if="artifactTypeForEditing===constants.review.artifactType.resourcesToProtect"
                  :all-resources-to-protect="allResourcesToProtect"
                  :resources-to-protect-to-edit="artifactToEdit"
                  v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.resourcesToProtect)"
              />
              <DatasetEditor v-if="artifactTypeForEditing===constants.review.artifactType.datasets"
                  edit-mode
                  stand-alone
                  :base-characteristics="existingCharacteristics"
                  :dataset-for-editing="artifactToEdit"
                  v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.datasets)"
                />
              <FeatureEditor v-if="artifactTypeForEditing===constants.review.artifactType.features"
                             :all-features="allFeatures"
                             :feature-to-edit="artifactToEdit"
                             v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.features)"
              />
              <SensorDimensionEditor v-if="artifactTypeForEditing===constants.review.artifactType.sensorDimensions"
                             :all-sensor-dimensions="allSensorDimensions"
                             :sensor-dimension-to-edit="artifactToEdit"
                             v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.sensorDimensions)"
              />
              <ProcessingStepEditor v-if="artifactTypeForEditing===constants.review.artifactType.biometricProcessingSteps"
                  :processing-step-for-editing="artifactToEdit"
                   v-on:artifact-created-updated="artifactSuccessfullyEdited(constants.review.artifactType.biometricProcessingSteps)"
                />
              <BaseEvaluationExtender
                      :base-evaluation-extension-to-edit="artifactToEdit"
                      :base-for-extension="baseEvaluationExtensionParentBase"
                      v-on:artifact-created-updated="updateBaseEvaluationExtension"
              />
            </template>
            <template v-slot:actions>
                <v-btn v-if="artifactTypeForEditing===constants.review.artifactType.baseEvaluationExtension"
                        text
                       :disabled="!artifactToEdit.baseEvaluationToMerge"
                       @click="persistUpdatedBaseEvaluationExtension"
                       color="primary"
                >Submit Changes</v-btn>
            </template>
        </DialogContainer>
        <ArtifactDeletionWarning
            :artifact-type="artifactTypeForDeletion"
            :warning-shown="showDeletionWarning"
            v-on:delete-artifact="continueDeletingArtifact"
            v-on:close-dialog="showDeletionWarning = false"
        />
    </v-card>
</template>

<script>


    import constants from "../../helpers/constants";
    import {ModelledElementReviewAPI} from "../../service/api/ModelledElementReviewAPI";
    import Vue from "vue";
    import ExperimentCriteriaBrowser from "../evaluations/criteria/EvaluationCriteriaBrowser.vue";
    import SampleDeviceBrowser from "../evaluations/experiment/data/SampleDeviceBrowser.vue";
    import LoggedInUserMixin from "../../mixins/LoggedInUserMixin";
    import DatasetBrowser from "../evaluations/experiment/data/DatasetBrowser.vue";
    import ResultMetricBrowser from "../evaluations/experiment/result/ResultMetricBrowser.vue";
    import BiometricCharacteristicsBrowser from "../characteristics/BiometricCharacteristicBrowser.vue";
    import DeviceCategoryBrowser from "../target_architecture/DeviceCategoryBrowser.vue";
    import ResourcesToProtectBrowser from "../target_architecture/ResourcesToProtectBrowser.vue";
    import ProcessingStepBrowser from "../evaluations/experiment/biometricsystem/ProcessingStepBrowser.vue";
    import FeatureBrowser from "@/components/evaluations/experiment/biometricsystem/FeatureBrowser.vue";
    import BaseBrowser from "@/components/base/BaseBrowser.vue";
    import EventBus from "@/helpers/eventBus";
    import SamplingContextBrowser from "@/components/evaluations/experiment/data/SamplingContextBrowser.vue";
    import SensorBrowser from "@/components/target_architecture/SensorBrowser.vue";
    import BiometricCharacteristicOperationsMixin from "@/mixins/model/BiometricCharacteristicOperationsMixin";
    import BiometricCharacteristicEditor from "@/components/characteristics/BiometricCharacteristicEditor.vue";
    import DeviceCategoryEditor from "@/components/target_architecture/DeviceCategoryEditor.vue";
    import {DeviceCategoryServiceAPI} from "@/service/api/DeviceCategoryServiceAPI";
    import SensorOperationsMixin from "@/mixins/model/SensorOperationsMixin";
    import toast from "@/helpers/toast";
    import ArtifactDeletionWarning from "@/components/review/ArtifactDeletionWarning.vue";
    import SensorEditor from "@/components/target_architecture/SensorEditor.vue";
    import DialogContainer from "@/components/util/DialogContainer.vue";
    import ResourcesToProtectEditor from "@/components/target_architecture/ResourcesToProtectEditor.vue";
    import {TargetArchitectureAPI} from "@/service/api/TargetArchitectureAPI";
    import {DatasetAPI} from "@/service/api/DatasetAPI";
    import SamplingContextEditor from "@/components/evaluations/experiment/data/SamplingContextEditor.vue";
    import {BiometricCharacteristicsService} from "@/service/api/BiometricCharacteristicsService";
    import SampleDeviceEditor from "@/components/evaluations/experiment/data/SampleDeviceEditor.vue";
    import DatasetEditor from "@/components/evaluations/experiment/data/DatasetEditor.vue";
    import FeatureEditor from "@/components/evaluations/experiment/biometricsystem/FeatureEditor.vue";
    import {BiometricSystemAPI} from "@/service/api/BiometricSystemAPI";
    import ResultMetricEditor from "@/components/evaluations/experiment/result/ResultMetricEditor.vue";
    import EvaluationCriteriaEditor from "@/components/evaluations/criteria/EvaluationCriteriaEditor.vue";
    import ProcessingStepEditor from "@/components/evaluations/experiment/biometricsystem/ProcessingStepEditor.vue";
    import {EvaluationScenarioServiceAPI} from "@/service/api/EvaluationScenarioServiceAPI";
    import SensorDimensionBrowser from "@/components/target_architecture/SensorDimensionBrowser.vue";
    import SensorDimensionEditor from "@/components/target_architecture/SensorDimensionEditor.vue";
    import {SensorAPI} from "@/service/api/SensorAPI";
    import BaseEvaluationExtensionBrowser from "@/components/evaluations/extension/BaseEvaluationExtensionBrowser.vue";
    import {
        BiometricAuthenticationSystemAndEvaluationAPI
    } from "@/service/api/BiometricAuthenticationSystemAndEvaluationAPI";
    import BaseEvaluationExtender from "@/components/evaluations/extension/BaseEvaluationExtender.vue";

    export default {
        name: "ArtifactReviewBrowser",
        components:{
          BaseEvaluationExtender,
          BaseEvaluationExtensionBrowser,
          SensorDimensionEditor,
          SensorDimensionBrowser,
          ProcessingStepEditor,
          EvaluationCriteriaEditor,
          ResultMetricEditor,
          FeatureEditor,
          DatasetEditor,
          SampleDeviceEditor,
          SamplingContextEditor,
          ResourcesToProtectEditor,
          DialogContainer,
          SensorEditor,
          ArtifactDeletionWarning,
          DeviceCategoryEditor,
          BiometricCharacteristicEditor,
          SensorBrowser,
          SamplingContextBrowser,
          BaseBrowser,
          FeatureBrowser,
          ProcessingStepBrowser,
          ResourcesToProtectBrowser,
          DeviceCategoryBrowser,
          BiometricCharacteristicsBrowser,
          ResultMetricBrowser, DatasetBrowser, ExperimentCriteriaBrowser, SampleDeviceBrowser},
        mixins: [LoggedInUserMixin, BiometricCharacteristicOperationsMixin, SensorOperationsMixin],
        props:{
        },
        data() {
            return {
              tab: null,
              targetArtifacts: {

              },
              baseToReviewCount: 0,
              baseEvaluationExtensionsToReviewCount: 0,
              featuresToReviewCount: 0,
              biometricCharacteristicsToReviewCount: 0,
              deviceCategoriesToReviewCount:0,
              sensorsToReviewCount:0,
              sensorDimensionsToReviewCount: 0,
              resourcesToProtectToReviewCount: 0,
              datasetsToReviewCount: 0,
              evaluationContextsToReviewCount: 0,
              sampleDevicesToReviewCount: 0,
              experimentCriteriaToReviewCount: 0,
              resultMetricsToReviewCount: 0,
              implementationCriteriaToReviewCount: 0,
              biometricProcessingStepsToReviewCount: 0,
              showsOwnArtifactsForReview:false,
              showsAllArtifactsToReview:false,
              // dialog container for editing
              artifactEditorOpened: false,
              // general target artifact for editing
              artifactToEdit: null,
              artifactTypeForEditing: "",
              //resources to protect editing
              allResourcesToProtect: [],
              //device category editing
              existingDeviceCategories: [],
              deviceCategoryToEdit: {},
              editingAllowedExistingDeviceCategory: false,
              // sampling contexts editing:
              allSamplingContexts: [],
              // features editing
              allFeatures: [],
              // sensor dimension editing
              allSensorDimensions: [],
              // base evaluation extension
              baseEvaluationExtensionParentBase: {},
              editedBaseEvaluationExtension: {},
              // general deletion of elements in review
              showDeletionWarning: false,
              artifactTypeForDeletion: "",
              currentArtifactMarkedForDeletion: {},
            }
        },
        watch: {


        },
        mounted:function() {
          this.$nextTick(function () {
            Vue.$log.debug("...mounted!");
          });
        },
        created: function() {
          this.$nextTick(function () {
            Vue.$log.debug("...created!");
            if (this.loadAllArtifactsForReview) {
              this.$log.debug("...get all artifacts requiring review");
              this.getAllArtifactsRequiringReview();
            }
            if (this.loadMyArtifactsForReview) {
              this.$log.debug("...get all of my artifacts in review");
              this.getArtifactsInReviewForMe();
            }
            // register to trigger full reload once bases updated
            EventBus.$on(constants.eventNames.global.updateBaseRequired, () => {
              this.getAllArtifactsRequiringReview();
            })
          });
        },
        computed: {
          loadAllArtifactsForReview: function() {
            return this.showsAllArtifactsToReview
          },
          loadMyArtifactsForReview: function() {
            return this.showsOwnArtifactsForReview
          },
          keyBaseTab: function() {
            return constants.review.artifactType.base;
          },
          keyBaseEvaluationExtensionTab: function() {
            return constants.review.artifactType.baseEvaluationExtension;
          },
          keyCharacteristicsTab: function() {
            return constants.review.artifactType.biometricCharacteristics;
          },
          keyDeviceCategoriesTab: function() {
            return constants.review.artifactType.deviceCategories;
          },
          keySensorsTab: function() {
            return constants.review.artifactType.sensors;
          },
          keySensorDimensionsTab: function() {
            return constants.review.artifactType.sensorDimensions;
          },
          keyResourcesToProtectTab: function() {
            return constants.review.artifactType.resourcesToProtect;
          },
          keyDatasetsTab: function() {
            return constants.review.artifactType.datasets;
          },
          keySamplingContextsTab: function() {
            return constants.review.artifactType.samplingContexts;
          },
          keySampleDevicesTab: function() {
            return constants.review.artifactType.sampleDevices;
          },
          keyBiometricProcessingSteps: function() {
            return constants.review.artifactType.biometricProcessingSteps;
          },
          keyFeatures: function() {
            return constants.review.artifactType.features;
          },
          keyExperimentEvaluationCriteria: function() {
            return constants.review.artifactType.experimentCriteria;
          },
          keyResultMetrics: function() {
            return constants.review.artifactType.resultMetrics;
          },
          keyImplementationEvaluationCriteriaTab: function() {
            return constants.review.artifactType.implementationCriteria;
          },
          constants: function() {
            return constants;
          },
          targetReviewBase: function() {
            if(this.targetArtifacts[constants.review.artifactType.base]) {
              return this.targetArtifacts[constants.review.artifactType.base];
            }
            return [];
          },
          targetReviewBaseEvaluationExtensions: function() {
              if(this.targetArtifacts[constants.review.artifactType.baseEvaluationExtension]) {
                  return this.targetArtifacts[constants.review.artifactType.baseEvaluationExtension];
              }
              return [];
          },
          targetReviewCharacteristics: function() {
            if(this.targetArtifacts[constants.review.artifactType.biometricCharacteristics]) {
              return this.targetArtifacts[constants.review.artifactType.biometricCharacteristics];
            }
            return [];
          },
          targetReviewDeviceCategories: function() {
            if(this.targetArtifacts[constants.review.artifactType.deviceCategories]) {
              return this.targetArtifacts[constants.review.artifactType.deviceCategories];
            }
            return [];
          },
          targetReviewSensors: function() {
            if(this.targetArtifacts[constants.review.artifactType.sensors]) {
              return this.targetArtifacts[constants.review.artifactType.sensors];
            }
            return [];
          },
          targetReviewSensorDimensions: function() {
            if(this.targetArtifacts[constants.review.artifactType.sensorDimensions]) {
              return this.targetArtifacts[constants.review.artifactType.sensorDimensions];
            }
            return [];
          },
          targetReviewResourcesToProtect: function() {
            if(this.targetArtifacts[constants.review.artifactType.resourcesToProtect]) {
              return this.targetArtifacts[constants.review.artifactType.resourcesToProtect];
            }
            return [];
          },
          targetReviewDatasets: function() {
            if(this.targetArtifacts[constants.review.artifactType.datasets]) {
              this.$log.debug("returning "+this.targetArtifacts[constants.review.artifactType.datasets].length+" datasets for review")
              return this.targetArtifacts[constants.review.artifactType.datasets];
            }
            return [];
          },
          targetReviewSamplingContexts: function() {
            if(this.targetArtifacts[constants.review.artifactType.samplingContexts]) {
              return this.targetArtifacts[constants.review.artifactType.samplingContexts];
            }
            return [];
          },
          targetReviewSampleDevices: function() {
            if(this.targetArtifacts[constants.review.artifactType.sampleDevices]) {
              return this.targetArtifacts[constants.review.artifactType.sampleDevices];
            }
            return [];
          },
          targetReviewBiometricProcessingSteps: function() {
            if(this.targetArtifacts[constants.review.artifactType.biometricProcessingSteps]) {
              return this.targetArtifacts[constants.review.artifactType.biometricProcessingSteps];
            }
            return [];
          },
          targetReviewFeatures: function() {
            if(this.targetArtifacts[constants.review.artifactType.features]) {
              return this.targetArtifacts[constants.review.artifactType.features];
            }
            return [];
          },
          targetReviewExperimentCriteria: function() {
            if(this.targetArtifacts[constants.review.artifactType.experimentCriteria]) {
              return this.targetArtifacts[constants.review.artifactType.experimentCriteria];
            }
            return [];
          },
          targetReviewResultMetrics: function() {
            if(this.targetArtifacts[constants.review.artifactType.resultMetrics]) {
              return this.targetArtifacts[constants.review.artifactType.resultMetrics];
            }
            return [];
          },
          targetReviewImplementationCriteria: function() {
            if(this.targetArtifacts[constants.review.artifactType.implementationCriteria]) {
              return this.targetArtifacts[constants.review.artifactType.implementationCriteria];
            }
            return [];
          },
          totalTargetReviewElementsCount: function() {
            return this.baseToReviewCount+
                this.baseEvaluationExtensionsToReviewCount+
                this.biometricCharacteristicsToReviewCount+
                this.deviceCategoriesToReviewCount+
                this.sensorsToReviewCount+
                this.sensorDimensionsToReviewCount+
                this.resourcesToProtectToReviewCount+
                this.datasetsToReviewCount+
                this.evaluationContextsToReviewCount+
                this.sampleDevicesToReviewCount+
                this.experimentCriteriaToReviewCount+
                this.resultMetricsToReviewCount+
                this.implementationCriteriaToReviewCount+
                this.biometricProcessingStepsToReviewCount+
                this.featuresToReviewCount;
          },
          showReviewAction: function() {
            console.log("...show review actions? :" + (this.showsAllArtifactsToReview || this.showsOwnArtifactsForReview));
            return this.showsAllArtifactsToReview || this.showsOwnArtifactsForReview;
          },
        },
        methods: {
          getArtifactsInReviewForMe: function() {
            var that = this;
            ModelledElementReviewAPI.getElementsInReviewForMe().then((response) => {
              Vue.$log.debug("get artifacts to review for current user");
              that.targetArtifacts = response.data;
              that.updateAllArtifactsReviewCount();
            });
          },
          getAllArtifactsRequiringReview: function() {
            var that = this;
            ModelledElementReviewAPI.getArtefactsToReviewForReviewer().then((response) => {
              Vue.$log.debug("get all artifacts needing review");
              that.targetArtifacts = response.data;
              that.updateAllArtifactsReviewCount();
            });
          },
          updateAllArtifactsReviewCount() {
            Object.entries(this.targetArtifacts).map(([artifactType, artifactsToReview]) => {

              this.updateArtifactToReviewCount({
                artifactType:artifactType,
                newCount:artifactsToReview.length,
              });

            })
          },
          updateArtifactToReviewCount(countUpdateForArtifactType) {
            this.$log.debug("Update artifact '"+countUpdateForArtifactType.artifactType+"' to review count to "+countUpdateForArtifactType.newCount);
            switch (countUpdateForArtifactType.artifactType) {
              case constants.review.artifactType.base:
                // if base number changes, possible many other artefacts were reviewed as well, so trigger full reload
                this.baseToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.baseEvaluationExtension:
                  this.baseEvaluationExtensionsToReviewCount=countUpdateForArtifactType.newCount;
                  break;
              case constants.review.artifactType.features:
                this.featuresToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.biometricCharacteristics:
                this.biometricCharacteristicsToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.deviceCategories:
                this.deviceCategoriesToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.sensors:
                this.sensorsToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.sensorDimensions:
                this.sensorDimensionsToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.resourcesToProtect:
                this.resourcesToProtectToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.datasets:
                this.datasetsToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.samplingContexts:
                this.evaluationContextsToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.sampleDevices:
                this.sampleDevicesToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.experimentCriteria:
                this.experimentCriteriaToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.resultMetrics:
                this.resultMetricsToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.implementationCriteria:
                this.implementationCriteriaToReviewCount=countUpdateForArtifactType.newCount;
                break;
              case constants.review.artifactType.biometricProcessingSteps:
                this.biometricProcessingStepsToReviewCount=countUpdateForArtifactType.newCount;
                break;
            }

            this.sendGlobalUpdateMessage();
          },
          updateArtifactsToReviewCount(countUpdateForArtifactType) {
            this.updateArtifactToReviewCount(countUpdateForArtifactType);
            this.$log.debug("Notify about total number of "+this.totalTargetReviewElementsCount+" artifacts to review");
            this.sendGlobalUpdateMessage();
          },
          sendGlobalUpdateMessage() {
            EventBus.$emit(constants.eventNames.local.amountOfArtifactsShownChanged, {
              artifactType: constants.review.artifactType.artifactsInReview,
              newCount:this.totalTargetReviewElementsCount,
              userSpecific: this.showsOwnArtifactsForReview,
            });
          },
          updateTargetArtifactsForReview(artifactType) {
            this.$log.debug("Update target artifacts of type '"+artifactType+"' for review");
            ModelledElementReviewAPI
                .getElementsInReviewForMeOfSpecificArtifactType(artifactType)
                .then((response) => {
                  this.targetArtifacts[artifactType] = response.data;
                  this.updateAllArtifactsReviewCount();
            });
          },
          editDeviceCategory(selectedDeviceCategory) {
            DeviceCategoryServiceAPI.getDeviceCategories().then(response => {
              this.existingDeviceCategories = response.data;
              this.editingAllowedExistingDeviceCategory = true;
              this.deviceCategoryToEdit = selectedDeviceCategory;
            })
          },
          resetDeviceCategoryToEdit() {
            this.deviceCategoryToEdit = {
              name: "",
            };
            this.editingAllowedExistingDeviceCategory = false;
          },
          markArtifactForDeletion: function(artifactToDelete, artifactType) {
            this.currentArtifactMarkedForDeletion = artifactToDelete;
            this.artifactTypeForDeletion = artifactType;
            this.showDeletionWarning = true;
          },
          resetArtifactMarkedForDeletion: function() {
            this.showDeletionWarning = false;
            this.artifactTypeForDeletion = "";
            this.currentArtifactMarkedForDeletion = {};
          },
          globallyDeleteSensorAndRefreshSensorsToReview: function() {
            this.globallyDeleteSensor(this.currentArtifactMarkedForDeletion).then(() => {
              toast.success("Successfully deleted sensor with name '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.sensors);
              this.updateTargetArtifactsForReview(constants.review.artifactType.deviceCategories);
            });
          },
          globallyDeleteSensorDimension: function() {
            SensorAPI.deleteSensorDimension(this.currentArtifactMarkedForDeletion).then(() => {
              toast.success("Successfully deleted sensor dimension with name '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.sensorDimensions);
              this.updateTargetArtifactsForReview(constants.review.artifactType.sensors);
            });
          },
          globallyDeleteResourcesToProtect: function() {
            return TargetArchitectureAPI.deleteResourcesToProtect(this.currentArtifactMarkedForDeletion).then(()=> {
              toast.success("Successfully deleted resources to protect with name '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.resourcesToProtect);
            });
          },
          globallyDeleteSamplingContext: function() {
            DatasetAPI.deleteSamplingContext(this.currentArtifactMarkedForDeletion).then(()=> {
              toast.success("Successfully deleted sampling context with description '"+this.currentArtifactMarkedForDeletion.description+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.samplingContexts);
            });
          },
          globallyDeleteBiometricCharacteristic: function() {
            BiometricCharacteristicsService.deleteBiometricCharacteristic(this.currentArtifactMarkedForDeletion).then(()=> {
              toast.success("Successfully deleted biometric characteristic '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.biometricCharacteristics);
            });
          },
          globallyDeleteSamplingDevice: function() {
            DatasetAPI.deleteSamplingDevice(this.currentArtifactMarkedForDeletion).then(()=> {
              toast.success("Successfully deleted sample device '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.sampleDevices);
            });
          },
          globallyDeleteDeviceCategory: function() {
            DeviceCategoryServiceAPI.deleteDeviceCategory(this.currentArtifactMarkedForDeletion).then(()=> {
              toast.success("Successfully deleted device category '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.deviceCategories);
              this.updateTargetArtifactsForReview(constants.review.artifactType.sampleDevices);
              this.updateTargetArtifactsForReview(constants.review.artifactType.sensors);
            });
          },
          globallyDeleteFeature: function() {
            BiometricSystemAPI.deleteFeature(this.currentArtifactMarkedForDeletion).then(() => {
              toast.success("Successfully deleted feautre'"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.features);
            });
          },
          globallyDeleteResultMetric: function() {
            EvaluationScenarioServiceAPI.deleteResultMetric(this.currentArtifactMarkedForDeletion).then(() => {
              toast.success("Successfully deleted result metric '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.resultMetrics);
              this.updateTargetArtifactsForReview(constants.review.artifactType.experimentCriteria);
            });
          },
          globallyDeleteImplementationEvaluationCriteria: function() {
            EvaluationScenarioServiceAPI.deleteImplementationEvaluationCriteria(this.currentArtifactMarkedForDeletion).then(() => {
              toast.success("Successfully deleted implementation-based evaluation criteria '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.implementationCriteria);
            });
          },
          globallyDeleteExperimentalEvaluationCriteria: function() {
            EvaluationScenarioServiceAPI.deleteExperimentalEvaluationCriteria(this.currentArtifactMarkedForDeletion).then(() => {
              toast.success("Successfully deleted experiment-based evaluation criteria '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.experimentCriteria);
            });
          },
          globallyDeleteBiometricProcessingstep: function() {
            BiometricSystemAPI.deleteBiometricProcessingStep(this.currentArtifactMarkedForDeletion).then(() => {
              toast.success("Successfully deleted biometric processing step '"+this.currentArtifactMarkedForDeletion.name+"'");
              this.resetArtifactMarkedForDeletion();
              this.updateTargetArtifactsForReview(constants.review.artifactType.biometricProcessingSteps);
            });
          },
            globallyDeleteBaseEvaluationExtensionToReview: function() {
              EvaluationScenarioServiceAPI.deleteBaseEvaluationExtension(this.currentArtifactMarkedForDeletion).then(() => {
                  toast.success("Successfully deleted evaluation extension for BASE '"+this.currentArtifactMarkedForDeletion.baseNameForExtension+"'");
                  this.resetArtifactMarkedForDeletion();
                  this.updateTargetArtifactsForReview(constants.review.artifactType.baseEvaluationExtension);
              });
          },
          continueDeletingArtifact: function() {
            switch(this.artifactTypeForDeletion) {
              case constants.review.artifactType.baseEvaluationExtension:
                  this.globallyDeleteBaseEvaluationExtensionToReview();
                  break;
              case constants.review.artifactType.sensors:
                this.globallyDeleteSensorAndRefreshSensorsToReview();
                break;
              case constants.review.artifactType.sensorDimensions:
                this.globallyDeleteSensorDimension();
                break;
              case constants.review.artifactType.resourcesToProtect:
                this.globallyDeleteResourcesToProtect();
                break;
              case constants.review.artifactType.samplingContexts:
                this.globallyDeleteSamplingContext();
                break;
              case constants.review.artifactType.biometricCharacteristics:
                this.globallyDeleteBiometricCharacteristic();
                break;
              case constants.review.artifactType.sampleDevices:
                this.globallyDeleteSamplingDevice();
                break;
              case constants.review.artifactType.deviceCategories:
                this.globallyDeleteDeviceCategory();
                break;
              case constants.review.artifactType.features:
                this.globallyDeleteFeature();
                break;
              case constants.review.artifactType.resultMetrics:
                this.globallyDeleteResultMetric();
                break;
              case constants.review.artifactType.experimentCriteria:
                this.globallyDeleteExperimentalEvaluationCriteria();
                break;
              case constants.review.artifactType.implementationCriteria:
                this.globallyDeleteImplementationEvaluationCriteria();
                break;
              case constants.review.artifactType.biometricProcessingSteps:
                this.globallyDeleteBiometricProcessingstep();
                break;
            }
          },
          editArtifact: function(artifactToEdit, artifactType) {
            this.$log.debug("\t...set artifact to edit of type '"+artifactType+"' in review browser to", artifactToEdit);
            switch(artifactType) {
              case constants.review.artifactType.resourcesToProtect:
                TargetArchitectureAPI.getResourcesToProtect().then(response => {
                  this.allResourcesToProtect = response.data;
                  this.artifactToEdit = artifactToEdit;
                  this.artifactTypeForEditing = artifactType;
                  this.artifactEditorOpened = true;
                });
                break;
              case constants.review.artifactType.samplingContexts:
                DatasetAPI.getSamplingContexts().then(response => {
                  this.allSamplingContexts = response.data;
                  this.artifactToEdit = artifactToEdit;
                  this.artifactTypeForEditing = artifactType;
                  this.artifactEditorOpened = true;
                })
                break;
              case constants.review.artifactType.features:
                BiometricSystemAPI.getFeatures().then(response => {
                  this.allFeatures = response.data;
                  this.artifactToEdit = artifactToEdit;
                  this.artifactTypeForEditing = artifactType;
                  this.artifactEditorOpened = true;
                })
                break;
              case constants.review.artifactType.sensorDimensions:
                SensorAPI.getSensorDimensions().then(response => {
                  this.allSensorDimensions = response.data;
                  this.artifactToEdit = artifactToEdit;
                  this.artifactTypeForEditing = artifactType;
                  this.artifactEditorOpened = true;
                })
                break;
              case constants.review.artifactType.resultMetrics:
                  this.artifactToEdit = artifactToEdit;
                  this.artifactTypeForEditing = artifactType;
                break;
              case constants.review.artifactType.implementationCriteria:
                this.artifactToEdit = artifactToEdit;
                this.artifactTypeForEditing = artifactType;
                break;
              case constants.review.artifactType.experimentCriteria:
                this.artifactToEdit = artifactToEdit;
                this.artifactTypeForEditing = artifactType;
                break;
              case constants.review.artifactType.baseEvaluationExtension:
                  BiometricAuthenticationSystemAndEvaluationAPI
                      .getBiometricAuthenticationSystemWithSpecificName(artifactToEdit.baseNameForExtension).then(response => {

                      this.baseEvaluationExtensionParentBase = response.data;
                      this.artifactToEdit = artifactToEdit;
                      this.artifactTypeForEditing = artifactType;
                      this.artifactEditorOpened = true;
                  });
                  break;
              default:
                this.artifactToEdit = artifactToEdit;
                this.artifactTypeForEditing = artifactType;
                this.artifactEditorOpened = true;
            }

          },
          artifactSuccessfullyEdited: function(artifactType) {
            this.artifactToEdit = {};
            this.artifactTypeForEditing = "";
            this.artifactEditorOpened = false;
            this.updateTargetArtifactsForReview(artifactType);
            switch(artifactType) {
              case constants.review.artifactType.sampleDevices:
                this.updateTargetArtifactsForReview(constants.review.artifactType.deviceCategories);
                this.updateTargetArtifactsForReview(constants.review.artifactType.sensors);
                break;
              case constants.review.artifactType.datasets:
                this.updateTargetArtifactsForReview(constants.review.artifactType.deviceCategories);
                this.updateTargetArtifactsForReview(constants.review.artifactType.sensors);
                break;
              case constants.review.artifactType.resultMetrics:
                this.updateTargetArtifactsForReview(constants.review.artifactType.experimentCriteria);
                break;
              case constants.review.artifactType.baseEvaluationExtension:
                this.updateTargetArtifactsForReview(constants.review.artifactType.baseEvaluationExtension);
                this.updateTargetArtifactsForReview(constants.review.artifactType.datasets);
                this.updateTargetArtifactsForReview(constants.review.artifactType.biometricProcessingSteps);
                this.updateTargetArtifactsForReview(constants.review.artifactType.experimentCriteria);
                this.updateTargetArtifactsForReview(constants.review.artifactType.implementationCriteria);
                break;
            }
          },
          updateBaseEvaluationExtension: function(baseEvaluationExtension) {
              this.$log.debug("updating base evaluation extension to merge later", baseEvaluationExtension);
              this.editedBaseEvaluationExtension = baseEvaluationExtension;
          },
          persistUpdatedBaseEvaluationExtension: function() {
              EvaluationScenarioServiceAPI.createUpdateBaseEvaluationExtension(this.editedBaseEvaluationExtension).then(() => {
                 this.artifactSuccessfullyEdited(constants.review.artifactType.baseEvaluationExtension);
              });
          }
        },
        beforeRouteEnter (to, from, next) {

          next(vm => {
            /*if(vm.$auth.isAuthenticated()) {
              Vue.$log.debug("DO..allow entering artifact review browser");
              if (to.path === constants.routes.ownArtifactsInReview.path) {
                vm.showsOwnArtifactsForReview = true;
              } else if (to.path === constants.routes.artifactsRequiringReview.path) {
                vm.showsAllArtifactsToReview = true;
              }
              return true;
            }
            Vue.$log.debug("DO NOT!..allow entering artifact review browser");
            return false;*/
            if (to.path === constants.routes.ownArtifactsInReview.path) {
              vm.showsOwnArtifactsForReview = true;
            } else if (to.path === constants.routes.artifactsRequiringReview.path) {
              vm.showsAllArtifactsToReview = true;
            }
          })
        }
    }
</script>

<style scoped>

</style>
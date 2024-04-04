<template>
    <v-card>
        <v-stepper v-model="modelProgress" @change="setCurrentlyPresentModelStep" :alt-labels="true">
            <v-stepper-header>
                <v-stepper-step :editable="characteristicsEditable" :complete="modelProgress>1"
                                step="1">{{characteristicsStepperText}}</v-stepper-step>

                <v-divider></v-divider>

                <v-stepper-step :editable="architectureEditable" :complete="modelProgress>2"
                                step="2">{{targetArchitectureStepperText}}</v-stepper-step>

                <v-divider></v-divider>

                <v-stepper-step :editable="evaluationEditable" :complete="modelProgress>3"
                                step="3">{{evaluationsStepperText}}</v-stepper-step>
                <v-divider></v-divider>

                <v-stepper-step :editable="evaluationCriteriaGrantsEditable" :complete="modelProgress>4"
                                step="4">{{evaluationCriteriaGrantsStepperText}}</v-stepper-step>
                <v-divider></v-divider>

                <v-stepper-step :editable="referenceEditable" :complete:="basystem.modellingProgress==='reference'"
                                step="5">{{referenceStepperText}}</v-stepper-step>
            </v-stepper-header>

            <v-stepper-items>
                <v-stepper-content step="1">
                    <v-container fluid>
                        <v-row row wrap>
                            <v-col cols="12" lg="12">
                                <v-text-field
                                        label="Name your System"
                                        required
                                        v-model="basystem.name"
                                        :rules="[rules.baseName]"
                                        hint="Use the name to later refer to your system"
                                />
                            </v-col>
                        </v-row>
                    </v-container>
                    <BiometricCharacteristicsSelector
                            :characteristics-state="basystem.biometricCharacteristics"
                            v-on:set-characteristics="setCharacteristics"
                            v-on:reset-characteristics="resetCharacteristics">
                    </BiometricCharacteristicsSelector>
                    <BaseModelProcessControls
                            :enabled="modelProgress<2"
                            :disabled-info-text="manualNavigationHint"
                            :proceed-action-text="'Proceed to target architecture'"
                            :proceed-activated="!editMode"
                            :update-activated="editMode"
                            :update-action-disabled="!proceedToTargetArchitectureAllowed"
                            :proceed-action-disabled="!proceedToTargetArchitectureAllowed"
                            :suspend-action-disabled="!proceedToTargetArchitectureAllowed"
                            v-on:cancel="cancelModelling(false)"
                            v-on:suspend="suspendModelling"
                            v-on:proceed="proceedToTargetArchitecture"
                            v-on:update="updateModel"
                    />
                </v-stepper-content>

                <v-stepper-content step="2">
                    <TargetArchitectureSelector
                            :target-architecture-state="basystem.targetArchitecture"
                            v-on:set-target-architecture="setTargetArchitecture"
                            v-on:reset-target-architecture="resetTargetArchitecture">
                    </TargetArchitectureSelector>
                    <BaseModelProcessControls
                            :enabled="modelProgress<3"
                            :disabled-info-text="manualNavigationHint"
                            :proceed-action-text="'Proceed to Evaluation'"
                            :proceed-action-disabled="!proceedToEvaluationAllowed"
                            :suspend-action-disabled="!proceedToEvaluationAllowed"
                            :update-action-disabled="!proceedToTargetArchitectureAllowed"
                            :proceed-activated="!editMode"
                            :update-activated="editMode"
                            v-on:cancel="cancelModellingDialog = true"
                            v-on:suspend="suspendModelling"
                            v-on:proceed="proceedToEvaluation"
                            v-on:update="updateModel"
                    />
                </v-stepper-content>

                <v-stepper-content step="3">
                    <BaseEvaluation
                            :base-characteristics="basystem.biometricCharacteristics"
                            :deployment-locations="allModelledDeviceCategories"
                            :parent-base-name="basystem.name"
                            :base-evaluation-state="basystem.baseEvaluation"
                            v-on:set-evaluation="setEvaluation"
                            v-on:reset-evaluation="resetEvaluation"/>
                    <BaseModelProcessControls
                            :enabled="modelProgress<4"
                            :disabled-info-text="manualNavigationHint"
                            :proceed-action-text="'Proceed to Criteria Grants'"
                            :proceed-activated="!editMode"
                            :update-activated="editMode"
                            :proceed-action-disabled="!proceedToCriteriaGrantsAllowed"
                            :suspend-action-disabled="!proceedToCriteriaGrantsAllowed"
                            :update-action-disabled="!proceedToEvaluationAllowed"
                            v-on:cancel="cancelModellingDialog = true"
                            v-on:suspend="suspendModelling"
                            v-on:proceed="proceedToCriteriaGrants"
                            v-on:update="updateModel"
                    />
                </v-stepper-content>
                <v-stepper-content step="4">
                    <EvaluationCriteriaGrantBrowser
                        :evaluation-criteria-grants="basystem.evaluationCriteriaGrants"
                        :base-evaluation="basystem.baseEvaluation"
                        v-on:set-evaluation-criteria-grants="setEvaluationCriteriaGrants"
                        :edit-mode="true"
                    />
                    <BaseModelProcessControls
                            :enabled="modelProgress<5"
                            :proceed-action-text="'Proceed to Model Reference'"
                            :disabled-info-text="manualNavigationHint"
                            :proceed-activated="!editMode"
                            :update-activated="editMode"
                            :proceed-action-disabled="!proceedToBaseReferenceAllowed"
                            :suspend-action-disabled="!proceedToBaseReferenceAllowed"
                            :update-action-disabled="!proceedToCriteriaGrantsAllowed"
                            v-on:cancel="cancelModellingDialog = true"
                            v-on:suspend="suspendModelling"
                            v-on:proceed="proceedToBaseReference"
                            v-on:update="updateModel"
                    />
                </v-stepper-content>
                <v-stepper-content step="5">
                    <ReferenceEditor
                            :description-content-of-reference="'biometric authentication system and its evaluations'"
                            :reference-asset-type=referenceBaseType
                            :pre-filled-reference="basystem.reference"
                            v-on:set-reference="setReference">
                    </ReferenceEditor>
                    <BaseModelProcessControls
                            :proceed-action-text="'Finish Modelling'"
                            :proceed-activated="!editMode"
                            :update-activated="editMode"
                            :proceed-action-disabled="!finishModellingAllowed"
                            :suspend-action-disabled="!finishModellingAllowed"
                            :update-action-disabled="!proceedToBaseReferenceAllowed"
                            v-on:cancel="cancelModellingDialog = true"
                            v-on:suspend="suspendModelling"
                            v-on:proceed="finishModelling"
                            v-on:update="updateModel"
                    />
                </v-stepper-content>
            </v-stepper-items>
        </v-stepper>
        <v-dialog v-model="cancelModellingDialog" persistent >
            <v-card>
                <v-card-title class="headline">Delete current modelling Progress?</v-card-title>
                <v-card-text>If you choose 'yes', all progress will be deleted. If you choose 'no' you can continue modelling later on</v-card-text>
                <v-card-actions>
                    <div class="flex-grow-1"></div>
                    <v-btn color="error" text @click="cancelModelling(true)">Yes</v-btn>
                    <v-btn color="primary" text @click="cancelModelling(false)">No</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-card>
</template>

<script>

    import {BiometricAuthenticationSystemAndEvaluationAPI} from "../../service/api/BiometricAuthenticationSystemAndEvaluationAPI";
    import toast from "../../helpers/toast";
    import EventBus from "../../helpers/eventBus";
    import BiometricCharacteristicsSelector from "../characteristics/BiometricCharacteristicsSelector";
    import BaseModelProcessControls from "./BaseModelProcessControls";
    import TargetArchitectureSelector from "../target_architecture/TargetArchitectureSelector";
    import BaseEvaluation from "../evaluations/BaseEvaluationEditor.vue";
    import constants from "../../helpers/constants";
    import {Util} from "../../helpers/util";
    import Vue from 'vue';
    import EvaluationCriteriaGrantBrowser from "../evaluations/criteria/grant/EvaluationCriteriaGrantBrowser";
    import {EvaluationScenarioServiceAPI} from "../../service/api/EvaluationScenarioServiceAPI";
    import LoggedInUserMixin from "@/mixins/LoggedInUserMixin";
    import ReferenceEditor from "@/components/reference/ReferenceEditor.vue";
    import {DeviceCategoryServiceAPI} from "@/service/api/DeviceCategoryServiceAPI";

    export default {
        components: {
            EvaluationCriteriaGrantBrowser,
            ReferenceEditor,
            BaseEvaluation,
            TargetArchitectureSelector,
            BaseModelProcessControls,
            BiometricCharacteristicsSelector
        },
        mixins: [LoggedInUserMixin],
        props: {
            activatorWidth: {
                type: String,
                default: "lg2 md2 sm2 xs2",
            },
            activatorColor: {
                type: String,
                default: "info",
            },
            showModelBaseDialog: {
                type: Boolean,
                default: false,
            },
            modelState: {
                type: Object,
                default: function() {
                    return {};
                }
            }
        },
        data() {
            return {
                modelBaseDialog: false,
                cancelModellingDialog: false,
                rules: {
                    baseName: value => {
                        const indexBase = this.allBases.findIndex(function(base) {
                            return base.name.toLowerCase() === value.toLowerCase();
                        })

                        if(indexBase>-1) {
                            if(this.basystem.id && this.basystem.id.length>0 && this.basystem.id === this.allBases[indexBase].id) {
                                return true;
                            } else {
                                return 'This name is already given, please choose a different one'
                            }
                        } else {
                            return true;
                        }
                    },
                },
                basystem:{
                    id: null,
                    name: "",
                    modellingProgress: "",
                    biometricCharacteristics:[],
                    targetArchitecture:{},
                    baseEvaluation: {
                        implementationSpecificEvaluationResults: [],
                        experimentSpecificEvaluation: {},
                    },
                    evaluationCriteriaGrants: [],
                    reference: {},
                    modelledElementDetail: {},
                },
                updatedEvaluationCriteriaGrants: [],
                editMode: false,
                allBases: [],
                currentlyPresentModelStep: -1,
                doNotPersistModelUpdatesBecauseOfStepperChange: false,
            }
        },
        beforeRouteEnter (to, from, next) {
            Vue.$log.debug("before route enter", to, from);
            if(to.params.name && !to.params.modelState) {
                BiometricAuthenticationSystemAndEvaluationAPI.getBiometricAuthenticationSystemWithSpecificName(to.params.name).then(
                    (response) => {
                      Vue.$log.debug("base by name response", response);
                        if(response.status===200) {
                          toast.success("Successfully restored model state of base '" + to.params.name + "'");
                          to.params["modelState"] = response.data;
                          next();
                        } else if(response.status===404) {
                          toast.warning("No modelled system with name '" + to.params.name + "' exists, just model '" + to.params.name + "' yourself ;)", 5000)
                          next(vm => (vm.basystem.name = to.params.name));
                        } else if(response.status===401) {
                          toast.warning("You are not allowed to model base '" + to.params.name + "'", 5000)
                          next("/");
                        }

                    }
                );
            } else {
                next();
            }
        },
        // when route changes and this component is already rendered,
        // the logic will be slightly different.
        beforeRouteUpdate (to, from, next) {
            Vue.$log.debug("before route update", to, from);
            BiometricAuthenticationSystemAndEvaluationAPI.getBiometricAuthenticationSystemWithSpecificName(to.params.name).then(
                (response) => {
                    switch (response.data.length) {
                        case 0:
                            toast.error("No modelled system with name '"+to.params.name+"' exists, you could model a new one!", 5000);
                            this.basystem.name = to.params.name;
                            next();
                            break;
                        case 1:
                            toast.success("Successfully restored model state of base '"+to.params.name+"'", 5000);
                            this.initBaSystemFromModelState(response.data[0]);
                            next();
                            break;
                        default:
                            toast.error("More than one system with '"+to.params.name+"' exists, you should check you data model for integrity :P");
                            next();
                            break;
                    }

                }
            );
        },
        computed: {
            referenceBaseType() {
                return constants.referenceAssetType.base;
            },
            manualNavigationHint: function() {
                let currentTask = "";

                switch(this.modelProgress) {
                    case 2:
                        currentTask = "Modelling Target architecture"
                        break;
                    case 3:
                        currentTask = "Modelling Evaluation"
                        break;
                    case 4:
                        currentTask = "Modelling Evaluation criteria grants"
                        break;
                    case 5:
                        currentTask = "Modelling Reference"
                        break;
                    default:
                        currentTask = "Modelling Biometric Characteristics"
                }

                return "Please use the top bar to return your original task ("+currentTask+"). Please note," +
                    " that you can only leave the current step if all information are valid";
            },
            modelProgress: {
                get:function() {
                    switch(this.basystem.modellingProgress) {
                        case constants.modellingProgress.characteristics:
                            return 2;
                        case constants.modellingProgress.targetArchitecture:
                            return 3;
                        case constants.modellingProgress.evaluation:
                            return 4;
                        case constants.modellingProgress.evaluationCriteriaGrants:
                            return 5;
                        case constants.modellingProgress.reference:
                            return 5;
                    }
                    return 1;
                },
                set: function(value) {
                    this.$log.debug("Set model progress to " + value);
                }
            },
            characteristicsStepperText: function() {
                if(this.modelProgress>=2) {
                    return "BASE '"+this.basystem.name
                        +"' includes "
                        +this.basystem.biometricCharacteristics.length+" characteristic(s) " /*+
                        "("
                        +this.basystem.biometricCharacteristics.map(m=>
                          {
                            if(m.type == constants.biometricCharacteristics.types.behavioral){
                              return m.userActivity.name
                            } else {
                              return m.name
                            }
                          }).join(", ")+")"*/;

                }
                return "Set Biometric Characteristics";
            },
            targetArchitectureStepperText: function() {

                if(this.modelProgress>=3) {

                    if(this.basystem.targetArchitecture.skipTargetArchitecture) {
                        return "No specific target architecture exists";
                    } else {
                        return "BASE targets deployment(s) on "+this.allModelledDeviceCategories.length+" different device type(s)";
                    }

                }
                return "Define Target Architecture";
            },
            evaluationsStepperText: function() {

                this.$log.debug("Calculating evaluations Stepper Text based on modelprogress ", this.modelProgress);

                if(this.modelProgress>=4) {

                    this.$log.debug("\tevaluation already modeled, set text based on: ", this.basystem.baseEvaluation);
                    let stepperText = "";

                    const experimentEvaluationDone =
                        this.basystem.baseEvaluation.experimentSpecificEvaluation
                            && this.basystem.baseEvaluation.experimentSpecificEvaluation.experimentSpecificEvaluationSetups;
                    const implementationEvaluationDone =
                        this.basystem.baseEvaluation.implementationSpecificEvaluationResults
                            && this.basystem.baseEvaluation.implementationSpecificEvaluationResults.length>0;

                    stepperText = (implementationEvaluationDone ? " Implementation specific evaluations " + (experimentEvaluationDone ? "and " : "were done")  : "")
                        + (experimentEvaluationDone ? this.basystem.baseEvaluation.experimentSpecificEvaluation.experimentSpecificEvaluationSetups.length
                        + (implementationEvaluationDone ? " e" : " E") + "xperiment specific evaluations were executed" : "");

                    return stepperText.length>0 ? stepperText : "Report Evaluation Results";
                }
                return "Report Evaluation Results";
            },
            evaluationCriteriaGrantsStepperText: function() {
                // whenever a criteria is created, a grant is created as well and put to "not granted" per default
                if(this.modelProgress>=5) {
                    return this.basystem.evaluationCriteriaGrants.length +" evaluation criteria grants exist";
                }

                return "Set Evaluation Criteria Grants";

            },
            referenceStepperText: function() {
                if(this.basystem.modellingProgress===constants.modellingProgress.reference) {
                    return (this.basystem.reference.assetModelledByOriginator ? "BASE was modelled by its originator " : "BASE was reported ")+
                            " specified by "+this.basystem.reference.furtherInformationReferences.length+" references";
                }
                return "Define Reference";
            },
            characteristicsEditable: function() {

                return this.evaluateIfStepIsEditable(0);

            },
            architectureEditable: function() {

                return this.evaluateIfStepIsEditable(1);

            },
            evaluationEditable: function() {

                return this.evaluateIfStepIsEditable(2);

            },
            evaluationCriteriaGrantsEditable: function() {

                return this.evaluateIfStepIsEditable(3);

            },
            referenceEditable: function() {

                return this.evaluateIfStepIsEditable(4);

            },
            proceedToTargetArchitectureAllowed() {
                return this.basystem.biometricCharacteristics.length>0 && this.basystem.name.length>0 && this.rules.baseName(this.basystem.name)===true;
            },
            proceedToEvaluationAllowed() {
                this.$log.debug("verifying whether target architecture is fine, target architecture:", this.basystem.targetArchitecture);
                this.$log.debug("\tis fine?: "+!Util.isEmpty(this.basystem.targetArchitecture));
                return !Util.isEmpty(this.basystem.targetArchitecture);
            },
            proceedToCriteriaGrantsAllowed() {
                if(this.basystem.baseEvaluation) {
                  return (this.basystem.baseEvaluation.implementationSpecificEvaluationResults && this.basystem.baseEvaluation.implementationSpecificEvaluationResults.length > 0)
                      || !Util.isEmpty(this.basystem.baseEvaluation.experimentSpecificEvaluation);
                }

                return false;
            },
            proceedToBaseReferenceAllowed() {
                // since grants will be created per default with "not granted" as the level, a modeler can continue instantly
                return true;
            },
            finishModellingAllowed() {
                // specific validation done in reference component
                return !Util.isEmpty(this.basystem.reference);
            },
            allModelledDeviceCategories() {
                return DeviceCategoryServiceAPI.getAllModelledDeviceCategoriesForBase(this.basystem.targetArchitecture);
            },
        },
        mounted: function() {
            // subscribe on global events
            EventBus.$on(constants.eventNames.global.updateBaseRequired, this.getAllBase);

            // get all Base to decide if new name is valid
            this.getAllBase();

        },
        created() {
            //TODO => initially query for system details
        },
        watch: {
            modelState: {
                deep:true,
                immediate: true,
                handler: function(newState) {

                    //potentially set local ba system if a specified model state is given
                    if (newState.id) {
                        this.$log.debug("Set model process core from model state from outside", newState);
                        this.initBaSystemFromModelState(newState);
                    }
                }
            },
            showModelBaseDialog: function(newState) {
                this.modelBaseDialog = newState;
            },
            currentlyPresentModelStep: function(newStep, oldStep) {
                this.$log.debug("Change current present step from "+oldStep+" to "+newStep+" while modelprogress is "+this.modelProgress);

                if(!this.doNotPersistModelUpdatesBecauseOfStepperChange) {
                    if (newStep === this.modelProgress && oldStep !== this.modelProgress) {
                        this.$log.debug("update model as it was switched manually between the states");
                        this.updateModel();
                    }
                } else {
                    this.$log.debug("Do not update model as simple step change to ", newStep);

                    //evaluation criteria grant need no reload and reference is always your entry point and thus loaded
                    this.updateStepperContent(newStep);
                    this.doNotPersistModelUpdatesBecauseOfStepperChange = false;
                }
            },
        },
        methods: {
            getAllBase: function() {
                BiometricAuthenticationSystemAndEvaluationAPI.getBiometricAuthenticationSystems().then((response) => {

                    this.allBases = response.data;

                });
            },
            setCurrentlyPresentModelStep: function(step) {
                this.currentlyPresentModelStep = Number(step);
                this.$log.debug("Setting current present model step to "+this.currentlyPresentModelStep);
            },
            updateStepperContent: function(newStep) {
                switch(newStep) {
                    case 1:
                        //characteristics
                        setTimeout(() => {  this.$emit(constants.eventNames.global.editCharacteristicsEvent, this.basystem.biometricCharacteristics); }, 500);
                        break;
                    case 2:
                        //target architecture
                        setTimeout(() => {this.$emit(constants.eventNames.global.editTargetArchitectureEvent, this.basystem.targetArchitecture);}, 500);
                        break;
                    case 3:
                        //evaluation
                        setTimeout(() => {this.$emit(constants.eventNames.global.editEvaluationEvent, this.basystem.baseEvaluation);}, 500);
                        break;
                }
            },
            initBaSystemFromModelState: function(modelState) {
                this.$log.debug("A model state was given for base '" + modelState.name + "' - start modelling from there!", modelState);
                if(!modelState.evaluationCriteriaGrants) {
                  modelState.evaluationCriteriaGrants = [];
                }
                this.basystem = modelState;
                this.doNotPersistModelUpdatesBecauseOfStepperChange = true;
            },
            proceedToTargetArchitecture: function () {

                const currentModellingProgress = this.basystem.modellingProgress;

                // check, whether modelling just started, so we might set the respective first step progress
                // if editing happened, lets take the existing progress, so do nothing
                if(currentModellingProgress.length===0) {
                    this.basystem.modellingProgress = constants.modellingProgress.characteristics;
                    this.setInitialModelLifecycleStateForDomainElement(this.basystem);
                }

                this.createUpdateBehavioralAuthenticationSystem().then(response => {

                    if(response.data.id) {
                        this.basystem.id = response.data.id;
                    } else {
                        this.basystem.id = response.data;
                    }
                    this.logBaseDetails();

                }).catch(() => {

                    // only reset modelling progress if we started modelling but not if we edited
                    if(currentModellingProgress.length===0) {
                        this.basystem.modellingProgress = "";
                    }

                })
            },
            logBaseDetails: function() {
              this.$log.debug("BA System after persisting after finishing '"+this.basystem.modellingProgress+"'", this.basystem);
            },
            proceedToEvaluation: function () {

                this.basystem.modellingProgress = constants.modellingProgress.targetArchitecture;

                this.createUpdateBehavioralAuthenticationSystem().then(() => {
                  this.logBaseDetails();
                }).catch(() => {
                    this.basystem.modellingProgress = constants.modellingProgress.characteristics;
                })



            },
            proceedToCriteriaGrants: function() {

                this.basystem.modellingProgress = constants.modellingProgress.evaluation;

                this.createUpdateBehavioralAuthenticationSystem().then().catch(() => {
                    this.basystem.modellingProgress = constants.modellingProgress.targetArchitecture;
                })

            },
            proceedToBaseReference: function() {

                this.basystem.modellingProgress = constants.modellingProgress.evaluationCriteriaGrants;
                this.basystem.evaluationCriteriaGrants = this.updatedEvaluationCriteriaGrants;

                this.createUpdateBehavioralAuthenticationSystem().then().catch(() => {
                    this.basystem.modellingProgress = constants.modellingProgress.evaluation;
                })

            },
            finishModelling: function() {

                this.basystem.modellingProgress = constants.modellingProgress.reference;

                this.createUpdateBehavioralAuthenticationSystem().then(() => {
                    toast.success("Thank you for modelling your BASE", 5000);
                    EventBus.$emit(constants.eventNames.global.updateBaseRequired);
                    this.$router.push({path: "/"});
                    this.resetBaSystem();
                }).catch(() => {
                    this.basystem.modellingProgress = constants.modellingProgress.evaluation;
                })

            },
            updateModel: function() {
                this.createUpdateBehavioralAuthenticationSystem().then(() => {
                    toast.success("Updated BASE");
                });
            },
            createUpdateBehavioralAuthenticationSystem: function() {

                this.$log.info("create update system", this.basystem);
                this.doNotPersistModelUpdatesBecauseOfStepperChange = true;

                return BiometricAuthenticationSystemAndEvaluationAPI.createUpdateBiometricAuthenticationSystem(this.basystem).then(
                    (response) => {
                        this.$log.debug("return response", response);
                        return response;
                    }
                );
            },
            suspendModelling: function() {
                this.createUpdateBehavioralAuthenticationSystem().then(() => {
                    //close model dialog
                    this.$emit(constants.eventNames.local.closeModelDialog);
                    //publish event to update all base as information may have changed
                    EventBus.$emit(constants.eventNames.global.updateBaseRequired);
                    this.resetBaSystem();
                });
            },
            cancelModelling: function(deleteModelProgress) {
                this.$log.debug("Cancel modelling called", "delete progress", deleteModelProgress);
                this.cancelModellingDialog = false;
                if(deleteModelProgress) {
                    BiometricAuthenticationSystemAndEvaluationAPI.deleteBiometricAuthenticationSystemAndEvaluation(this.basystem).then(() => {
                        this.$emit(constants.eventNames.local.closeModelDialog);
                        this.resetBaSystem();
                        this.browseHome();
                    });
                } else {
                    this.$emit(constants.eventNames.local.closeModelDialog);
                    this.resetBaSystem();
                    this.browseHome();
                }

            },
            resetBaSystem: function() {
                this.doNotPersistModelUpdatesBecauseOfStepperChange = true;
                this.basystem = {
                    id: null,
                    modellingProgress: "",
                    characteristics:[],
                    evaluationCriteriaGrants: [],
                    targetArchitecture:{},
                    baseEvaluation: {
                      implementationSpecificEvaluationResults: [],
                      experimentSpecificEvaluation: {},
                    },
                    reference: {},
                };
            },
            setCharacteristics: function(characteristics) {
                this.$log.debug("update characteristics for parent base ", characteristics);
                this.basystem.biometricCharacteristics = characteristics;
            },
            resetCharacteristics: function() {
                this.basystem.biometricCharacteristics = [];
            },
            setTargetArchitecture: function(targetArchitecture) {
                this.basystem.targetArchitecture = targetArchitecture;
            },
            resetTargetArchitecture: function() {
                this.basystem.targetArchitecture = {};
            },
            setEvaluation: function(evaluation) {

                this.$log.debug("...set base evaluation for base", evaluation);
                this.basystem.baseEvaluation = evaluation;
                this.$log.debug("\t...possibly update evaluation criteria grants")

                let criteriaUsedInEvaluation = [];

                // get all criteria from implementation specific evaluations
                evaluation.implementationSpecificEvaluationResults.forEach((implSpecificResult)=> {
                    criteriaUsedInEvaluation.push(implSpecificResult.criteria);
                })

                Vue.$log.debug("criterias referenced by impl specific results", criteriaUsedInEvaluation);

                let criteriaIdsFromExperimentResults = [];
                if(evaluation.experimentSpecificEvaluation &&
                    evaluation.experimentSpecificEvaluation.experimentSpecificEvaluationSetups) {
                    // if experiment specific evaluation exists, get criteria to retrieve for grants
                    evaluation.experimentSpecificEvaluation.experimentSpecificEvaluationSetups.forEach((experimentSpecificResult) => {
                        experimentSpecificResult.specificResults.forEach((result) => {
                            criteriaIdsFromExperimentResults.push(result.resultMetric.parentEvaluationCriteriaId);
                        });
                    })

                    if(criteriaIdsFromExperimentResults.length>0) {
                        Vue.$log.debug("ids of criteria from experiment-specific evaluations to retrieve", criteriaIdsFromExperimentResults);
                        EvaluationScenarioServiceAPI.getExperimentSpecificEvaluationCriteriasById(criteriaIdsFromExperimentResults).then(response => {

                            Vue.$log.debug("\tcriteria:", response.data);
                            //merge with potential implementation based criteria
                            criteriaUsedInEvaluation = criteriaUsedInEvaluation.concat(response.data);

                            // merge criteria
                            this.basystem.evaluationCriteriaGrants =
                                EvaluationScenarioServiceAPI.mergeWithExistingGrants(criteriaUsedInEvaluation, this.basystem.evaluationCriteriaGrants);

                            this.$log.debug("\t\t...updated evaluation criteria grants", this.basystem.evaluationCriteriaGrants);
                        });
                    }
                } else {
                    this.basystem.evaluationCriteriaGrants =
                        EvaluationScenarioServiceAPI.mergeWithExistingGrants(criteriaUsedInEvaluation, this.basystem.evaluationCriteriaGrants);

                    this.$log.debug("\t\t...updated evaluation criteria grants", this.basystem.evaluationCriteriaGrants);
                }




            },
            resetEvaluation: function() {
                this.basystem.baseEvaluation = {
                    implementationSpecificEvaluationResults: [],
                    experimentSpecificEvaluation: {},
                };
            },
            setEvaluationCriteriaGrants: function(evaluationCriteriaGrants) {
                this.updatedEvaluationCriteriaGrants = evaluationCriteriaGrants;
            },
            setReference: function(reference) {
                this.basystem.reference = reference;
                this.createUpdateBehavioralAuthenticationSystem();
            },
            evaluateIfStepIsEditable: function(requiredModelProgress) {

                this.$log.debug("Evaluate if step with required model progress of "+requiredModelProgress+" is editable");

                let isEditable = false;

                if(this.currentlyPresentModelStep === -1) {

                    // no manual stepper navigation was done until now, set editable based on model progress
                    // since target architecture is the second step, it is always editable once characteristics were
                    // modelled successfully

                    if(this.modelProgress>requiredModelProgress) {
                        // characteristics were modelled successfully -> target architecture can be edited
                        isEditable = true;
                    }

                } else {

                    this.$log.debug("Modelling was done before, current step: ", this.currentlyPresentModelStep);
                    this.$log.debug("\trequired model progress: ", requiredModelProgress);

                    // manual navigation has been done already, see if target architecture step is editable
                    if (this.currentlyPresentModelStep !== (requiredModelProgress+1)) {
                        // it was not navigated to target architecture but some other step
                        // target architecture should be editable
                        // as long as we have no validation issue in currently opened step and a respective model progress

                        isEditable = this.stepEditableDependingOnOverallModelProgressAndValidationStatus(requiredModelProgress);

                    } else {

                        if(this.currentlyPresentModelStep>1) {
                            this.$log.debug("The current present model step is the most current model task todo!");
                        }
                        isEditable = true;

                    }
                }

                this.$log.debug("Step with required model progress of "+requiredModelProgress+" is "+(isEditable ? "editable" : "not editable"));

                return isEditable;

            },
            stepEditableDependingOnOverallModelProgressAndValidationStatus: function(requiredModelProgress) {

                this.$log.debug("Evaluate, whether step is editable after manual navigation referring to required progress of "+requiredModelProgress);
                this.$log.debug("\tmodel progress: " + this.modelProgress);
                this.$log.debug("\tcurrently present model step: " + this.currentlyPresentModelStep);

                if(this.modelProgress > requiredModelProgress) {
                    // model progress allows editing of specific step in general
                    // depending on the validity of the current present step we decide if editing is possible
                    if (this.currentlyPresentModelStep === 1) {
                        // it was moved to the characteristics step manually, current step is editable if
                        // the characteristics step is valid
                        this.$log.debug("\tcurrently in characteristics, check if valid");
                        return this.proceedToTargetArchitectureAllowed;
                    } else if (this.currentlyPresentModelStep === 2) {
                        // it was moved to the target architecture step manually, current step is editable if
                        // the target architecture step is valid or if model progress fits to target architecture
                        this.$log.debug("\tcurrently in target architecture");
                        if(this.modelProgress === 2) {
                            this.$log.debug("\toverall progress is target architecture, so editable");
                            return true;
                        } else {
                            this.$log.debug("\toverall progress is different, so check if valid");
                            return this.proceedToEvaluationAllowed;
                        }
                    } else if (this.currentlyPresentModelStep === 3) {
                        // it was moved to the evaluation step manually, current step is editable if
                        // the evaluation step is valid
                        this.$log.debug("\tcurrently in evaluation");
                        if(this.modelProgress === 3) {
                            this.$log.debug("\toverall progress is evaluation, so editable");
                            return true;
                        } else {
                            this.$log.debug("\toverall progress is different, so check if valid");
                            return this.proceedToCriteriaGrantsAllowed;
                        }
                    } else if (this.currentlyPresentModelStep === 4){
                        // it was moved to the criteria grant step manually, current step is editable if
                        // the criteria grant step is valid
                        this.$log.debug("\tcurrently in criteria grants");
                        if(this.modelProgress === 4) {
                            this.$log.debug("\toverall progress is criteria grants, so editable");
                            return true;
                        } else {
                            this.$log.debug("\toverall progress is different, so check if valid");
                            return this.proceedToBaseReferenceAllowed;
                        }
                    } else {
                        // it was moved to the reference step manually, current step is editable if
                        // the reference step is valid
                        this.$log.debug("\tcurrently in reference");
                        if(this.modelProgress === 5) {
                            this.$log.debug("\toverall progress is reference, so editable");
                            return true;
                        } else {
                            this.$log.debug("\toverall progress is different, so check if valid");
                            return this.finishModellingAllowed;
                        }
                    }
                } else {
                    // although manual step navigation is possible, the overall progress does not allow modelling
                    // in terms of the required progress
                    return false;
                }

            }
        }
    }
</script>

<style>
</style>

<template>
  <v-card>
    <v-card-title class="headline">{{editMode ? 'Edit ':'Create new '}} processing step</v-card-title>
    <v-container>
      <v-form ref="processingStepForm">
        <v-row>
          <v-col cols="12" lg="6">
            <v-text-field
                v-model="processingStepToEdit.name"
                label="Name of Processing Step"
                :rules=[rules.processingStepUnique]
            />
          </v-col>
          <v-col lg="3">
            <v-select
                :label="'select processing type'"
                v-model="processingStepToEdit.processingType"
                :disabled="editMode"
                :items="processingTypes"
                :rules=[rules.processingStepUnique]
            />
          </v-col>
          <v-col lg="3" v-if="!editMode">
            <v-checkbox
                v-model="addToProcessingChainAfterCreation"
                label="Add to processing chain after creation"
            ></v-checkbox>
          </v-col>
        </v-row>
        <v-row v-if="processingStepToEdit.processingType===constants.processingsteptypes.feature">
          <v-col lg="6">
            <v-select
                :items="allExistingFeatures"
                v-model="selectedFeatures"
                small-chips
                clearable
                multiple
                deletable-chips
                return-object
                item-text="name"
                item-value="id"
                label="Please select at least one calculated feature"/>
          </v-col>
          <v-col lg="4">
            <v-text-field
                v-model="newFeatureName"
                label="Enter new feature name"
                :rules="[rules.newFeatureName]"
            />
          </v-col>
          <v-col lg="2">
            <v-btn text color="primary" :disabled="!addFeatureAllowed" @click="addNewFeature">Add</v-btn>
          </v-col>
        </v-row>
        <v-row v-if="processingStepToEdit.processingType===constants.processingsteptypes.decision">
          <v-col lg="12">
            <v-radio-group v-model="processingStepToEdit.decisionMode" row>
              <v-radio label="Identification" value="identification"></v-radio>
              <v-radio label="Verification" value="verification"></v-radio>
            </v-radio-group>
          </v-col>
        </v-row>
        <v-row>
          <Reference
              :pre-filled-reference="processingStepToEdit.reference"
              :reference-asset-type="processingStepReferenceAssetType"
              :description-content-of-reference="'Biometric Processing Step'"
              v-on:set-reference="setReferenceForProcessingStep"
              :additional-tooltip="', e.g. by specifying a detailed algorithm description of your processing'"
          />
        </v-row>
        <v-row v-if="machineLearningStep">
          <v-col cols="12"  lg="3">
            <v-checkbox
                v-model="processingStepToEdit.isDeepLearning"
                label="Deep Learning?"
            ></v-checkbox>
          </v-col>
          <v-col lg="9">
            <v-text-field
                v-model="processingStepToEdit.modelUrl"
                label="Model URL"
                hint="Optional, maybe you exported your model or even provide an ONNX version in case you used a deep learning model"/>
          </v-col>
        </v-row>
      </v-form>
    </v-container>
    <v-card-actions>
      <v-btn color="primary" :disabled="!processingStepToEditValid" text @click="createSaveProcessingStep">{{editMode ? 'Save ': 'Add '}} Processing Step</v-btn>
      <v-btn v-if="!editMode" color="error" text @click="cancelEditing">Cancel</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import constants from "@/helpers/constants";
import {Util} from "@/helpers/util";
import Reference from "@/components/reference/ReferenceEditor.vue";
import {BiometricSystemAPI} from "@/service/api/BiometricSystemAPI";
import toast from "@/helpers/toast";

export default {
  name: "ProcessingStepEditor",
  components: {Reference},
  data: function() {
    return {
      processingStepToEdit: {
        id: null,
        viewId:"",
        reference: {},
        type: "",
        name: "",
        processingType:"",
        decisionMode: "",
        isDeepLearning: false,
        modelUrl: "",
      },
      addToProcessingChainAfterCreation: false,
      selectedFeatures: [],
      rules: {
        newFeatureName: value => {
          const indexFeature = this.allExistingFeatures.findIndex(function(feature) {
            return feature.name === value;
          })

          if(indexFeature>-1) {
            return 'This feature already exist, please name a different one'
          } else {
            return true;
          }
        },
        processingStepUnique: () => this.processingStepNameUnique
            || 'A processing step "'+this.processingStepToEdit.name+'" of type "' + this.processingStepToEdit.processingType + '" already exists\'',
      },
      newFeatureName: "",
      allExistingFeatures: [],
      allBiometricProcessingSteps: [],
      editMode: false,
    }
  },
  props: {
      processingStepForEditing: {
        type: Object,
        default: function() {
          return {};
        }
      },
      featureTypeAllowed: {
        type: Boolean,
        default: true,
      }
  },
  watch: {
    processingStepForEditing: {
      immediate: true,
      deep: true,
      handler: function(processingStepForEditing) {
        if(processingStepForEditing
            && processingStepForEditing.processingType) {
          this.processingStepToEdit = Util.deepCopyObject(processingStepForEditing);
          if(this.processingStepToEdit.id) {
            this.editMode = true;
          }
        } else {
          this.editMode = false;
        }
      }
    },
    processingStepToEdit: {
      immediate: true,
      deep: true,
      handler: function(processingStepToEdit) {
        if(processingStepToEdit.name.length>0
          && processingStepToEdit.processingType.length>0) {
          this.$nextTick(() => {
            this.$refs.processingStepForm.validate();
          });
        }
      }
    }
  },
  computed: {

    processingStepNameUnique() {

      this.$log.debug("\t...check uniqueness of processing step name", this.processingStepToEdit.name);
      if(this.editMode) {
        if(this.processingStepForEditing.name===this.processingStepToEdit.name) {
          return true;
        }
      }

      if(this.allProcessingSteps) {
        this.$log.debug("\t...check uniqueness of processing step name and type", this.processingStepToEdit.name, this.processingStepToEdit.processingType);
        if(this.allProcessingSteps.filter((processingStep) => {
          return processingStep.name.toLowerCase() === this.processingStepToEdit.name.toLowerCase() &&
              processingStep.processingType === this.processingStepToEdit.processingType
        }).length === 0) {
          return true;
        }
      }
      return false;

    },
    constants() {
      return constants;
    },
    processingTypes() {
      if(!this.featureTypeAllowed) {
        return constants.processingsteptypes.getAllNoFeature();
      }
      return constants.processingsteptypes.getAll();
    },
    addFeatureAllowed() {
      if(this.newFeatureName.length===0) {
        return false;
      }
      return this.rules.newFeatureName(this.newFeatureName)===true;
    },
    processingStepToEditValid() {
      switch(this.processingStepToEdit.processingType) {
        case constants.processingsteptypes.feature:
          if(this.selectedFeatures.length===0) {
            return "At least on feature needs to be given!";
          }
          break;
        case constants.processingsteptypes.decision:
          if(this.processingStepToEdit.decisionMode.length===0) {
            return "Please provide the decision mode";
          }
          break;
      }
      return this.processingStepNameUnique;
    },
    machineLearningStep() {
      return this.processingStepToEdit.processingType===constants.processingsteptypes.decision
          || this.processingStepToEdit.processingType===constants.processingsteptypes.matching
    },
    processingStepReferenceAssetType() {
      return constants.referenceAssetType.processingStep;
    },
  },
  mounted() {
    this.$log.debug("mounted processing step editor!");
    this.getAllProcessingSteps();
    this.getAllFeatures();
  },
  methods: {
    getAllProcessingSteps: function() {
      BiometricSystemAPI.getProcessingSteps().then((response) => {
        this.allProcessingSteps = response.data;
      });
    },
    getAllFeatures: function() {
      BiometricSystemAPI.getFeatures().then((response) => {
        this.allExistingFeatures = response.data;
      });
    },
    createSaveProcessingStep:function() {

      this.$log.debug("Create new '"+this.processingStepToEdit.processingType+"' processing step on server side");

      let processingStepToEdit = {};
      if(this.processingStepToEdit.processingType===constants.processingsteptypes.decision) {
        processingStepToEdit = {
          name: this.processingStepToEdit.name,
          processingType: this.processingStepToEdit.processingType,
          decisionMode: this.processingStepToEdit.decisionMode,
          modelUrl: this.processingStepToEdit.modelUrl,
          isDeepLearning: this.processingStepToEdit.isDeepLearning,
          reference: this.processingStepToEdit.reference,
          type: constants.processingsteptypes.decision
        };
      } else if(this.processingStepToEdit.processingType===constants.processingsteptypes.matching) {
        processingStepToEdit = {
          name: this.processingStepToEdit.name,
          reference: this.processingStepToEdit.reference,
          isDeepLearning: this.processingStepToEdit.isDeepLearning,
          modelUrl: this.processingStepToEdit.modelUrl,
          processingType: this.processingStepToEdit.processingType,
          decisionMode: this.processingStepToEdit.decisionMode,
          type: constants.processingsteptypes.matching
        }
      } else {
        processingStepToEdit = {
          name: this.processingStepToEdit.name,
          processingType: this.processingStepToEdit.processingType,
          reference: this.processingStepToEdit.reference,
          type: constants.processingsteptypes.simple
        };
      }

      BiometricSystemAPI.createUpdateProcessingStep(processingStepToEdit).then((response) => {
        if(!this.processingStepToEdit.id) {
          this.processingStepToEdit["id"] = response.data;
        }
        if(this.editMode) {
          this.$emit(constants.eventNames.local.artifactCreatedUpdated, this.processingStepToEdit);
        } else {
          this.$emit(constants.eventNames.local.artifactCreatedUpdated,
              {step:this.processingStepToEdit,
                features:this.selectedFeatures,
                addToChain:this.addToProcessingChainAfterCreation});
        }
        this.resetNewProcessingStep();
      })
    },
    resetNewProcessingStep: function() {
      this.processingStepToEdit = {
        type: "",
        name: "",
        processingType: "",
        decisionMode: "",
        reference: {},
        isDeepLearning: false,
        modelUrl: "",
      }
      this.addToChain = false;
    },
    cancelEditing: function() {
      this.$emit(constants.eventNames.local.closeDialog);
    },
    setReferenceForProcessingStep: function(reference) {
      this.processingStepToEdit.reference = reference;
    },
    addNewFeature: function() {
      BiometricSystemAPI.createUpdateFeature( {name: this.newFeatureName}).then(()=> {
        toast.success("Successfully added new feature '"+this.newFeatureName+"'");
        BiometricSystemAPI.getFeatures().then((response) => {
          this.allExistingFeatures = response.data;
          this.selectedFeatures.push(this.allExistingFeatures[this.allExistingFeatures.findIndex(feature => {
            return feature.name === this.newFeatureName
          })])
          this.newFeatureName="";
        });

      });
    },
  }
}
</script>

<style scoped>

</style>
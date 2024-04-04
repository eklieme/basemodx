<template>
    <v-col :lg="activatorWidth">
        <v-dialog v-model="evaluationCriteriaEditorDialog" persistent>
            <template v-slot:activator="{ on }">
                <v-btn text v-on="on" slot="activator" color="info">Add Criteria</v-btn>
            </template>
            <v-card>
                <v-card-title>
                    <span class="headline">{{editMode ? 'Save ': 'Add new '}} Criteria</span>
                </v-card-title>
                <v-card-text>
                    <v-form ref="form">
                        <v-container fluid>
                            <v-row>
                                <v-col cols="12" lg="4">
                                    <v-text-field
                                        label="Name of criteria"
                                        :rules="[criteriaNameUnique]"
                                        required
                                        v-model="evaluationCriteriaToEdit.name"
                                    />
                                </v-col>
                                <v-col lg="4">
                                    <v-select
                                        :items="criteriaCategories"
                                        label="Criteria Category"
                                        required
                                        v-model="evaluationCriteriaToEdit.category"
                                    />
                                </v-col>
                                <v-col lg="4">
                                    <v-radio-group v-model="evaluationCriteriaToEdit.type" row :disabled="!typeSelectable">
                                        <v-radio label="Implementation Specific" value="implementation_specific"/>
                                        <v-radio label="Experiment Specific" value="experiment_specific"/>
                                    </v-radio-group>
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col cols="12" lg="12">
                                    <v-textarea
                                            label="Describe the criteria"
                                            required
                                            auto-grow
                                            :rules="[contentProvidedRule]"
                                            hint="Give a short summary on the specifics of this evaluation criteria"
                                            v-model="evaluationCriteriaToEdit.description"
                                    />
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col lg="4">
                                    <v-textarea
                                            label="Describe the rules for granting"
                                            required
                                            auto-grow
                                            :rules="[contentProvidedRule]"
                                            hint="Give a short summary on when the criteria is not granted, quasi-granted, or granted"
                                            v-model="evaluationCriteriaToEdit.grantedRule.rule"
                                    />
                                </v-col>
                                <v-col lg="4">
                                    <v-textarea
                                        label="Describe the rules for quasi granting"
                                        required
                                        auto-grow
                                        :rules="[contentProvidedRule]"
                                        hint="Give a short summary on when the criteria is quasi-granted"
                                        v-model="evaluationCriteriaToEdit.quasiGrantedRule.rule"
                                    />
                                </v-col>
                                <v-col lg="4">
                                    <v-textarea
                                        label="Describe the rules for not granting"
                                        required
                                        auto-grow
                                        :rules="[contentProvidedRule]"
                                        hint="Give a short summary on when the criteria is not granted"
                                        v-model="evaluationCriteriaToEdit.notGrantedRule.rule"
                                    />
                                </v-col>
                            </v-row>
                            <v-row>
                              <Reference
                                :additional-tooltip="', e.g. a paper that described why this is a valid criteria'"
                                :reference-asset-type="evaluationCriteriaReference"
                                :pre-filled-reference="evaluationCriteriaToEdit.reference"
                                v-on:set-reference="setReferenceForNewCriteria"
                                :description-content-of-reference="'Evaluation Criteria'"
                              />
                            </v-row>
                        </v-container>
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-btn color="primary" text @click="addSaveEvaluationCriteria()" :disabled="!evaluationCriteriaValid" >{{editMode ? 'Save ':'Add ' }} Criteria</v-btn>
                    <v-btn color="error" text @click="cancelEditing">Cancel</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-col>
</template>

<script>

    import {EvaluationScenarioServiceAPI} from "../../../service/api/EvaluationScenarioServiceAPI";
    import constants from "../../../helpers/constants";
    import toast from "../../../helpers/toast";
    import Reference from "@/components/reference/ReferenceEditor.vue";
    import {Util} from "@/helpers/util";

    export default {
        components: {Reference},
        name: "EvaluationCriteriaEditor",
        data() {
            return {
                evaluationCriteriaToEdit: {
                    id: null,
                    name: "",
                    category: "usability",
                    description:"",
                    grantedRule: {
                      grantingLevel: "granted",
                      rule: "",
                    },
                    quasiGrantedRule: {
                      grantingLevel: "quasiGranted",
                      rule: "",
                    },
                    notGrantedRule: {
                      grantingLevel: "notGranted",
                      rule: "",
                    },
                    type:"",
                    resultMetrics: [],
                    reference: {
                      referenceAssetType: constants.referenceAssetType.evaluationCriteria,
                    },
                },
                evaluationCriteriaEditorDialog:false,
                criteriaCategories: [
                    "usability",
                    "deployability",
                    "security",
                ],
                existingCriterias: [],
                editMode: false,

            }
        },
        props: {
            activatorWidth: {
                type: String,
                default: "xs2",
            },
            criteriaType: {
                type: String,
                default: "",
            },
            typeSelectable: {
                type: Boolean,
                default: true,
            },
            evaluationCriteriaForEditing: {
              type: Object,
              default: function() {
                return {};
              }
            }
        },
        computed: {
          evaluationCriteriaReference: function() {
            return constants.referenceAssetType.evaluationCriteria;
          },
          evaluationCriteriaValid: function() {

            return this.criteriaNameUnique(this.evaluationCriteriaToEdit.name)
                && this.evaluationCriteriaToEdit.description.length>0
                && this.evaluationCriteriaToEdit.grantedRule.rule.length>0
                && this.evaluationCriteriaToEdit.quasiGrantedRule.rule.length>0
                && this.evaluationCriteriaToEdit.notGrantedRule.rule.length>0
                && this.evaluationCriteriaToEdit.category.length>0
                && this.evaluationCriteriaToEdit.type.length>0
                && this.evaluationCriteriaToEdit.name.length>0
          },
        },
        watch: {
            newCriteria: {
                deep: true,
                handler: function() {
                    if(this.$refs.form) {
                        this.$refs.form.validate();
                    }
                }
            },
            criteriaType: function(newType) {
                //set criteria type
                this.$log.debug("New criteria type: "+newType);
                this.evaluationCriteriaToEdit.type = newType;
            },
            evaluationCriteriaForEditing: {
                immediate: true,
                deep: true,
                handler: function(evaluationCriteriaForEditing) {
                  if(evaluationCriteriaForEditing
                      && evaluationCriteriaForEditing.id) {
                    this.$log.debug("\t...set eval criteria of type '"+this.criteriaType+"' for editing", evaluationCriteriaForEditing);
                    this.evaluationCriteriaToEdit = Util.deepCopyObject(evaluationCriteriaForEditing);
                    if(this.evaluationCriteriaToEdit.type===constants.evaluationTypes.enriched_experimentSpecific) {
                        this.evaluationCriteriaToEdit.type = constants.evaluationTypes.experimentSpecific;
                    }
                    this.evaluationCriteriaEditorDialog = true;
                    this.editMode = true;
                  } else {
                    this.evaluationCriteriaEditorDialog = false;
                    this.editMode = false;
                  }
                }
              },
        },
        mounted: function() {
            //get all existing criterias to make sure that no one gets added twice
            this.getTypeSpecificExistingCriterias();
            this.evaluationCriteriaToEdit.type = this.criteriaType;
        },
        methods: {
            cancelEditing: function() {
                this.evaluationCriteriaEditorDialog = false;
                this.$emit(constants.eventNames.local.cancelEditing);
            },
            criteriaNameUnique: function(newCriteriaName) {

                if(this.editMode) {
                  if(newCriteriaName === this.evaluationCriteriaForEditing.name) {
                    return true;
                  }
                }

                const context = this;
                const indexCriteria = this.existingCriterias.findIndex(function(criteria) {
                    return criteria.name.toLowerCase() === newCriteriaName.toLowerCase() && criteria.category === context.evaluationCriteriaToEdit.category;
                })

                if(indexCriteria>-1) {
                    return 'A criteria with this name already exists in the category "'+
                        this.evaluationCriteriaToEdit.category+'", please choose a different name or category'
                } else {
                    return true;
                }
            },
            contentProvidedRule: function(content) {
              return content.length>0;
            },
            getTypeSpecificExistingCriterias: function() {
                const context = this;
                if(this.criteriaType === constants.evaluationTypes.experimentSpecific) {
                    EvaluationScenarioServiceAPI.getExperimentSpecificEvaluationCriterias().then(
                        (response) => {
                            context.existingCriterias = response.data;
                        }
                    )
                } else {
                    EvaluationScenarioServiceAPI.getImplementationSpecificEvaluationCriterias().then(
                        (response) => {
                            context.existingCriterias = response.data;
                        }
                    )
                }
            },
            setReferenceForNewCriteria: function(reference) {
              this.evaluationCriteriaToEdit.reference = reference;
            },
            addSaveEvaluationCriteria: function() {

                EvaluationScenarioServiceAPI.createUpdateEvaluationCriteria(this.evaluationCriteriaToEdit).then(
                    (response) => {
                        if(!this.evaluationCriteriaToEdit.id) {
                          this.evaluationCriteriaToEdit["id"] = response.data;
                        }
                        this.$emit('criteria-added', this.evaluationCriteriaToEdit.category);
                        this.$emit(constants.eventNames.local.artifactCreatedUpdated, this.evaluationCriteriaToEdit);
                        if(this.editMode) {
                          toast.success("Successfully saved criteria '" + this.evaluationCriteriaToEdit.name + "'");
                        } else {
                          toast.success("Successfully created criteria '" + this.evaluationCriteriaToEdit.name + "'");
                        }
                        this.evaluationCriteriaToEdit = {
                            id: null,
                            name: "",
                            category: "",
                            description: "",
                            type: this.criteriaType,
                            reference: {
                              referenceAssetType: constants.referenceAssetType.evaluationCriteria,
                            },
                            grantedRule: {
                              grantingLevel: "granted",
                              rule: "",
                            },
                            quasiGrantedRule: {
                              grantingLevel: "quasiGranted",
                              rule: "",
                            },
                            notGrantedRule: {
                              grantingLevel: "notGranted",
                              rule: "",
                            },
                            resultMetrics: [],
                        };
                        this.evaluationCriteriaEditorDialog = false;
                        this.getTypeSpecificExistingCriterias();
                    }
                );
            },

        }
    }
</script>

<style scoped>

</style>
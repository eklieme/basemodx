<template>
    <v-container fluid>
        <v-row>
            <v-col cols="12" lg="12" v-if="standAlone">
                <span class="headline">{{descriptionContentOfReference}} reference</span>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="9" v-if="standAlone">
                <div
                        v-if="referenceToEdit.furtherInformationReferences.length === 0"
                        key="title"
                        class="title font-weight-light grey--text"
                >
                    Please provide at least one reference to the {{descriptionContentOfReference}}
                </div>
                <div
                        v-else
                        key="title"
                        class="title grey--text"
                >
                    {{referenceSummary}}
                </div>
            </v-col>
            <v-col cols="12" lg="9" v-if="!standAlone" >
              {{referenceSummary}}
            </v-col>
            <v-col lg="2">
                <v-dialog v-model="referenceInformationDialog" transition="dialog-bottom-transition">
                    <template v-slot:activator="{ on }">
                        <v-btn text v-on="on" color="primary" >References</v-btn>
                    </template>
                    <v-card>
                        <v-card-text>
                            <v-container fluid>
                                <v-row>
                                    <v-col cols="12" lg="12">
                                        <span class="headline">{{ referencesText }}</span>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col cols="12" lg="3">
                                        <v-checkbox
                                                v-model="referenceToEdit.assetModelledByOriginator"
                                                :label="'I am the originator of the '+descriptionContentOfReference"
                                        ></v-checkbox>
                                    </v-col>
                                    <v-col lg="8" offset-lg="1">
                                        <v-text-field
                                                label="E-Mail"
                                                v-if="referenceToEdit.assetModelledByOriginator"
                                                v-model="referenceToEdit.originatorEmail"
                                                :rules="[rules.email]"
                                        ></v-text-field>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col cols="12" lg="12">
                                        <div
                                                v-if="referenceToEdit.furtherInformationReferences.length === 0"
                                                key="title"
                                                class="title font-weight-light grey--text"
                                        >
                                            Please provide at least one specific reference to the {{descriptionContentOfReference}} such as a web link providing more information about it, for download, or the title of the paper describing it.
                                        </div>
                                        <template v-else>
                                            <div
                                                    key="title"
                                                    class="title font-weight-light grey--text"
                                            >
                                                Reference(s)
                                            </div>
                                            <v-data-table
                                                          :headers="furtherInformationReferencesHeaders"
                                                          :items="referenceToEdit.furtherInformationReferences"
                                                          hide-default-footer
                                            >
                                                <template v-slot:body=" {items} ">
                                                    <tbody>
                                                        <tr v-for="item in items" :key="item.name">
                                                            <td>{{ item.reference }}</td>
                                                            <td>{{ item.furtherInformationReferenceType }}</td>
                                                            <td>
                                                                <v-icon
                                                                        @click="deleteReference(item)"
                                                                >
                                                                    delete
                                                                </v-icon>
                                                                <v-icon
                                                                        @click="editReference(item)"
                                                                >
                                                                    edit
                                                                </v-icon>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </template>
                                            </v-data-table>
                                        </template>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col cols="12" lg="5">
                                        <v-text-field
                                                label="Reference"
                                                required
                                                v-model="furtherInformationReferenceToEdit.reference"
                                        ></v-text-field>
                                    </v-col>
                                    <v-col offset-lg="1" lg="4">
                                        <v-select
                                                :items="furtherInformationReferenceTypes"
                                                v-model="furtherInformationReferenceToEdit.furtherInformationReferenceType"
                                                :menu-props="{ maxHeight: '400', closeOnContentClick: true }"
                                                label="Type of reference"
                                                persistent-hint
                                                return-object
                                        ></v-select>
                                    </v-col>
                                    <v-col lg="1">
                                        <v-btn text color="primary" :disabled="!specificReferenceToEditValid" @click="saveSpecificReference">
                                            {{this.isNewSpecificReference ? "Add" : "Save"}} Specific Reference
                                        </v-btn>
                                    </v-col>
                                </v-row>
                            </v-container>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="primary" text @click="saveReferenceInformation()" :disabled="!referenceValid">Save Reference</v-btn>
                            <v-btn color="error" text @click="referenceInformationDialog = false">Cancel</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
              <v-tooltip bottom>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon
                      color="primary"
                      dark
                      v-bind="attrs"
                      v-on="on"
                  >
                    info
                  </v-icon>
                </template>
                <span>A reference helps to follow your model and makes it easier to reproduce your evaluations{{additionalTooltip}}</span>
              </v-tooltip>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>

    import {Util} from "@/helpers/util";

    export default {
        name: "ReferenceEditor",
        components: {},
        props: {
            descriptionContentOfReference: {
                type: String,
                default: "Generic reference"
            },
            referenceAssetType: {
                type: String,
                default: "unspecified",
            },
            preFilledReference: {
                type: Object,
                default: function() {
                    return {};
                }
            },
            standAlone: {
                type: Boolean,
                default: false,
            },
            referencesText: {
                type: String,
                default: "References",
            },
            additionalTooltip: {
                type: String,
                default:"",
            }
        },
        data() {
            return {
                referenceInformationDialog: false,
                assetModelledByOriginator: false,
                originatorEmail: "",
                referenceToEdit: {
                    assetModelledByOriginator: false,
                    originatorEmail: "",
                    furtherInformationReferences: [],
                    referenceAssetType: this.referenceAssetType,
                },
                furtherInformationReferenceToEdit: {
                    furtherInformationReferenceType: "",
                    reference: "",
                },
                rules: {
                    email: value => {
                        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
                        return pattern.test(value) || 'Invalid e-mail.'
                    },
                },
                isNewSpecificReference: true,
                indexSpecificReferenceToEdit: -1,
                furtherInformationReferenceTypes: ["paper", "weblink"],
                furtherInformationReferencesHeaders: [
                    { text: 'Reference', value: 'reference' },
                    { text: 'Type', value: 'referenceType'},
                    { text: 'Actions', value: 'actions'},
                ],
            }
        },
        methods: {
            saveSpecificReference: function() {
                if(this.isNewSpecificReference) {
                    this.referenceToEdit.furtherInformationReferences.push(this.furtherInformationReferenceToEdit);
                } else {
                    this.referenceToEdit.furtherInformationReferences.splice(this.indexSpecificReferenceToEdit,1,this.furtherInformationReferenceToEdit);
                    this.isNewSpecificReference = true;
                }
                this.resetNewSpecificReference();
            },
            resetNewSpecificReference: function() {
                this.furtherInformationReferenceToEdit = {
                    furtherInformationReferenceType: "",
                    reference: "",
                };
            },
            editReference: function(reference) {
                this.indexSpecificReferenceToEdit = this.specificReferences.indexOf(reference);
                this.furtherInformationReferenceToEdit = reference;
                this.isNewSpecificReference = false;
            },
            deleteReference: function(reference) {
                const index = this.referenceToEdit.furtherInformationReferences.indexOf(reference);
                this.referenceToEdit.furtherInformationReferences.splice(index, 1);
            },
            saveReferenceInformation: function() {
                this.$emit("set-reference", this.referenceToEdit);
                this.referenceInformationDialog = false;
            },
            updateReferenceToEdit: function(newReference) {
                this.$log.debug("Update reference to edit with", newReference);
                this.referenceToEdit = newReference;
            }

        },
        computed: {
            specificReferenceToEditValid: function() {
                return this.furtherInformationReferenceToEdit.furtherInformationReferenceType.length>0
                    && this.furtherInformationReferenceToEdit.reference.length>0;
            },
            referenceValid: function() {
                let originatorValid = (!this.referenceToEdit.assetModelledByOriginator ||
                    (this.referenceToEdit.assetModelledByOriginator && this.referenceToEdit.originatorEmail.length>0 && this.rules.email(this.referenceToEdit.originatorEmail)===true));
                return this.referenceToEdit.furtherInformationReferences.length>0
                    && originatorValid;
            },
            referenceSummary: function() {
                return (this.referenceToEdit.assetModelledByOriginator ? "This "+this.descriptionContentOfReference+ " was created by "+this.referenceToEdit.originatorEmail : "This "+this.descriptionContentOfReference+" already existed before and is")
                    + " further specified by "+this.referenceToEdit.furtherInformationReferences.length+" references";
            },
        },
        watch: {
            preFilledReference: {
                deep: true,
                immediate: true,
                handler: function(newReferenceState) {
                    if(newReferenceState &&
                        !Util.isEmpty(newReferenceState)
                        && newReferenceState.furtherInformationReferences
                        && newReferenceState.furtherInformationReferences.length>0) {
                        this.updateReferenceToEdit(this.preFilledReference);
                    }
                }
            }
        }
    }
</script>

<style scoped>

</style>
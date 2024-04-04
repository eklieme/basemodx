<template>
    <v-container fluid>
        <v-row>
            <v-col cols="12" lg="12">
                <span class="headline">Experiment participant details</span>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="9">
                <template v-if="editMode">
                    <v-select
                        label="Please provide information about the experiment participants (age, gender)"
                        v-model="participantsKnowledge"
                        return-object
                        small-chips
                        :items="possibleParticipantsKnowledge">
                    </v-select>
                </template>
                <div
                        class="title grey--text"
                >
                    {{participantInformationSummary}}
                </div>
            </v-col>
            <v-col lg="3" v-if="numberOfParticipantsKnown">
                <v-text-field
                        v-model.number="participantInformationToEdit.numberOfParticipants"
                        hide-details
                        v-if="editMode"
                        single-line
                            label="Number of Participants"
                            type="number"
                />
            </v-col>
            <v-col offset-lg="1" lg="2" v-if="fullParticipantDetailsKnown && editMode">
                <v-dialog v-model="participantInformationDialog" transition="dialog-bottom-transition">
                    <template v-slot:activator="{ on }">
                        <v-btn v-on="on" text block slot="activator" color="primary" >Participant information</v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">Experiment participants {{participantInformationToEdit.participantDetails.length > 0 ? "("+participantInformationToEdit.participantDetails.length+")" : ""}}</span>
                        </v-card-title>
                        <v-card-text>
                            <v-container fluid>
                                <v-row>
                                    <v-col cols="12" lg="12">
                                        <v-data-table
                                                v-if="participantInformationToEdit.participantDetails.length>0"
                                                :headers="participantHeaders"
                                                :items="participantInformationToEdit.participantDetails"
                                                class="elevation-1"
                                                hide-default-footer
                                        >
                                            <template v-slot:item.action = {item}>
                                                <td>
                                                    <v-icon
                                                            @click="deleteParticipant(item)"
                                                    >
                                                        delete
                                                    </v-icon>
                                                    <v-icon
                                                            @click="editParticipant(item)"
                                                    >
                                                        edit
                                                    </v-icon>
                                                    <v-icon
                                                            @click="duplicateParticipant(item)"
                                                    >
                                                        file_copy
                                                    </v-icon>
                                                </td>
                                            </template>
                                            <template v-slot:no-data>
                                                <v-btn color="primary" @click="resetParticipants">Reset</v-btn>
                                            </template>
                                        </v-data-table>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col cols="12" lg="12">
                                        <div class="title font-weight-light grey--text pa-3 text-xs-center">
                                        New Participant
                                        </div>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col cols="12" lg="3">
                                        <v-slider
                                                v-model="participantToEdit.age"
                                                thumb-label="always"
                                                label="Age"
                                                :max=105
                                        ></v-slider>
                                    </v-col>
                                </v-row>
                                <v-row>
                                    <v-col lg="3">
                                        <SelectAutoAddComboBox
                                                :pre-selected-entity="participantToEdit.gender"
                                                :entity-name="'Gender'"
                                                :all-entities="allGenders"
                                                v-on:entity-list-changed="gendersChanged"
                                        />
                                    </v-col>
                                    <v-col cols="12" lg="9">
                                        <v-btn color="primary" text :disabled="!addParticipantAllowed" @click="saveParticipant">
                                            {{this.isNewParticipant ? "Add" : "Save"}} Participant
                                        </v-btn>
                                    </v-col>
                                </v-row>
                            </v-container>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn color="primary" text @click="saveParticipantInformation()" :disabled="!participantDetailsValid">Save Information</v-btn>
                            <v-btn color="error" text @click="participantInformationDialog = false">Cancel</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>


    import SelectAutoAddComboBox from "../../../util/SelectAutoAddComboBox";
    import {Util} from "../../../../helpers/util";
    import constants from "../../../../helpers/constants";
    export default {
        components: {SelectAutoAddComboBox},
        name: "ParticipantInformation",
        props: {
            participantInformation: {
                type:Object,
                default: function() {
                    return {};
                }
            },
            editMode: {
                type: Boolean,
                default: true,
            }
        },
        data() {
            return {
                participantHeaders: [
                    { text: 'Age', value: 'age' },
                    { text: 'Gender', value: 'gender'},
                    { text: 'Action', value: 'action'},
                ],
                allGenders: [
                    "male",
                    "female",
                    "diverse",
                ],
                participantToEdit: {
                  age: 0,
                  gender: "",
                },
                participantsKnowledge: constants.participantInformation.noDetailsKnown,
                possibleParticipantsKnowledge: [
                    constants.participantInformation.allDetailsKnown,
                    constants.participantInformation.numberParticipantsKnown,
                    constants.participantInformation.noDetailsKnown,
                ],
                isNewParticipant: true,
                indexParticipantToEdit: -1,
                participantInformationDialog:false,
                participantInformationToEdit: {},
            }
        },
        computed: {
            participantDetailsValid() {
                return this.participantInformation.participantDetails.length>0;
            },
            fullParticipantDetailsKnown() {
                return this.participantsKnowledge.value === constants.participantInformation.allDetailsKnown.value;
            },
            numberOfParticipantsKnown() {
                return this.participantsKnowledge.value === constants.participantInformation.numberParticipantsKnown.value;
            },
          participantInformationSummary() {
              if(this.participantInformation.participantDetailsUnknown) {
                  return "The reporter of this dataset does not know anything about the participants";
              } else {
                  if(this.participantInformation.participantDetails.length>0) {
                      const participantAges = this.participantInformation.participantDetails.map(participantDetail => participantDetail.age);
                      return "Data was collected from " + this.participantInformation.participantDetails.length +
                          " participants with gender distribution of "
                          + this.participantInformation.participantDetails.filter(function (participantDetail) {
                              return participantDetail.gender === 'male'
                          }).length + "m/"
                          + this.participantInformation.participantDetails.filter(function (participantDetail) {
                              return participantDetail.gender === 'female'
                          }).length + "f/"
                          + this.participantInformation.participantDetails.filter(function (participantDetail) {
                              return participantDetail.gender === 'diverse'
                          }).length + "d"
                          + " and age distribution of "
                          + "max: " + Math.max(...participantAges)
                          + ", min: " + Math.min(...participantAges)
                          + ", mean: " + participantAges.reduce((x, y) => x + y, 0) / participantAges.length;
                  } else {
                      return "Data was collected from "+this.participantInformation.numberOfParticipants+" participants";
                  }
              }
          },
          addParticipantAllowed: function() {
            return this.participantToEdit.age>0 && this.participantToEdit.gender.length>0
          }
        },
        watch: {
            participantInformation: {
                immediate: true,
                deep: true,
                handler: function(participantInformation) {
                    this.$log.debug("participant information changed to", participantInformation);
                  if(participantInformation) {
                    if (participantInformation.participantDetailsUnknown) {
                      this.participantsKnowledge = constants.participantInformation.noDetailsKnown;
                    } else {
                      if (participantInformation.participantDetails.length > 0) {
                        this.participantsKnowledge = constants.participantInformation.allDetailsKnown;
                      } else {
                        this.participantsKnowledge = constants.participantInformation.numberParticipantsKnown;
                      }
                    }
                  }
                  this.participantInformationToEdit = this.participantInformation;
                }
            },
            participantsKnowledge: {
                immediate: true,
                deep: true,
                handler: function(participantsKnowledgeState) {
                    this.$log.debug("participant knowledge state changed", participantsKnowledgeState);
                    this.participantInformationToEdit.participantDetails = [];
                    switch(participantsKnowledgeState.value) {
                        case constants.participantInformation.noDetailsKnown.value:
                            this.participantInformationToEdit.participantDetailsUnknown = true;
                            break;
                        case constants.participantInformation.allDetailsKnown.value:
                            this.participantInformationToEdit.participantDetailsUnknown = false;
                            break;
                        case constants.participantInformation.numberParticipantsKnown.value:
                            this.participantInformationToEdit.participantDetailsUnknown = false;
                            break;
                    }
                },
            }
        },
        methods: {
            saveParticipantInformation: function() {
                this.participantInformationDialog = false;
            },
            saveParticipant: function() {
                if(this.isNewParticipant) {
                    this.participantInformationToEdit.participantDetails.push(this.participantToEdit);
                } else {
                    this.participantInformationToEdit.participantDetails.splice(this.indexParticipantToEdit,1,this.participantToEdit);
                    this.indexParticipantToEdit = -1;
                    this.isNewParticipant = true;
                }
                this.participantToEdit = {
                    age: 0,
                    gender: ""
                }
            },
            gendersChanged:function(allGenders, selectedGender) {
                this.participantToEdit.gender = selectedGender;
            },
            editParticipant: function(participant) {
                const index = this.participantInformation.participantDetails.indexOf(participant);
                this.participantToEdit = this.participantInformation.participantDetails[index];
                this.isNewParticipant = false;
            },
            duplicateParticipant: function(participant) {
                this.participantInformationToEdit.participantDetails.push(Util.deepCopyObject(participant));
            },
            deleteParticipant: function(participant) {
                this.indexParticipantToEdit = this.participantInformation.participantDetails.indexOf(participant);
                this.participantInformationToEdit.participantDetails.splice(this.indexParticipantToEdit, 1);
            },
            resetParticipants: function() {
                this.participantInformationToEdit.participantDetails = [];
            }
        }
    }
</script>

<style scoped>

</style>
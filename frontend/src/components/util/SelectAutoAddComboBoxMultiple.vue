<template>
    <v-combobox
            v-model="selectedEntityArray"
            :items="allEntities"
            :value="preSelectedEntities"
            :search-input.sync="searchEntity"
            hide-selected
            :label="realLabel"
            :menu-props="menuProps"
            :disabled="disabled"
            persistent-hint
            :clearable="clearable"
            :multiple="true"
            :small-chips="showAsSmallChips"
            :chips="showAsChips"
            :deletable-chips="true"

    >
        <template v-slot:no-data>
            <v-list-item v-if="allEntities.length===0 && (!searchEntity || (searchEntity && searchEntity.length===0))">
                <v-list-item-content>
                    <v-list-item-title>
                        Please enter a new {{entityName}}
                    </v-list-item-title>
                </v-list-item-content>
            </v-list-item>
            <v-list-item v-if="allEntities.length===1 && (!searchEntity || (searchEntity && searchEntity.length===0))">
                <v-list-item-content>
                    <v-list-item-title>
                        You can add further {{entityNameLower}}s by simply adding text
                    </v-list-item-title>
                </v-list-item-content>
            </v-list-item>
            <v-list-item v-if="searchEntity && searchEntity.length>0">
                <v-list-item-content>
                    <v-list-item-title >
                        Press <kbd>enter</kbd> to create "<strong>{{ searchEntity }}</strong>" as a new {{entityNameLower}}
                    </v-list-item-title>
                </v-list-item-content>
            </v-list-item>
        </template>
        <template v-slot:append-outer v-if="appendIcon">
            <v-icon
                    small
                    @click="iconClicked"
            >
                {{specificIcon}}
            </v-icon>
        </template>
        <template v-slot:prepend-item v-if="prependActionForAllTimes && selectedEntityArray.length>0">
            <v-list-item
                    ripple
                    @click="informAboutActionForAllSelectedItems"
            >
            <v-list-item-content>
                <v-list-item-title>{{labelActionForAllItems}}</v-list-item-title>
            </v-list-item-content>
            </v-list-item>
            <v-divider class="mt-2"></v-divider>
        </template>
    </v-combobox>
</template>

<script>

    export default {
        name: "SelectAutoAddComboBoxMultiple",
        components: {},
        props: {
            entityName: String,
            allEntities: Array,
            preSelectedEntities: {
                type: Array,
                default: function() {
                    return [];
                },
            },
            presentationStyle: {
                type: String,
                default: "small-chips"
            },
            disabled: Boolean,
            appendIcon: {
                type:Boolean,
                default: false,
            },
            specificIcon: {
                type:String,
                default: "",
            },
            closeOnSelection: {
                type: Boolean,
                default: false,
            },
            clearable: {
                type: Boolean,
                default: true,
            },
            label: {
                type:String,
                default:"",
            },
            prependActionForAllTimes: {
                type: Boolean,
                default: false,
            },
            labelActionForAllItems: {
                type: String,
                default: "",
            }
        },
        data() {
          return {
              searchEntity: "",
              selectedEntityArray: [],
              firstChange: false,
              allEntitiesToChange: [],
          }
        },
        watch: {
            selectedEntityArray: {
              deep: true,
              immediate: true,
              handler: function(selectedEntities) {
                this.$log.debug("selected Entities changed to in multiple selectAdd Box", selectedEntities);
                if(!this.firstChange) {
                    if(selectedEntities.length===0) {
                        this.$log.debug("no entities selected add all!");
                        this.publishEntityChange(false);
                    } else {
                        let newEntity = selectedEntities[selectedEntities.length - 1];
                        if (newEntity != null && newEntity.length > 0) {
                            if (this.allEntities.indexOf(newEntity) === -1) {
                                this.allEntitiesToChange.push(newEntity)
                                this.searchEntity = "";

                                this.publishEntityChange(true);
                            } else {
                                this.publishEntityChange(false);
                            }
                        }
                    }
                } else {
                    this.firstChange = false;
                }

              }
            },
            allEntities: {
              deep: true,
              immediate: true,
              handler: function(allEntities) {
                this.allEntitiesToChange = allEntities;
              }
            },
            preSelectedEntities: {
                deep:true,
                handler: function(preSelectedEntities, previouslySelectedEntities) {
                    this.$log.debug("pre selected Entities from outside changed in multiple selectAdd Box (to/from)", preSelectedEntities, previouslySelectedEntities);

                    // check if different
                    if (preSelectedEntities.length !== previouslySelectedEntities.length
                        && preSelectedEntities.length>0) {
                        this.firstChange = true;
                        this.selectedEntityArray = preSelectedEntities;
                    }
                }
            }
        },
        mounted() {
            if(this.preSelectedEntities && this.preSelectedEntities.length>0) {
                this.firstChange = true;
                this.selectedEntityArray = this.preSelectedEntities;
            }
        },
        computed: {
            entityNameLower(){
                return this.entityName.toLowerCase();
            },
            realLabel() {
                return (this.label.length>0 ? this.label : this.entityName)
            },
            menuProps() {
                if(this.closeOnClick) {
                    return { closeOnContentClick: true };
                }
                return {};
            },
            showAsChips() {
                return this.presentationStyle === "chips";
            },
            showAsSmallChips() {
                return this.presentationStyle === "small-chips";
            },
        },
        methods: {
            publishEntityChange(newElementAdded) {
                this.$emit("entity-list-changed", this.allEntities, this.selectedEntityArray[this.selectedEntityArray.length-1], newElementAdded, this.selectedEntityArray);
            },
            iconClicked() {
                this.$emit("icon-clicked", this.entityName);
            },
            informAboutActionForAllSelectedItems() {
                this.$emit("trigger-action-for-all-selected-items");
            },
            arraysEqual(array1, array2) {
                return array1.length === array2.length
                && array1.sort().every(function(value, index) {
                    return value === array2.sort()[index]
                });
            }
        }
    }
</script>

<style scoped>

</style>
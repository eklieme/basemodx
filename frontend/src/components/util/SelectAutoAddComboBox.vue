<template>
    <v-combobox
            v-model="selectedEntitySimple"
            :items="allEntities"
            :search-input.sync="searchEntity"
            hide-selected
            :label="realLabel"
            :menu-props="menuProps"
            :disabled="disabled"
            :small-chips="showAsSmallChips"
            :chips="showAsChips"
            persistent-hint
            :deletable-chips="deletableChips"
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
                        You can add further {{entityNameLower}}s by simply typing its name
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
    </v-combobox>
</template>

<script>

    export default {
        name: "SelectAutoAddComboBox",
        components: {},
        props: {
            entityName: String,
            allEntities: Array,
            preSelectedEntity: {
                type: String,
                default: "",
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
            label: {
                type:String,
                default:"",
            },
            deletableChips: {
                type: Boolean,
                default: false,
            }
        },
        data() {
          return {
              searchEntity: "",
              selectedEntitySimple: "",
              considerPublishChange: true,
              allEntitiesToChange: [],
          }
        },
        watch: {
            selectedEntitySimple(newEntity) {

                this.$log.debug("selected entity changed");
                if(this.considerPublishChange) {
                    if (newEntity != null && newEntity.length > 0) {
                        if (this.allEntitiesToChange.indexOf(newEntity) === -1) {
                            this.allEntitiesToChange.push(newEntity)
                            this.searchEntity = "";
                            this.$log.debug("publish change, new Entity")
                            this.publishEntityChange(true);
                        } else {
                            this.$log.debug("publish change, selected Entity")
                            this.publishEntityChange(false);
                        }
                    } else {
                        this.$log.debug("new entity is null");
                        this.publishEntityNulled();
                    }
                } else {
                    this.considerPublishChange = true;
                }

            },
            preSelectedEntity(newSelection, oldSelection) {
                this.$log.debug("pre selected entity changed from '"+oldSelection+"' to '"+newSelection+"' for '"+this.entityName+"'")
                this.setSelectedEntity(newSelection);
            },
            allEntities: {
                deep: true,
                immediate: true,
                handler: function(allEntities) {
                  this.allEntitiesToChange = allEntities;
                  this.setSelectedEntity(this.preSelectedEntity);
                }
            }
        },
        mounted: function() {
            //if sth. is pre-selected => do select!
            this.setSelectedEntity(this.preSelectedEntity);
        },
        computed: {
            entityNameLower(){
                return this.entityName.toLowerCase();
            },
            realLabel() {
                return (this.label.length>0 ? this.label : this.entityName)
            },
            showAsChips() {
                return this.presentationStyle === "chips";
            },
            showAsSmallChips() {
                return this.presentationStyle === "small-chips";
            },
            menuProps() {
                if(this.closeOnClick) {
                    return { closeOnContentClick: true };
                }
                return {};
            },
        },
        methods: {
            publishEntityChange(newElementAdded) {
                this.$emit("entity-list-changed", this.allEntities, this.selectedEntitySimple, newElementAdded);
            },
            publishEntityNulled() {
                this.$emit("selected-entity-nulled");
            },
            setSelectedEntity(preSelection) {
                this.$log.debug("Consider setting selection based on pre selected entity: '"+preSelection+"' for '"+this.entityName+"'");
                if(preSelection && this.allEntities.indexOf(preSelection)>-1) {
                    this.$log.debug("Set selected entity based on preselection for '"+this.entityName+"'");
                    this.selectedEntitySimple = preSelection;
                } else {
                    this.$log.debug("ignore preselection for '"+this.entityName+"'");
                    this.selectedEntitySimple = "";
                }
            },
            iconClicked() {
                this.$emit("icon-clicked", this.entityName);
            }
        }
    }
</script>

<style scoped>

</style>
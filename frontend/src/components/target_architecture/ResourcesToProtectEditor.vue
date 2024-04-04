<template>
  <v-container fluid>
    <v-row cols="12" lg="12">
      <v-col lg="10">
        <v-text-field
            v-model="resourcesToProtect.name"
            label="'Resources To Protect'"
            :rules=[resourcesToProtectRule.unique]
        />
      </v-col>
      <v-col lg="2">
        <v-btn color="primary" text @click="saveResourcesToProtect" :disabled="!resourcesToProtectUnique">Save Resources To Protect</v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import {TargetArchitectureAPI} from "@/service/api/TargetArchitectureAPI";
import constants from "@/helpers/constants";
import {Util as Utils} from "@/helpers/util";

export default {
  name: "ResourcesToProtectEditor",
  data() {
    return {
      resourcesToProtect: {},
      resourcesToProtectRule: {
        unique: () => this.resourcesToProtectUnique || 'Resources to protect with this name already exists'
      },
    }
  },
  props: {
    resourcesToProtectToEdit: {
      type: Object,
      default: function () {
        return {};
      }
    },
    allResourcesToProtect: {
      type: Array,
      default: function() {
        return [];
      }
    }
  },
  watch: {
    resourcesToProtectToEdit: {
      immediate: true,
      deep: true,
      handler: function(resourceToProtectToEdit) {
        if(resourceToProtectToEdit) {
          this.$log.debug("\t...set resources to protect to edit to ", resourceToProtectToEdit);
          this.resourcesToProtect = Utils.deepCopyObject(resourceToProtectToEdit);
        }
      }
    }
  },
  methods: {
    saveResourcesToProtect: function() {
      TargetArchitectureAPI.createUpdateResourceToProtect(this.resourcesToProtect).then(() => {
        this.$emit(constants.eventNames.local.artifactCreatedUpdated);
      })
    }
  },
  computed: {
    resourcesToProtectUnique: function() {
      if(this.resourcesToProtect.name === this.resourcesToProtectToEdit.name) {
        return true;
      }
      return this.allResourcesToProtect.filter(resourcesToProtect => {
        return resourcesToProtect.name === this.resourcesToProtect.name;
      }).length===0;
    }
  }
}
</script>

<style scoped>

</style>
<template>
  <v-container fluid>
    <v-row cols="12" lg="12">
      <v-col lg="10">
        <v-text-field
            v-model="samplingContext.description"
            label="'Sampling Context'"
            :rules=[samplingContextRule.unique]
        />
      </v-col>
      <v-col lg="2">
        <v-btn color="primary" text @click="saveSamplingContext" :disabled="!samplingContextUnique">Save Sampling Context</v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>

import constants from "@/helpers/constants";
import {Util as Utils} from "@/helpers/util";
import {DatasetAPI} from "@/service/api/DatasetAPI";

export default {
  name: "SamplingContextEditor",
  data() {
    return {
      samplingContext: {},
      samplingContextRule: {
        unique: () => this.samplingContextUnique || 'A Sampling Context with this name already exists'
      },
    }
  },
  props: {
    samplingContextToEdit: {
      type: Object,
      default: function () {
        return {};
      }
    },
    allSamplingContexts: {
      type: Array,
      default: function() {
        return [];
      }
    }
  },
  watch: {
    samplingContextToEdit: {
      immediate: true,
      deep: true,
      handler: function(samplingContextToEdit) {
        if(samplingContextToEdit && samplingContextToEdit.description) {
          this.$log.debug("\t...set sampling context to edit to ", samplingContextToEdit);
          this.samplingContext = Utils.deepCopyObject(samplingContextToEdit);
        }
      }
    }
  },
  methods: {
    saveSamplingContext: function() {
      DatasetAPI.createUpdateSamplingContext(this.samplingContext).then(() => {
        this.$emit(constants.eventNames.local.artifactCreatedUpdated);
      })
    }
  },
  computed: {
    samplingContextUnique: function() {
      if(this.samplingContext.description === this.samplingContextToEdit.description) {
        return true;
      }
      return this.allSamplingContexts.filter(samplingContext => {
        return samplingContext.description === this.samplingContext.description;
      }).length===0;
    }
  }
}
</script>

<style scoped>

</style>
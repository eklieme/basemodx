<template>
  <v-container fluid>
    <v-row cols="12" lg="12">
      <v-col lg="10">
        <v-text-field
            v-model="sensorDimension.name"
            label="Sensor Dimension"
            :rules=[featureRule.unique]
        />
      </v-col>
      <v-col lg="2">
        <v-btn color="primary" text @click="saveSensorDimension" :disabled="!featureUnique">Save sensorDimension</v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>

import constants from "@/helpers/constants";
import {Util as Utils} from "@/helpers/util";
import {SensorAPI} from "@/service/api/SensorAPI";

export default {
  name: "SensorDimensionEditor",
  data() {
    return {
      sensorDimension: {},
      featureRule: {
        unique: () => this.featureUnique || 'A sensorDimension with this name already exists'
      },
    }
  },
  props: {
    sensorDimensionToEdit: {
      type: Object,
      default: function () {
        return {};
      }
    },
    allSensorDimensions: {
      type: Array,
      default: function() {
        return [];
      }
    }
  },
  watch: {
    sensorDimensionToEdit: {
      immediate: true,
      deep: true,
      handler: function(sensorDimensionToEdit) {
        if(sensorDimensionToEdit && sensorDimensionToEdit.name) {
          this.$log.debug("\t...set sensorDimension to edit to ", sensorDimensionToEdit);
          this.sensorDimension = Utils.deepCopyObject(sensorDimensionToEdit);
        }
      }
    }
  },
  methods: {
    saveSensorDimension: function() {
      SensorAPI.createUpdateSensorDimension(this.sensorDimension).then(() => {
        this.$emit(constants.eventNames.local.artifactCreatedUpdated);
      })
    }
  },
  computed: {
    featureUnique: function() {
      if(this.sensorDimension.name === this.sensorDimensionToEdit.name) {
        return true;
      }
      return this.allSensorDimensions.filter(sensorDimension => {
        return sensorDimension.name === this.sensorDimension.name;
      }).length===0;
    }
  }
}
</script>

<style scoped>

</style>
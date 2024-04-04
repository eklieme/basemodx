<template>
  <v-container fluid>
    <v-row cols="12" lg="12">
      <v-col lg="10">
        <v-text-field
            v-model="feature.name"
            label="'Feature'"
            :rules=[featureRule.unique]
        />
      </v-col>
      <v-col lg="2">
        <v-btn color="primary" text @click="saveFeature" :disabled="!featureUnique">Save Feature</v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>

import constants from "@/helpers/constants";
import {Util as Utils} from "@/helpers/util";
import {BiometricSystemAPI} from "@/service/api/BiometricSystemAPI";

export default {
  name: "FeatureEditor",
  data() {
    return {
      feature: {},
      featureRule: {
        unique: () => this.featureUnique || 'A feature with this name already exists'
      },
    }
  },
  props: {
    featureToEdit: {
      type: Object,
      default: function () {
        return {};
      }
    },
    allFeatures: {
      type: Array,
      default: function() {
        return [];
      }
    }
  },
  watch: {
    featureToEdit: {
      immediate: true,
      deep: true,
      handler: function(featureToEdit) {
        if(featureToEdit && featureToEdit.name) {
          this.$log.debug("\t...set feature to edit to ", featureToEdit);
          this.feature = Utils.deepCopyObject(featureToEdit);
        }
      }
    }
  },
  methods: {
    saveFeature: function() {
      BiometricSystemAPI.createUpdateFeature(this.feature).then(() => {
        this.$emit(constants.eventNames.local.artifactCreatedUpdated);
      })
    }
  },
  computed: {
    featureUnique: function() {
      if(this.feature.name === this.featureToEdit.name) {
        return true;
      }
      return this.allFeatures.filter(feature => {
        return feature.name === this.feature.name;
      }).length===0;
    }
  }
}
</script>

<style scoped>

</style>
<template>
  <v-dialog v-model="dialogIsShown">
    <v-card>
      <v-card-title>
        <h2>{{dialogTitle}}</h2>
      </v-card-title>
      <v-card-text>
        <slot name="content"></slot>
      </v-card-text>
      <v-card-actions>
          <slot name="actions"></slot>
        <v-btn color="error" text @click="closeDialog">Cancel</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script>

import constants from "@/helpers/constants";

export default {

  name: "DialogContainer",
  data() {
    return {
      dialogIsShown: false,
    }
  },
  props: {
    dialogOpened: {
      type: Boolean,
      default: false
    },
    dialogTitle: {
      type: String,
      default: "",
    }
  },
  watch: {
    dialogOpened: {
      immediate: true,
      handler: function(dialogOpened) {
        if(dialogOpened) {
          this.dialogIsShown = true;
        } else {
          this.dialogIsShown = false;
        }
      }
    }
  },
  computed: {

  },
  methods: {
    closeDialog: function() {
      this.$emit(constants.eventNames.local.closeDialog);
    }
  },
}
</script>
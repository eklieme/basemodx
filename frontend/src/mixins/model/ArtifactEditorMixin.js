import constants from "@/helpers/constants";

export default {
    data: () => ({
        artifactEditorShown: false,
    }),
    props: {
        editMode: {
            type: Boolean,
            default: false,
        },
        editorShown: {
            type: Boolean,
            default: false,
        }
    },
    computed: {
        constants: function() {
            return constants;
        },
        editorOpened: function() {
            return this.artifactEditorShown;
        }
    },
    watch: {
      editorShown: {
          immediate: true,
          deep: true,
          handler: function(editorShown) {
              this.$log.debug("...artifact editor shown set to", editorShown);
              this.artifactEditorShown = editorShown;
          }
      }
    },
    created: function() {

    },
    mounted: function() {
    },
    methods: {
        closeEditor: function() {
            this.artifactEditorShown = false;
            this.$emit(constants.eventNames.local.closeDialog);
        },
        openEditor: function() {
            this.artifactEditorShown = true;
        },
    }
}
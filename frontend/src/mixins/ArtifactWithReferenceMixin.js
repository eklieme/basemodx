export default {
    data: () => ({

    }),
    props: {
    },
    computed: {

    },
    created: function() {

    },
    mounted: function() {
    },
    methods: {
        summarizeReference(reference) {
            if(reference && reference.furtherInformationReferences &&
                reference.furtherInformationReferences.length>0) {
                return reference.furtherInformationReferences.length+" given";
            } else {
                return "-";
            }
        },
    }
}
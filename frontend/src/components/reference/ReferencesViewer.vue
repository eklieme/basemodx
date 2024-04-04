<template>
    <v-container fluid>
        <v-row>
            <v-col cols="12" lg="12">
                <span>{{referenceSummary}}</span>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12" lg="12">
                <v-data-table
                        :headers="furtherInformationReferencesHeaders"
                        :items="reference.furtherInformationReferences"
                        hide-default-footer
                >
                    <template v-slot:body=" {items} ">
                        <tbody>
                            <tr v-for="item in items" :key="item.name">
                                <td>{{ item.reference }}</td>
                                <td>{{ item.furtherInformationReferenceType }}</td>
                            </tr>
                        </tbody>
                    </template>
                </v-data-table>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import constants from "@/helpers/constants";

    export default {
        props: {
            reference: {
                type: Object,
                default: function() {
                    return {};
                },
            },
        },
        data() {
            return {
                furtherInformationReferencesHeaders: [
                    { text: 'Reference', value: 'reference' },
                    { text: 'Type', value: 'referenceType'},
                ],
            }
        },
        methods: {

        },
        computed: {
            referenceSummary: function() {
                if(this.reference) {
                    return (this.reference.assetModelledByOriginator ? "The " + this.referenceAssetTypeName + " was created by "
                        + this.reference.originatorEmail + " further specified by the following references:"
                        : " This " + this.referenceAssetTypeName + " was not reported by the modeling person. Nevertheless, the" +
                        " following references were given:")
                }
                return "";

            },
            referenceAssetTypeName: function() {
                if(this.reference) {
                    switch (this.reference.referenceAssetType) {
                        case constants.referenceAssetType.processingStep:
                            return "processing step";
                        case constants.referenceAssetType.evaluationCriteria:
                            return "evaluation criteria";
                    }

                    return this.reference.referenceAssetType;
                }

                return "unknown";
            },
        },
        watch: {
            reference: {
                immediate: true,
                deep: true,
                handler: function(newReference) {
                    this.$log.debug(newReference);
                }
            }
        }
    }
</script>

<style scoped>

</style>
import constants from "@/helpers/constants";
import {BiometricCharacteristicsService} from "@/service/api/BiometricCharacteristicsService";

export default {
    data: () => ({
        editingAllowedExistingBiometricCharacteristic: false,
        biometricCharacteristicToEdit: {},
        existingCharacteristics: [],
    }),
    props: {
    },
    computed: {
        constants: function() {
            return constants;
        }
    },
    created: function() {

    },
    mounted: function() {
    },
    methods: {
        editBiometricCharacteristic: function(biometricCharacteristic) {
            this.$log.debug("trigger editing of biometric characteristic", biometricCharacteristic);
            this.editingAllowedExistingBiometricCharacteristic = true;
            this.biometricCharacteristicToEdit = biometricCharacteristic;
        },
        getAllCharacteristics: function() {
            let that = this;
            this.editingAllowedExistingBiometricCharacteristic = false;
            this.biometricCharacteristicToEdit = {};
            BiometricCharacteristicsService.getBiometricCharacteristics().then(response => {
                this.existingCharacteristics = that.enrichAllAvailableCharacteristicsForPresentation(response.data);
                that.$log.debug("\t found "+this.existingCharacteristics.length+" existing characteristics", this.existingCharacteristics);
            });
        },
        enrichAllAvailableCharacteristicsForPresentation: function(characteristics) {
            return characteristics.map(characteristic => {
                return BiometricCharacteristicsService.transformCharacteristicForPresentation(characteristic);
            });
        },
        resetBiometricCharacteristicToEdit: function() {
            this.editingAllowedExistingBiometricCharacteristic = false;
            this.biometricCharacteristicToEdit = {};
        }
    }
}
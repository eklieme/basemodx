import constants from "@/helpers/constants";
import {DeviceCategoryServiceAPI} from "@/service/api/DeviceCategoryServiceAPI";

export default {
    data: () => ({
        deviceEditorDialogOpen: false,
        editDeviceCategory: false,
        allDeviceCategories: [],
    }),
    props: {
    },
    computed: {
        constants: function() {
            return constants;
        },
    },
    created: function() {

    },
    mounted: function() {
    },
    methods: {
        triggerDeviceCategoryCreation: function() {
            this.deviceEditorDialogOpen = true;
            this.editDeviceCategory = false;
        },
        triggerDeviceCategoryEditing: function() {
            this.deviceEditorDialogOpen = true;
            this.editDeviceCategory = true;
        },
        getDeviceCategories() {
            this.$log.debug("...get all device categories!");
            DeviceCategoryServiceAPI.getDeviceCategories().then(response => {
                this.allDeviceCategories = response.data;
            })
        },
        getDeviceCategoriesPromise() {
            this.$log.debug("...get all device categories promise!");
            return DeviceCategoryServiceAPI.getDeviceCategories();
        },
    }
}
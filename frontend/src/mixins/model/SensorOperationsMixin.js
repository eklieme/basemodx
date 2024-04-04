import constants from "@/helpers/constants";
import {SensorAPI} from "@/service/api/SensorAPI";

export default {
    data: () => ({
        existingSensors: [],
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
        globallyDeleteSensor(sensor) {
            return SensorAPI.deleteSensor(sensor);
        },
        getAllSensors: function() {
            SensorAPI.getSensors().then(response => {
                this.existingSensors = response.data;
            })
        },
    }
}
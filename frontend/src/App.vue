<template>
    <v-app>
        <v-content>
            <BaseToolbar/>
            <v-container fluid>
              <router-view :key="$route.path"></router-view>
            </v-container>
        </v-content>
    </v-app>
</template>

<script>
    import api from "./service/api/api";
    import toast from "./helpers/toast";
    import BaseToolbar from "./components/base/BaseToolbar";
    import Vue from "vue";

    export default {
        name: 'App',
        components: {BaseToolbar},
        created: function() {
            Vue.$log.info("App created");
            this.startSelfCheck();
        },
        data() {
            return {
                selfCheckRunning:true,
                selfCheckResult: {
                    generalStatus: 0,
                    mongoDB: 0,
                },
                selfCheckSuccessful:false,
                basystem: {},
                showModelBaseDialog: false,
                toggle_exclusive: 0,
            }
        },
        methods: {
            processSelfCheckResponse: function(response) {
              switch(response.status) {
                case 200:
                  this.selfCheckResult.generalStatus = 1;
                  this.selfCheckResult.mongoDB = 1;
                  this.selfCheckSuccessful = true;
                  toast.success("Self-Check successful!");
                  break;
              }
            },
            processSelfCheckError: function(error) {
              if (error && error.response) {
                switch (error.response.status) {
                  case 503: {
                    this.selfCheckResult.generalStatus = 1;
                    const health = error.response.data;
                    if (health.details.mongo.status !== 'UP') {
                      this.selfCheckResult.mongoDB = 0;
                      this.selfCheckSuccessful = false;
                    }
                    break;
                  }
                }
              } else {
                this.selfCheckResult.generalStatus = 0;
                this.selfCheckSuccessful = false;
              }
            },
            startSelfCheck: function() {
                this.selfCheckRunning = true;
                this.selfCheckSuccessful = false;
                this.selfCheckResult = {
                    generalStatus: 0,
                    mongoDB: 0,
                };

                // do all self checks:
                // - system health
                api.get("/actuator/health", {errorHandle: false}).then(response => {
                  if (response) {
                    this.processSelfCheckResponse(response);
                  }
                }).catch((error) => {
                    this.processSelfCheckError(error);
                }).finally(() => {
                  this.selfCheckRunning = false;
                });

            }
        },
        mounted() {
          Vue.$log.info("App mounted")
        },

    }
</script>

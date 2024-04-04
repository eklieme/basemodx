<template>
    <v-toolbar
            dense>
        <v-toolbar-title>
            <a @click="browseHome" class="font-weight-light">base</a>
            <a @click="browseHome" class="headline">Mod</a>
            <a @click="browseHome" class="font-weight-light">X</a>
            <span class="subtitle-1">&nbsp;Biometric Authentication Systems and Evaluations Modelling</span>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-toolbar-items>
            <!--<v-btn text><router-link to="/">Browse modelled BASE</router-link></v-btn>-->

            <v-icon @click="browseHome">home</v-icon>
            <v-menu :disabled="!userIsAdmin" offset-y open-on-hover close-on-click close-on-content-click>
              <template v-slot:activator="{ on }">
                <v-btn
                    v-on="on"
                    text
                    v-show="userIsAdmin"
                >
                  Admin settings ...
                </v-btn>
              </template>
              <v-list nav flat>
                <v-list-item>
                  <v-list-item-title
                      style="cursor: pointer">TODO</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
            <v-btn
                text
                v-show="baseInDemoMode && !userIsReviewer && !userIsAdmin && userIsAuthenticated"
            >
              <span @click="assignReviewerRoleToCurrentUser">Make me a reviewer</span>
            </v-btn>
            <v-btn
                text
                v-show="userIsReviewer && artifactsToReviewCount>0"
            >
              <router-link :to="{ name: constants.routes.artifactsRequiringReview.name, params: {showAllArtifactsRequiringReview: true} }">I Need to review ({{ artifactsToReviewCount }})</router-link>
            </v-btn>
            <v-btn
                text
                v-show="userIsAuthenticated && artifactsInUserSpecificReviewCount>0"
            >
              <router-link :to="{ name: constants.routes.ownArtifactsInReview.name, params: {showMyArtifactsInReview: true} }">My Artifacts In
                Review ({{ artifactsInUserSpecificReviewCount }})</router-link>
            </v-btn>
            <v-menu offset-y open-on-hover close-on-click close-on-content-click>
                <template v-slot:activator="{ on }">
                    <v-btn
                            v-on="on"
                            text
                    >
                        Browse ...
                    </v-btn>
                </template>
                <v-list nav flat>
                    <v-list-item>
                        <v-list-item-title><router-link to="/">BASE</router-link></v-list-item-title>
                    </v-list-item>
                    <v-list-item>
                        <v-list-item-title><router-link :to="{ name:  'browse-datasets', params: {standAlone: true} }">Datasets</router-link></v-list-item-title>
                    </v-list-item>
                    <v-list-item>
                        <v-list-item-title><router-link :to="{ name: 'browse-sampledevices', params: {standAlone: true, selectable: false } }">Sample Devices</router-link></v-list-item-title>
                    </v-list-item>
                    <v-list-item>
                      <v-list-item-title><router-link :to="{ name: routeNameBrowseBiometricSystems, params: {standAlone: true, selectable: false, showModellerInformation: true } }">Biometric Systems</router-link></v-list-item-title>
                    </v-list-item>
                    <v-list-item>
                        <v-list-item-title><router-link :to="{ name: 'browse-criteria', params: {editMode: false} }">Evaluation Criteria</router-link></v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
            <v-menu :disabled="!userIsAuthenticated" offset-y open-on-hover close-on-click close-on-content-click>
                <template v-slot:activator="{ on }">
                    <v-btn
                            v-on="on"
                            text
                            v-show="userIsAuthenticated"
                    >
                        Model ...
                    </v-btn>
                </template>
                <v-list nav flat>
                      <v-list-item>
                          <v-list-item-title><router-link to="/model/base">BASE</router-link></v-list-item-title>
                      </v-list-item>
                      <v-list-item>
                          <v-list-item-title><router-link :to="{ name: routeNameModelDatasets, params: {standAlone: true } }">Dataset</router-link></v-list-item-title>
                      </v-list-item>
                      <v-list-item>
                          <v-list-item-title><router-link :to="{ name: routeNameModelSampleDevices, params: {standAlone: true } }">Sample Device</router-link></v-list-item-title>
                      </v-list-item>
                      <v-list-item>
                          <v-list-item-title><router-link to="/model/criteria">Evaluation Criteria</router-link></v-list-item-title>
                      </v-list-item>
                </v-list>
            </v-menu>
            <v-menu offset-y open-on-hover close-on-click close-on-content-click>
              <template v-slot:activator="{ on }">
                <v-icon v-if="userIsAuthenticated" v-on="on" style="cursor: pointer">mdi-account-cancel</v-icon>
                <v-icon v-else v-on="on" style="cursor: pointer">mdi-account</v-icon>
              </template>
              <v-list nav flat>
                <template v-if="userIsAuthenticated">
                  <v-list-item>
                    <v-list-item-title
                        style="cursor: pointer" @click="socialLogout">Logout {{loggedInUser.socialLoginUserId}} from {{loggedInUser.socialLoginProvider}}</v-list-item-title>
                  </v-list-item>
                </template>
                <template v-else>
                  <v-list-item>
                    <v-list-item-title
                        style="cursor: pointer" @click="socialLogin('github')"><a>Login with GitHub</a></v-list-item-title>
                  </v-list-item>
                </template>
              </v-list>
            </v-menu>
        </v-toolbar-items>
    </v-toolbar>
</template>

<script>
    import constants from "../../helpers/constants";
    import Vue from "vue";
    import toast from "../..//helpers/toast";
    import EventBus from "../../helpers/eventBus";
    import {AuthenticationAPI} from "../../service/api/AuthenticationAPI";
    import {ModelledElementReviewAPI} from "../../service/api/ModelledElementReviewAPI";
    import api from "../../service/api/api";
    import LoggedInUserMixin from "../../mixins/LoggedInUserMixin";

    export default {
        name: "BaseToolbar",
        mixins: [LoggedInUserMixin],
        computed: {
            constants() {
              return constants
            },
            routeNameModelDatasets: function(){
                return constants.routes.modelDatasets.name;
            },
            routeNameBrowseBiometricSystems: function() {
                return constants.routes.browseBiometricSystems.name
            },
            routeNameModelSampleDevices: function(){
                return constants.routes.modelSampleDevices.name;
            },
            baseInDemoMode: function() {
                return process.env.VUE_APP_DEMO_MODE === 'true';
            }
        },
        data() {
          return {
            showLoginDialog: false,
            artifactsInUserSpecificReviewCount: 0,
            artifactsToReviewCount: 0,
          }
        },
        beforeRouteEnter (to, from, next) {
              Vue.$log.debug("before entering route to, from", to, from);
              next();
        },
        created: function() {
          Vue.$log.info("base toolbar created");
        },
        mounted() {
          Vue.$log.info("base toolbar mounted");
          this.$nextTick(function () {
            if (this.$auth.isAuthenticated()) {
              this.getUserInformation();
            }
          });
          EventBus.$on(constants.eventNames.local.amountOfArtifactsShownChanged, (changeInformation) => {
            this.$log.debug("update overall artifact for review count (userSpecific: "+changeInformation.userSpecific
                +") in base toolbar to ", changeInformation.newCount);
            if(changeInformation.userSpecific) {
              this.artifactsInUserSpecificReviewCount = changeInformation.newCount;
            } else {
              this.artifactsToReviewCount = changeInformation.newCount;
            }
          });
        },
      methods: {
            browseHome: function() {
              this.$router.push({path: '/'});
            },
            assignReviewerRoleToCurrentUser: function() {
              let modifiedUser = this.$userAuthService.getLoggedInUser();
              modifiedUser.baseUserRoles = ["modeller", "reviewer"];
              AuthenticationAPI.updateBaseUser(this.$userAuthService.getLoggedInUser(modifiedUser)).then(() => {
                this.$log.debug("\t...successfully changed user!");
                toast.success("You successfully became a 'REVIEWER', page will reload shortly", 1500);
                const that = this;
                setTimeout(function () {
                    that.$router.go();
                }, 1500);

              });
            },
            getArtifactsInReviewCount: function() {
              var that = this;
              ModelledElementReviewAPI.getElementsInReviewForMe().then((response) => {
                that.artifactsInUserSpecificReviewCount = this.countArtifactsModelContext(response.data);
                Vue.$log.debug("...get "+that.artifactsInUserSpecificReviewCount.length+" of elements in review for current user");
              });
            },
            getArtifactsToReviewCount: function() {
              var that = this;
              ModelledElementReviewAPI.getArtefactsToReviewForReviewer().then((response) => {
                that.artifactsToReviewCount = this.countArtifactsModelContext(response.data);
                Vue.$log.debug("...get "+that.artifactsToReviewCount.length+" elements to review for all users");
              });
            },
            countArtifactsModelContext: function(artifactMap) {
              return artifactMap[constants.review.artifactType.base].length
                +artifactMap[constants.review.artifactType.baseEvaluationExtension].length
                +artifactMap[constants.review.artifactType.datasets].length
                +artifactMap[constants.review.artifactType.sampleDevices].length
                +artifactMap[constants.review.artifactType.experimentCriteria].length
                +artifactMap[constants.review.artifactType.resultMetrics].length
                +artifactMap[constants.review.artifactType.implementationCriteria].length
                  +artifactMap[constants.review.artifactType.biometricProcessingSteps].length
                  +artifactMap[constants.review.artifactType.biometricCharacteristics].length
                  +artifactMap[constants.review.artifactType.resourcesToProtect].length
                  +artifactMap[constants.review.artifactType.deviceCategories].length
                  +artifactMap[constants.review.artifactType.samplingContexts].length
            },
            getUserInformation: function() {
              var that = this;

              Vue.$log.debug("\tfirst...set authorization token");
              api.defaults.headers.common['Authorization'] = 'Bearer '+this.$auth.getToken();
              AuthenticationAPI.getUserInformationForLoggedInUser().then((response) => {
                switch (response.status) {
                  case 302:
                    var userInformation = response.data;

                    that.processSuccessfulLogin(userInformation);

                    /*toast.success("You successfully logged in using "+userInformation.socialLoginProvider
                        +" (from cache). Please remember that any personal modeling action will be linked to your account identifier "
                        +response.data.context.entity.socialLoginUserId
                        +" from provider "+response.data.context.entity.socialLoginProvider, 5000);*/
                      toast.success("welcome back, '"+userInformation.socialLoginUserId+"'", 3000)
                    break;
                  case 404: {
                    toast.warning("Although log in information were found, no user information were found in the backend, logging out...", 5000);
                    that.$auth.logout();
                    that.$userAuthService.logoutUser();
                    EventBus.$emit(constants.eventNames.global.userLoggedOut);
                  }
                }
              });
            },
            getUserBasedArtifactReviewDetails: function(baseUserRoles) {
              Vue.$log.debug("User logged in and is modeller, get artifacts in review count");
              this.getArtifactsInReviewCount();
              if(baseUserRoles.includes(constants.roles.reviewer)) {
                Vue.$log.debug("User is also reviewer, get artifacts in review count");
                this.getArtifactsToReviewCount();
              }

            },
            socialLogin: function(provider) {

              var that = this;

              this.showLoginDialog = true;
              this.$auth.authenticate(provider).then(function(response) {
                Vue.$log.debug("successfully logged in '"+response.data.socialLoginUserId+"'!");
                Vue.$log.debug("\t...set authorization token");
                api.defaults.headers.common['Authorization'] = 'Bearer '+that.$auth.getToken();

                that.processSuccessfulLogin(response.data);

                that.showLoginDialog = false;
                toast.success("You successfully logged in using "+provider
                    +". Please remember that any personal modeling action will be linked to your account identifier "
                    +response.data.socialLoginUserId
                    +" from provider "+response.data.socialLoginProvider, 5000);
              });
            },
            processSuccessfulLogin: function(userData) {
              this.$userAuthService.setLoggedInUser(userData);
              this.$userAuthService.setAuthenticated();

              EventBus.$emit(constants.eventNames.global.userLoggedIn);
              this.getUserBasedArtifactReviewDetails(userData.baseUserRoles);
            },
            socialLogout: function() {
              Vue.$log.debug("Log out '"+this.loggedInUser.socialLoginUserId+"'");
              var that = this;

              this.$auth.logout().then(() => {

                AuthenticationAPI.logoutUser().then(() => {
                  Vue.$log.debug("\t...remove authorization token");
                  delete api.defaults.headers.common['Authorization'];
                  that.$userAuthService.logoutUser();
                  EventBus.$emit(constants.eventNames.global.userLoggedOut);
                  toast.success("You successfully logged out", 5000);
                  this.$router.go("/");
                });
              });
            }
        }
    }
</script>

<style scoped>

</style>
import EventBus from "@/helpers/eventBus";
import constants from "@/helpers/constants";

export default {
    data: () => ({
        loggedInUser: {
            socialLoginUserId: "",
            socialLoginProvider: "",
            baseUserRoles: [],
        },
        userIsAuthenticated: false,
        uniqueUserId: "anonymous",
        userIsReviewer: false,
        userIsAdmin: false,
    }),
    props: {
    },
    computed: {

    },
    watch: {
      loggedInUser: {
          deep: true,
          immediate: true,
          handler: function(newLoggedInUser) {
              this.$log.debug("..logged in user changed to :");
              this.$log.debug(newLoggedInUser);
              this.userIsAuthenticated = this.loggedInUser && this.loggedInUser.socialLoginUserId.length>0;
              if(this.loggedInUser.socialLoginProvider!=="" && this.loggedInUser.socialLoginUserId!=="") {
                  this.uniqueUserId = this.loggedInUser.socialLoginProvider + "_" + this.loggedInUser.socialLoginUserId;
              }
              if(this.isAuthenticated && this.loggedInUser) {
                  this.userIsReviewer = this.loggedInUser.baseUserRoles.includes(constants.roles.reviewer);
                  this.userIsAdmin = this.loggedInUser.baseUserRoles.includes(constants.roles.admin);
              }
              this.$log.debug("...new loggedInUser is reviewer!");
              this.$log.debug("...new loggedInUser is admin!");
          }
      }
    },
    created: function() {

    },
    mounted: function() {
        // 1.: get user information from service
        this.updateUserInformationFromUserAuthService();

        var that = this;
        // 2. subscribe for log in information
        EventBus.$on(constants.eventNames.global.userLoggedIn,() => {
            that.updateUserInformationFromUserAuthService();
        });

        // 3. subscribe for log out
        EventBus.$on(constants.eventNames.global.userLoggedOut,() => {
            that.updateUserInformationFromUserAuthService();
        });
    },
    methods: {
        updateUserInformationFromUserAuthService: function() {
            this.loggedInUser = this.$userAuthService.getLoggedInUser();
            this.isAuthenticated = this.$userAuthService.isAuthenticated();
        },
        getUniqueUserId: function() {
            return this.loggedInUser.socialLoginProvider+"_"+this.loggedInUser.socialLoginUserId;
        },
        setInitialModelLifecycleStateForDomainElement: function(domainElement) {
            if(!domainElement.modelledElementDetail) {
                domainElement["modelledElementDetail"] = {};
            }
            domainElement.modelledElementDetail = {
                    modelledInitiallyBy : this.getUniqueUserId(),
                    elementLifecycle : {
                        lifecycleState : constants.review.lifecycleState.created,
                        lifecycleConversation : [],
                    }
            };
        }
    },

}
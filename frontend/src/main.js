import Vue from 'vue'
import VueLogger from 'vuejs-logger';
import VueRouter from 'vue-router';
import axios from "axios";
import VueAxios from "vue-axios";
import VueAuthenticate from "vue-authenticate";
import App from './App.vue';
import routes from './helpers/routes';
import vuetify from "./plugins/vuetify";
import 'nprogress/nprogress.css';
import UserAuthenticationService from "./service/auth/UserAuthenticationService";

Vue.config.productionTip = false


// logger
const loggerOptions = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : true,
  separator: '|',
  showConsoleColors: true
};

//logger
Vue.use(VueLogger, loggerOptions);

//axios
Vue.use(VueAxios, axios);

//authenticate
Vue.use(VueAuthenticate, {

  baseUrl:process.env.VUE_APP_API_BASE_URL,
  providers: {
    github: {
      clientId:process.env.VUE_APP_GITHUB_LOGIN_CLIENT_ID,
      redirectUri:process.env.VUE_APP_GITHUB_LOGIN_REDIRECT_URL,
      optionalUrlParams: ['scope'],
      scope: ['user:email'],
      scopeDelimiter: ' ',
    },
  }
})

// router
Vue.use(VueRouter);
const router = new VueRouter({routes});

// user authentication service
Vue.prototype.$userAuthService = new UserAuthenticationService();

new Vue({
  router,
  vuetify,
  render: h => h(App),
}).$mount('#app')



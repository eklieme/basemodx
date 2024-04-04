// configure axios
import axios from "axios/index";
import toast from "../../helpers/toast";
import nprogress from "nprogress";

let api = null;

if(process.env.VUE_APP_ALLOW_ORIGIN_REQUIRED) {
    api = axios.create({
        baseURL: process.env.VUE_APP_API_BASE_URL,
    });
} else {
    api = axios.create({
        baseURL: process.env.VUE_APP_API_BASE_URL,
    });
}


nprogress.configure({ showSpinner: false });

api.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
api.defaults.headers.common['Access-Control-Allow-Origin'] = 'http://localhost:8080';

api.interceptors.request.use(request => {
    const accessToken = localStorage.getItem("vue-authenticate.vueauth_token");
    if(!request.headers.common['Authorization']) {
        if(accessToken) {
            console.log("Found and initially set access token '" + accessToken + "' before request");
            request.headers.common['Authorization'] = 'Bearer ' + accessToken;
        } else {
            console.log("No access token found, continue as anonymous user");
        }
    } else {
        console.log("Authentication token already set");
    }
    nprogress.start();
    return request
});

api.interceptors.response.use(
    function(response) {
        nprogress.done();
        console.debug("returning response", response);
        if(response.data
            && response.data.status
            && response.data.status !== response.status) {
            console.debug("\t...set entity-based response status to", response.data.status);
            response.status = response.data.status;
        }
        if(response.data
            && response.data.entity) {
            console.debug("\t...set data based on entity to", response.data.entity);
            response.data = response.data.entity;
        }
        return response;
    },
    function(error) {

        nprogress.done();

        console.log("error occurred!", JSON.stringify(error));


        if(error && error.response) {
            if (error.response.status && error.response.status === 401) {
                showErrorToast(error, "You are not allowed to view that page, maybe you need to login?");
                /*setTimeout(function () {
                    location.assign("/");
                }, 1000);*/
                return Promise.reject(error);
            } else if (error.response.status && error.response.status === 404) {
                showErrorToast(error, "The entity you requested is not available");
                return Promise.reject(error);
            } else {

                // check for errorHandle helpers
                if (error.config) {
                    if (Object.prototype.hasOwnProperty.call(error.config, 'errorHandle') && error.config.errorHandle === false) {
                        showErrorToast(error);
                        return Promise.reject(error);
                    }
                }
            }
        }

        showErrorToast(error);

    }
);

let showErrorToast = function(error, message) {

    if (error && error.response) {
        if (error.response.data && error.response.data.message) {
            toast.error(error.response.data.message);
        } else {
            toast.error(message);
        }
    } else {
        toast.error("Network Error occured. Please check if the backend is working");
    }

};


export default api;
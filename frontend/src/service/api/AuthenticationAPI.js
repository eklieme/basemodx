import api from './api';
const API_URL_AUTHENTICATION = '/auth';

export class AuthenticationAPI {

    constructor(){
    }

    static logoutUser() {
        return api.post(API_URL_AUTHENTICATION+"/logout");
    }

    static getUserInformationForLoggedInUser() {
        return api.get(API_URL_AUTHENTICATION+"/user");
    }

    static updateBaseUser(baseUser) {
        return api.post(API_URL_AUTHENTICATION+"/user", baseUser);
    }

}

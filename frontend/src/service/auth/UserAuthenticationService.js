
class UserAuthenticationService {
    userIsAuthenticated = false;
    loggedInUser = {};

    constructor() {
        this.initLoggedInUser();
    }

    initLoggedInUser() {
        this.loggedInUser = {
            socialLoginUserId: "",
            socialLoginProvider: "",
            baseUserRoles: [],
        }
    }

    isAuthenticated() {
        return this.userIsAuthenticated;
    }

    setAuthenticated() {
        this.userIsAuthenticated = true;
    }

    getLoggedInUser() {
        return this.loggedInUser;
    }

    setLoggedInUser(loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    logoutUser() {
        this.initLoggedInUser();
        this.userIsAuthenticated = false;
    }



}

export default UserAuthenticationService;
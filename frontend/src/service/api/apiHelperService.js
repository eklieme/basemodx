import api from "./api";
import Vue from "vue";


let helper = {
    createUpdateDomainObject: function(url, domainObject, handleErrors=true) {

        if(domainObject.id && domainObject.id.length>0) {
            return api.put(url+"/"+domainObject.id, domainObject,{errorHandle: handleErrors});
        } else {
            return api.post(url, domainObject,
                {errorHandle: handleErrors, transformResponse: [function (data) {
                        Vue.$log.debug("transforming response after post", data);
                        return (data ? JSON.parse(data)["id"] : data);
                    }]});
        }
    }

}

export default helper;
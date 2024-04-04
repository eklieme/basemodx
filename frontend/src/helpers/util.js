export class Util {

    static deepCopyObject(object) {
        return JSON.parse(JSON.stringify(object));
    }

    static isEmpty(object) {
        for(const key in object) {
            if(Object.prototype.hasOwnProperty.call(object, key))
                return false;
        }
        return true;
    }

}
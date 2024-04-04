import 'izitoast/dist/css/iziToast.min.css'
import iZtoast from 'izitoast'

const toast = {
    error: (message, timeout=1000, title = 'Error') => {
        return iZtoast.error({
            title: title,
            message: message,
            displayMode: 2,
            position: 'bottomCenter',
            timeout: timeout,
        });
    },
    warning: (message, timeout=1000, title = 'Warning') => {
        return iZtoast.warning({
            title: title,
            message: message,
            displayMode: 2,
            position: 'bottomCenter',
            timeout: timeout,
        });
    },
    success: (message, timeout=1000, title = 'Success') => {
        return iZtoast.success({
            title: title,
            message: message,
            position: 'bottomCenter',
            timeout: timeout,
        });
    }
};

export default toast;
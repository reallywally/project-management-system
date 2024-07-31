import axios from 'axios';
import store from '../store';

axios.interceptors.request.use(
    config => {
        const token = store.getters.token;
        console.log("------")
        console.log(token)
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

export default axios;

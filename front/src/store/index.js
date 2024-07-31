import { createStore } from 'vuex';
import axios from 'axios';

const store = createStore({
    state: {
        isAuthenticated: !!localStorage.getItem('token'),
        token: localStorage.getItem('token') || ''
    },
    mutations: {
        login(state, token) {
            state.isAuthenticated = true;
            state.token = token;
            localStorage.setItem('token', token);
        },
        logout(state) {
            state.isAuthenticated = false;
            state.token = '';
            localStorage.removeItem('token');
        }
    },
    actions: {
        async login({ commit }, credentials) {
            try {
                const response = await axios.post('http://localhost:8082/auth/signin', credentials);
                const token = response.data.jwt;
                commit('login', token);
            } catch (error) {
                console.error('Error logging in:', error);
            }
        },
        logout({ commit }) {
            commit('logout');
        }
    },
    getters: {
        isAuthenticated: (state) => state.isAuthenticated,
        token: (state) => state.token
    }
});

export default store;

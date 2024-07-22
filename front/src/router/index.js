import { createRouter, createWebHistory } from 'vue-router';
import Login from '../components/Login.vue';
import Dashboard from '../components/Dashboard.vue';

const routes = [
    {
        path: '/',
        name: 'Login',
        component: Login
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard
    }
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

export default router;

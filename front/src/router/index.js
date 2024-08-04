// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/LoginView.vue';
import Dashboard from '../views/DashboardView.vue';
import Client from '../views/ClientView.vue';
import store from '../store';
import ProjectView from "@/views/ProjectView";
import ProjectCreate from "@/components/project/ProjectCreate";
import ProjectRead from "@/components/project/ProjectRead";

const routes = [
    {
        path: '/',
        name: 'Login',
        component: Login
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { requiresAuth: true }
    },
    {
        path: '/client',
        name: 'Client',
        component: Client,
        meta: { requiresAuth: true }
    },
    {
        path: '/projects',
        name: 'Project',
        component: ProjectView,
        meta: { requiresAuth: true }
    },
    {
        path: '/projects/create',
        name: 'ProjectCreate',
        component: ProjectCreate,
        meta: { requiresAuth: true }
    },
    {
        path: "/projects/:projectId",
        name: "ProjectRead",
        component: ProjectRead,
        props: true,
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});

router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!store.getters.isAuthenticated) {
            next({
                path: '/login',
                query: { redirect: to.fullPath }
            });
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router;

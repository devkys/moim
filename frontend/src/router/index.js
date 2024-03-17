import { createRouter, createWebHistory } from 'vue-router'

// import TourListView from '../views/TourListView.vue'
import RegisterPage from "@/components/RegisterPage.vue";
import LoginPage from "@/components/LoginPage.vue";
import MainBoard from "@/components/MainBoard.vue";
import ErrorPage from "@/components/ErrorPage.vue";

const router = createRouter({
    // history mode
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'home',
            component: LoginPage
        },
        {
            path: '/register',
            name: 'register',
            component: RegisterPage
        },
        {
            path: '/main',
            name: 'main',
            component: MainBoard
        },
        {
            path: '/login',
            name: 'login',
            component: LoginPage
        },
        {
            path: '/:pathMatch(.*)*',
            name: '404',
            component: ErrorPage
        },

    ]
})

export default router
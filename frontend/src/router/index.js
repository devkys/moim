import { createRouter, createWebHistory } from 'vue-router'

// import TourListView from '../views/TourListView.vue'
import HelloWorld from "@/components/HelloWorld.vue";
import MainBoard from "@/components/MainBoard.vue";
import ErrorPage from "@/components/ErrorPage.vue";

const router = createRouter({
    // history mode
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'home1',
            component: MainBoard
        },
        {
            path: '/home',
            name: 'home',
            component: HelloWorld
        },
        {
            path: '/main',
            name: 'main',
            component: MainBoard
        },
        {
            path: '/404-notFound',
            name: '404',
            component: ErrorPage
        }
    ]
})

export default router
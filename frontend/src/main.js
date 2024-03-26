import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import PrimeVue from 'primevue/config'

const vuetify = createVuetify({
    components,
    directives,
})

// eslint-disable-next-line no-undef
createApp(App).use(vuetify).use(router).use(PrimeVue).mount('#app')


import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import BubbleChat from "vue-bubble-chat"

const vuetify = createVuetify({
    components,
    directives,
})

createApp(App).use(vuetify).use(router).use(BubbleChat).mount('#app')


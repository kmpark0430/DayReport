import { createApp } from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify' // Vuetify 플러그인 불러오기

const app = createApp(App)
app.use(vuetify)
app.mount('#app')
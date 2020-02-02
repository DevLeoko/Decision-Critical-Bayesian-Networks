import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import "./registerServiceWorker";
import axios from "./plugins/axios";
import VueAxios from "vue-axios";
import vuetify from "./plugins/vuetify";
import VueI18n from "vue-i18n";
import { i18n } from "./internationalization/translation";

Vue.config.productionTip = false;

Vue.use(VueAxios, axios);
Vue.use(VueI18n);

new Vue({
  i18n,
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount("#app");

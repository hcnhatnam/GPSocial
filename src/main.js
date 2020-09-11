import Vue from "vue";

import "normalize.css/normalize.css"; // A modern alternative to CSS resets

import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import locale from "element-ui/lib/locale/lang/en"; // lang i18n

import "@/styles/index.scss"; // global css

import App from "./App";
import store from "./store";
import router from "./router";

import "@/icons"; // icon
import "@/permission"; // permission control
import * as VueGoogleMaps from "vue2-google-maps";
import Chat from "vue-beautiful-chat";
import VueTouchRipple from "vue-touch-ripple";
/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
if (process.env.NODE_ENV === "production") {
  const {
    mockXHR
  } = require("../mock");
  mockXHR();
}
Vue.use(VueTouchRipple, {
  speed: 1.1,
  color: "#000",
  opacity: 0.2
});
// set ElementUI lang to EN
Vue.use(ElementUI, {
  locale
});
// 如果想要中文版 element-ui，按如下方式声明
// Vue.use(ElementUI)
Vue.use(VueGoogleMaps, {
  load: {
    key: "AIzaSyA6YoUgZr7e2tdHf7v_GfcwmfSvjyhaA10",
    libraries: "places" // This is required if you use the Autocomplete plugin
    // OR: libraries: 'places,drawing'
    // OR: libraries: 'places,drawing,visualization'
    // (as you require)

    // If you want to set the version, you can do so:
    // v: '3.26',
  }

  // If you intend to programmatically custom event listener code
  // (e.g. `this.$refs.gmap.$on('zoom_changed', someFunc)`)
  // instead of going through Vue templates (e.g. `<GmapMap @zoom_changed="someFunc">`)
  // you might need to turn this on.
  // autobindAllEvents: false,

  // If you want to manually install components, e.g.
  // import {GmapMarker} from 'vue2-google-maps/src/components/marker'
  // Vue.component('GmapMarker', GmapMarker)
  // then disable the following:
  // installComponents: true,
});
Vue.use(Chat);
Vue.config.productionTip = false;

export const EventBus = new Vue({
  el: "#app",
  router,
  store,
  render: h => h(App)
});

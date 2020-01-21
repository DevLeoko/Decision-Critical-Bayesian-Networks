import "material-design-icons-iconfont/dist/material-design-icons.css";
import Vue from "vue";
import Vuetify from "vuetify/lib";
import colors from "vuetify/lib/util/colors";

Vue.use(Vuetify);

const primary = colors.blue.darken4;
// const primary = colors.blueGrey.darken2;

export default new Vuetify({
  theme: {
    themes: {
      light: {
        primary
      },
      dark: {
        primary
      }
    }
  }
});

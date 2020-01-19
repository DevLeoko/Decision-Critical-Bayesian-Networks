import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

function generateDefaultRoute(name: String) {
  return {
    path: `/${name.toLowerCase()}`,
    name: name.toLowerCase(),
    component: () => import(`./views/${name}.vue`)
  };
}

export default new Router({
  routes: [
    generateDefaultRoute("Login"),
    generateDefaultRoute("ResetPassword"),
    generateDefaultRoute("ForgotPassword")
  ]
});

import RouterView from "./views/RouterView.vue";
import Vue from "vue";
import Router from "vue-router";
import { i18n } from "./internationalization/translation";

Vue.use(Router);

function generateDefaultRoute(name: String, children = undefined) {
  return {
    path: `/${name.toLowerCase()}`,
    name: name.toLowerCase(),
    component: () => import(`./views/${name}.vue`),
    children
  };
}

export default new Router({
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/:lang",
      component: RouterView,
      beforeEnter(to, from, next) {
        const lang = to.params.lang;
        if (!["en", "de"].includes(lang)) return next("en");
        if (i18n.locale !== lang) {
          i18n.locale = lang;
        }
        return next();
      },
      children: [
        {
          name: "SuperAdmin",
          path: "super-admin",
          component: () => import("./views/SuperAdmin.vue")
        },
        {
          name: "Login",
          path: "login",
          meta: { unauthorized: true },
          component: () => import("./views/Login.vue")
        },
        {
          name: "ResetPassword",
          path: "reset-password",
          meta: { unauthorized: true },
          component: () => import("./views/ResetPassword.vue")
        },
        {
          name: "ForgotPassword",
          path: "forgot-password",
          meta: { unauthorized: true },
          component: () => import("./views/ForgotPassword.vue")
        },
        {
          name: "EvidenceFormulaBase",
          path: "evidence-formula",
          component: () => import("./views/EvidenceFormula.vue")
        },
        {
          name: "EvidenceFormula",
          path: "evidence-formula/:id",
          component: () => import("./views/EvidenceFormula.vue")
        },
        {
          name: "GraphBase",
          path: "graph",
          component: () => import("./views/graph/Index.vue")
        },
        {
          name: "Graph",
          path: "graph/:id",
          component: () => import("./views/graph/Index.vue"),
          children: [
            {
              name: "Edit Graph",
              path: "edit",
              component: () => import("./views/graph/Edit.vue")
            },
            {
              name: "Test Graph",
              path: "",
              component: () => import("./views/graph/Test.vue")
            }
          ]
        }
      ]
    },
    {
      path: "*",
      redirect: "/en/login"
    }
  ]
});

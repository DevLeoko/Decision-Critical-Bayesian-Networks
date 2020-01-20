import Vue from "vue";
import Router from "vue-router";

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
  routes: [
    {
      name: "SuperAdmin",
      path: "/super-admin",
      component: () => import("./views/SuperAdmin.vue")
    },
    {
      name: "Login",
      path: "/login",
      meta: { unauthorized: true },
      component: () => import("./views/Login.vue")
    },
    {
      name: "ResetPassword",
      path: "/reset-password",
      meta: { unauthorized: true },
      component: () => import("./views/ResetPassword.vue")
    },
    {
      name: "ForgotPassword",
      path: "/forgot-password",
      meta: { unauthorized: true },
      component: () => import("./views/ForgotPassword.vue")
    },
    {
      name: "EvidenceFormulaBase",
      path: "/evidence-formula",
      component: () => import("./views/EvidenceFormula.vue")
    },
    {
      name: "EvidenceFormula",
      path: "/evidence-formula/:id",
      component: () => import("./views/EvidenceFormula.vue")
    },
    {
      name: "GraphBase",
      path: "/graph",
      component: () => import("./views/graph/Index.vue")
    },
    {
      name: "Graph",
      path: "/graph/:id",
      component: () => import("./views/graph/Index.vue"),
      children: [
        {
          name: "Edit Graph",
          path: "edit",
          component: () => import("./views/graph/Edit.vue")
        },
        {
          name: "Test Graph",
          path: "test",
          component: () => import("./views/graph/Test.vue")
        }
      ]
    }
  ]
});

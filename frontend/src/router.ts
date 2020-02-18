import Vue from "vue";
import Router from "vue-router";
import store, { Role } from "./store";

Vue.use(Router);

function generateDefaultRoute(name: String, children = undefined) {
  return {
    path: `/${name.toLowerCase()}`,
    name: name.toLowerCase(),
    component: () => import(`./views/${name}.vue`),
    children
  };
}

export const router = new Router({
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
      path: "/graph/:id",
      component: () => import("./views/graph/Index.vue"),
      children: [
        {
          name: "Test Graph",
          path: "",
          component: () => import("./views/graph/Test.vue")
        },
        {
          name: "Edit Graph",
          path: "edit",
          component: () => import("./views/graph/Edit.vue")
        }
        // {
        //   name: "Test Graph",
        //   path: "test",
        //   component: () => import("./views/graph/Test.vue")
        // }
      ]
    }
  ]
});

// Make sure to not add any redirect loops!
router.beforeEach((to, from, next) => {
  if (store.state.isUserLoggedIn) {
    if (!to.name) {
      if ((store.state.user.role as Role) == "SUPERADMIN") {
        next({
          name: "SuperAdmin"
        });
        return;
      } else {
        next({
          name: "GraphBase"
        });
        return;
      }
    }
  } else {
    if (!to.meta.unauthorized) {
      next({
        name: "Login"
      });
      return;
    }
  }

  next();
});

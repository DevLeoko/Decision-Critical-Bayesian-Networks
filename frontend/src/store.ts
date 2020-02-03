import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

type Role = "ADMIN" | "MODERATOR" | "SUPERADMIN";

interface User {
  name: string;
  email: string;
  role: Role;
}

export default new Vuex.Store({
  plugins: [createPersistedState()],

  state: {
    token: "",
    user: { name: "", email: "" },
    isUserLoggedIn: false
  } as { token: string; user: User; isUserLoggedIn: boolean },

  mutations: {
    setToken(state, token) {
      state.token = token;
      state.isUserLoggedIn = !!token;
    },

    setUser(state, user) {
      state.user = user;
    }
  },

  actions: {
    setToken({ commit }, token) {
      commit("setToken", token);
    },

    setUser({ commit }, user) {
      commit("setUser", user);
    },

    logout({ commit }) {
      commit("setToken", null);
      commit("setUser", null);
    }
  }
});

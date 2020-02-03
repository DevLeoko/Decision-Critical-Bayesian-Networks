import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

interface User {
  name: string;
  email: string;
  role: string;
}

export default new Vuex.Store({
  plugins: [createPersistedState()],

  state: {
    token: "",
    user: { name: "", email: "", role: "" },
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

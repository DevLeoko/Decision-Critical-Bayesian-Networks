import store from "@/store";
import { router } from "@/router";
import Axios from "axios";

const axiosInstance = Axios.create({
  baseURL: process.env.VUE_APP_BACKEND_API
});

axiosInstance.defaults.headers.common[
  "Authorization"
] = `Bearer ${store.state.token}`;

store.subscribe(mutation => {
  if (mutation.type == "setToken") {
    axiosInstance.defaults.headers.common[
      "Authorization"
    ] = `Bearer ${mutation.payload}`;
  }
});

axiosInstance.interceptors.response.use(
  response => response,
  function(error) {
    if (error.response !== undefined && error.response.status == 403) {
      store.dispatch("logout");
      router.push({ name: "login" });
    } else {
      return Promise.reject(error);
    }
  }
);

export default axiosInstance;

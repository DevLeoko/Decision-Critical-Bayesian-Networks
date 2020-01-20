<template>
  <v-app>
    <menu-bar />

    <v-content>
      <v-container fluid fill-height align-start>
        <router-view></router-view>
      </v-container>
    </v-content>
  </v-app>
</template>

<script lang="ts">
import MenuBar from "@/components/MenuBar.vue";
import Vue from "vue";

export default Vue.extend({
  name: "App",
  components: { MenuBar },
  data: () => ({}),

  async created() {
    if (this.$store.state.isUserLoggedIn) {
      const user = (await this.axios.post("/refreshAccount")).data.user;
      this.$store.dispatch("setUser", user);
    } else {
      if (!this.$route.meta.unauthorized) {
        this.$router.push({
          name: "Login"
        });
      }
    }
  }
});
</script>

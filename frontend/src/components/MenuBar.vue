<template>
  <v-app-bar clipped-left app class="primary">
    <img src="@/assets/logo.svg" height="70%" />

    <!-- Logged out -->
    <template v-if="!$store.state.isUserLoggedIn && false">
      <v-spacer></v-spacer>

      <language-selector />
    </template>

    <!-- Logged in -->
    <template v-else>
      <v-spacer></v-spacer>

      <language-selector />
      <v-btn class="ml-4 px-3" @click="logout" dark color="grey darken-3">
        Logout
      </v-btn>
    </template>
  </v-app-bar>
</template>

<script lang="ts">
import LanguageSelector from "@/components/LanguageSelector.vue";
import Vue from "vue";
export default Vue.extend({
  components: { LanguageSelector },
  methods: {
    logout: function() {
      this.$store.dispatch("logout");
      this.$router.push({
        name: "login"
      });
    }
  }
});
</script>

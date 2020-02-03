<template>
  <v-app-bar clipped-left app class="primary">
    <img
      @click="$store.dispatch('setToken', 'uff')"
      src="@/assets/LogoSLight.svg"
      height="70%"
    />

    <!-- Logged out -->
    <template v-if="!$store.state.isUserLoggedIn">
      <v-spacer></v-spacer>

      <language-selector />
    </template>

    <!-- Logged in -->
    <template v-else>
      <v-spacer></v-spacer>

      <language-selector />
      <v-btn
        v-if="$store.state.user.role == 'ADMIN'"
        class="ml-4"
        :to="{ name: graphView ? 'EvidenceFormulaBase' : 'GraphBase' }"
      >
        {{ graphView ? "Evidence-Formulas" : "Grpahview" }}
      </v-btn>
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
        name: "Login"
      });
    }
  },
  computed: {
    graphView(): boolean {
      return (this.$route.fullPath as string).startsWith("/graph");
    }
  }
});
</script>

<template>
  <v-menu>
    <template v-slot:activator="{ on }">
      <v-btn v-on="on">
        {{ currentLang }}
      </v-btn>
    </template>
    <v-list>
      <v-list-item
        v-for="(lang, index) in langs"
        :key="index"
        @click="setLocale(lang)"
      >
        <v-list-item-title>{{ lang }}</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script lang="ts">
import { messages } from "../internationalization/translation";
import Vue from "vue";

export default Vue.extend({
  data() {
    return {
      langs: Object.keys(messages),
      currentLang: this.$i18n.locale
    };
  },
  methods: {
    setLocale(lang: string) {
      this.currentLang = lang;
      this.$i18n.locale = lang;
      document.documentElement.setAttribute("lang", this.currentLang);
      this.$router.push({
        name: this.$router.currentRoute.name,
        params: { lang: this.$i18n.locale }
      });
    }
  }
});
</script>

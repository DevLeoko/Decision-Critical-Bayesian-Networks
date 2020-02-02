<template>
  <v-select
    color="white"
    style="max-width: 80px"
    dense
    class="mt-5"
    solo
    :items="langs"
    v-model="currentLang"
    :label="currentLang"
    @change="setLocale"
  ></v-select>
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
    setLocale() {
      this.$i18n.locale = this.currentLang;
      document.documentElement.setAttribute("lang", this.currentLang);
      this.$router.push({
        name: this.$router.currentRoute.name,
        params: { lang: this.$i18n.locale }
      });
    }
  }
});
</script>

import { en } from "./languages/english.json";
import { de } from "./languages/german.json";
import Vue from "vue";
import VueI18n from "vue-i18n";

Vue.use(VueI18n);

export const messages = {
  en,
  de
};

export const i18n = new VueI18n({
  locale: "en",
  fallbackLocale: "de",
  messages
});

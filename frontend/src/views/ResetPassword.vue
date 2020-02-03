<template>
  <small-view>
    <h1 class="font-weight-light">Reset password.</h1>

    <v-alert type="error" v-model="hasError" dismissible outlined dense>{{ error }}</v-alert>

    <v-form ref="form" v-model="valid" @submit="submit">
      <v-text-field
        :rules="passwordValidation"
        @change="$refs.form.validate()"
        v-model="password"
        solo
        label="new password"
        type="password"
      ></v-text-field>

      <v-text-field
        :rules="[v => v == password || 'Passwords do not match']"
        solo
        label="retype new password"
        type="password"
      ></v-text-field>

      <v-btn
        :disabled="!valid"
        type="submit"
        :loading="loading"
        color="primary"
        class="mt-2 ml-2"
      >Reset password</v-btn>

      <v-btn color="primary" depressed class="ml-4 mt-2" dark to="login">Back to login</v-btn>
    </v-form>
  </small-view>
</template>

<style lang="scss" scoped>
h1 {
  font-size: 40px;
  span {
    font-size: 24px;
  }
}
</style>

<script lang="ts">
import SmallView from "@/components/SmallView.vue";
import { passwordValidation } from "@/utils/validation.ts";
import Vue from "vue";

export default Vue.extend({
  components: { SmallView },
  data() {
    return {
      valid: false,
      error: undefined,
      hasError: false,
      loading: false,

      password: undefined,

      passwordValidation
    };
  },

  created() {
    if (!this.$route.query || !this.$route.query.key) {
      this.$router.push({
        name: "login",
        params: {
          lang: this.$i18n.locale,
          info: "Missing reset-token! Visit 'forgot your password?' first."
        }
      });
    }
  },

  methods: {
    submit() {
      this.loading = true;

      this.axios
        .post("/reset-password", {
          password: this.password,
          token: this.$route.query.key
        })
        .then(() => {
          this.$router.push({
            name: "login",
            params: {
              lang: this.$i18n.locale,
              info: "Password changed! You can sign in now."
            }
          });
        })
        .catch(err => {
          this.hasError = true;
          this.error = err.response.data.message;
        })
        .then(() => {
          this.loading = false;
        });
    }
  }
});
</script>

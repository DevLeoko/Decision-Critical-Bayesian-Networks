<template>
  <small-view>
    <h1 class="font-weight-light">
      Forgot your password?
    </h1>

    <v-alert type="success" v-if="success" outlined>
      {{ success }}
    </v-alert>
    <v-alert type="error" v-model="hasError" dismissible outlined dense>
      {{ error }}
    </v-alert>

    <v-form v-model="valid" v-if="!success" @submit="submit">
      <p>
        No worries! Just enter your email address and we will send you a link to
        rest you password.
      </p>

      <v-text-field
        label="email"
        v-model="email"
        :rules="mailValidation"
        solo
        required
      ></v-text-field>

      <v-btn
        :disabled="!valid"
        type="submit"
        :loading="loading"
        color="primary"
        class="mt-2 ml-2"
        >reset password</v-btn
      >

      <v-btn color="primary" depressed class="ml-4 mt-2" dark to="login">
        Back to login
      </v-btn>
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
import { mailValidation } from "@/utils/validation.ts";
import Vue from "vue";

export default Vue.extend({
  components: { SmallView },
  data() {
    return {
      valid: false,
      error: undefined,
      hasError: false,
      success: "",
      loading: false,

      email: undefined,

      mailValidation
    };
  },

  methods: {
    submit() {
      this.loading = true;

      this.axios
        .post("/request-reset", { email: this.email })
        .then(() => {
          this.success = "All done! Check your emails for the reset-link.";
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

<template>
  <small-view>
    <v-layout row wrap>
      <v-flex xs12>
        <h1 class="font-weight-light ml-2 mb-2">{{ $t("login.signIn") }}</h1>
        <v-alert type="info" v-if="info" dismissible outlined>{{
          info
        }}</v-alert>
        <v-alert type="error" v-model="hasError" dismissible outlined dense>{{
          error
        }}</v-alert>
        <v-form v-model="valid" @submit="submit">
          <v-text-field
            :label="$t('login.email')"
            v-model="email"
            solo
            :rules="mailValidation"
            required
          ></v-text-field>

          <v-text-field
            :label="$t('login.password')"
            type="password"
            v-model="password"
            solo
            :rules="required('login.passwordRequired')"
            required
          ></v-text-field>

          <v-btn
            :disabled="!valid"
            type="submit"
            :loading="loading"
            color="primary"
            class="mt-2 ml-2"
            >{{ $t("login.signIn") }}</v-btn
          >
          <v-btn
            color="primary"
            class="mt-2 ml-4"
            text
            :to="{ name: 'ForgotPassword' }"
            >{{ $t("login.forgotPwQ") }}</v-btn
          >
        </v-form>
      </v-flex>
    </v-layout>
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
import { mailValidation, required } from "@/utils/validation.ts";
import Vue from "vue";
import { AxiosError } from "axios";
import { Role } from "@/store";

export default Vue.extend({
  components: { SmallView },
  data() {
    return {
      mailValidation,
      required,

      error: "",
      hasError: false,
      valid: false,
      loading: false,

      info: "",

      email: undefined,
      password: undefined
    };
  },

  created() {
    if (this.$route.params && this.$route.params.info)
      this.info = this.$route.params.info;
  },

  methods: {
    submit(event: Event) {
      event.preventDefault();

      this.loading = true;

      this.axios
        .post("/login", {
          username: this.email,
          password: this.password
        })
        .then(res => {
          this.$store.dispatch("setToken", res.data.token);
          this.$store.dispatch("setUser", res.data.user);

          if ((this.$store.state.user.role as Role) == "SUPERADMIN") {
            this.$router.push({
              name: "SuperAdmin"
            });
          } else {
            this.$router.push({
              name: "GraphBase"
            });
          }
        })
        .catch((err: AxiosError) => {
          this.hasError = true;
          if (err.response!.status == 401)
            this.error = this.$t("login.invalidUserNameOrPassword").toString();
          else this.error = err.response!.data.message;
        })
        .then(() => {
          this.loading = false;
        });
    }
  }
});
</script>

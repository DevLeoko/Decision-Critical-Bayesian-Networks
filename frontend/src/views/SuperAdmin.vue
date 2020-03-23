<template>
  <v-container>
    <v-dialog v-model="editing" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="headline">{{ formTitle }}</span>
        </v-card-title>

        <v-card-text>
          <v-snackbar color="error" timeout="3000" v-model="hasError">
            {{ this.error }}
            <v-btn icon @click="resetError()">
              <v-icon>clear</v-icon>
            </v-btn>
          </v-snackbar>

          <v-container>
            <v-form
              ref="userForm"
              v-model="valid"
              @submit="saveUser(editedUser)"
            >
              <v-row>
                <v-col>
                  <v-text-field
                    v-model="editedUser.username"
                    :label="$t('superAdmin.username')"
                    :rules="nameValidation"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  <v-text-field
                    v-model="editedUser.email"
                    :label="$t('superAdmin.email')"
                    :rules="mailValidation"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-row>
                <v-col>
                  <v-select
                    :items="roles"
                    v-model="editedUser.role"
                    :label="$t('superAdmin.role')"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-spacer />
                <v-btn color="error" text @click="stopEditing()">
                  {{ $t("superAdmin.cancel") }}
                </v-btn>
                <v-btn
                  :disabled="!valid"
                  color="primary"
                  type="submit"
                  class="ml-2"
                >
                  {{ $t("superAdmin.save") }}
                </v-btn>
              </v-row>
            </v-form>
          </v-container>
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-row>
      <v-col>
        <v-text-field
          class="mb-5"
          height="60px"
          v-model="search"
          clearable
          clear-icon="clear"
          solo
          :label="this.$t('superAdmin.searchUsers')"
          hide-details
        />
        <v-list elevation="4" class="py-0">
          <v-subheader class="headline">
            {{ $t("superAdmin.users") }}
            <v-spacer />
          </v-subheader>
          <v-divider />

          <v-list-item v-if="filteredUsers.length === 0">
            <v-list-item-title>{{
              $t("superAdmin.noUsersFound")
            }}</v-list-item-title>
          </v-list-item>

          <v-list-item
            :key="user.username + user.email + user.role"
            v-for="user in filteredUsers"
          >
            <v-list-item-avatar>
              <v-icon large>
                {{ iconFromRole(user.role) }}
              </v-icon>
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-title class="headline">
                {{ user.username }}
              </v-list-item-title>
              <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
            </v-list-item-content>
            <v-btn icon @click="editUser(user)">
              <v-icon>edit</v-icon>
            </v-btn>
            <v-btn icon @click="deleteUser(user)">
              <v-icon>delete</v-icon>
            </v-btn>
          </v-list-item>
        </v-list>
        <v-btn class="primary mt-5" @click="editUser(defaultUser)">
          <v-icon>add</v-icon> {{ $t("superAdmin.createUser") }}
        </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import axios from "axios";
import { mailValidation, nameValidation } from "@/utils/validation.ts";

interface User {
  id: number;
  username: String;
  email: String;
  role: String;
}

export default Vue.extend({
  data() {
    return {
      mailValidation,
      nameValidation,
      users: [] as User[],
      search: "",
      editedUser: {} as User,
      editing: false,
      roles: ["MODERATOR", "ADMIN"] as String[],
      valid: false,
      hasError: false,
      error: "" as String
    };
  },

  mounted() {
    this.updateUserList();
    this.editedUser = this.defaultUser();
  },

  methods: {
    iconFromRole(role: String) {
      switch (role) {
        case "MODERATOR":
          return "supervised_user_circle";
        case "ADMIN":
          return "verified_user";
        case "SUPERADMIN":
          return "verified_user";
      }
    },

    deleteUser(user: User) {
      this.axios
        .delete(`/users/${user.id}`)
        .then(() => this.updateUserList())
        .catch(this.displayError);
    },

    editUser(user: User) {
      this.editing = true;
      Object.assign(this.editedUser, user);
    },

    stopEditing() {
      this.editing = false;
      this.editedUser = this.defaultUser();
      this.resetError();
      (this.$refs.userForm as any).reset();
    },

    displayError() {
      this.hasError = true;
      this.error = this.$t("superAdmin.failedAction").toString();
    },

    resetError() {
      this.hasError = false;
      this.error = "";
    },

    saveUser(user: User) {
      if (user.id === -1) {
        this.axios
          .post("/users", user)
          .then(() => {
            this.updateUserList();
            this.stopEditing();
          })
          .catch(this.displayError);
      } else {
        this.axios
          .put(`/users/${user.id}`, user)
          .then(() => {
            this.updateUserList();
            this.stopEditing();
          })
          .catch(this.displayError);
      }
    },

    updateUserList() {
      this.axios
        .get("/users")
        .then(resp => (this.users = resp.data))
        .catch(this.displayError);
    },

    defaultUser(): User {
      return { id: -1, username: "", email: "", role: "MODERATOR" };
    }
  },
  computed: {
    filteredUsers(): Array<User> {
      let search = this.search;
      return this.users
        .filter(function(user: User) {
          return user.role !== "SUPERADMIN";
        })
        .filter(function(user: User) {
          if (!search || search.trim() === "") {
            return true;
          }
          return (
            user.username.toLowerCase().includes(search.toLowerCase()) ||
            user.email.toLowerCase().includes(search.toLowerCase()) ||
            user.role.toLowerCase().includes(search.toLowerCase())
          );
        });
    },
    formTitle(): String {
      return this.editedUser === this.defaultUser()
        ? this.$t("superAdmin.createUser").toString()
        : this.$t("superAdmin.editUser").toString();
    }
  }
});
</script>

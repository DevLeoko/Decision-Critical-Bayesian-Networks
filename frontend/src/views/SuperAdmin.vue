<template>
  <v-container>
    <v-dialog v-model="editing" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="headline">{{ formTitle }}</span>
        </v-card-title>

        <v-card-text>
          <v-container>
            <v-row>
              <v-col>
                <v-text-field
                  v-model="editedUser.name"
                  label="Username"
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row>
              <v-col>
                <v-text-field
                  v-model="editedUser.email"
                  label="Email"
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row>
              <v-col>
                <v-select
                  :items="roles"
                  v-model="editedUser.role"
                  label="Role"
                />
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="error" text @click="stopEditing()">
            Cancel
          </v-btn>
          <v-btn color="primary" text @click="saveUser()">Save</v-btn>
        </v-card-actions>
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
          label="Search users"
          hide-details
        />
        <v-list elevation="4">
          <v-subheader class="headline">
            Users
            <v-spacer />
          </v-subheader>
          <v-divider />

          <v-list-item
            :key="user.name + user.email + user.role"
            v-for="(user, index) in filteredUsers"
          >
            <v-list-item-avatar>
              <v-icon large>
                {{ iconFromRole(user.role) }}
              </v-icon>
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-title class="headline">
                {{ user.name }}
              </v-list-item-title>
              <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
            </v-list-item-content>
            <v-btn icon @click="editUser(user)">
              <v-icon>edit</v-icon>
            </v-btn>
            <v-btn icon @click="deleteUser(index)">
              <v-icon>delete</v-icon>
            </v-btn>
          </v-list-item>
        </v-list>
        <v-btn class="primary mt-5" @click="editUser(defaultUser)">
          <v-icon>add</v-icon> Create User
        </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import axios from "axios";

interface User {
  id: number;
  name: String;
  email: String;
  role: String;
}

export default Vue.extend({
  data() {
    return {
      users: [
        {
          id: 0,
          name: "moderator",
          email: "moderator@dcbn.io",
          role: "MODERATOR"
        },
        { id: 0, name: "admin", email: "admin@dcbn.io", role: "ADMIN" },
        {
          id: 0,
          name: "superadmin",
          email: "superadmin@dcbn.io",
          role: "SUPERADMIN"
        },
        { id: 0, name: "fabio", email: "fabio@dcbn.io", role: "MODERATOR" }
      ] as User[],
      search: "",
      editedUser: {} as User,
      editedUserIndex: 0,
      editing: false,
      roles: ["MODERATOR", "ADMIN"] as String[]
    };
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
      axios;
    },
    editUser(user: User, index: number) {
      this.editing = true;
      this.editedUserIndex = index;
      Object.assign(this.editedUser, user);
    },
    stopEditing() {
      this.editing = false;
      this.editedUser = this.defaultUser();
      this.editedUserIndex = -1;
    },
    saveUser() {},
    defaultUser(): User {
      return { id: -1, name: "", email: "", role: "MODERATOR" };
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
            user.name.toLowerCase().includes(search.toLowerCase()) ||
            user.email.toLowerCase().includes(search.toLowerCase()) ||
            user.role.toLowerCase().includes(search.toLowerCase())
          );
        });
    },
    formTitle(): String {
      return this.editedUser === this.defaultUser()
        ? "Create User"
        : "Edit User";
    }
  }
});
</script>

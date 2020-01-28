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
                  v-model="editedUser.username"
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
          <v-btn color="primary" text @click="saveUser(editedUser)">Save</v-btn>
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
        <v-list elevation="4" class="py-0">
          <v-subheader class="headline">
            Users
            <v-spacer />
          </v-subheader>
          <v-divider />

          <v-list-item v-if="filteredUsers.length === 0">
            <v-list-item-title>No users found!</v-list-item-title>
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
  username: String;
  email: String;
  role: String;
}

export default Vue.extend({
  data() {
    return {
      users: [] as User[],
      search: "",
      editedUser: {} as User,
      editing: false,
      roles: ["MODERATOR", "ADMIN"] as String[]
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
        .then(_ => this.updateUserList())
        .catch(err => console.log(err.response));
    },

    editUser(user: User) {
      this.editing = true;
      Object.assign(this.editedUser, user);
    },

    stopEditing() {
      this.editing = false;
      this.editedUser = this.defaultUser();
    },

    saveUser(user: User) {
      if (user.id === -1) {
        this.axios
          .post("/users", user)
          .then(_ => {
            this.updateUserList();
            this.stopEditing();
          })
          .catch(err => console.log(err.response));
      } else {
        this.axios
          .put(`/users/${user.id}`, user)
          .then(_ => {
            this.updateUserList();
            this.stopEditing();
          })
          .catch(err => console.log(err.response));
      }
    },

    updateUserList() {
      this.axios
        .get("/users")
        .then(resp => (this.users = resp.data))
        .catch(err => console.log(err.response));
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
        ? "Create User"
        : "Edit User";
    }
  }
});
</script>

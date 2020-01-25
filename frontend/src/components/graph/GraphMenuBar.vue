<template>
  <v-card height="0px">
    <v-card-title class="blue white--text">
      <span class="headline">Editor</span>
      <v-btn color="primary" class="ml-3"><v-icon>save</v-icon></v-btn>
      <v-btn color="primary" class="ml-3"><v-icon>add</v-icon></v-btn>
      <v-btn color="primary" class="ml-3">
        <v-icon>arrow_right_alt</v-icon></v-btn
      ><v-btn color="primary" class="ml-3">
        <v-icon>arrow_right_alt</v-icon>Time</v-btn
      >
      <v-select
        :items="items"
        v-model="value"
        label="label"
        dense
        outlined
        class="ml-3"
      ></v-select>
      <v-btn color="primary" class="ml-3"> <v-icon>undo</v-icon></v-btn>
      <v-btn color="primary" class="ml-3"> <v-icon>redo</v-icon></v-btn>
      <v-text-field
        class="ml-3"
        outlined
        dense
        label="Timesteps"
        v-model="value"
      ></v-text-field>

      <v-spacer></v-spacer>
      <v-btn color="primary">Switch to Testenvironment</v-btn>
    </v-card-title>
  </v-card>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({});
</script>

<template>
  <v-toolbar bottom>
    <v-toolbar-title>Editor</v-toolbar-title>

    <v-btn small color="primary" class="ml-3"><v-icon>save</v-icon></v-btn>
    <v-btn small color="primary" class="ml-3" @click="$emit('nodeAdd')"
      ><v-icon>add </v-icon></v-btn
    >
    <v-btn small color="primary" class="ml-3" @click="$emit('edgeAdd')">
      <v-icon>arrow_right_alt</v-icon></v-btn
    ><v-btn small color="primary" class="ml-3" @click="$emit('edgeTAdd')">
      <v-icon>arrow_right_alt</v-icon>Time</v-btn
    >
    <v-select class="ml-3" outlined flat dense></v-select>
    <v-btn small color="primary" class="ml-3"> <v-icon>undo</v-icon></v-btn>
    <v-btn small color="primary" class="ml-3"> <v-icon>redo</v-icon></v-btn>
    <v-text-field
      class="ml-3"
      outlined
      dense
      label="Timesteps"
      v-model="value"
    ></v-text-field>

    <v-spacer></v-spacer>
    <v-btn small color="primary" class="ml-3">Switch to Testenvironment</v-btn>
  </v-toolbar>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({});
</script>

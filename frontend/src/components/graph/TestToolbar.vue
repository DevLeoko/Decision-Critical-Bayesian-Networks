<template>
  <v-toolbar small bottom>
    <v-toolbar-title>Test</v-toolbar-title>

    <v-btn color="success" class="ml-3"
      ><v-icon class="mr-1">check</v-icon>Test</v-btn
    >
    <v-btn small color="primary" class="ml-3">Clear graph</v-btn>
    <v-btn small color="primary" class="ml-3">Export state</v-btn
    ><v-btn small color="primary" class="ml-3"> Import state</v-btn>
    <v-spacer></v-spacer>
    <v-btn small color="primary">Switch to Editor</v-btn>
  </v-toolbar>
</template>

<script lang="ts">
import Vue from "vue";
export default Vue.extend({});
</script>

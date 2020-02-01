<template>
  <v-toolbar small bottom style="width: 100%">
    <v-btn color="success" class="ml-3" @click="$emit('test', graphResp)">
      <v-icon class="mr-1">check</v-icon>Test
    </v-btn>
    <v-spacer></v-spacer>
    <v-btn small color="primary" class="ml-3">Clear graph</v-btn>
    <v-btn small color="primary" class="ml-3">Export state</v-btn
    ><v-btn small color="primary" class="ml-3"> Import state</v-btn>
    <v-spacer></v-spacer>
    <v-btn small color="primary lighten-2">Switch to Editor</v-btn>
  </v-toolbar>
</template>

<script lang="ts">
import Vue from "vue";

import graphResp from "@/../tests/resources/graph1_resp.json";
export default Vue.extend({
  data() {
    return {
      graphResp
    };
  }
});
</script>

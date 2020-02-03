<template>
  <v-toolbar small bottom style="width: 100%">
    <v-btn color="success" class="ml-3" @click="$emit('test', graphResp)">
      <v-icon class="mr-1">check</v-icon>{{ $t("testToolbar.test") }}
    </v-btn>
    <v-spacer></v-spacer>
    <v-btn small color="primary" class="ml-3">{{
      $t("testToolbar.clearGraph")
    }}</v-btn>
    <v-btn small color="primary" class="ml-3">{{
      $t("testToolbar.exportState")
    }}</v-btn
    ><v-btn small color="primary" class="ml-3">{{
      $t("testToolbar.importState")
    }}</v-btn>
    <v-spacer></v-spacer>
    <v-btn small color="primary lighten-2">{{
      $t("testToolbar.switchToEditor")
    }}</v-btn>
  </v-toolbar>
</template>

<script lang="ts">
import Vue from "vue";

// TODO fetch actual response from backend
import graphResp from "@/../tests/resources/graph1_resp.json";
export default Vue.extend({
  data() {
    return {
      graphResp
    };
  }
});
</script>

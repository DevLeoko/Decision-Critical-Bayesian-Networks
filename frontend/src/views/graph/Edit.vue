<template>
  <v-layout row wrap class="ma-1">
    <v-flex xs12>
      <edit-bar />
    </v-flex>
  </v-layout>
</template>
<script lang="ts">
import EditBar from "@/components/graph/GraphMenuBar.vue";
import Vue from "vue";
export default Vue.extend({
  components: {
    EditBar
  }
});
</script>

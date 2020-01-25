<template>
  <!-- $route.params.id  -->
  <v-row style="height: 100%">
    <folder-view :graphs="graphs"></folder-view>
    <v-col><router-view></router-view></v-col>
    <v-col
      v-if="$route.params.id === undefined"
      cols="6"
      offset="3"
      align-self="center"
    >
      <v-alert type="info" color="grey darken-1" :value="true">
        Please select a graph or create a new one!
      </v-alert>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import FolderView from "@/components/graph/GraphFolderView.vue";

import Vue from "vue";
export default Vue.extend({
  components: { FolderView },
  data() {
    return {
      graphs: [
        { name: "TestGraph", id: 10 },
        { name: "testFolder/Graph1", id: 11 },
        { name: "testFolder/Graph2", id: 12 },
        { name: "testFolder2/Graph45", id: 13 },
        { name: "testFolder/1/grrr", id: 14 },
        { name: "testFolder/1/uff", id: 15 }
      ]
    };
  }
});
</script>

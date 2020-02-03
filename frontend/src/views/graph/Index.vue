<template>
  <!-- $route.params.id  -->
  <v-row style="height: 100%" no-gutters>
    <folder-view :graphs="graphs"></folder-view>
    <v-col
      v-if="$route.params.id === undefined"
      cols="6"
      offset="3"
      align-self="center"
    >
      <v-alert type="info" color="grey darken-1" :value="true">
        {{ $t("graphIndex.selectOrCreate") }}
      </v-alert>
    </v-col>
    <router-view v-else></router-view>
  </v-row>
</template>

<script lang="ts">
import FolderView from "@/components/graph/FolderView.vue";

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

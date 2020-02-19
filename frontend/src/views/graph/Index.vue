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
    <router-view v-else :key="$route.name + $route.params.id"></router-view>
  </v-row>
</template>

<style lang="scss">
#mynetwork {
  width: 100%;
  height: 100%;
  max-height: 100vh;
  border: 1px solid lightgray;
}
</style>

<script lang="ts">
import FolderView from "@/components/graph/FolderView.vue";
import { dcbn } from "@/utils/graph/graph";

import Vue from "vue";
export default Vue.extend({
  components: { FolderView },
  data() {
    return {
      graphs: [] as dcbn.DenseGraph[]
    };
  },

  created() {
    this.axios.get("/graphs?withStructure=false").then(res => {
      const graphArray = res.data as dcbn.Graph[];
      graphArray.forEach(graph => {
        this.graphs.push({
          name: graph.name,
          id: graph.id
        });
      });
    });
  }
});
</script>

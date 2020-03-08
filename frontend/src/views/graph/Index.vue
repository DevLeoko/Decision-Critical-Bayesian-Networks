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
        {{ $t("graph.index.selectOrCreate") }}
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

  methods: {
    fetchGraphs(delayed: boolean) {
      this.axios.get(`/graphs?delayed=${delayed}`).then(res => {
        const graphArray = res.data as { graph: dcbn.Graph; locked: boolean }[];

        this.graphs = [];
        graphArray.forEach(obj => {
          this.graphs.push({
            name: obj.graph.name,
            id: obj.graph.id,
            locked: obj.locked
          });
        });
        this.fetchGraphs(true);
      });
    }
  },

  created() {
    this.fetchGraphs(false);
  }
});
</script>

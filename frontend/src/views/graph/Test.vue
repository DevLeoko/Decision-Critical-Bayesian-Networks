<template>
  <div style="max-height: 100%; width: 100%">
    <test-toolbar />
    <div id="mynetwork" ref="network"></div>
  </div>
</template>

<style lang="scss" scoped>
#mynetwork {
  width: 100%;
  height: 100%;
  border: 1px solid lightgray;
}
</style>

<script lang="ts">
interface Node {
  id: number;
  label: string;
}

interface Link {
  id?: number;
  from: number;
  to: number;
}

import TestToolbar from "@/components/graph/TestToolbar.vue";
import Vue from "vue";
import vis from "vis-network";
import graph from "@/../tests/resources/graph1.json";

export default Vue.extend({
  components: {
    TestToolbar
  },

  data() {
    return {
      graph,

      nodes: [
        { id: 1, label: "Node 1" },
        { id: 2, label: "Node 2" },
        { id: 3, label: "Node 3" },
        { id: 4, label: "Node 4" },
        { id: 5, label: "Node 5" }
      ]
    };
  },

  mounted() {
    // create an array with edges
    var edges = new vis.DataSet<Link>([
      { from: 1, to: 3 },
      { from: 1, to: 2 },
      { from: 2, to: 4 },
      { from: 2, to: 5 },
      { from: 3, to: 3 }
    ]);

    // create a network
    var container = document.getElementById("mynetwork");

    var data = {
      nodes: this.nodes,
      edges: edges
    };

    console.log(this.nodes);

    var options = {};
    var network = new vis.Network(container!, data, options);
  }

  // watch: {
  //   graph() {

  //   }
  // }
});
</script>

<template>
  <div>
    <!-- <h1>Test graph {{ $route.params.id }}</h1> -->
    <test-toolbar />

    <div id="mynetwork" ref="network"></div>
  </div>
</template>

<style lang="scss" scoped>
#mynetwork {
  width: 600px;
  height: 400px;
  border: 1px solid lightgray;
}
</style>

<script lang="ts">
import TestToolbar from "@/components/graph/TestToolbar.vue";
import Vue from "vue";
import vis from "vis-network";

export default Vue.extend({
  components: {
    TestToolbar
  },

  mounted() {
    // create an array with nodes
    var nodes = new vis.DataSet([
      { id: 1, label: "Node 1" },
      { id: 2, label: "Node 2" },
      { id: 3, label: "Node 3" },
      { id: 4, label: "Node 4" },
      { id: 5, label: "Node 5" }
    ]);

    // create an array with edges
    var edges = new vis.DataSet<any>([
      { from: 1, to: 3 },
      { from: 1, to: 2 },
      { from: 2, to: 4 },
      { from: 2, to: 5 },
      { from: 3, to: 3 }
    ]);

    // create a network
    var container = document.getElementById("mynetwork");

    var data = {
      nodes: nodes,
      edges: edges
    };
    var options = {};
    var network = new vis.Network(container!, data, options);
  }
});
</script>

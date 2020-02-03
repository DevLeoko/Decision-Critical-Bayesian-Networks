<template>
  <div>
    <edit-bar />
    <div id="mynetwork" ref="network"></div>
  </div>
</template>

<style lang="css" scoped>
#mynetwork {
  width: fit-content;
  height: fit-content;
  border: 1px solid lightgray;
}
</style>

<script lang="ts">
import EditBar from "@/components/graph/EditorToolbar.vue";
import Vue from "vue";
import vis from "vis-network";

export default Vue.extend({
  components: {
    EditBar
  },
  mounted() {
    // create an array with nodes
    var nodes = new vis.DataSet([
      { id: 1, label: "Node 1" },
      { id: 2, label: "Node 2" },
      { id: 3, label: "Node 3" },
      { id: 4, label: "Node 4" },
      { id: 5, label: "Node 5" },
      { id: 6, label: "Node 6" }
    ]);

    // create an array with edges
    var edges = new vis.DataSet<any>([
      { from: 1, to: 3 },
      { from: 1, to: 2 },
      { from: 2, to: 4 },
      { from: 2, to: 5 },
      { from: 3, to: 6 }
    ]);

    // create a network
    var container = document.getElementById("mynetwork");

    var data = {
      nodes: nodes,
      edges: edges
    };
    var options = {
      physics: {
        enabled: false
      },
      nodes: {
        shape: "square",
        title: undefined,
        value: undefined
      },
      edges: {
        arrows: {
          to: {
            enabled: true,
            imageHeight: undefined,
            imageWidth: undefined,
            scaleFactor: 1,
            src: undefined,
            type: "arrow"
          },
          smooth: {
            enabled: true
          }
        }
      },
      manipulation: {
        enabled: true,
        initiallyActive: true,
        addEdge: true,
        editNode: undefined,
        deleteNode: true,
        deleteEdge: true
      },
      layout: {}
    };

    var network = new vis.Network(container!, data, options);
  }
});
</script>
